# 1. 베이스 이미지: 자바 21이 미리 깔린 리눅스 환경
FROM eclipse-temurin:21-jre

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 jar를 컨테이너 안으로 복사 (app.jar 라는 이름으로)
COPY build/libs/*.jar app.jar

# 4. 8080 포트를 외부에 알림
EXPOSE 8080

# 5. 컨테이너가 시작되면 이 명령으로 앱 실행
ENTRYPOINT ["java", "-jar", "app.jar"]