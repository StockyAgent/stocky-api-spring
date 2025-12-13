# 1. 빌드 단계 (Builder Stage)
# Gradle과 JDK가 포함된 이미지를 기반으로 빌드합니다.
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

# 소스 코드 복사
COPY . .

# Gradle 빌드 실행 (테스트는 CI 단계에서 하므로 여기선 스킵하여 속도 향상)
# -x test: 테스트 제외
RUN ./gradlew clean build -x test

# ----------------------------------------------------

# 2. 실행 단계 (Runtime Stage)
# 실제 실행할 가벼운 이미지를 만듭니다.
FROM amazoncorretto:21-alpine-jdk
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일만 쏙 가져옵니다.
# (build/libs/ 안에 jar가 하나만 생긴다고 가정)
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar

# 실행 명령어
# 프로파일을 'prod'로 설정하려면 -Dspring.profiles.active=prod 추가 가능
ENTRYPOINT ["java", "-jar", "app.jar"]