# rosette

A REST server for events, posters, bookings, uploads and more.


## Development setup

### MongoDB

1. Install `MongoDB 2.4.6` from http://www.mongodb.org/downloads
2. Follow instructions in its READMY file
3. MongoDB shall be run with its default port number `27017`
3. Install `MongoHub` from http://mongohub.todayclose.com and use it to inspect the databases

### Maven

1. Install `Maven 2.2.1` from http://maven.apache.org/download.cgi

### Eclipse

1. Install `Spring Tool Suite` from http://spring.io/tools/sts
2. Inside STS - Install `m2e Configurator for Groovy` from http://dist.springsource.org/release/GRECLIPSE/e4.2
3. Inside STS - Install `JRebel for Eclipse (3.3+) -> m2e Integration` from http://update.zeroturnaround.com/update-site
4. Open `Preferences -> Maven -> Installations` and add the directory where `Maven 2.2.1` is. Select it.
5. Import the Rosette project with `File -> Import... -> Maven -> Existing Maven Projects`
6. If you get errors that says

  > The declaration package "se.rytt..." does not match the expected package "main.se.rytt..."

  then do the following:
  1. Right-click at "src" below "rosette" in `Package Explorer` and select `Build Path -> Remove from Build Path`
  2. Right-click at "src/main/java" below "rosette" in `Package Explorer` and select `Build Path -> Use as Source Folder`
7. Open view `Servers` and select `VMware vFabric tc Server Developer Edition v2.9`. Right-click and select `Add and Remove...`. Add `rosette`. Start the server. Rosette is not running at localhost:8080/rosette

8. Add `cordate` the same way as `rosette`
9. Double-click at `VMware vFabric tc Server Developer Edition v2.9` in `Servers` view.
10. Change `bio.http.port` to `9000`
11. Click at the `Modules` tab and change `rosette` to have path `/`


## Installation

1. Set profile to "production" with JVM arg `-Dspring.profiles.active=production`
2. Set JVM arg `-Drosette.baseUrl=http://rosetteHostName:rosettePortNr`
