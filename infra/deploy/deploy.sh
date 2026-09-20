#!/usr/bin/env bash
set -euo pipefail

IMAGE_TAG="${1:?Usage: ./infra/deploy/deploy.sh <image-tag>}"
COMPOSE_FILE="docker-compose.prod.yml"
LAST_GOOD_FILE=".last_good_sha"
HEALTH_TIMEOUT_SECONDS="${HEALTH_TIMEOUT_SECONDS:-240}"
HEALTH_POLL_INTERVAL="${HEALTH_POLL_INTERVAL:-5}"
CONTAINERS_TO_CHECK=(gymai-postgres gymai-redis gymai-rabbitmq gymai-auth gymai-plan gymai-notification gymai-frontend gymai-gateway)

[[ -f .env ]] || { echo "[deploy] ERROR: .env is missing" >&2; exit 2; }
set -a
# shellcheck disable=SC1091
source .env
set +a

SITE_BASE_URL="${SITE_BASE_URL:-https://gymai.neelahouse.cloud}"
LOCAL_URL="http://127.0.0.1:${GYMAI_HTTP_PORT:-4001}"

log() { echo "[deploy] $(date -u '+%Y-%m-%dT%H:%M:%SZ') $*"; }

require_env() {
  local missing=() name
  for name in GHCR_NAMESPACE POSTGRES_USER POSTGRES_PASSWORD REDIS_PASSWORD RABBITMQ_USERNAME RABBITMQ_PASSWORD JWT_SECRET GOOGLE_CLIENT_ID GOOGLE_CLIENT_SECRET GITHUB_CLIENT_ID GITHUB_CLIENT_SECRET GMAIL_USERNAME GMAIL_APP_PASSWORD; do
    if [[ -z "${!name:-}" || "${!name}" == REPLACE_* ]]; then missing+=("$name"); fi
  done
  if (( ${#missing[@]} )); then
    log "ERROR: missing production values: ${missing[*]}"
    exit 2
  fi
}

login_ghcr() {
  if [[ -n "${GHCR_USERNAME:-}" && -n "${GHCR_PAT:-}" ]]; then
    log "Authenticating to GHCR as ${GHCR_USERNAME}..."
    printf '%s' "$GHCR_PAT" | docker login ghcr.io --username "$GHCR_USERNAME" --password-stdin >/dev/null
  else
    log "GHCR credentials not present; assuming packages are public or the host is already logged in."
  fi
}

wait_for_healthy() {
  local elapsed=0 all_healthy status c
  while (( elapsed < HEALTH_TIMEOUT_SECONDS )); do
    all_healthy=true
    for c in "${CONTAINERS_TO_CHECK[@]}"; do
      status="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}no-healthcheck{{end}}' "$c" 2>/dev/null || echo missing)"
      if [[ "$status" != healthy ]]; then
        all_healthy=false
        log "waiting: $c -> $status"
      fi
    done
    if [[ "$all_healthy" == true ]]; then
      log "All production containers are healthy."
      return 0
    fi
    sleep "$HEALTH_POLL_INTERVAL"
    elapsed=$((elapsed + HEALTH_POLL_INTERVAL))
  done
  log "ERROR: health gate timed out after ${HEALTH_TIMEOUT_SECONDS}s."
  return 1
}

smoke_test() {
  local failures=0 code
  check() {
    local label="$1" url="$2" expected="$3"
    code="$(curl -fsS -o /dev/null -w '%{http_code}' --max-time 10 "$url" || true)"
    if [[ "$code" != "$expected" ]]; then
      log "FAIL: $label -> expected $expected, got $code ($url)"
      failures=$((failures + 1))
    else
      log "OK: $label ($code)"
    fi
  }
  check "gateway health" "$LOCAL_URL/health" 200
  check "frontend" "$LOCAL_URL/" 200
  check "plan service health" "$LOCAL_URL/api/fitness/health" 200
  return "$failures"
}

public_smoke_test() {
  local url code
  for path in / /api/fitness/health; do
    url="${SITE_BASE_URL}${path}"
    code="$(curl -fsS -o /dev/null -w '%{http_code}' --max-time 15 "$url" || true)"
    if [[ "$code" == 200 ]]; then
      log "OK (public, non-blocking): $url"
    else
      log "WARN (public, non-blocking): $url -> $code"
    fi
  done
}

deploy_tag() {
  local tag="$1"
  IMAGE_TAG="$tag" docker compose --env-file .env -f "$COMPOSE_FILE" pull
  IMAGE_TAG="$tag" docker compose --env-file .env -f "$COMPOSE_FILE" up -d --remove-orphans
}

show_failure_diagnostics() {
  log "===== COMPOSE PS ====="; docker compose -f "$COMPOSE_FILE" ps || true
  for service in gateway frontend auth-service plan-service notification-service postgres redis rabbitmq; do
    log "===== $service logs ====="
    docker compose -f "$COMPOSE_FILE" logs --no-color --tail=120 "$service" || true
  done
}

require_env
login_ghcr

previous_tag=""
[[ -f "$LAST_GOOD_FILE" ]] && previous_tag="$(cat "$LAST_GOOD_FILE")"

log "Deploying exact image tag: $IMAGE_TAG"
deploy_tag "$IMAGE_TAG"

if wait_for_healthy && smoke_test; then
  printf '%s\n' "$IMAGE_TAG" > "$LAST_GOOD_FILE"
  log "Deployment succeeded: $IMAGE_TAG"
  public_smoke_test || true
  docker image prune -f >/dev/null 2>&1 || true
  exit 0
fi

show_failure_diagnostics
log "New deployment failed health/smoke gate."

if [[ -n "$previous_tag" && "$previous_tag" != "$IMAGE_TAG" ]]; then
  log "Rolling back to last known-good tag: $previous_tag"
  deploy_tag "$previous_tag"
  if wait_for_healthy && smoke_test; then
    log "Rollback succeeded. Bad tag $IMAGE_TAG is no longer live."
    public_smoke_test || true
    exit 1
  fi
  show_failure_diagnostics
  log "ERROR: rollback also failed. Manual intervention required."
  exit 2
fi

log "No previous known-good tag is recorded; automatic rollback is unavailable."
exit 2
