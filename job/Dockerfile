# -------------------------------------------------
# Stage 1: Build the Spring-Boot fat-jar
# -------------------------------------------------
FROM gradle:8.5.0-jdk17-alpine AS build
WORKDIR /app

# Cache Gradle dependencies
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon || true

# Copy source and build
COPY src ./src
RUN gradle bootJar --no-daemon -x test


# -------------------------------------------------
# Stage 2: Extract the layered jar
# -------------------------------------------------
FROM eclipse-temurin:17-jre-alpine AS extract
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract


# -------------------------------------------------
# Stage 3: Runtime – distroless (≈55 MB)
# -------------------------------------------------
FROM gcr.io/distroless/java17-debian12

WORKDIR /app
COPY --from=extract /app/dependencies/ ./
COPY --from=extract /app/spring-boot-loader/ ./
COPY --from=extract /app/snapshot-dependencies/ ./
COPY --from=extract /app/application/ ./

EXPOSE 8082

# ------------------------------------------------------------------
# Advanced G1GC + JVM tuning for Render Free Tier (512 MiB RAM)
# ------------------------------------------------------------------
#  • Max heap = 70 % of container memory  →  ~350 MiB
#  • Low pause-time goal (≤ 100 ms)
#  • Parallel & ConcMark threads limited to 2 (0.5 vCPU)
#  • Fast random source, no unnecessary background work
# ------------------------------------------------------------------
ENV JAVA_OPTS="\
  -Xms150m -Xmx350m \
  -XX:MaxRAMPercentage=70.0 \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=100 \
  -XX:G1HeapRegionSize=4m \
  -XX:InitiatingHeapOccupancyPercent=35 \
  -XX:G1ReservePercent=15 \
  -XX:ParallelGCThreads=2 \
  -XX:ConcGCThreads=2 \
  -XX:+UnlockExperimentalVMOptions \
  -XX:G1NewSizePercent=20 \
  -XX:G1MaxNewSizePercent=30 \
  -XX:+ExitOnOutOfMemoryError \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/tmp/heapdump.hprof \
  -Djava.security.egd=file:/dev/./urandom \
  -Dspring.backgroundpreinitializer.ignore=true \
  -Dserver.tomcat.threads.max=50 \
  -Dserver.tomcat.threads.min-spare=10 \
  -Dserver.tomcat.accept-count=100 \
  -Dserver.tomcat.max-connections=200"

# ------------------------------------------------------------------
# Entrypoint – use the layered launcher
# ------------------------------------------------------------------
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]