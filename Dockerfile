FROM openjdk:17-jdk
EXPOSE 8081
CMD ["./mvnw", "clean", "package"]
ARG JAR_FILE=./build/libs/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]