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