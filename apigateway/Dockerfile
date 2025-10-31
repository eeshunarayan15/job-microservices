# Build stage with GraalVM
FROM ghcr.io/graalvm/native-image-community:17 AS build

RUN microdnf install -y findutils

WORKDIR /build

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

COPY src ./src

# Build with increased memory
RUN ./gradlew nativeCompile --no-daemon -Dorg.gradle.jvmargs="-Xmx8g"

# Run stage
FROM ubuntu:22.04

RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    curl \
    && rm -rf /var/lib/apt/lists/*

RUN useradd -m -u 1001 appuser

WORKDIR /app

COPY --from=build /build/build/native/nativeCompile/apigateway-service ./app

RUN chmod +x ./app && chown appuser:appuser ./app

USER appuser

EXPOSE 8085

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["./app"]