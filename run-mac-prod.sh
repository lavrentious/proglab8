#!/bin/bash
dir=$(pwd)
port=4564
port=$port ./gradlew build && (osascript -e "tell application \"Terminal\" to do script \"cd '$dir' && port=$port java -jar apps/client/build/libs/lab7Client.jar\"" ; ENV_PATH=production.env java -jar apps/server/build/libs/lab7Server.jar)