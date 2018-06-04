
# Rosette

Rosette is a REST API server. Its main focus is to serve calendar events, slide shows, articles and podcasts. Read more about Rosette at GitHub [LeafCoders/rosette](https://github.com/LeafCoders/rosette).

## Running with Docker Compose

Rosette uses MySQL as database. The Rosette docker image has built in support for waiting to start until a database connection has been established. That means that you can start both the database and the server at the same time. As with the Docker Compose example below.

```yaml
# docker-compose.yaml
version: '3'

services:
  mysql-database:
    image: mysql:5.7.21
    restart: always
    environment:
      - MYSQL_ROOT_PASSWORD=root
      - MYSQL_DATABASE=rosette-db
      - MYSQL_USER=dbuser
      - MYSQL_PASSWORD=dbpassword
    volumes:
      - mysql-data:/var/lib/mysql

  rosette-server:
    image: leafcoders/rosette:latest
    depends_on:
      - mysql-database
    restart: always
    ports:
      - 80:9000
    environment:
      - DATABASE_HOST=mysql-database
      - DATABASE_PORT=3306
      - DATABASE_NAME=rosette-db
      - DATABASE_USERNAME=dbuser
      - DATABASE_PASSWORD=dbpassword
      - MAIL_HOST=
      - MAIL_PORT=
      - MAIL_USERNAME=
      - MAIL_PASSWORD=
      - MAIL_SMTP_AUTH=false
      - MAIL_SMTP_STARTTLS=false
      - ROSETTE_JWTSECRET= // Must at least be 10 chars
      - ROSETTE_URL= // Public url to rosette server
      - ROSETTE_FILES=/var/lib/rosette
      - CORDATE_URL= // Public url to cordate client
    volumes:
      - rosette-files:/var/lib/rosette

volumes:
  mysql-data:
  rosette-files:
```

Start with:  
```$ docker-compose up -d```