docker image build --no-cache -t spring-api-service:latest -f DockerfileNative .


#docker-compose up -d
#docker save -o ./build/spring-api-service.tar spring-api-service:latest
docker builder prune -f