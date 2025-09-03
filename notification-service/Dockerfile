
# ---- Stage 1: Build Application ----
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

# Set the working directory
WORKDIR /app

# Copy dependency and project files for caching
COPY pom.xml ./
COPY .mvn/ ./.mvn/
# Download dependencies with multi-threaded, parallel build
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn dependency:go-offline -B -T 2C

# Copy source code
COPY src ./src

# Build the application, skipping tests
RUN --mount=type=cache,target=/root/.m2/repository \
    mvn clean package -B -DskipTests=true

# Extract the layered JAR for optimal runtime
RUN mkdir -p extracted && \
    java -Djarmode=layertools -jar target/*.jar extract --destination extracted/

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install essential tools and create a non-root user
RUN apk add --no-cache dumb-init curl && \
    adduser -D -g 'spring' spring

# Correctly create the application and logs directories with proper permissions
RUN mkdir -p /app/logs && chown -R spring:spring /app

# Set working directory and copy the layered application files
WORKDIR /app
COPY --from=builder --chown=spring:spring /app/extracted/dependencies/ ./
COPY --from=builder --chown=spring:spring /app/extracted/spring-boot-loader/ ./
COPY --from=builder --chown=spring:spring /app/extracted/snapshot-dependencies/ ./
COPY --from=builder --chown=spring:spring /app/extracted/application/ ./

# Switch to the non-root user
USER spring

# Expose the application port
EXPOSE 8080

# Realistic and effective JVM flags
ENV JAVA_OPTS="\
    -server \
    -XX:MaxRAMPercentage=75.0 \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=100 \
    -XX:+UseStringDeduplication \
    -Djava.security.egd=file:/dev/./urandom"

# Health check
HEALTHCHECK --interval=20s --timeout=5s --start-period=45s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Start the application using dumb-init for proper signal handling
ENTRYPOINT ["dumb-init", "--", "java", "org.springframework.boot.loader.launch.JarLauncher"]










