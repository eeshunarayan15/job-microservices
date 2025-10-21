# Stage 1: Build the application
# Use a full Gradle and JDK image for building
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /home/gradle/src
COPY build.gradle settings.gradle ./
COPY src ./src
# Execute the Gradle build to generate the executable JAR, skipping tests (-x test).
# The jar name is assumed to be in the format: <project.name>-<version>.jar (job-0.0.1-SNAPSHOT.jar)
RUN gradle clean assemble bootJar --no-daemon -x test

# Stage 2: Create the final, lightweight image
# This stage uses a minimal Java Runtime Environment (JRE)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built JAR from the 'build' stage
# Adjust the filename if your version changes in build.gradle
COPY --from=build /home/gradle/src/build/libs/job-0.0.1-SNAPSHOT.jar app.jar

# Expose the configured port (8082 for the Job service)
EXPOSE 8082

# Run the application, explicitly activating the 'docker' profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]