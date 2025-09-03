# ---- Stage 1: Build Application ----
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml and download dependencies first (better caching)
COPY pom.xml ./
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn dependency:go-offline -B -T 2C

# Copy the rest of the source and build
COPY src ./src
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn clean package -B -DskipTests

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install essential tools and create a non-root user
RUN apk add --no-cache dumb-init curl && \
    adduser -D -g 'spring' spring

# Create app folder and logs directory
RUN mkdir -p /app/logs && chown -R spring:spring /app

WORKDIR /app

# Copy only the built JAR (fat JAR)
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

USER spring

EXPOSE 8080

ENV JAVA_OPTS="\
    -server \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=100 \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom"

HEALTHCHECK --interval=20s --timeout=5s --start-period=45s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["dumb-init", "--", "sh", "-c", "java $JAVA_OPTS -jar app.jar"]
