-- MySQL dump 10.13  Distrib 5.6.36-82.0, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: car_rent
-- ------------------------------------------------------
-- Server version	5.6.36-82.0-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
/*!50112 SELECT COUNT(*) INTO @is_rocksdb_supported FROM INFORMATION_SCHEMA.SESSION_VARIABLES WHERE VARIABLE_NAME='rocksdb_bulk_load' */;
/*!50112 SET @save_old_rocksdb_bulk_load = IF (@is_rocksdb_supported, 'SET @old_rocksdb_bulk_load = @@rocksdb_bulk_load', 'SET @dummy_old_rocksdb_bulk_load = 0') */;
/*!50112 PREPARE s FROM @save_old_rocksdb_bulk_load */;
/*!50112 EXECUTE s */;
/*!50112 SET @enable_bulk_load = IF (@is_rocksdb_supported, 'SET SESSION rocksdb_bulk_load = 1', 'SET @dummy_rocksdb_bulk_load = 0') */;
/*!50112 PREPARE s FROM @enable_bulk_load */;
/*!50112 EXECUTE s */;
/*!50112 DEALLOCATE PREPARE s */;

--
-- Table structure for table `country`
--

CREATE DATABASE car_rent;
USE car_rent;

DROP TABLE IF EXISTS `country`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `country` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=253 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `country`
--

LOCK TABLES `country` WRITE;
/*!40000 ALTER TABLE `country` DISABLE KEYS */;
INSERT INTO `country` VALUES (1,'Абхазия'),(2,'Австралия'),(3,'Австрия'),(4,'Азад Кашмир'),(5,'Азербайджан'),(6,'Аландские острова'),(7,'Албания'),(8,'Алжир'),(9,'Ангилья'),(10,'Ангола'),(11,'Андорра'),(12,'Антигуа и Барбуда'),(13,'Аргентина'),(14,'Армения'),(15,'Аруба'),(16,'Афганистан'),(17,'Багамские Острова'),(18,'Бангладеш'),(19,'Барбадос'),(20,'Бахрейн'),(21,'Белиз'),(22,'Белоруссия'),(23,'Бельгия'),(24,'Бенин'),(25,'Бермудские острова'),(26,'Болгария'),(27,'Боливия'),(28,'Бонэйр'),(29,'БиГ'),(30,'Ботсвана'),(31,'Бразилия'),(32,'Бруней'),(33,'Буркина-Фасо'),(34,'Бурунди'),(35,'Бутан'),(36,'Вануату'),(37,'Ватикан'),(38,'Великобритания'),(39,'Венгрия'),(40,'Венесуэла'),(41,'Американские Виргинские острова'),(42,'Британские Виргинские острова'),(43,'Восточный Тимор'),(44,'Вьетнам'),(45,'Габон'),(46,'Гаити'),(47,'Гайана'),(48,'Гамбия'),(49,'Гана'),(50,'Гваделупа'),(51,'Гватемала'),(52,'Французская Гвиана'),(53,'Гвинея'),(54,'Гвинея-Бисау'),(55,'Германия'),(56,'Гернси'),(57,'Гибралтар'),(58,'Гондурас'),(59,'Гонконг'),(60,'Гренада'),(61,'Гренландия'),(62,'Греция'),(63,'Грузия'),(64,'Гуам'),(65,'Дания'),(66,'Джерси'),(67,'Джибути'),(68,'Доминика'),(69,'Доминиканская Республика'),(70,'ДНР'),(71,'Египет'),(72,'Замбия'),(73,'Зимбабве'),(74,'Израиль'),(75,'Индия'),(76,'Индонезия'),(77,'Иордания'),(78,'Ирак'),(79,'Иран'),(80,'Ирландия'),(81,'Исландия'),(82,'Испания'),(83,'Италия'),(84,'Йемен'),(85,'Кабо-Верде'),(86,'Казахстан'),(87,'Каймановы Острова'),(88,'Камбоджа'),(89,'Камерун'),(90,'Канада'),(91,'Катар'),(92,'Кения'),(93,'Кипр'),(94,'ТРСК'),(95,'Киргизия'),(96,'Кирибати'),(97,'Китайская Народная Республика'),(98,'Китайская Республика'),(99,'Кокосовые острова'),(100,'Колумбия'),(101,'Коморские Острова'),(102,'Демократическая Республика Конго'),(103,'Республика Конго'),(104,'КНДР'),(105,'Республика Корея'),(106,'Косово'),(107,'Коста-Рика'),(108,'Кот-д’Ивуар'),(109,'Куба'),(110,'Кувейт'),(111,'Острова Кука'),(112,'Кюрасао'),(113,'Лаос'),(114,'Латвия'),(115,'Лесото'),(116,'Либерия'),(117,'Ливан'),(118,'Ливия'),(119,'Литва'),(120,'Лихтенштейн'),(121,'ЛНР'),(122,'Люксембург'),(123,'Маврикий'),(124,'Мавритания'),(125,'Мадагаскар'),(126,'Майотта'),(127,'Макао'),(128,'Македония'),(129,'Малави'),(130,'Малайзия'),(131,'Мали'),(132,'Мальдивы'),(133,'Мальта'),(134,'Марокко'),(135,'Мартиника'),(136,'Маршалловы Острова'),(137,'Мексика'),(138,'Микронезия'),(139,'Мозамбик'),(140,'Молдавия'),(141,'Монако'),(142,'Монголия'),(143,'Монтсеррат'),(144,'Мьянма'),(145,'Остров Мэн'),(146,'Нагорный Карабах'),(147,'Намибия'),(148,'Науру'),(149,'Непал'),(150,'Нигер'),(151,'Нигерия'),(152,'Нидерланды'),(153,'Никарагуа'),(154,'Ниуэ'),(155,'Новая Зеландия'),(156,'Новая Каледония'),(157,'Норвегия'),(158,'Остров Норфолк'),(159,'ОАЭ'),(160,'Оман'),(161,'Пакистан'),(162,'Палау'),(163,'Палестинская национальная администрация'),(164,'Панама'),(165,'Папуа — Новая Гвинея'),(166,'Парагвай'),(167,'Перу'),(168,'Острова Питкэрн'),(169,'Французская Полинезия'),(170,'Польша'),(171,'Португалия'),(172,'ПМР'),(173,'Пуэрто-Рико'),(174,'Реюньон'),(175,'Остров Рождества'),(176,'Россия'),(177,'Руанда'),(178,'Румыния'),(179,'Саба'),(180,'Сальвадор'),(181,'Самоа'),(182,'Американское Самоа'),(183,'Сан-Марино'),(184,'Сан-Томе и Принсипи'),(185,'Саудовская Аравия'),(186,'Сахарская Арабская Демократическая Республика'),(187,'Свазиленд'),(188,'Острова Святой Елены, Вознесения и Тристан-да-Кунья'),(189,'Северные Марианские острова'),(190,'Сейшельские Острова'),(191,'Сенегал'),(192,'Сен-Бартельми'),(193,'Сен-Мартен'),(194,'Сен-Пьер и Микелон'),(195,'Сент-Винсент и Гренадины'),(196,'Сент-Китс и Невис'),(197,'Сент-Люсия'),(198,'Сербия'),(199,'Сингапур'),(200,'Синт-Мартен'),(201,'Синт-Эстатиус'),(202,'Сирия'),(203,'Словакия'),(204,'Словения'),(205,'США'),(206,'Соломоновы Острова'),(207,'Сомали'),(208,'Судан'),(209,'Суринам'),(210,'Сьерра-Леоне'),(211,'Таджикистан'),(212,'Таиланд'),(213,'Танзания'),(214,'Тёркс и Кайкос'),(215,'Того'),(216,'Токелау'),(217,'Тонга'),(218,'Тринидад и Тобаго'),(219,'Тувалу'),(220,'Тунис'),(221,'Туркмения'),(222,'Турция'),(223,'Уганда'),(224,'Узбекистан'),(225,'Украина'),(226,'Уоллис и Футуна'),(227,'Уругвай'),(228,'Фарерские острова'),(229,'Фиджи'),(230,'Филиппины'),(231,'Финляндия'),(232,'Фолклендские острова'),(233,'Франция'),(234,'Хорватия'),(235,'ЦАР'),(236,'Чад'),(237,'Черногория'),(238,'Чехия'),(239,'Чили'),(240,'Швейцария'),(241,'Швеция'),(242,'Шри-Ланка'),(243,'Эквадор'),(244,'Экваториальная Гвинея'),(245,'Эритрея'),(246,'Эстония'),(247,'Эфиопия'),(248,'Южная Осетия'),(249,'ЮАР'),(250,'Южный Судан'),(251,'Ямайка'),(252,'Япония');
/*!40000 ALTER TABLE `country` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `customer` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  `id_country` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_country_name` (`id_country`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (7,'Hugh Jackman',2),(6,'Валерий Смирнов',176),(1,'Дмитрий Семенов',176),(2,'Мария Клинских',176),(5,'Михаил Стасов',176),(3,'John Smith',205),(4,'Robert Taylor',205);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rental_point`
--

DROP TABLE IF EXISTS `rental_point`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rental_point` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_country` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `latitude` decimal(9,6) NOT NULL DEFAULT '0.000000',
  `longitude` decimal(9,6) NOT NULL DEFAULT '0.000000',
  PRIMARY KEY (`id`),
  KEY `idx_country` (`id_country`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='Точки проката';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rental_point`
--

LOCK TABLES `rental_point` WRITE;
/*!40000 ALTER TABLE `rental_point` DISABLE KEYS */;
INSERT INTO `rental_point` VALUES (1,176,'Московский центр проката',37.303913,55.807147),(2,176,'Пермский центр проката',56.180870,57.980989),(3,176,'Нижегородский пункт проката',44.015128,56.285595),(4,176,'Ярославский пункт проката',39.860013,57.585595),(5,176,'Казанский пункт проката',49.105128,55.815595);
/*!40000 ALTER TABLE `rental_point` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rental_point_vehicle`
--

DROP TABLE IF EXISTS `rental_point_vehicle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rental_point_vehicle` (
  `id_rent_point` int(11) NOT NULL,
  `id_vehicle` int(11) NOT NULL,
  `available` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_rent_point`,`id_vehicle`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Наличие ТС в точках проката';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rental_point_vehicle`
--

LOCK TABLES `rental_point_vehicle` WRITE;
/*!40000 ALTER TABLE `rental_point_vehicle` DISABLE KEYS */;
INSERT INTO `rental_point_vehicle` VALUES (1,1,1),(1,5,1),(1,8,1),(1,9,1),(2,2,0),(2,7,0),(3,6,0),(4,4,0),(5,3,0);
/*!40000 ALTER TABLE `rental_point_vehicle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicle`
--

DROP TABLE IF EXISTS `vehicle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vehicle` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` enum('car','bike','scooter') DEFAULT NULL,
  `name` varchar(100) NOT NULL,
  `id_vendor` int(11) NOT NULL,
  `license_plate` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_license_plate` (`license_plate`),
  KEY `idx_vendor` (`id_vendor`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='ТС';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle`
--

LOCK TABLES `vehicle` WRITE;
/*!40000 ALTER TABLE `vehicle` DISABLE KEYS */;
INSERT INTO `vehicle` VALUES (1,'car','camry',1,'A 123 BC'),(2,'bike','wave',2,'D 345 EF'),(3,'scooter','dfx',2,'G 877 HT'),(4,'bike','yam-1',4,'G 543 GF'),(5,'car','Ram 1500',7,'H 454 AZ'),(6,'car','Touring',8,'E 345 SD'),(7,'car','488 GTB',9,'U 007 SP'),(8,'car','CX-9',3,'E 846 HT'),(9,'car','Veyron',19,'B 558 BT');
/*!40000 ALTER TABLE `vehicle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vehicle_rent`
--

DROP TABLE IF EXISTS `vehicle_rent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vehicle_rent` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_vehicle` int(11) NOT NULL,
  `id_customer` int(11) NOT NULL,
  `id_rent_point` int(11) NOT NULL,
  `rent_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `id_return_point` int(11) DEFAULT NULL,
  `return_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8 COMMENT='Информация о прокате ТС';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vehicle_rent`
--

LOCK TABLES `vehicle_rent` WRITE;
/*!40000 ALTER TABLE `vehicle_rent` DISABLE KEYS */;
INSERT INTO `vehicle_rent` VALUES (9,3,1,2,'2017-09-23 15:05:46',2,'2017-09-23 16:05:46'),(10,2,1,2,'2017-09-23 15:06:45',2,'2017-09-23 17:06:45'),(11,1,2,2,'2017-09-21 16:08:11',2,'2017-09-22 18:08:11'),(12,3,1,2,'2017-09-22 15:00:00',2,'2017-09-22 16:00:00'),(13,3,1,2,'2017-09-21 15:00:00',2,'2017-09-21 17:00:00'),(14,4,2,2,'2017-09-20 16:15:51',2,'2017-09-20 20:15:51'),(15,1,1,2,'2017-09-24 10:20:21',2,'2017-09-24 15:52:30'),(16,3,2,2,'2017-09-24 10:20:30',2,'2017-09-24 10:44:40'),(17,4,2,2,'2017-09-24 10:44:51',2,'2017-09-24 16:43:46'),(18,1,1,2,'2017-09-24 15:52:45',2,'2017-09-24 15:53:22'),(19,1,1,2,'2017-09-24 15:54:15',2,'2017-09-24 16:53:07'),(20,4,2,2,'2017-09-24 16:43:56',2,'2017-09-24 16:51:28'),(21,4,2,2,'2017-09-24 16:51:59',2,'2017-09-24 16:53:16'),(22,4,2,2,'2017-09-24 16:56:39',2,'2017-09-24 16:59:20'),(23,1,1,2,'2017-09-24 17:00:52',1,'2017-09-28 18:32:00'),(24,8,2,1,'2017-09-28 18:05:04',1,'2017-09-28 18:05:14'),(25,2,1,2,'2017-09-28 18:32:45',2,'2017-09-28 18:33:12'),(26,5,2,4,'2017-09-28 18:35:08',1,'2017-09-28 18:48:07'),(27,6,1,4,'2017-09-28 18:35:19',3,'2017-09-28 19:34:18'),(28,7,3,5,'2017-09-28 18:35:36',1,'2017-09-28 19:20:31'),(29,2,1,2,'2017-09-28 18:36:02',NULL,NULL),(30,4,4,2,'2017-09-28 18:36:12',4,'2017-09-28 19:34:32'),(31,3,7,2,'2017-09-28 18:36:21',5,'2017-09-28 19:34:47'),(32,5,5,1,'2017-09-28 19:14:10',1,'2017-09-28 19:19:37'),(33,7,5,1,'2017-09-28 19:21:14',2,'2017-09-28 19:33:59'),(34,3,2,5,'2017-09-28 19:35:06',NULL,NULL),(35,4,2,4,'2017-09-28 19:35:26',NULL,NULL),(36,6,3,3,'2017-09-28 19:35:44',NULL,NULL),(37,7,5,2,'2017-09-28 19:36:02',NULL,NULL);
/*!40000 ALTER TABLE `vehicle_rent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vendor`
--

DROP TABLE IF EXISTS `vendor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vendor` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='Марка ТС';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendor`
--

LOCK TABLES `vendor` WRITE;
/*!40000 ALTER TABLE `vendor` DISABLE KEYS */;
INSERT INTO `vendor` VALUES (10,'Bugatti'),(8,'Chrysler'),(7,'Dodge'),(9,'Ferrari'),(2,'Honda'),(3,'Mazda'),(5,'Suzuki'),(1,'Toyota'),(4,'Yamaha');
/*!40000 ALTER TABLE `vendor` ENABLE KEYS */;
UNLOCK TABLES;
/*!50112 SET @disable_bulk_load = IF (@is_rocksdb_supported, 'SET SESSION rocksdb_bulk_load = @old_rocksdb_bulk_load', 'SET @dummy_rocksdb_bulk_load = 0') */;
/*!50112 PREPARE s FROM @disable_bulk_load */;
/*!50112 EXECUTE s */;
/*!50112 DEALLOCATE PREPARE s */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-09-28 23:27:14
