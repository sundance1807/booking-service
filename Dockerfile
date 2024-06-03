FROM gradle:8.7.0-jdk17 AS build
WORKDIR /app
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle build

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar booking-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "booking-service.jar"]
