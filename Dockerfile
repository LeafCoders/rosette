FROM java:8
ADD /build/libs/rosette-0.0.1-SNAPSHOT.jar rosette.jar
ADD start-rosette.sh start-rosette.sh
RUN bash -c "touch /rosette.jar"
RUN bash -c 'chmod +x /start-rosette.sh'
VOLUME "/tmp"
ENTRYPOINT ["/bin/bash", "/start-rosette.sh"]
