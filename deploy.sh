#!/bin/bash

# Stop bash when failer occur
set -e

# Constant
HOST="root@103.75.185.147"
PORT=24700
ROOT_NAME="fstart-service"
GIT_URL="https://oauth2:glpat-qetHzKiVaCVbqPVZzPFt@gitlab.com/fstart/fstart-service.git"
PATH_PEM_FILE="fstart.pem"

# Script
START_TIME=$(date +%s)
echo "+++ Build +++"

read -p "Source branch: " BRANCH
read -p "Environment (stg/prod): " ENV
if [[ $ENV != "stg" && $ENV != "prod" ]]; then
  echo "[WARN] The value of environment variable must be 'stg' or 'prod'."
  exit 1
fi

echo "\n+++ Connecting to server with ${HOST}..."

SSH_CMD="ssh -i ${PATH_PEM_FILE} -p ${PORT} ${HOST} "
MOVE_CMD="cd ${ROOT_NAME} && "

echo "+++ Fetching new source..."
$SSH_CMD "rm -rf ${ROOT_NAME}"
$SSH_CMD "git clone -b ${BRANCH} ${GIT_URL}"

echo "+++ Building..."
$SSH_CMD "${MOVE_CMD} gradle clean build -x test"

echo "\n+++ Starting..."
echo "\n+++ [NOTE] After starting successfully, press Crlt + C to exit."
$SSH_CMD "sudo pkill java"
$SSH_CMD "nohup java -jar -Dspring.profiles.active=${ENV} -Dserver.port=8082 ${ROOT_NAME}/build/libs/${ROOT_NAME}-0.0.1-SNAPSHOT.jar &"
$SSH_CMD "nohup java -jar -Dspring.profiles.active=${ENV} -Dserver.port=8083 ${ROOT_NAME}/build/libs/${ROOT_NAME}-0.0.1-SNAPSHOT.jar &"

END_TIME=$(date +%s)
EXEC_TIME=$(($END_TIME-$START_TIME))
echo "\n+++ Finish $EXEC_TIME seconds +++"
