# Remove the existing jar and tar files
Remove-Item -Path ".\build\libs\api-service-1.0.0.jar" -ErrorAction SilentlyContinue
Remove-Item -Path ".\build\spring-api-service.tar" -ErrorAction SilentlyContinue

# Remove the Docker image
docker rmi spring-api-service:latest

# Build the Spring Boot JAR
.\gradlew bootJar

# Build the new Docker image
docker image build --no-cache -t spring-api-service:latest .

# The commented out docker save command from the original script
# docker save -o ".\build\spring-api-service.tar" spring-api-service:latest

# Start the Docker Compose services
docker-compose up -d

# Prune the Docker build cache
docker builder prune -f