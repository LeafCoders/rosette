#!/bin/bash

while ! exec 6<>/dev/tcp/${DATABASE_HOST}/${DATABASE_PORT}; do
    echo "Trying to connect to MySQL at ${DATABASE_PORT}..."
    sleep 10
done

set JAVA_OPTS=""
exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=production org.springframework.boot.loader.JarLauncher ${@}
