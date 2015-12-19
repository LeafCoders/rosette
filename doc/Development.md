# Development

## Development setup

### Java 8 JDK

1. Install `Java 8` from http://www.oracle.com/technetwork/java/javase/index.html


### Gradle

1. Install `Gradle 2.x` from https://gradle.org/gradle-download


### MongoDB

1. Install `MongoDB 3.1.x` from http://www.mongodb.org/downloads
2. Follow instructions in its README file
3. MongoDB shall be run with its default port number `27017`
4. Install `MongoHub` from http://mongohub.todayclose.com and use it to inspect the databases


### Eclipse

TODO: Rewrite with use of Gradle

1. Install `Spring Tool Suite` from http://spring.io/tools/sts
2. Inside STS - Install `m2e Configurator for Groovy` from http://dist.springsource.org/release/GRECLIPSE/e4.2
3. Inside STS - Install `JRebel for Eclipse (3.3+) -> m2e Integration` from http://update.zeroturnaround.com/update-site
4. Open `Preferences -> Maven -> Installations` and add the directory where `Maven 2.2.1` is. Select it.
5. Import the Rosette project with `File -> Import... -> Maven -> Existing Maven Projects`
6. If you get errors that says

  > The declaration package "se.leafcoders..." does not match the expected package "main.se.leafcoders..."

  then do the following:
  1. Right-click at "src" below "rosette" in `Package Explorer` and select `Build Path -> Remove from Build Path`
  2. Right-click at "src/main/java" below "rosette" in `Package Explorer` and select `Build Path -> Use as Source Folder`
7. Open view `Servers` and select `VMware vFabric tc Server Developer Edition v2.9`. Right-click and select `Add and Remove...`. Add `rosette`. Start the server. Rosette is not running at localhost:8080/rosette
8. Add `cordate` the same way as `rosette`
9. Double-click at `VMware vFabric tc Server Developer Edition v2.9` in `Servers` view.
10. Change `bio.http.port` to `9000`
11. Click at the `Modules` tab and change `rosette` to have path `/`


### FakeSMTP

Use FakeSMTP to simulate a local SMTP server. FakeSMTP has a GUI that shows sent emails.

1. Install `FakeSMTP` from http://nilhcem.github.io/FakeSMTP and use it to receive mail sent from Rosette
2. Run fakeSMTP.jar (just double click it) and enter `1234` as `Listening port`
3. Click `Start server`


## Running server

1. Select `RosetteApplication.java` in Eclipse `Package Explorer` view
2. Right-click and select `Run As -> Spring Boot App`


## Running tests

1. Start MongodDB at port `27017`
2. Start rosette server
3. Run tests with JUnit 4 TestRunner
