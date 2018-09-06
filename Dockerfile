FROM java:8
ARG JAR_FILE
ADD /build/libs/${JAR_FILE} rosette.jar
ADD /setup/docker/start-rosette.sh start-rosette.sh
RUN bash -c "touch /rosette.jar"
RUN bash -c 'chmod +x /start-rosette.sh'
VOLUME "/tmp"
ENTRYPOINT ["/bin/bash", "/start-rosette.sh"]
