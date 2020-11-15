FROM adoptopenjdk:14-jre-hotspot as builder
WORKDIR application
ARG JAR_VERSION
COPY /build/libs/rosette-${JAR_VERSION}-SNAPSHOT.jar rosette.jar
RUN java -Djarmode=layertools -jar rosette.jar extract

FROM adoptopenjdk:14-jre-hotspot
WORKDIR application
COPY --from=builder application/dependencies/ ./
COPY --from=builder application/spring-boot-loader/ ./
COPY --from=builder application/snapshot-dependencies/ ./
COPY --from=builder application/application/ ./
ADD /setup/docker/start-rosette.sh ./start-rosette.sh
RUN bash -c 'chmod +x ./start-rosette.sh'
VOLUME "/tmp"
ENTRYPOINT ["/bin/bash", "./start-rosette.sh"]
