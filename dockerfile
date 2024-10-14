# 1. Base image로 JDK 17 사용
FROM openjdk:17-jdk-slim

# 2. AWS 및 Terraform 관련 빌드 아규먼트 정의
ARG AWS_ACCESS_KEY_ID
ARG AWS_SECRET_ACCESS_KEY
ARG TERRAFORM_ENDPOINT

# 3. 환경 변수로 설정
ENV AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID
ENV AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY
ENV TERRAFORM_ENDPOINT=$TERRAFORM_ENDPOINT

# 4. 빌드된 JAR 파일을 이미지로 복사
COPY build/libs/McpBackend-0.0.1-SNAPSHOT.jar /app.jar

# 5. 애플리케이션을 실행할 명령어
ENTRYPOINT ["java", "-jar", "/app.jar"]