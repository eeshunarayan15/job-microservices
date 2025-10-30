# Stage 1: Build (same as above)
FROM gradle:8.5.0-jdk17-alpine AS build
WORKDIR /home/gradle/src

COPY gradle ./gradle
COPY gradlew build.gradle settings.gradle ./
RUN --mount=type=cache,target=/root/.gradle \
    gradle dependencies --no-daemon || true

COPY src ./src
RUN --mount=type=cache,target=/root/.gradle \
    gradle clean bootJar --no-daemon -x test

# Stage 2: Runtime with Distroless (NO shell, ultra-secure)
FROM gcr.io/distroless/java17-debian12:nonroot

LABEL maintainer="eeshunarayan15"
LABEL description="Company Microservice"
LABEL version="0.0.1-SNAPSHOT"

WORKDIR /app

# Copy JAR
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Expose port
EXPOSE 8081

# Run as non-root (distroless default)
USER nonroot:nonroot

# Distroless doesn't support HEALTHCHECK or ENV, use direct command
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:+UseG1GC", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]