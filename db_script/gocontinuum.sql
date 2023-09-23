-- MySQL dump 10.13  Distrib 8.0.34, for Win64 (x86_64)
--
-- Host: continuum-dev-1-mysql.mysql.database.azure.com    Database: gocontinuum
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `address2` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `zip_code` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (1,NULL,NULL,'1234','5678','indore','india','452011',NULL);
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `admin_pages`
--

DROP TABLE IF EXISTS `admin_pages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_pages` (
  `my_row_id` bigint unsigned NOT NULL AUTO_INCREMENT /*!80023 INVISIBLE */,
  `page_id` bigint NOT NULL,
  `admin_id` bigint NOT NULL,
  PRIMARY KEY (`my_row_id`),
  KEY `FK2n3wgvrxg2ai1henirjehwfkp` (`admin_id`),
  KEY `FKootr39d0dkqtamn6bfdpjjcjf` (`page_id`),
  CONSTRAINT `FK2n3wgvrxg2ai1henirjehwfkp` FOREIGN KEY (`admin_id`) REFERENCES `sidebar_admin` (`id`),
  CONSTRAINT `FKootr39d0dkqtamn6bfdpjjcjf` FOREIGN KEY (`page_id`) REFERENCES `page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_pages`
--

LOCK TABLES `admin_pages` WRITE;
/*!40000 ALTER TABLE `admin_pages` DISABLE KEYS */;
/*!40000 ALTER TABLE `admin_pages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `audit_log`
--

DROP TABLE IF EXISTS `audit_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `highlight` varchar(255) DEFAULT NULL,
  `rma_no` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `user` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmyukpp6mrvulvpkorwsf8b55u` (`user`),
  CONSTRAINT `FKmyukpp6mrvulvpkorwsf8b55u` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_log`
--

LOCK TABLES `audit_log` WRITE;
/*!40000 ALTER TABLE `audit_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `audit_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS `client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `client_address` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `client_name` varchar(255) DEFAULT NULL,
  `contact_no` bigint DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client`
--

LOCK TABLES `client` WRITE;
/*!40000 ALTER TABLE `client` DISABLE KEYS */;
/*!40000 ALTER TABLE `client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `client_config`
--

DROP TABLE IF EXISTS `client_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `client_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `erpconnection_string` varchar(255) DEFAULT NULL,
  `erpdata_sych_interval` varchar(255) DEFAULT NULL,
  `host` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `port` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `allow_rstck_fees` bit(1) NOT NULL,
  `email_from` varchar(255) DEFAULT NULL,
  `emailto` varchar(255) DEFAULT NULL,
  `erp_company_id` varchar(255) DEFAULT NULL,
  `fee_type` varchar(255) DEFAULT NULL,
  `filter_search_configuration` varchar(255) DEFAULT NULL,
  `force_acceptandc` bit(1) NOT NULL,
  `notification_enable` bit(1) NOT NULL,
  `re_stocking_amount` decimal(19,2) DEFAULT NULL,
  `return_policy_period` int DEFAULT NULL,
  `seperatedbinstance` bit(1) NOT NULL,
  `client_id` bigint DEFAULT NULL,
  `questions_required` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKge25sgvd2tag83vq4uuar5nd7` (`client_id`),
  CONSTRAINT `FKge25sgvd2tag83vq4uuar5nd7` FOREIGN KEY (`client_id`) REFERENCES `client` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `client_config`
--

LOCK TABLES `client_config` WRITE;
/*!40000 ALTER TABLE `client_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `client_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact`
--

DROP TABLE IF EXISTS `contact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contact` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `contact_email_id` varchar(255) DEFAULT NULL,
  `contact_id` varchar(255) DEFAULT NULL,
  `contact_name` varchar(255) DEFAULT NULL,
  `contact_phone_no` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact`
--

LOCK TABLES `contact` WRITE;
/*!40000 ALTER TABLE `contact` DISABLE KEYS */;
INSERT INTO `contact` VALUES (5,'2023-09-14 16:48:52','2023-09-14 16:48:52','Jhon@gmail.com',NULL,'Jhon','984537584');
/*!40000 ALTER TABLE `contact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contacts`
--

DROP TABLE IF EXISTS `contacts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contacts` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `facebook` varchar(255) DEFAULT NULL,
  `linkedin` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `skype` varchar(255) DEFAULT NULL,
  `website` varchar(255) DEFAULT NULL,
  `alternative_number` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contacts`
--

LOCK TABLES `contacts` WRITE;
/*!40000 ALTER TABLE `contacts` DISABLE KEYS */;
INSERT INTO `contacts` VALUES (1,NULL,NULL,'superadmin@gmail.com','superadmin.facebook','superadmin.linkedin','superadmin note','83748375','superadmin.skype','www.superadmin.com',NULL);
/*!40000 ALTER TABLE `contacts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `customer_id` varchar(255) DEFAULT NULL,
  `customer_type` varchar(255) DEFAULT NULL,
  `display_name` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `status` bit(1) NOT NULL,
  `client_config_id` bigint DEFAULT NULL,
  `ship_to` bigint DEFAULT NULL,
  `store_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK13y8s3rdv7k18swbdfvnxgk2x` (`client_config_id`),
  KEY `FK5j122wy1l71evv9lxbnofcncs` (`ship_to`),
  KEY `FK4b6xs77vaijup4s0q2s06mvdo` (`store_id`),
  CONSTRAINT `FK13y8s3rdv7k18swbdfvnxgk2x` FOREIGN KEY (`client_config_id`) REFERENCES `client_config` (`id`),
  CONSTRAINT `FK4b6xs77vaijup4s0q2s06mvdo` FOREIGN KEY (`store_id`) REFERENCES `store` (`id`),
  CONSTRAINT `FK5j122wy1l71evv9lxbnofcncs` FOREIGN KEY (`ship_to`) REFERENCES `store_address` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer`
--

LOCK TABLES `customer` WRITE;
/*!40000 ALTER TABLE `customer` DISABLE KEYS */;
INSERT INTO `customer` VALUES (1,'2023-09-14 14:40:41','2023-09-14 14:40:41','164977',NULL,'DOUG VANDERWEL','DVANDERWEL@VALVESOFTWARE.COM','DOUG','VANDERWEL',NULL,'425-583-9719',_binary '\0',NULL,NULL,NULL),(2,'2023-09-19 19:30:03','2023-09-19 19:30:03','',NULL,NULL,NULL,NULL,NULL,NULL,NULL,_binary '\0',NULL,NULL,NULL);
/*!40000 ALTER TABLE `customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_address`
--

DROP TABLE IF EXISTS `order_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `address_type` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `email_address` varchar(255) DEFAULT NULL,
  `fax` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `street1` varchar(255) DEFAULT NULL,
  `street2` varchar(255) DEFAULT NULL,
  `zipcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_address`
--

LOCK TABLES `order_address` WRITE;
/*!40000 ALTER TABLE `order_address` DISABLE KEYS */;
INSERT INTO `order_address` VALUES (12,'2023-09-14 16:48:52','2023-09-14 16:48:52',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(13,'2023-09-14 16:48:52','2023-09-14 16:48:52','Billing','Los Angeles','USA',NULL,'456-789-1234',NULL,NULL,'123-456-7890','California','123 Main St','Apt 4B','90001');
/*!40000 ALTER TABLE `order_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `amount` decimal(19,2) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `purchase_date` datetime DEFAULT NULL,
  `quantity` int NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `bill_to` bigint DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `ship_to` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKm5rpmmaeusgo8dtrx9sblma72` (`bill_to`),
  KEY `FKt4dc2r9nbvbujrljv3e23iibt` (`order_id`),
  KEY `FKpwle2ofrgp7sh7ofd0f97eemm` (`ship_to`),
  CONSTRAINT `FKm5rpmmaeusgo8dtrx9sblma72` FOREIGN KEY (`bill_to`) REFERENCES `order_address` (`id`),
  CONSTRAINT `FKpwle2ofrgp7sh7ofd0f97eemm` FOREIGN KEY (`ship_to`) REFERENCES `order_address` (`id`),
  CONSTRAINT `FKt4dc2r9nbvbujrljv3e23iibt` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item_documents`
--

DROP TABLE IF EXISTS `order_item_documents`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item_documents` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `return_order_item_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKls8mrcvrp65rs5nlllm1mnysf` (`return_order_item_id`),
  CONSTRAINT `FKls8mrcvrp65rs5nlllm1mnysf` FOREIGN KEY (`return_order_item_id`) REFERENCES `return_order_item` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item_documents`
--

LOCK TABLES `order_item_documents` WRITE;
/*!40000 ALTER TABLE `order_item_documents` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_item_documents` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ormorder` bigint DEFAULT NULL,
  `ponumber` varchar(255) DEFAULT NULL,
  `contact_id` bigint DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `invoice_no` varchar(255) DEFAULT NULL,
  `order_date` datetime DEFAULT NULL,
  `requested_date` datetime DEFAULT NULL,
  `sales_location_id` bigint DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `billto` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `ship_to` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqqxnrsey6en1btm779795s8i2` (`billto`),
  KEY `FK624gtjin3po807j3vix093tlf` (`customer_id`),
  KEY `FKsdwbsebsdi7efw2cgvltt4ts2` (`ship_to`),
  KEY `FKel9kyl84ego2otj2accfd8mr7` (`user_id`),
  CONSTRAINT `FK624gtjin3po807j3vix093tlf` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FKel9kyl84ego2otj2accfd8mr7` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKqqxnrsey6en1btm779795s8i2` FOREIGN KEY (`billto`) REFERENCES `order_address` (`id`),
  CONSTRAINT `FKsdwbsebsdi7efw2cgvltt4ts2` FOREIGN KEY (`ship_to`) REFERENCES `order_address` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `page`
--

DROP TABLE IF EXISTS `page`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `page` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `page`
--

LOCK TABLES `page` WRITE;
/*!40000 ALTER TABLE `page` DISABLE KEYS */;
/*!40000 ALTER TABLE `page` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

DROP TABLE IF EXISTS `permissions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `enabled` bit(1) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `permission` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
INSERT INTO `permissions` VALUES (1,_binary '','full access','FULL_ACCESS'),(2,_binary '',NULL,'VIEW_ONLY'),(3,_binary '',NULL,'EDIT & VIEW ONLY');
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions_roles`
--

DROP TABLE IF EXISTS `permissions_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permissions_roles` (
  `my_row_id` bigint unsigned NOT NULL AUTO_INCREMENT /*!80023 INVISIBLE */,
  `role_id` bigint NOT NULL,
  `permission_id` bigint NOT NULL,
  PRIMARY KEY (`my_row_id`),
  KEY `FKff6bcp6bbaup2irutar3dfaks` (`permission_id`),
  KEY `FK9j7vx1vojmoa6rs21eggd46xn` (`role_id`),
  CONSTRAINT `FK9j7vx1vojmoa6rs21eggd46xn` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKff6bcp6bbaup2irutar3dfaks` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions_roles`
--

LOCK TABLES `permissions_roles` WRITE;
/*!40000 ALTER TABLE `permissions_roles` DISABLE KEYS */;
INSERT INTO `permissions_roles` (`my_row_id`, `role_id`, `permission_id`) VALUES (1,1,1);
/*!40000 ALTER TABLE `permissions_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reason_code`
--

DROP TABLE IF EXISTS `reason_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reason_code` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `customer` bigint DEFAULT NULL,
  `parent_reason_code_id` bigint DEFAULT NULL,
  `store` bigint DEFAULT NULL,
  `img_mandatory` bit(1) NOT NULL,
  `isPopUp` tinyint(1) DEFAULT NULL,
  `popUpDetails` varchar(225) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgpxs8mmfl7jwlxbqljpin321x` (`customer`),
  KEY `FK3vs5ex0g3st3p8m4xk2yhtjhx` (`parent_reason_code_id`),
  KEY `FKaw2mkwbmw1u29905jnv7ncux8` (`store`),
  CONSTRAINT `FK3vs5ex0g3st3p8m4xk2yhtjhx` FOREIGN KEY (`parent_reason_code_id`) REFERENCES `reason_code` (`id`),
  CONSTRAINT `FKaw2mkwbmw1u29905jnv7ncux8` FOREIGN KEY (`store`) REFERENCES `store` (`id`),
  CONSTRAINT `FKgpxs8mmfl7jwlxbqljpin321x` FOREIGN KEY (`customer`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reason_code`
--

LOCK TABLES `reason_code` WRITE;
/*!40000 ALTER TABLE `reason_code` DISABLE KEYS */;
INSERT INTO `reason_code` VALUES (1,NULL,NULL,'Item Damaged In Transit','RMA - DAMAGED IN TRANSIT',NULL,NULL,NULL,1,_binary '',0,''),(2,NULL,NULL,'Defective Item/ Poor Quality Item','RMA - DEFECTIVE ITEM/ OUT-OF-BOX FAILURE/ POOR QUALITY',NULL,NULL,NULL,1,_binary '',1,'This return reason will require a conversation with The Lab Depot CSR Team. Please call The Lab Depot Customer Service after submission of your RMA Request or look out for a call from us'),(3,NULL,NULL,'Incorrect Item Ordered','RMA - INCORRECT ITEM ORDERED',NULL,NULL,NULL,1,_binary '\0',0,NULL),(4,NULL,NULL,'Item did not match specifications','RMA - MANUFACTURER INCORRECT SPECIFICATIONS',NULL,NULL,NULL,1,_binary '',0,NULL),(5,NULL,NULL,'Ordered too many','RMA - ORDER QUANTITY',NULL,NULL,NULL,1,_binary '\0',0,NULL),(6,NULL,NULL,'Item received is not item ordered','RMA - SUPPLIER ERROR INCORRECT ITEM',NULL,NULL,NULL,1,_binary '',0,NULL),(7,NULL,NULL,'Warranty/ Repair/ Replace','RMA - WARRANTY / REPAIR / REPLACE',NULL,NULL,NULL,1,_binary '',1,'This return reason will require a conversation with The Lab Depot CSR Team. Please call The Lab Depot Customer Service after submission of your RMA Request or look out for a call from us'),(8,NULL,NULL,'Other (fill in issue)','',NULL,NULL,NULL,1,_binary '',0,NULL);
/*!40000 ALTER TABLE `reason_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `return_order`
--

DROP TABLE IF EXISTS `return_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `return_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `ormorder` bigint DEFAULT NULL,
  `ponumber` varchar(255) DEFAULT NULL,
  `company_id` varchar(255) DEFAULT NULL,
  `contact_id` varchar(255) DEFAULT NULL,
  `currency` varchar(255) DEFAULT NULL,
  `invoice_no` varchar(255) DEFAULT NULL,
  `order_date` datetime DEFAULT NULL,
  `order_no` varchar(255) DEFAULT NULL,
  `requested_date` datetime DEFAULT NULL,
  `rma_order_no` varchar(255) DEFAULT NULL,
  `sales_location_id` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `billto` bigint DEFAULT NULL,
  `con_id` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `ship_to` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `isinvoice_linked` bit(1) NOT NULL,
  `isdocument_linked` bit(1) DEFAULT NULL,
  `next_activity_date` datetime DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKn0fj8x72s7v2rmd2s5ebym3mk` (`billto`),
  KEY `FK58imdprcr9arrpl3rkckl2syr` (`con_id`),
  KEY `FKhgovjsbgbfea8uvjpnrfsteqk` (`customer_id`),
  KEY `FKn2sljliebe26nb6l17mtislgv` (`ship_to`),
  KEY `FK8s6ybwety0mp005rnksb6mcq` (`user_id`),
  CONSTRAINT `FK58imdprcr9arrpl3rkckl2syr` FOREIGN KEY (`con_id`) REFERENCES `contact` (`id`),
  CONSTRAINT `FK8s6ybwety0mp005rnksb6mcq` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKhgovjsbgbfea8uvjpnrfsteqk` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FKn0fj8x72s7v2rmd2s5ebym3mk` FOREIGN KEY (`billto`) REFERENCES `order_address` (`id`),
  CONSTRAINT `FKn2sljliebe26nb6l17mtislgv` FOREIGN KEY (`ship_to`) REFERENCES `order_address` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `return_order`
--

LOCK TABLES `return_order` WRITE;
/*!40000 ALTER TABLE `return_order` DISABLE KEYS */;
INSERT INTO `return_order` VALUES (7,'2023-09-14 16:48:52','2023-09-14 16:48:52',NULL,'200000424','LD001','45560',NULL,NULL,'2023-09-14 16:48:51','424338','2023-09-14 16:48:51','427213','101','Under Review',NULL,5,1,12,NULL,_binary '\0',NULL,NULL,NULL);
/*!40000 ALTER TABLE `return_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `return_order_item`
--

DROP TABLE IF EXISTS `return_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `return_order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `amount` decimal(19,2) DEFAULT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `courier_name` varchar(255) DEFAULT NULL,
  `follow_up_date` datetime DEFAULT NULL,
  `incident_date` datetime DEFAULT NULL,
  `item_name` varchar(255) DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `problem_desc` varchar(255) DEFAULT NULL,
  `purchase_date` datetime DEFAULT NULL,
  `quanity` int NOT NULL,
  `re_stocking_amount` decimal(19,2) DEFAULT NULL,
  `reason_code` varchar(255) DEFAULT NULL,
  `received_quantity` int NOT NULL,
  `received_state` varchar(255) DEFAULT NULL,
  `return_amount` decimal(19,2) DEFAULT NULL,
  `return_comments` varchar(255) DEFAULT NULL,
  `shipping_cost` decimal(19,2) DEFAULT NULL,
  `shipping_tax` decimal(19,2) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `tracking_number` bigint DEFAULT NULL,
  `tracking_url` varchar(255) DEFAULT NULL,
  `bill_to` bigint DEFAULT NULL,
  `return_order_id` bigint DEFAULT NULL,
  `ship_to` bigint DEFAULT NULL,
  `assign_to` bigint DEFAULT NULL,
  `isdocument_linked` bit(1) DEFAULT NULL,
  `isinvoice_linked` bit(1) DEFAULT NULL,
  `item_desc` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmol29utwhfs12ot5fo570qd16` (`bill_to`),
  KEY `FKphw0qlk01pabwoythsfmtoi5e` (`return_order_id`),
  KEY `FK8wd0m9u36jt5o311lc0bv5drf` (`ship_to`),
  KEY `FKd1b43jml4enixwspgh8chwo0x` (`assign_to`),
  CONSTRAINT `FK8wd0m9u36jt5o311lc0bv5drf` FOREIGN KEY (`ship_to`) REFERENCES `order_address` (`id`),
  CONSTRAINT `FKd1b43jml4enixwspgh8chwo0x` FOREIGN KEY (`assign_to`) REFERENCES `user` (`id`),
  CONSTRAINT `FKmol29utwhfs12ot5fo570qd16` FOREIGN KEY (`bill_to`) REFERENCES `order_address` (`id`),
  CONSTRAINT `FKphw0qlk01pabwoythsfmtoi5e` FOREIGN KEY (`return_order_id`) REFERENCES `return_order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `return_order_item`
--

LOCK TABLES `return_order_item` WRITE;
/*!40000 ALTER TABLE `return_order_item` DISABLE KEYS */;
INSERT INTO `return_order_item` VALUES (7,'2023-09-14 16:48:52','2023-09-14 16:48:52',NULL,NULL,NULL,NULL,NULL,'FLOW-48',NULL,'any desc come from UI',NULL,1,NULL,'RMA - BILL-TO CORRECTION',0,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,NULL,7,13,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `return_order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `return_room`
--

DROP TABLE IF EXISTS `return_room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `return_room` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `follow_up_date` datetime DEFAULT NULL,
  `message` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `return_order_item_id` bigint DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `assign_to` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKs2trd3dgfvt9afv2fad646swj` (`assign_to`),
  KEY `FKjpa7jr0jh4lax5xgl4vnlvkjy` (`return_order_item_id`),
  CONSTRAINT `FKjpa7jr0jh4lax5xgl4vnlvkjy` FOREIGN KEY (`return_order_item_id`) REFERENCES `return_order_item` (`id`),
  CONSTRAINT `FKs2trd3dgfvt9afv2fad646swj` FOREIGN KEY (`assign_to`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `return_room`
--

LOCK TABLES `return_room` WRITE;
/*!40000 ALTER TABLE `return_room` DISABLE KEYS */;
/*!40000 ALTER TABLE `return_room` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rma_invoice_info`
--

DROP TABLE IF EXISTS `rma_invoice_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rma_invoice_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_invoice_linked` bit(1) NOT NULL,
  `retry_count` int DEFAULT NULL,
  `rma_order_no` varchar(255) DEFAULT NULL,
  `return_order_id` bigint DEFAULT NULL,
  `isdocument_linked` bit(1) NOT NULL,
  `is_document_linked` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5bko24188133vt7csiwnihgm` (`return_order_id`),
  CONSTRAINT `FK5bko24188133vt7csiwnihgm` FOREIGN KEY (`return_order_id`) REFERENCES `return_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rma_invoice_info`
--

LOCK TABLES `rma_invoice_info` WRITE;
/*!40000 ALTER TABLE `rma_invoice_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `rma_invoice_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rma_pages`
--

DROP TABLE IF EXISTS `rma_pages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rma_pages` (
  `page_id` bigint NOT NULL,
  `rma_id` bigint NOT NULL,
  PRIMARY KEY (`page_id`,`rma_id`),
  KEY `FK38dqvsy4ygdqmexwugybhxtau` (`rma_id`),
  CONSTRAINT `FK38dqvsy4ygdqmexwugybhxtau` FOREIGN KEY (`rma_id`) REFERENCES `sidebarrma` (`id`),
  CONSTRAINT `FKdhgfensjqt5c8357s8nubgvgk` FOREIGN KEY (`page_id`) REFERENCES `page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rma_pages`
--

LOCK TABLES `rma_pages` WRITE;
/*!40000 ALTER TABLE `rma_pages` DISABLE KEYS */;
/*!40000 ALTER TABLE `rma_pages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_pages`
--

DROP TABLE IF EXISTS `role_pages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_pages` (
  `role_id` bigint NOT NULL,
  `page_id` bigint NOT NULL,
  PRIMARY KEY (`role_id`,`page_id`),
  KEY `FKanuyqpmi85i99w0wo0ska6lek` (`page_id`),
  CONSTRAINT `FKanuyqpmi85i99w0wo0ska6lek` FOREIGN KEY (`page_id`) REFERENCES `page` (`id`),
  CONSTRAINT `FKomej6fxta5yy4ahedu616utbn` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_pages`
--

LOCK TABLES `role_pages` WRITE;
/*!40000 ALTER TABLE `role_pages` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_pages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role` varchar(255) NOT NULL,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'SUPER_ADMIN',NULL,NULL),(2,'ADMIN',NULL,NULL),(3,'RETURN_PROCESSOR',NULL,NULL),(4,'END_USER',NULL,NULL);
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sidebar_admin`
--

DROP TABLE IF EXISTS `sidebar_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sidebar_admin` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `icon` varchar(255) DEFAULT NULL,
  `is_navigate` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `admin_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3vaqk7lbacx4jlvqrs5t9jjso` (`admin_id`),
  CONSTRAINT `FK3vaqk7lbacx4jlvqrs5t9jjso` FOREIGN KEY (`admin_id`) REFERENCES `page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sidebar_admin`
--

LOCK TABLES `sidebar_admin` WRITE;
/*!40000 ALTER TABLE `sidebar_admin` DISABLE KEYS */;
/*!40000 ALTER TABLE `sidebar_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sidebarrma`
--

DROP TABLE IF EXISTS `sidebarrma`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sidebarrma` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `icon` varchar(255) DEFAULT NULL,
  `is_navigate` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `rma_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKp71ekm37eglcxg5hu613xcgom` (`rma_id`),
  CONSTRAINT `FKp71ekm37eglcxg5hu613xcgom` FOREIGN KEY (`rma_id`) REFERENCES `page` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sidebarrma`
--

LOCK TABLES `sidebarrma` WRITE;
/*!40000 ALTER TABLE `sidebarrma` DISABLE KEYS */;
/*!40000 ALTER TABLE `sidebarrma` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status_config`
--

DROP TABLE IF EXISTS `status_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status_config` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `background_color` varchar(255) DEFAULT NULL,
  `color` varchar(255) DEFAULT NULL,
  `is_tracking_avl` bit(1) NOT NULL,
  `statustype` varchar(255) DEFAULT NULL,
  `statuslabl` varchar(255) DEFAULT NULL,
  `status_type` varchar(255) DEFAULT NULL,
  `is_emailsend` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status_config`
--

LOCK TABLES `status_config` WRITE;
/*!40000 ALTER TABLE `status_config` DISABLE KEYS */;
INSERT INTO `status_config` VALUES (1,NULL,NULL,'green','yellow',_binary '\0','RmaLevel','Return Requested','RmaLevel',_binary '\0'),(2,NULL,NULL,'while','red',_binary '\0','RmaLevel','Under review','RmaLevel',_binary '\0'),(3,NULL,NULL,'red','black',_binary '\0','RmaLevel','Authorized','RmaLevel',_binary '\0'),(4,NULL,NULL,'red','green',_binary '\0','RmaLevel','Recieved Under review','RmaLevel',_binary '\0'),(5,NULL,NULL,'blue','yellow',_binary '\0','RmaLevel','Approved','RmaLevel',_binary '\0'),(6,NULL,NULL,'purple','while',_binary '\0','RmaLevel','RMA Denied','RmaLevel',_binary '\0'),(7,NULL,NULL,'silver','golden',_binary '\0','Lineitem','Awating Vender Approval','Lineitem',_binary '\0'),(8,NULL,NULL,'silver','red',_binary '\0','Lineitem','Awating Carrier Approval','Lineitem',_binary '\0'),(9,NULL,NULL,'purple','green',_binary '\0','Lineitem','Need more pictures','Lineitem',_binary '\0'),(10,NULL,NULL,'silver','purple',_binary '\0','Lineitem','Require more customer information','Lineitem',_binary '\0'),(11,NULL,NULL,'indigo','blue',_binary '\0','Lineitem','Need more pictures','Lineitem',_binary '\0'),(12,NULL,NULL,'silver','golden',_binary '\0','Lineitem','Need more Context','Lineitem',_binary '\0'),(13,NULL,NULL,'red','green',_binary '','Lineitem','Authorized awaiting transinspecting','Lineitem',_binary '\0'),(14,NULL,NULL,'silver','red',_binary '','Lineitem','Authorized in transit','Lineitem',_binary '\0');
/*!40000 ALTER TABLE `status_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store`
--

DROP TABLE IF EXISTS `store`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `store_code` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `category` varchar(255) DEFAULT NULL,
  `content_encoding` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `fee_type` varchar(255) DEFAULT NULL,
  `no_of_stores` int DEFAULT NULL,
  `reason_listing` varchar(255) DEFAULT NULL,
  `store_name` varchar(255) DEFAULT NULL,
  `store_state` bit(1) DEFAULT NULL,
  `store_type` varchar(255) DEFAULT NULL,
  `ship_to` bigint DEFAULT NULL,
  `store_locale_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3o2j67vneegx03wl2kxeqc4n9` (`ship_to`),
  KEY `FKj3ip6mklaf3e3utg9xx782olv` (`store_locale_id`),
  CONSTRAINT `FK3o2j67vneegx03wl2kxeqc4n9` FOREIGN KEY (`ship_to`) REFERENCES `store_address` (`id`),
  CONSTRAINT `FKj3ip6mklaf3e3utg9xx782olv` FOREIGN KEY (`store_locale_id`) REFERENCES `store_locale` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store`
--

LOCK TABLES `store` WRITE;
/*!40000 ALTER TABLE `store` DISABLE KEYS */;
INSERT INTO `store` VALUES (1,'2023-08-24 12:19:48','2023-08-24 12:19:48','STORE001',NULL,'Electronics','UTF-8','Store Description',_binary '','Flat',1,'New Listing','My Store',_binary '','Retail',1,NULL);
/*!40000 ALTER TABLE `store` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_address`
--

DROP TABLE IF EXISTS `store_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `address_type` varchar(255) DEFAULT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `fax` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `street1` varchar(255) DEFAULT NULL,
  `street2` varchar(255) DEFAULT NULL,
  `zipcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_address`
--

LOCK TABLES `store_address` WRITE;
/*!40000 ALTER TABLE `store_address` DISABLE KEYS */;
INSERT INTO `store_address` VALUES (1,'2023-08-24 12:19:48','2023-08-24 12:19:48',NULL,'Cityville','Countryland',NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `store_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `store_locale`
--

DROP TABLE IF EXISTS `store_locale`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `store_locale` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `locale` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `store_locale`
--

LOCK TABLES `store_locale` WRITE;
/*!40000 ALTER TABLE `store_locale` DISABLE KEYS */;
/*!40000 ALTER TABLE `store_locale` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tbl_tenant_master`
--

DROP TABLE IF EXISTS `tbl_tenant_master`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tbl_tenant_master` (
  `tenant_client_id` int NOT NULL AUTO_INCREMENT,
  `db_name` varchar(255) NOT NULL,
  `driver_class` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `status` varchar(255) NOT NULL,
  `url` varchar(255) NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `erp_company_id` varchar(255) NOT NULL,
  PRIMARY KEY (`tenant_client_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tbl_tenant_master`
--

LOCK TABLES `tbl_tenant_master` WRITE;
/*!40000 ALTER TABLE `tbl_tenant_master` DISABLE KEYS */;
INSERT INTO `tbl_tenant_master` VALUES (1,'gocontinuum','com.mysql.cj.jdbc.Driver','Y29udGludXVtYWRtaW5QYXNz@','active','jdbc:mysql://continuum-dev-1-mysql.mysql.database.azure.com:3306/gocontinuum\n','continuumadmin','LD001'),(2,'labdepotdev','com.mysql.cj.jdbc.Driver','Y29udGludXVtYWRtaW5QYXNz@','active','jdbc:mysql://continuum-dev-1-mysql.mysql.database.azure.com:3306/labdepotdev','continuumadmin','LD001'),(3,'midland','com.mysql.cj.jdbc.Driver','Y29udGludXVtYWRtaW5QYXNz@','active','jdbc:mysql://continuum-dev-1-mysql.mysql.database.azure.com:3306/midland','continuumadmin','LD001');
/*!40000 ALTER TABLE `tbl_tenant_master` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `enabled` bit(1) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `gender` tinyint DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `login_dt` datetime DEFAULT NULL,
  `note` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `secured` bit(1) NOT NULL,
  `status` bit(1) NOT NULL,
  `updated_dt` datetime DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `useraddressuserid` bigint DEFAULT NULL,
  `usercontactuserid` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `full_name` varchar(255) NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `age` varchar(255) DEFAULT NULL,
  `blood_group` varchar(255) DEFAULT NULL,
  `dob` varchar(255) DEFAULT NULL,
  `marital_status` varchar(255) DEFAULT NULL,
  `nationality` varchar(255) DEFAULT NULL,
  `profile` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `role_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKffsq9mmoan94s83kmxj2ckgji` (`useraddressuserid`),
  KEY `FKcs0fivql4kh9kcrqlfq5br7wy` (`usercontactuserid`),
  KEY `FKdptx0i3ky01svofwjytq5iry0` (`customer_id`),
  KEY `FK60qlg9oata44io3a80yh31536` (`role_id`),
  CONSTRAINT `FK60qlg9oata44io3a80yh31536` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKcs0fivql4kh9kcrqlfq5br7wy` FOREIGN KEY (`usercontactuserid`) REFERENCES `contacts` (`id`),
  CONSTRAINT `FKdptx0i3ky01svofwjytq5iry0` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FKffsq9mmoan94s83kmxj2ckgji` FOREIGN KEY (`useraddressuserid`) REFERENCES `addresses` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (10,'2023-08-22 19:15:41','2023-09-19 19:30:05','john.e@example.com',_binary '','John',1,'Doe','2023-08-10 10:00:00','This is a note about the user.','$2a$10$436OG6vyXfEFUHtB13QwpeCa69SbdYTdLusKbH94KoAUqoUpMQbMm',_binary '',_binary '','2023-08-11 12:00:00',NULL,NULL,1,1,2,'John Doe','SUPER_ADMIN',NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime DEFAULT NULL,
  `updated_date` datetime DEFAULT NULL,
  `user_roles` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role_roles`
--

DROP TABLE IF EXISTS `user_role_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role_roles` (
  `my_row_id` bigint unsigned NOT NULL AUTO_INCREMENT /*!80023 INVISIBLE */,
  `user_role_id` bigint NOT NULL,
  `roles_id` bigint NOT NULL,
  PRIMARY KEY (`my_row_id`),
  UNIQUE KEY `UK_kismf623t4ga27iu890ra9n7w` (`roles_id`),
  KEY `FK5axn5b3nfy5345pfmm9yjwhyu` (`user_role_id`),
  CONSTRAINT `FK5axn5b3nfy5345pfmm9yjwhyu` FOREIGN KEY (`user_role_id`) REFERENCES `user_role` (`id`),
  CONSTRAINT `FKsbcdiu1r7vohga0h4q5qnt3ot` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role_roles`
--

LOCK TABLES `user_role_roles` WRITE;
/*!40000 ALTER TABLE `user_role_roles` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_role_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `roles_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`roles_id`),
  KEY `FKdbv8tdyltxa1qjmfnj9oboxse` (`roles_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (10,1);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-09-22 19:02:33
