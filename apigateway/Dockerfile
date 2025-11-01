# Build stage with GraalVM Java 21
FROM ghcr.io/graalvm/native-image-community:21 AS build

# Install required build tools
RUN microdnf install -y findutils

WORKDIR /build

# Copy Gradle wrapper and config
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Download dependencies (cached layer)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# Copy source code
COPY src ./src

# Build native image
# Note: Use nativeCompile (Gradle plugin task)
RUN ./gradlew nativeCompile --no-daemon

# Runtime stage - minimal image
FROM ubuntu:22.04

# Install only runtime essentials
RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy native executable from build stage
COPY --from=build /build/build/native/nativeCompile/apigateway-service ./app

# Make it executable
RUN chmod +x ./app

# Expose port (adjust if needed)
EXPOSE 8085

# Let Render (or any platform) inject profile via ENV
# e.g., SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["./app"]