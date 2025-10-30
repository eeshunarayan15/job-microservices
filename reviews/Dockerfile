FROM gradle:8.5.0-jdk17-alpine AS build
WORKDIR /home/gradle/src
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle clean bootJar --no-daemon -x test -Dorg.gradle.jvmargs="-Xmx2g"

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
RUN rm -rf /tmp/* /var/cache/apk/*  # Clean Alpine cache
EXPOSE 8083
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar", "--spring.profiles.active=docker"]
