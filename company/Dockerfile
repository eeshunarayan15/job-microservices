# ============================================
# Multi-stage build for Spring Boot Application (Gradle)
# ============================================

# Stage 1: Build the application
FROM gradle:8.5.0-jdk17-alpine AS build

# Set working directory
WORKDIR /home/gradle/src

# Copy Gradle wrapper and configuration files first (for layer caching)
COPY gradle gradle
COPY gradlew .
COPY build.gradle settings.gradle ./

# Download dependencies (cached layer if dependencies don't change)
RUN gradle dependencies --no-daemon

# Copy source code
COPY src ./src

# Build the application (skip tests for faster build)
RUN gradle clean bootJar --no-daemon -x test

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL maintainer="eeshunarayan15"
LABEL description="Company Microservice"
LABEL version="0.0.1-SNAPSHOT"

# Set working directory
WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Create a non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring && \
    chown -R spring:spring /app

# Switch to non-root user
USER spring:spring

# Expose port
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# JVM Options for container
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=100 -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
