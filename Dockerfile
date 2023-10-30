FROM openjdk:17-jdk-alpine3.14
RUN date
RUN apk add tzdata
RUN cp /usr/share/zoneinfo/Asia/Bangkok /etc/localtime
RUN date
VOLUME /tmp
EXPOSE 8080
ADD /build/libs/api-service-1.0.0.jar app.jar
#ENTRYPOINT ["java","-jar","app.jar", "--spring.config.location=/usr/spring-data/env/application.yml"]
ENTRYPOINT ["java","-Xms1G","-Xmx1G","-XX:MaxMetaspaceSize=256m","-XX:+UseG1GC","-XX:+HeapDumpOnOutOfMemoryError","-XX:HeapDumpPath=/usr/spring-data/logs/heapdump.hprof ","-jar","app.jar"]