FROM openjdk:11
COPY docker/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]