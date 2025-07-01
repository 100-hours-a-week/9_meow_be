# 1) Build 단계: Gradle Wrapper + JDK17
FROM gradle:8.6-jdk17 AS builder
WORKDIR /home/gradle/project

# 캐시를 위해 빌드스크립트만 먼저 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle gradle

# 의존성 캐싱: wrapper 스크립트 권한 부여
RUN chmod +x gradlew

# 소스 전체 복사 및 빌드
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# 2) Runtime 단계: 경량 JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 빌드 결과물(JAR) 가져오기
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# 서비스 포트
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
