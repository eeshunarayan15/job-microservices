# Stage 1: Build the application
# This stage uses a full Gradle and JDK to build your .jar file
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /home/gradle/src
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build bootJar --no-daemon

# Stage 2: Create the final, lightweight image
# This stage uses a minimal Java-only runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy only the built .jar file from the 'build' stage
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Expose the correct port from your properties
EXPOSE 8088

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]