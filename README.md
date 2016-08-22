# mileage_calculator
setup up

1) create mysql schema and user by scripts/db_init.sql

2) unzip scripts/db_data_init.zip, then run scripts/create_table.sql and then insert.sql

3) install 3rd party lib to local mvn by scripts/add_mvn_lib.bat

4) run "mvn package" to build

todo: use runtime env variable to setup db connection, current hard coded in .\src\main\webapp\META-INF\context.xml