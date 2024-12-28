FROM amazoncorretto:21.0.4-alpine3.18

WORKDIR /app

COPY target/GpsTrackerConsumer-0.0.2-SNAPSHOT.jar app.jar

EXPOSE 8992

ENTRYPOINT ["java", "-Dspring.profiles.active=deploy", "-jar", "app.jar"]