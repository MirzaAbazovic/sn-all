# Use openjdk 23 slim
FROM openjdk:23-slim

RUN mkdir /app
COPY adlatus-domain/target/*.jar /app/adlatus.jar

CMD ["java", "-jar", "/app/adlatus.jar"]