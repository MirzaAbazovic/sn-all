# Use alpine-based JRE 17 image
FROM eclipse-temurin:17-jre-alpine

# Install curl
RUN apk update && \
apk upgrade && \
apk --no-cache add curl

RUN mkdir /app
COPY target/*.jar /app/config-server.jar

CMD ["java", "-jar", "/app/config-server.jar"]