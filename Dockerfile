# FROM openjdk:17-jdk
FROM eclipse-temurin:17-jre-jammy
# 타임존 설정
ENV TZ=Asia/Seoul
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/application.yml"]