# DROP DATABASE car_rent;
CREATE DATABASE car_rent;
USE car_rent;

CREATE TABLE vendor (
  id INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  PRIMARY KEY(id)
) ENGINE=INNODB, DEFAULT CHARSET=UTF8, COMMENT='Марка ТС';

CREATE TABLE vehicle (
  id INT(11) NOT NULL AUTO_INCREMENT,
  `type` ENUM('car', 'bike', 'scooter'),
  `name` VARCHAR(100) NOT NULL,
  id_vendor INT(11) NOT NULL,
  license_plate VARCHAR(50),
  PRIMARY KEY(id),
  UNIQUE KEY idx_license_plate (license_plate),
  KEY idx_vendor (id_vendor)
) ENGINE=INNODB, DEFAULT CHARSET=UTF8, COMMENT='ТС';

CREATE TABLE country (
  id INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100),
  PRIMARY KEY(id)
) ENGINE=INNODB, DEFAULT CHARSET=UTF8;

CREATE TABLE rental_point (
  id INT(11) NOT NULL AUTO_INCREMENT,
  id_country INT(11) NOT NULL,
  `name` VARCHAR(100),
  PRIMARY KEY(id),
  KEY idx_country (id_country)
) ENGINE=INNODB, DEFAULT CHARSET=UTF8, COMMENT='Точки проката';

CREATE TABLE customer (
  id INT(11) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100),
  id_country INT(11) NOT NULL,
  PRIMARY KEY(id),
  UNIQUE KEY idx_country_name (`id_country`, `name`)
) ENGINE=INNODB, DEFAULT CHARSET=UTF8;

CREATE TABLE vehicle_rent (
  id INT(11) NOT NULL AUTO_INCREMENT,
  id_vehicle INT(11) NOT NULL,
  id_customer INT(11) NOT NULL,
  id_rent_point INT(11) NOT NULL,
  rent_time TIMESTAMP NOT NULL,
  id_return_point INT(11) NULL,
  return_time TIMESTAMP NULL,
  PRIMARY KEY(id)
  # TODO: составить список индексов после оптимизации
) ENGINE=INNODB, DEFAULT CHARSET=UTF8, COMMENT='Информация о прокате ТС';

CREATE TABLE rental_point_vehicle (
  id_rent_point INT(11) NOT NULL,
  id_vehicle INT(11) NOT NULL,
  available TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY(id_rent_point, id_vehicle)
) ENGINE=INNODB, DEFAULT CHARSET=UTF8, COMMENT='Наличие ТС в точках проката';