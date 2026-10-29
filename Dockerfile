FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 컨테이너 상태 확인용 curl과 실행 계정
RUN apk add --no-cache curl && addgroup -S app && adduser -S app -G app

ENV TZ=Asia/Seoul
ENV SPRING_PROFILES_ACTIVE=prod

COPY --chown=app:app build/libs/hangarae.jar app.jar

USER app
EXPOSE 8080

ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "app.jar"]