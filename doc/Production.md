# Production

Here are instructions of how to run Rosette in production mode


## Build and run with docker

Starts MySQL, Rosette and Cordate.

1. Update `rosette` version `x.yy` in `gradle.properties`
1. Build a docker image of `rosette` with: `./gradlew buildImage` (from folder `/rosette`)
1. Build a docker image of `cordate` with: `docker build --no-cache -t leafcoders/cordate:x.yy .` (from folder `/cordate`)
1. Copy `setup/docker/start_all.sh` and `setup/docker/docker-compose.yml` to a folder.
1. Edit `start_all.sh` and enter your own values.
1. Edit `docker-compose.yml` and enter your own values.
1. In your folder, run: `./start_all.sh` to start.


## Publish images to Docker Hub

1. Sign in with the Docker Desktop application
1. Publish `rosette` image with: `docker push leafcoders/rosette:x.yy`
1. Publish `cordate` image with: `docker push leafcoders/cordate:x.yy`


## Configuration

Rosette specific properties are descried here:

- `baseUrl` - Must be a public accessible url to the Rosette application,
- `jwtSecretToken` - A secret token for JWT authentication. Must be at least 10 characters long.
- `defaultMailFrom` - Mail sent from Rosette will have this address in the from field.
- `adminMailTo` - Mail address to administrator. Help request will be sent to this address.
- `filesPath` - Folder where all assets will be stored. Rosette must have write permissions to that folder.
- `cordateUrl` - Must be a public accessible url to the Cordate web application.
- `fileClientCacheMaxAge` - Number of seconds that files should be cached on client.
 

Read more about other configuration properties in https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html


## Repair database migration with FlyWay

Sometimes it is necessary to change an old FlyWay migration (even if you shouldn't do that).
An example is when we changed MySQL version from 5.7 to 8.0.
We had to rename the table `groups` because that name was introduces as a reserved keyword in MySql 8.0.2.
The migration `V0001_InitalSetup.sql` had to be modified for development setup to work with MySQL 8.

If the application fails to start with the following exception `FlywayValidateException: Validate failed: Migrations have failed validation`.
The you need to repair the FlyWay history table.
Follow these steps.

1. Install FlyWay command line tool
1. Find the jar file `rosette.jar` for the current version of Rosette
1. Run `flyway -user=root -password=root -url=jdbc:mysql://localhost:3306/test -jarDirs=pathTo/rosetteJarDir -locations=classpath:BOOT-INF/classes/db/migration validate`
   It shall show the error `Migration checksum mismatch for migration version ...``
1. Run `flyway -user=root -password=root -url=jdbc:mysql://localhost:3306/test -jarDirs=pathTo/rosetteJarDir -locations=classpath:BOOT-INF/classes/db/migration repair`
   It shall show the success message `Repairing Schema History table for version ...`
1. Start the application again


## Migrate data from other system

Rosette Manager is a static web application that can be used to import data from other system. It's located in `setup/manager`


## Access metrics and logs during runtime

Only super admins can access metrics and logs. An user can only be set as super admin via sql:

`update users set is_super_admin = 1 where id = ?;`

The following urls are available:

- `https://rosette.url/actuator?X-AUTH-TOKEN=<jwt-of-super-admin-user>` - To see all available endpoints
- `https://rosette.url/actuator/info?X-AUTH-TOKEN=<jwt-of-super-admin-user>`
- `https://rosette.url/actuator/flyway?X-AUTH-TOKEN=<jwt-of-super-admin-user>`
- `https://rosette.url/actuator/health?X-AUTH-TOKEN=<jwt-of-super-admin-user>`
- `https://rosette.url/actuator/logfile?X-AUTH-TOKEN=<jwt-of-super-admin-user>` - Requires `logging.file` property to be specified
- `https://rosette.url/actuator/loggers?X-AUTH-TOKEN=<jwt-of-super-admin-user>`
- `https://rosette.url/actuator/metrics?X-AUTH-TOKEN=<jwt-of-super-admin-user>`
- `https://rosette.url/actuator/scheduledtasks?X-AUTH-TOKEN=<jwt-of-super-admin-user>`
