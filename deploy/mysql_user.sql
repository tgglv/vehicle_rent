CREATE USER 'dev'@'localhost' IDENTIFIED BY 'dev';
GRANT ALL PRIVILEGES ON * . * TO 'dev'@'localhost';
FLUSH PRIVILEGES;