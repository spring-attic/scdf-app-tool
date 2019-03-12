#!/usr/bin/env bash

STREAM_APP_VERSION=Celsius-SR2
TASK_APP_VERSION=Clark-GA

wget -q -O rabbit-stream-apps.properties "https://bit.ly/$STREAM_APP_VERSION-stream-applications-rabbit-maven"
wget -q -O kafka-10-stream-apps.properties "https://bit.ly/$STREAM_APP_VERSION-stream-applications-kafka-10-maven"
wget -q -O task-apps.properties  "https://bit.ly/$TASK_APP_VERSION-task-applications-maven"

