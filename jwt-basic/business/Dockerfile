FROM amazoncorretto:17-alpine-jdk
LABEL authors="seungsu"

ARG JAR_FILE_PATH=./build/libs/*.jar
COPY ${JAR_FILE_PATH} app.jar

EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]