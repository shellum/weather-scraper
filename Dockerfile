FROM openjdk:23-ea-17-jdk-bullseye
WORKDIR /
COPY target/scala-2.12/weather-scraper.jar /
CMD ["java", "-jar", "weather-scraper.jar"]