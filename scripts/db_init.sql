CREATE DATABASE IF NOT EXISTS asia_mile;
CREATE USER 'demo'@'%' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON asia_mile.* TO 'demo'@'%' WITH GRANT OPTION;