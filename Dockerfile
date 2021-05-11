FROM openjdk:16-jdk-alpine
VOLUME /tmp
EXPOSE 8084
ADD /build/libs/java-springboot-starter-1.0.0.jar app.jar
COPY edrdata /usr/edrdata/
ENTRYPOINT ["java","-jar","app.jar", "--spring.config.location=/usr/edrdata/env/application.yml"]