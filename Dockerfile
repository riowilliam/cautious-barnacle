FROM maven:3.8.6-openjdk-8 AS build
WORKDIR /app
COPY . .
RUN mvn clean package assembly:single -DskipTests

FROM openjdk:8-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/fision-service-1.0-SNAPSHOT.jar /app/myapp.jar

ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
