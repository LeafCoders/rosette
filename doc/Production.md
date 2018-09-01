# Production

Here are instructions of how to run Rosette in production mode


## Build an run with  docker

Starts MySQL, Rosette and Cordate.

1. Build a docker image of `rosette` with: `docker build -t leafcoders/rosette:1.0 .` (from folder `/rosette`)
2. Build a docker image of `cordate` with: `docker build --no-cache -t leafcoders/cordate:1.0 .` (from folder `/cordate`)
3. Copy `setup/docker/start_all.sh` and `setup/docker/docker-compose.yml` to a folder.
4. Edit `start_all.sh` and enter your own values.
5. Edit `docker-compose.yml` and enter your own values.
6. In your folder, run: `./start_all.sh` to start.


## Configuration

Rosette specific properties are descried here:

- `baseUrl` - Must be a public accessible url to the Rosette application
- `jwtSecretToken` - A secret token for JWT authentication. Must be at least 10 characters long
- `defaultMailFrom` - Mail sent from Rosette will have this address in the from field 
- `adminMailTo` - Mail address to administrator. Help request will be sent to this address. 
- `filesPath` - Folder where all assets will be stored. Rosette must have write permissions to that folder
- `cordateUrl` - Must be a public accessible url to the Cordate web application
 

Read more about other configuration properties in https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html


## Migrate data

Rosette Manager is a static web application that can be used to import data from other system. It's located in `setup/manager`
