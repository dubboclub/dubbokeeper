FROM openjdk:8-oracle

COPY target/dubbokeeper-server-1.2.0-SNAPSHOT.jar /app/dubbokeeper-server-1.2.0-SNAPSHOT.jar
ENV JAVA_OPTS="-Xms768m -Xmx768m"
ENTRYPOINT exec java $JAVA_OPTS -jar /app/dubbokeeper-server-1.2.0-SNAPSHOT.jar
