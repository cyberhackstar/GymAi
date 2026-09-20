# GymAI production readiness — Oracle VM

## Production deployment path

`Cloudflare HTTPS -> cloudflared on Oracle VM -> 127.0.0.1:4001 -> gymai-gateway -> frontend/auth/plan/notification`

PostgreSQL, Redis, and RabbitMQ remain private Docker services on `gymai_net` and are not published to the VM host.

## Included production services

The production Compose file deploys the application components that are wired into the current frontend/API flow:

- `auth-service`
- `plan-service`
- `notification-service`
- Angular `frontend`
- Nginx `gateway`
- PostgreSQL, Redis, RabbitMQ

`payment-service` and `tracking-service` remain in the repository but are intentionally not added to the production Compose/gateway because the current project does not expose a complete, verified production routing path for those services.

## CI/CD controls

A push to `main` runs secret scanning, backend tests, Angular production build/unit tests, Nginx validation, Compose validation, ARM64 image builds, Trivy image scans, GHCR push, then SSH deployment to the Oracle VM.

Images are tagged with the exact Git commit SHA. The VM deployment waits for all defined healthchecks and runs local HTTP smoke tests before marking the SHA as known-good. A failed deployment attempts automatic rollback to the previous known-good SHA.

## Application hardening applied

- Removed the committed CloudAMQP credential from notification configuration.
- Removed committed runtime log output from the plan service.
- Tightened production CORS to the GymAI public origin while retaining localhost development patterns.
- Protected plan-service APIs with JWT authentication and admin-role authorization for admin routes.
- Corrected Maven wrapper permissions for Linux CI.
- Moved H2 test dependencies into the normal Maven dependency section.
- Made production frontend API calls same-origin through the Nginx gateway.
- Added a PostgreSQL logical-backup script with configurable retention.

## Remaining application-level considerations

The current services use `spring.jpa.hibernate.ddl-auto=update` by default because the repository does not contain a migration system. Application rollback therefore does not roll the database schema backward.

The repository also contains application designs for additional services/features that are not part of the production Compose path above. Those should be wired and tested separately before exposing them through the public gateway.

Rotate any credential that was ever committed before this cleanup, even though the production source no longer contains it.
## Dependency security baseline

The Java services use Spring Boot 3.5.16 with Spring Cloud 2025.0.3 where applicable. Security-sensitive transitive dependencies that remain newer than Boot's managed baseline are pinned explicitly: pgJDBC 42.7.13 and RabbitMQ Java client 5.36.0. These pins address the vulnerable versions identified by the CI Trivy scan.
