FROM openjdk:19-alpine
COPY build/libs/quizGemini-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]



## Start with a base image containing Java runtime
#FROM openjdk:19-alpine
#
## Add Maintainer Info
#LABEL maintainer="your-email@example.com"
#
## Add a volume pointing to /tmp
#VOLUME /tmp
#
## Make port 8080 available to the world outside this container
#EXPOSE 8832
#
## The application's jar file
#ARG JAR_FILE=out/artifacts/quizGemini_jar/quizGemini.jar
#
## Add the application's jar to the container
#ADD ${JAR_FILE} quizGemini.jar
##COPY out/artifacts/quizGemini_jar/quizGemini.jar /app.jar
#
#
## Run the jar file
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/quizGemini.jar"]
