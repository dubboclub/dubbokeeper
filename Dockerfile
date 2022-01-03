FROM openjdk:8-oracle

COPY target/dubbokeeper-1.1.2-SNAPSHOT-assembly/ /app/dubbokeeper/
CMD ["/app/dubbokeeper/bin/start-ui.sh"]
