FROM openjdk:17
EXPOSE 9090
ADD target/user-managemement-0.0.1-SNAPSHOT.jar user-managemement.jar
ENTRYPOINT ["java", "-jar", "user-managemement.jar"]