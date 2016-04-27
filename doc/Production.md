# Production

Here are instructions of how to run Rosette in production mode

## Installation

### Java 8 JDK

1. Install `Java 8` from http://www.oracle.com/technetwork/java/javase/index.html


### Gradle (only for build)

1. Install `Gradle 2.x` from https://gradle.org/gradle-download


### Tomcat 8

1. Install `Tomcat 8.x` from http://tomcat.apache.org/download-80.cgi


### MongoDB

1. Install `MongoDB 3.1.x` from http://www.mongodb.org/downloads
2. Follow instructions in its README file
3. MongoDB shall be run with its default port number `27017`


## Build for production

1. Checkout https://github.com/LeafCoders/rosette.git
2. Run `gradle buildWarAndJar`
3. Executable jar (including Tomcat 8) will be created at `build/libs/rosette-x.x.x.jar`
4. War will be created at `build/libs/rosette-x.x.x.war`


## Configuration

The application can be configured in different ways. Application properties can be set from a
Tomcat Context file or/and from a application.yml file. 

### Tomcat Context

The Tomcat Context file is used for configuring the applications. The file `conf/context.xml`
is applied to all applications but there is also a way to specify a context file for each
application.  
The tags `Engine` and `Host` are specified in Tomcat's `conf/server.xml`. The names of them will specify
the path where the application specific Context file should be placed. If `Engine = Catalina`, `Host = localhost`
and the application's name is `rosette`, the path will be `conf/Catalina/localhost/rosette.xml`.  
  
The file `setup/tomcat-context-rosette.xml` in Rosette's source code can be used as a starter.  

### Property file

The environment variable `spring.config.location` can override properties inside the application war.
Setting it to `file:/some/path/rosette/application-production.yml` will override properties in
application war (properties in application war will be used as defaults).

Rosette specific properties are descried here:

- `baseUrl` - Must be a public accessible url to the Rosette application
- `jwtSecretToken` - A secret token for JWT authentication. Must be at least 10 characters long
- `defaultMailFrom` - Mail sent from Rosette will have this address in the from field 

Read more about the configuration properties in https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html


## Migrate data

Rosette Manager is a static web application that can be used to import data from other system. It's located in `setup/manager`


## Running server

1. Start MongodDB
2. Start Tomcat with `bin/startup.sh`

## Logging

Tomcat creates log files in the `logs` folder. The log files contains different kind of information:

- `catalina.out` - Information from the application
- `localhost_access_log` - Logs each request to the application

