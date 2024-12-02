FROM openjdk:21

ADD build/libs/car-listing-consumer-1.0.0.jar car-listing-consumer.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "car-listing-consumer.jar"]