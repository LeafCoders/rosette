# Production

Here are instructions of how to run Rosette in production mode

## Installation

### Java 8 JDK

1. Install `Java 8` from http://www.oracle.com/technetwork/java/javase/index.html


### Gradle

1. Install `Gradle 2.x` from https://gradle.org/gradle-download


### MongoDB

1. Install `MongoDB 3.1.x` from http://www.mongodb.org/downloads
2. Follow instructions in its README file
3. MongoDB shall be run with its default port number `27017`


## Build for production

1. Checkout https://github.com/LeafCoders/rosette.git
2. Run `gradle buildWarAndJar`
3. Executable jar (including Tomcat 8) will be created at `build/libs/rosette-x.x.x.jar`


## Configuration

1. Create the file `application.yml` in same folder as the jar file
  ```yaml
  spring:
    profiles.active: production

  ---
  spring:
    profiles: production

  server:
    port: 80

  spring.data.mongodb:
    database: rosette

  spring.mail:
    protocol: smtp
    host: 
    port: 
    username: 
    password: 

  rosette:
    baseUrl: http://public-accessible-host:80
    jwtSecretToken: ...
    defaultMailFrom: no-reply@localhost
  ```
2. Read more about the configuration options in https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html


## Running server

1. Start MongodDB
2. Start rosette server in folder where `rosette-x.x.x.jar` and `application.yaml` is
  ```
  java -jar rosette-x.x.x.jar
  ```

