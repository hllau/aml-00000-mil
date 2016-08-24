# mileage_calculator
setup up

1) create mysql schema and user by scripts/db_init.sql

2) unzip scripts/db_data_init.zip, then run scripts/create_table.sql and then insert.sql

3) run "mvn package" to build

4) following env variable would be expected during tomcat startup
00000_DB_USER_NAME
00000_DB_PASSWORD
00000_DB_HOST_NAME
00000_DB_PORT

## Docker

(Optional) If there is no MYSQL in place, the following docker command runs a MYSQL server instance and creates a database "nextgen" on startup.

```sh
$ docker run -d \
--name temp-mysql \
-e MYSQL_DATABASE=nextgen \
-e MYSQL_ROOT_PASSWORD=nextgenroot \
-e MYSQL_USER=admin \
-e MYSQL_PASSWORD=nextgen \
mysql
```

### Build the docker image with tomcat:8jre
```sh
$ ./docker-build.sh
```

### Execute the docker image
```sh
$ ./docker-run.sh
```

Access the application by visiting http://localhost:8080/demo
