FROM openjdk:8-oracle

COPY target/dubbokeeper-server-1.2.0-SNAPSHOT.jar /app/dubbokeeper-server-1.2.0-SNAPSHOT.jar
CMD ["java", "-Xms768m", "-Xmx768m", "-jar", "/app/dubbokeeper-server-1.2.0-SNAPSHOT.jar"]
