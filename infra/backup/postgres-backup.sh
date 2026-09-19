#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$ROOT_DIR"

if [ ! -f .env ]; then
  echo "ERROR: $ROOT_DIR/.env not found" >&2
  exit 1
fi

set -a
# shellcheck disable=SC1091
source .env
set +a

BACKUP_DIR="${BACKUP_DIR:-/home/ubuntu/backups/gymai}"
RETENTION_DAYS="${RETENTION_DAYS:-14}"
TIMESTAMP="$(date -u '+%Y%m%dT%H%M%SZ')"
OUTPUT="${BACKUP_DIR}/gym_ai_${TIMESTAMP}.sql.gz"

mkdir -p "$BACKUP_DIR"
chmod 700 "$BACKUP_DIR"

echo "[backup] Starting PostgreSQL backup -> $OUTPUT"
docker exec gymai-postgres pg_dump \
  --username="$POSTGRES_USER" \
  --dbname="${POSTGRES_DB:-gym_ai}" \
  --no-owner \
  --no-acl \
  | gzip -9 > "$OUTPUT"

chmod 600 "$OUTPUT"
find "$BACKUP_DIR" -type f -name 'gym_ai_*.sql.gz' -mtime "+$RETENTION_DAYS" -delete

echo "[backup] Completed. Retained backups:"
ls -lh "$BACKUP_DIR"/gym_ai_*.sql.gz 2>/dev/null || true
