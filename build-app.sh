#!/bin/bash
rm ./build/libs/api-service-1.0.0.jar
rm ./build/spring-api-service.tar

docker rmi spring-api-service:latest
./gradlew bootJar

docker image build --no-cache -t spring-api-service:latest .

docker save -o ./build/spring-api-service.tar spring-api-service:latest
