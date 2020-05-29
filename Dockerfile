FROM adoptopenjdk:14-jre-hotspot
ARG JAR_VERSION
ADD /build/libs/rosette-${JAR_VERSION}-SNAPSHOT.jar rosette.jar
ADD /setup/docker/start-rosette.sh start-rosette.sh
RUN bash -c "touch /rosette.jar"
RUN bash -c 'chmod +x /start-rosette.sh'
VOLUME "/tmp"
ENTRYPOINT ["/bin/bash", "/start-rosette.sh"]
