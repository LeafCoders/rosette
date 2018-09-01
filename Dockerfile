FROM java:8
ADD /build/libs/rosette-0.2.jar rosette.jar
ADD /setup/docker/start-rosette.sh start-rosette.sh
RUN bash -c "touch /rosette.jar"
RUN bash -c 'chmod +x /start-rosette.sh'
VOLUME "/tmp"
ENTRYPOINT ["/bin/bash", "/start-rosette.sh"]
