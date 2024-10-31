FROM maven:3.8.6-openjdk-8 AS build
WORKDIR /app
COPY . .
RUN mvn clean package assembly:single -DskipTests
FROM openjdk:8-jdk-alpine
WORKDIR /app
RUN apk add --no-cache \
    ttf-dejavu \
    freetype \
    fontconfig \
    tzdata \
    && cp /usr/share/zoneinfo/Asia/Bangkok /etc/localtime \
    && echo "Asia/Bangkok" > /etc/timezone

COPY --from=build /app/target/fision-service-1.0-SNAPSHOT.jar /app/myapp.jar
ENV TZ=Asia/Bangkok
ENTRYPOINT ["java", "-jar", "/app/myapp.jar"]
