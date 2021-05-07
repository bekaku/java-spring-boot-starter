FROM openjdk:16-jdk-alpine
VOLUME /tmp
EXPOSE 8084
ADD /build/libs/java-springboot-starter-1.0.0.jar java-springboot-starter-1.0.0.jar
ENTRYPOINT ["java","-jar","java-springboot-starter-1.0.0.jar"]