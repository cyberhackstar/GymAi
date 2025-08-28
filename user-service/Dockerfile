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

# ---- Stage 1: Build the application ----
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy pom and download dependencies first (caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -q

# ---- Stage 2: Run the application ----
FROM eclipse-temurin:21-jre-alpine

# Add non-root user for security
RUN addgroup -g 1000 spring && adduser -u 1000 -G spring -s /bin/sh -D spring

WORKDIR /app

# Copy built JAR from builder stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership to spring user
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring

# Expose only container-internal port (no host mapping, NPM will proxy)
EXPOSE 8080

# Optimized JVM flags for Raspberry Pi 4, 8GB RAM
ENV JAVA_OPTS="-server \
               -XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=60.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -XX:+OptimizeStringConcat \
               -Xss256k \
               -XX:MetaspaceSize=96m \
               -XX:MaxMetaspaceSize=256m \
               -Djava.security.egd=file:/dev/./urandom \
               -Djava.awt.headless=true"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]