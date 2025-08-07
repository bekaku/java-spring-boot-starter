# Use a lightweight JDK image
FROM eclipse-temurin:21-jdk-alpine

# Set environment variables
ENV TZ=Asia/Bangkok \
    JAVA_OPTS="-Xms1G -Xmx1G -Xss256k -XX:MaxMetaspaceSize=256m -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=75.0 -XX:MaxGCPauseMillis=100 -XX:+ParallelRefProcEnabled -XX:+UseCompressedOops -XX:+UseCompressedClassPointers -XX:+AlwaysPreTouch -XX:+TieredCompilation -XX:+UseStringDeduplication -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/usr/spring-data/logs/heapdump.hprof"

# Install required packages and set timezone
RUN apk --no-cache add curl tzdata && \
    cp /usr/share/zoneinfo/Asia/Bangkok /etc/localtime && \
    echo "Asia/Bangkok" > /etc/timezone

# Create non-root user and group
# RUN addgroup -S springapigroup && adduser -S springapi -G springapigroup

# Set working directory
WORKDIR /app


# Copy the built JAR file with root user
COPY /build/libs/api-service-1.0.0.jar app.jar

# Copy the built JAR file and give ownership to the non-root user
#COPY --chown=springapi:springapigroup /build/libs/api-service-1.0.0.jar app.jar

# Make sure heapdump path exists and is writable
# RUN mkdir -p /usr/spring-data/logs && chown -R springapi:springapigroup /usr/spring-data

# Use non-root user
USER springapi

# Expose the application port
EXPOSE 8080

# Health check using curl
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Define entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.config.additional-location=/usr/spring-data/env/ -Dspring.profiles.active=prod -jar app.jar"]
