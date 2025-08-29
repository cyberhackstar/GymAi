# # ---- Stage 1: Build the application ----
# FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
# WORKDIR /app

# # Copy pom and download dependencies first (caching)
# COPY pom.xml .
# RUN mvn dependency:go-offline -B

# # Copy source code and build
# COPY src ./src
# RUN mvn clean package -DskipTests

# # ---- Stage 2: Run the application ----
# FROM eclipse-temurin:21-jre-alpine
# WORKDIR /app

# # Copy built JAR from builder stage
# COPY --from=build /app/target/*.jar app.jar

# # Expose only container-internal port (no host mapping, NPM will proxy)
# EXPOSE 8080

# # Optimization flags (good for Pi 4, 8GB RAM)
# ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseZGC"

# # Run app
# ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# ==============================================================================
# ULTRA-OPTIMIZED DOCKERFILE FOR RASPBERRY PI 4 (8GB RAM)
# Optimizations: Build cache, layer reduction, performance tuning, security
# ==============================================================================

# ---- Stage 1: Dependency Resolution (Cached Layer) ----
# FROM maven:3.9.9-eclipse-temurin-21-alpine AS deps
# WORKDIR /app
# # Copy only dependency files for maximum cache efficiency
# COPY pom.xml ./
# COPY .mvn/ ./.mvn/
# # Parallel dependency download with retry and offline mode prep
# RUN mvn dependency:go-offline -B -T 2C --fail-never && \
#     mvn dependency:resolve-sources -B -T 2C --fail-never

# # ---- Stage 2: Build Application (Optimized) ----
# FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
# WORKDIR /app

# # Copy cached dependencies from previous stage
# COPY --from=deps /root/.m2 /root/.m2
# COPY --from=deps /app/pom.xml ./
# COPY --from=deps /app/.mvn ./.mvn

# # Copy source and build with maximum optimizations
# COPY src ./src
# RUN mvn clean package -B -T 2C -q \
#     -DskipTests=true \
#     -Dmaven.compile.fork=true \
#     -Dmaven.compiler.maxmem=1024m \
#     -XX:+TieredCompilation \
#     -XX:TieredStopAtLevel=1 && \
#     # Extract JAR for faster startup (Spring Boot optimization)
#     mkdir -p extracted && \
#     java -Djarmode=layertools -jar target/*.jar extract --destination extracted/

# # ---- Stage 3: Runtime (Ultra-Optimized) ----
# FROM eclipse-temurin:21-jre-alpine AS runtime

# # Install performance monitoring tools and optimize base image
# RUN apk add --no-cache \
#     dumb-init \
#     wget \
#     curl && \
#     # Create optimized user
#     addgroup -g 1001 -S spring && \
#     adduser -u 1001 -S spring -G spring && \
#     # Optimize filesystem
#     echo 'spring soft nofile 65536' >> /etc/security/limits.conf && \
#     echo 'spring hard nofile 65536' >> /etc/security/limits.conf

# WORKDIR /app

# # Copy extracted layers for faster startup (Spring Boot layered approach)
# COPY --from=build --chown=spring:spring /app/extracted/dependencies/ ./
# COPY --from=build --chown=spring:spring /app/extracted/spring-boot-loader/ ./
# COPY --from=build --chown=spring:spring /app/extracted/snapshot-dependencies/ ./
# COPY --from=build --chown=spring:spring /app/extracted/application/ ./

# # Switch to non-root user
# USER spring:spring

# # Expose port
# EXPOSE 8080

# # ULTRA-OPTIMIZED JVM FLAGS FOR RASPBERRY PI 4 (8GB RAM)
# ENV JAVA_OPTS="\
#     -server \
#     -XX:+UnlockExperimentalVMOptions \
#     -XX:+UseContainerSupport \
#     -XX:MaxRAMPercentage=65.0 \
#     -XX:InitialRAMPercentage=50.0 \
#     \
#     # Garbage Collection Optimization \
#     -XX:+UseG1GC \
#     -XX:MaxGCPauseMillis=100 \
#     -XX:G1HeapRegionSize=16m \
#     -XX:G1ReservePercent=25 \
#     -XX:G1MixedGCCountTarget=8 \
#     -XX:G1OldCSetRegionThresholdPercent=10 \
#     -XX:+G1UseAdaptiveIHOP \
#     -XX:G1MixedGCLiveThresholdPercent=85 \
#     \
#     # Performance Optimizations \
#     -XX:+UseStringDeduplication \
#     -XX:+OptimizeStringConcat \
#     -XX:+UseCompressedOops \
#     -XX:+UseCompressedClassPointers \
#     -XX:+TieredCompilation \
#     -XX:TieredStopAtLevel=4 \
#     -XX:CompileThreshold=1000 \
#     \
#     # Memory Optimizations \
#     -Xss256k \
#     -XX:MetaspaceSize=128m \
#     -XX:MaxMetaspaceSize=256m \
#     -XX:MaxDirectMemorySize=256m \
#     -XX:ReservedCodeCacheSize=128m \
#     -XX:InitialCodeCacheSize=64m \
#     \
#     # ARM64 Specific Optimizations \
#     -XX:+UseFMA \
#     -XX:+UseAES \
#     -XX:+UseAESIntrinsics \
#     -XX:+UseSHA \
#     -XX:+UseSHA1Intrinsics \
#     -XX:+UseSHA256Intrinsics \
#     \
#     # Fast Startup & Security \
#     -Djava.security.egd=file:/dev/./urandom \
#     -Djava.awt.headless=true \
#     -Dspring.backgroundpreinitializer.ignore=true \
#     -Dspring.jmx.enabled=false \
#     \
#     # Network & API Optimizations \
#     -Djava.net.preferIPv4Stack=true \
#     -Dnetworkaddress.cache.ttl=60 \
#     -Dsun.net.inetaddr.ttl=60 \
#     -Dcom.sun.management.jmxremote=false \
#     -Dio.netty.allocator.maxOrder=7 \
#     -Dio.netty.recycler.maxCapacityPerThread=0 \
#     -Dspring.reactor.netty.pool.maxConnections=1000 \
#     -Dspring.reactor.netty.pool.maxIdleTime=30s \
#     \
#     # File I/O Optimizations \
#     -Djava.io.tmpdir=/tmp \
#     -XX:+UseFastAccessorMethods"

# # Advanced health check with retries and performance monitoring
# # Note: localhost:8080 is correct here - healthcheck runs INSIDE the container
# HEALTHCHECK --interval=20s --timeout=5s --start-period=45s --retries=3 \
#     CMD curl -f --connect-timeout 3 --max-time 5 http://localhost:8080/actuator/health/readiness || exit 1

# # Alternative health check methods (choose one):
# # CMD wget --no-verbose --tries=1 --spider --timeout=5 http://localhost:8080/actuator/health/readiness || exit 1
# # CMD curl -f --connect-timeout 3 --max-time 5 http://127.0.0.1:8080/actuator/health || exit 1

# # Labels for monitoring and management
# LABEL maintainer="your-team@company.com" \
#       version="1.0" \
#       description="Ultra-optimized Spring Boot app for Raspberry Pi 4" \
#       java.version="21" \
#       optimization.level="maximum"

# # Use dumb-init for proper signal handling and zombie reaping
# ENTRYPOINT ["dumb-init", "--", "sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]

# # ==============================================================================
# # PERFORMANCE NOTES:
# # - Layered JAR extraction reduces startup time by ~40%
# # - G1GC tuned for low-latency with 100ms max pause
# # - Memory settings optimized for 8GB Pi 4 (using ~5GB max)
# # - ARM64 CPU intrinsics enabled for crypto/hash operations
# # - Network stack optimizations for faster connection handling
# # - String deduplication saves ~15% heap memory
# # - Tiered compilation balances startup vs peak performance
# # ==============================================================================

# ==============================================================================
# OPTIMIZED DOCKERFILE FOR RASPBERRY PI 4 (8GB RAM)
# ==============================================================================

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










