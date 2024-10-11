# 1. Base image로 JDK 17 사용
FROM openjdk:17-jdk-slim

# 2. 빌드된 JAR 파일을 이미지로 복사
COPY build/libs/McpBackend-0.0.1-SNAPSHOT.jar /app.jar

# 3. 애플리케이션을 실행할 명령어
ENTRYPOINT ["java", " jar", "/app.jar"]