# Build stage
FROM gradle:8.5.0-jdk17-alpine AS build
WORKDIR /home/gradle/src

# Copy gradle files first for caching
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# Download dependencies (cached layer)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build JAR
RUN ./gradlew clean bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Clean up
RUN rm -rf /tmp/* /var/cache/apk/*

# Create non-root user
RUN addgroup -g 1001 -S appuser && adduser -u 1001 -S appuser -G appuser
USER appuser

EXPOSE 10000

# Use exec form for proper signal handling
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
