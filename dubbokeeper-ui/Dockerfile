FROM openjdk:8-oracle

COPY target/dubbokeeper-ui-1.2.0-SNAPSHOT.war /app/dubbokeeper-ui-1.2.0-SNAPSHOT.war
CMD ["java", "-Xms512m", "-Xmx512m", "-jar", "/app/dubbokeeper-ui-1.2.0-SNAPSHOT.war"]
