FROM maven:3.9.11-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2/repository mvn -B -ntp dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2/repository mvn -B -ntp clean package -DskipTests
RUN mkdir -p extracted && java -Djarmode=layertools -jar target/*.jar extract --destination extracted/

FROM eclipse-temurin:21-jre-alpine AS runtime
RUN apk add --no-cache dumb-init curl \
    && addgroup -S -g 10002 spring \
    && adduser -S -D -H -u 10002 -G spring spring \
    && mkdir -p /app /var/log/gymai \
    && chown -R spring:spring /app /var/log/gymai
WORKDIR /app
COPY --from=builder --chown=spring:spring /app/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /app/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /app/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /app/extracted/application/ ./
USER spring
EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=55 -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Djava.security.egd=file:/dev/./urandom -Djava.awt.headless=true -Dspring.jmx.enabled=false"
HEALTHCHECK --interval=20s --timeout=5s --start-period=45s --retries=3 CMD curl -fsS http://127.0.0.1:8080/actuator/health || exit 1
ENTRYPOINT ["dumb-init", "--", "sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
