FROM openjdk:8-oracle

COPY target/dubbokeeper-ui-1.2.0-SNAPSHOT.war /app/dubbokeeper-ui-1.2.0-SNAPSHOT.war
ENV JAVA_OPTS="-Xms512m -Xmx512m"
ENTRYPOINT exec java $JAVA_OPTS -jar /app/dubbokeeper-ui-1.2.0-SNAPSHOT.war
