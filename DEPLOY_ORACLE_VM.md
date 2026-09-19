# GymAI Oracle VM deployment

## Topology

`Cloudflare HTTPS -> cloudflared (native on Oracle VM) -> 127.0.0.1:4001 -> GymAI gateway -> frontend/auth/plan/notification`

PostgreSQL, Redis, and RabbitMQ are private Docker services and are not published to the VM host. The gateway is the only published socket for this stack and it binds to loopback, so it does not collide with the other applications already using host Nginx.

## First-time VM setup

```bash
mkdir -p /home/ubuntu/apps/gymai
cd /home/ubuntu/apps/gymai
git clone <YOUR_REPO_URL> .
cp .env.example .env
nano .env
chmod 600 .env
```

The VM needs Docker Engine + Compose v2, Git, curl, and network access to GHCR. For a private GHCR package, keep `GHCR_USERNAME` + a GitHub token with `read:packages` in `.env`.

Before the first deployment, confirm the GitHub Actions secrets `DEPLOY_HOST`, `DEPLOY_SSH_KEY`, and `DEPLOY_KNOWN_HOSTS` are configured.

## Cloudflare

Add this tunnel ingress entry:

```yaml
- hostname: gymai.neelastack.com
  service: http://localhost:4001
```

Do not add Certbot/Let’s Encrypt or another origin-side HTTPS listener for this stack.

## OAuth provider updates

Change the Google OAuth authorized redirect URI to:

`https://gymai.neelahouse.cloud/login/oauth2/code/google`

Change the GitHub OAuth callback URL to:

`https://gymai.neelahouse.cloud/login/oauth2/code/github`

## GitHub Actions flow

Every `main` push runs secret scanning, backend tests, frontend build/unit tests, Compose/Nginx validation, then builds ARM64 images on `ubuntu-24.04-arm`. The images are scanned and pushed to GHCR under immutable commit-SHA tags. The deploy job SSHes to the VM and runs `infra/deploy/deploy.sh`. GitHub documents `ubuntu-24.04-arm` for ARM64 GitHub-hosted jobs; it is currently a public-preview runner label.

## Database backups

This stack keeps PostgreSQL data in a persistent Docker volume, but a persistent volume is not a backup. Install a host cron entry (for example, daily at 02:30 UTC) for: `cd /home/ubuntu/apps/gymai && ./infra/backup/postgres-backup.sh`. The script defaults to `/home/ubuntu/backups/gymai` and keeps 14 days of compressed logical dumps; set `BACKUP_DIR` and `RETENTION_DAYS` in the environment when you need different retention. Periodically copy the backup directory off the VM and test a restore on a separate PostgreSQL instance.

## Rollback

The deploy script writes `.last_good_sha` only after the new version passes the local health and smoke gates. A failed deployment automatically tries that last known-good image tag. Database rollback is not automatic; this project currently uses `ddl-auto: update` because it has no committed migration system.

## Manual validation

```bash
cd /home/ubuntu/apps/gymai
docker compose -f docker-compose.prod.yml ps
curl -I http://127.0.0.1:4001/
curl -I http://127.0.0.1:4001/api/fitness/health
curl -I https://gymai.neelahouse.cloud/
```

For service logs:

```bash
docker compose -f docker-compose.prod.yml logs --tail=200 auth-service
docker compose -f docker-compose.prod.yml logs --tail=200 plan-service
docker compose -f docker-compose.prod.yml logs --tail=200 notification-service
```
