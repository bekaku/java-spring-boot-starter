# Use a lightweight JDK image
FROM eclipse-temurin:21-jdk-alpine

# Set environment variables
ENV TZ=Asia/Bangkok \
    JAVA_OPTS="-Xms1G -Xmx1G -XX:MaxMetaspaceSize=256m -XX:+UseG1GC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/usr/spring-data/logs/heapdump.hprof"

# Install required packages and set timezone
RUN apk --no-cache add curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Bangkok /etc/localtime && \
    echo "Asia/Bangkok" > /etc/timezone

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY /build/libs/api-service-1.0.0.jar app.jar

# Expose the application port
EXPOSE 8080

# Health check using curl
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Define entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.config.additional-location=/usr/spring-data/env/ -Dspring.profiles.active=prod -jar app.jar"]
