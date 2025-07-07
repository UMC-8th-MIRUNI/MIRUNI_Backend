FROM openjdk:17-jdk
# 타임존 설정
ENV TZ=Asia/Seoul
WORKDIR /app
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/application.yml"]