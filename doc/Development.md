# Development

## Development setup

### Java 8 JDK

1. Install `Java 8` from http://www.oracle.com/technetwork/java/javase/index.html


### Docker

1. Install `Docker x.x.x` from x


### Eclipse 

1. Install `Spring Tool Suite 3.9.0` from http://spring.io/tools/sts
2. Install `Buildship Grade Integration` from Eclipse Marketplace
3. Import the Rosette project with `File -> Import... -> Gradle -> Existing Gradle Project`


### FakeSMTP

Use FakeSMTP to simulate a local SMTP server. FakeSMTP has a GUI that shows sent emails.

1. Install `FakeSMTP` from http://nilhcem.github.io/FakeSMTP and use it to receive mail sent from Rosette
2. Run fakeSMTP.jar (just double click it) and enter `1234` as `Listening port`
3. Click `Start server`


## Running server

1. Open `Boot Dashboard` tool window. `rosette` should be listed here
2. Select `rosette` and click start button 


## Running tests

1. Run tests with JUnit TestRunner
