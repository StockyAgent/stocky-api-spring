# Stocky API Spring

주식 관리 시스템 백엔드 API 프로젝트

## 기술 스택

- **Java**: 21
- **Spring Boot**: 3.5.7
- **Database**: MySQL
- **Cache**: Redis
- **Build Tool**: Gradle

## 주요 의존성

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Data Redis
- Spring Boot Starter Validation
- Spring Boot Starter Actuator
- Spring Boot DevTools
- MySQL Connector
- Lombok
- Spring Dotenv

## 시작하기

### 사전 요구사항

- Java 21
- MySQL 8.0+
- Redis (선택사항)
- Gradle 8.x

### 환경 설정

1. `.env.example` 파일을 복사하여 `.env` 파일을 생성합니다:
   ```bash
   cp .env.example .env
   ```

2. `.env` 파일을 편집하여 데이터베이스 정보를 입력합니다:
   ```properties
   DB_URL=jdbc:mysql://localhost:3306/stocky_db?useSSL=false&serverTimezone=UTC
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   ```

3. MySQL 데이터베이스를 생성합니다:
   ```sql
   CREATE DATABASE stocky_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

### 빌드 및 실행

```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

### 테스트

```bash
./gradlew test
```

## 프로젝트 구조

```
src/
├── main/
│   ├── java/
│   │   └── dev/stocky/api/
│   │       └── StockyApiSpringApplication.java
│   └── resources/
│       ├── application.yml
│       ├── static/
│       └── templates/
└── test/
    └── java/
        └── dev/stocky/api/
            └── StockyApiSpringApplicationTests.java
```

## 개발 가이드

- JPA를 사용한 데이터베이스 접근
- Spring Validation을 사용한 요청 검증
- Actuator를 통한 애플리케이션 모니터링
- Redis를 사용한 캐싱 (선택사항)

## 라이선스

이 프로젝트는 개인 프로젝트입니다.

