# Build stage with Java 21
FROM eclipse-temurin:21-jdk-jammy AS build

WORKDIR /build

# Copy Gradle wrapper and config
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# Download dependencies (cached layer)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon

# Copy source code
COPY src ./src

# Build JAR file
RUN ./gradlew bootJar --no-daemon

# Runtime stage with Java 21 JRE
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /build/build/libs/*.jar ./app.jar

# Expose port (Render uses 10000)
EXPOSE 10000

# Run the application
ENTRYPOINT ["java", "-jar", "./app.jar"]