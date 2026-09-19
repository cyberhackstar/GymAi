# GymAI

GymAI is an Angular + Spring Boot application with authentication, fitness-plan, and notification services.

## Production deployment

This repository now includes an Oracle VM production stack in `docker-compose.prod.yml`, a dedicated loopback-bound Nginx gateway in `infra/nginx/gymai.conf`, an automatic health-gated rollback deploy script in `infra/deploy/deploy.sh`, and GitHub Actions in `.github/workflows/ci-cd.yml`.

See `DEPLOY_ORACLE_VM.md` for the exact VM/Cloudflare/OAuth setup.
