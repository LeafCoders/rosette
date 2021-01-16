# Development

## Development setup

### Java 15 JDK (Liberica JDK)

1. Install `Java 15 JDK` from https://bell-sw.com/pages/downloads/#/java-15-current


### Docker

1. Install `Docker Desktop 3.x.x` from https://www.docker.com/products/docker-desktop
2. Create and run docker container with `docker run --name=mysql --env="MYSQL_ROOT_PASSWORD=root" --publish 3306:3306 -d mysql:8.0.22`
3. Run from a terminal: `docker exec -it mysql /bin/bash` to enter the running docker image
4. Start mysql with `mysql -uroot -proot`
5. Create the test database with `create database test;`
6. From Visual Studio Code, open `SeedTest.java` and run the `seed` test to setup the database tables.

When you need start the container again (after computer reboot) just run `docker run mysql`


### Visual Studio Code

1. Install latest `Visual Studio Code` from https://code.visualstudio.com
2. Install the workspace recommended extensions. Go to `Extensions` and search for `@recommended` to see the extensions.
3. Configure Java runtime with command `>Java: Configure Java Runtime`.  
   The tab `Project JDKs` shall have `rosette, 15, Gradle` at `Workspace Overview`.
   The tab `Java Tooling Runtime` shall have a `JDK 15` selected from you local machine.
   The tab `Installed JDKs` lists your installed JDKs. Verify that a JDK for Java 15 i listed here.

#### Gradle build vs Java Tooling build

The `build.gradle` file defines this project.
Everything that is required to build the project is available though that file.
Visual Studio Code use a plugin for Java to compile, run application and run tests.
Gradle and Java Tooling puts the build output in different directories. Gradle at `/build` and Java Tooling at `/bin`.

Clean Gradle output with gradle task `gradle clean`. Clean Java Toolkit output with command `>Java: Force Java Compilation` (select Full).


### FakeSMTP

Use FakeSMTP to simulate a local SMTP server. FakeSMTP has a GUI that shows sent emails.

1. Install `FakeSMTP` from http://nilhcem.github.io/FakeSMTP and use it to receive mail sent from Rosette
2. Run fakeSMTP.jar (just double click it) and enter `1234` as `Listening port`
3. Click `Start server`


## Running server

1. Make sure that the `mysql` docker container is running `docker run mysql`.
2. Make sure that the database is populated with data. Run the `seed` test in `SeedTest.java`.
3. Open `Gradle tasks` tool window. Start `rosette > application > bootRun`


## Running tests

1. Run tests with JUnit TestRunner
