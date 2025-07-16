#!/bin/bash
set -eux

# 기존 컨테이너 제거
docker rm -f deployment-backend-1 || true

# 로그 디렉토리 준비
mkdir -p /var/log/spring-boot
chmod 755 /var/log/spring-boot

# Spring Boot + OTEL Java‑Agent 로 실행
docker run -d --name deployment-backend-1 \
  --network host \
  -v /opt/otel/opentelemetry-javaagent.jar:/agent/opentelemetry-javaagent.jar:ro \
  -v /var/log/spring-boot:/var/log/spring-boot \
  -e OTEL_EXPORTER_OTLP_ENDPOINT="http://localhost:4317" \
  -e OTEL_EXPORTER_OTLP_INSECURE=true \
  -e OTEL_TRACES_EXPORTER="otlp" \
  -e OTEL_METRICS_EXPORTER="otlp" \
  -e OTEL_LOGS_EXPORTER="otlp" \
  -e OTEL_EXPORTER_OTLP_TRACES_PROTOCOL='grpc' \
  -e OTEL_EXPORTER_OTLP_METRICS_PROTOCOL='grpc' \
  -e OTEL_EXPORTER_OTLP_LOGS_PROTOCOL='grpc' \
  -e OTEL_SERVICE_NAME="dev-service" \
  -e OTEL_RESOURCE_ATTRIBUTES="deployment.environment=develop" \
  --entrypoint java \
  380561000828.dkr.ecr.ap-northeast-2.amazonaws.com/prod/9_meow_be:latest \
      -javaagent:/agent/opentelemetry-javaagent.jar \
      -Dotel.instrumentation.logging.enabled=true \
      -Duser.timezone=Asia/Seoul \
      -jar app.jar