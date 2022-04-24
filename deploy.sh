#!/usr/bin/env bash

REPOSITORY=/home/ec2-user/action
cd $REPOSITORY

APP_NAME=moamoa-0.0.1-SNAPSHOT.jar
JAR_NAME=$(ls $REPOSITORY/build/libs/ | grep '.jar' | tail -n 1)
JAR_PATH=$REPOSITORY/build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f $APP_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 종료할것 없음."
else
  echo "> kill -9 $CURRENT_PID"
  kill -9 $CURRENT_PID
  sleep 5
fi

echo "> $JAR_PATH 배포"
nohup java -jar $JAR_PATH --spring.config.location=/home/ec2-user/action/application.yml > /dev/null 2> /dev/null < /dev/null &
