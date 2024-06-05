FROM openjdk
COPY target/jakartaee9-jsf3-0.0.1-SNAPSHOT.war /app.war
ENTRYPOINT ["java", "-jar", "/app.war"]
