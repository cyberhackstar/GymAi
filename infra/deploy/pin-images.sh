#!/usr/bin/env bash
set -euo pipefail
PLATFORM="linux/arm64"
IMAGES=(
  "postgres:16-alpine"
  "redis:7-alpine"
  "rabbitmq:4.1-alpine"
  "nginx:1.27-alpine"
)

echo "# Resolved on $(date -u '+%Y-%m-%dT%H:%M:%SZ') for ${PLATFORM}."
for image in "${IMAGES[@]}"; do
  echo "Resolving ${image}..." >&2
  docker buildx imagetools inspect "$image" --format '{{json .Manifest}}' | python3 -c '
import json,sys
m=json.load(sys.stdin)
if "manifests" in m:
    for item in m["manifests"]:
        p=item.get("platform",{})
        if p.get("architecture")=="arm64" and p.get("os")=="linux":
            print(f"image: {item.get("digest")}  # {sys.argv[1]}")
            raise SystemExit
    raise SystemExit("arm64 manifest not found")
print(f"image: {m.get("digest")}  # {sys.argv[1]}")
' "$image"
done
