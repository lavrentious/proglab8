#!/bin/bash
dir=$(pwd)
port=4564
# port=$port ./gradlew build && (osascript -e "tell application \"Terminal\" to do script \"cd '$dir' && port=$port java -jar apps/client/build/libs/lab8Client.jar\"" ; ENV_PATH=development.env java -jar apps/server/build/libs/lab8Server.jar)
port=$port ./gradlew build && (osascript -e "tell application \"Terminal\" to do script \"cd '$dir' && port=$port ./gradlew :apps:client:run\"" ; ENV_PATH=development.env java -jar apps/server/build/libs/lab8Server.jar)