# Production

Here are instructions on how to build and run Rosette in production.


## Build a new version for production

### Build new version

1. Update `rosette` version `x.yy` in `gradle.properties`
1. Run `./gradlew clean` (from folder `/rosette`) to clean the build folder
1. Build a docker image of `rosette` with: `./gradlew bootBuildImage` (from folder `/rosette`)
1. Build a docker image of `cordate` with: `docker build --no-cache -t leafcoders/cordate:x.yy .` (from folder `/cordate`)

### Test version in local docker environment

1. Copy `setup/docker/start_all.sh` and `setup/docker/docker-compose.yml` to a folder.
1. Edit `start_all.sh` and enter your own values.
1. Edit `docker-compose.yml` and enter your own values.
1. In your folder, run: `./start_all.sh` to start.

### Publish images to Docker Hub

1. Sign in with the Docker Desktop application
1. Publish `rosette` image with: `docker push leafcoders/rosette:x.yy`
1. Publish `cordate` image with: `docker push leafcoders/cordate:x.yy`


## Setup for production

### Permissions to file storage

The Rosette image doesn't run the Java application as root user.
It creates a user `cnb` with user id `1000` that runs the Java application.
This user `cnb` doesn't have write permissions to the mounted voulume `rosette-files` by default.
You need to give the user write permissions to that folder otherwise it won't be possible to upload files.

Here are two ways to add write permissions to `cnb` user:

```
# Add these lines to your docker-compose.yml file and run it. Remove after success. 
volume-setup:
  image: alpine:latest
  volumes:
    - rosette-files:/home/cnb/rosette
  command: /bin/sh -c "cd /home/cnb && chmod -R 755 rosette && chown -R 1000:1000 rosette && ls -al"
```

or

```
# In terminal. Find the volume name. Use it in the last command (replace xxx).
docker volume ls
docker run -it --rm -v xxx_rosette-files:/home/cnb/rosette alpine:latest /bin/sh -c "cd /home/cnb && chmod -R 755 rosette && chown -R 1000:1000 rosette && ls -al"
```


### Configuration

Rosette specific properties are descried here.
Use "SNAKE_CASE" if you specify them in a docker compose file. Eg. `ROSETTE_BASE_URL`.

- `baseUrl` - Must be a public accessible url to the Rosette application,
- `jwtSecretToken` - A secret token for JWT authentication. Must be at least 10 characters long.
- `defaultMailFrom` - Mail sent from Rosette will have this address in the from field.
- `adminMailTo` - Mail address to administrator. Help request will be sent to this address.
- `filesPath` - Folder where all assets will be stored. Rosette must have write permissions to that folder.
- `cordateUrl` - Must be a public accessible url to the Cordate web application.
- `fileClientCacheMaxAge` - Number of seconds that files should be cached on client.
 

Read more about other configuration properties in https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html


## Maintenance

### Application files and logs

```
# Start a terminal to your running Rosette container
docker exec -it rosette-server /bin/bash

# To list application files
ls /workspace

# To show log content
cat /workspace/logs/spring.log

# To list uploaded files
ls /home/cnb/rosette-files
```


### Repair database migration with FlyWay

Sometimes it is necessary to change an old FlyWay migration (even if you shouldn't do that).
An example is when we changed MySQL version from 5.7 to 8.0.
We had to rename the table `groups` because that name was introduces as a reserved keyword in MySql 8.0.2.
The migration `V0001_InitalSetup.sql` had to be modified for development setup to work with MySQL 8.

If the application fails to start with the following exception `FlywayValidateException: Validate failed: Migrations have failed validation`.
The you need to repair the FlyWay history table.
Follow these steps.

1. Set the property `rosette.flyway-command` to `repair`
1. Start Rosette application.
   It shall show the success message `Repairing Schema History table for version ...` but the application will exit with an exception.
   Rosette application will only start if the property is empty or `migrate`.
1. Remove the property and start Rosette application again.


### Migrate data from other system

Rosette Manager is a static web application that can be used to import data from other system. It's located in `setup/manager`


### Access metrics and logs during runtime

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
