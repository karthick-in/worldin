FROM openjdk:8-jdk-alpine
VOLUME /tmp

# environment variable with default value
ENV SPRING_PROFILE=dev

ADD target/worldin.jar app.jar
ENTRYPOINT ["java","-Dspring.profiles.active=${SPRING_PROFILE}","-jar","/app.jar"]