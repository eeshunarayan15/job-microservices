FROM ghcr.io/graalvm/native-image-community:17 AS build

# Install required build tools
RUN microdnf install -y findutils

WORKDIR /build

COPY gradlew .
# Copy gradle wrapper
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon
# Copy build files
COPY build.gradle settings.gradle* ./

# Download dependencies (cached layer)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# Copy source code
COPY src ./src

# Build with increased memory
RUN ./gradlew nativeCompile --no-daemon -Dorg.gradle.jvmargs="-Xmx8g"
# Build native image with increased memory
RUN ./gradlew nativeCompile --no-daemon -Dorg.gradle.jvmargs="-Xmx8g -XX:MaxMetaspaceSize=2g"

# Run stage
FROM ubuntu:22.04

# Install runtime dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create non-root user
RUN useradd -m -u 1001 appuser

WORKDIR /app

# Copy the native executable
COPY --from=build /build/build/native/nativeCompile/apigateway-service ./app

# Set permissions
RUN chmod +x ./app && chown appuser:appuser ./app

# Switch to non-root user
USER appuser

EXPOSE 8085

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8085/actuator/health || exit 1