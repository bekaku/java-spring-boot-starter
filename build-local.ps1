# Remove the existing JAR and tar files.
Remove-Item -Path ".\build\libs\api-service-1.0.0.jar" -ErrorAction SilentlyContinue
Remove-Item -Path ".\build\spring-api-service.tar" -ErrorAction SilentlyContinue

# Remove the Docker image.
docker rmi spring-api-service:latest

# Build the Spring Boot JAR file.
.\gradlew bootJar

# Build the new Docker image using the specified Dockerfile.
docker image build --no-cache -t spring-api-service:latest -f DockerfileLocal .

# Start the services defined in your docker-compose file.
docker-compose up -d

# Prune the Docker build cache to free up space.
docker builder prune -f