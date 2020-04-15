FROM openjdk:8-jre-alpine
COPY ./target/analyzer-0.0.1-SNAPSHOT.jar analyzer.jar
EXPOSE 8080
CMD java -jar analyzer.jar