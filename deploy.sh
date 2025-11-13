#!/bin/bash

# EC2 배포 스크립트
echo "🚀 Angler 배포 시작..."

# JAR 파일 경로
JAR_NAME="Angler-0.0.1-SNAPSHOT.jar"
APP_DIR="/home/ubuntu/angler"

# 기존 프로세스 종료
echo "📦 기존 애플리케이션 종료 중..."
PID=$(pgrep -f $JAR_NAME)
if [ -n "$PID" ]; then
    kill -15 $PID
    echo "기존 프로세스($PID) 종료됨"
    sleep 5
fi

# 디렉토리 생성
mkdir -p $APP_DIR
cd $APP_DIR

# JAR 파일 실행
echo "🎯 애플리케이션 시작 중..."
nohup java -jar \
    -DOPENAI_API_KEY=$OPENAI_API_KEY \
    -DGOOGLE_API_KEY=$GOOGLE_API_KEY \
    -DDB_URL=$DB_URL \
    -DDB_USERNAME=$DB_USERNAME \
    -DDB_PASSWORD=$DB_PASSWORD \
    $JAR_NAME > app.log 2>&1 &

echo "✅ 배포 완료! PID: $!"
echo "📋 로그 확인: tail -f $APP_DIR/app.log"
