# Development

## Development setup

### Java 14 JDK (AdoptOpenJDK HotSpot)

1. Install `Java 14` from https://adoptopenjdk.net/?variant=openjdk14&jvmVariant=hotspot


### Docker

1. Install `Docker x.x.x` from x
2. Create and run docker container with `docker run --name=mysql --env="MYSQL_ROOT_PASSWORD=root" --publish 3306:3306 -d mysql:5.7.21`
3. Run from a terminal: `docker exec -it mysql /bin/bash` to enter the running docker image
4. Start mysql with `mysql -uroot -proot`
5. Create the test database with `create database test;`
6. From Visual Studio Code, open `SeetTest.java` and run the `seed` test to setup the database tables.

When you need start the container again (after computer reboot) just run `docker run mysql`


### Visual Studio Code

1. Install latest `Visual Studio Code` from https://code.visualstudio.com
2. Install the workspace recommended extensions. Go to `Extensions` and search for `@recommended` to see the extensions.


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
