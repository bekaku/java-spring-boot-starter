-- MySQL dump 10.13  Distrib 8.1.0, for Linux (x86_64)
--
-- Host: localhost    Database: spring_starter
-- ------------------------------------------------------
-- Server version	8.1.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `access_token`
--

DROP TABLE IF EXISTS `access_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `access_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) DEFAULT NULL,
  `expires_at` datetime(6) DEFAULT NULL,
  `lastest_active` datetime(6) DEFAULT NULL,
  `logouted_date` datetime(6) DEFAULT NULL,
  `revoked` tinyint(1) DEFAULT '0',
  `service` int DEFAULT '1',
  `token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `api_client` bigint DEFAULT NULL,
  `login_log` bigint DEFAULT NULL,
  `user` bigint DEFAULT NULL,
  `user_agent` bigint DEFAULT NULL,
  `fcm_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fcm_enable` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_1djybee0iap4odfl91gkxoxem` (`token`),
  KEY `IDXkf29lomp4g8kwqr49239nfpjo` (`revoked`),
  KEY `FK5kmvrg6uuo55il7lx84mimu4f` (`api_client`),
  KEY `FK9adhg4bm3rvd167xpgg38aqfs` (`login_log`),
  KEY `FKjll8aufysmo6yvf124vsqpd81` (`user`),
  KEY `FKsca4jypki6xernocxdhewrlgk` (`user_agent`),
  KEY `IDXi8vvu91hco9k5ymwafnff27jo` (`fcm_token`),
  KEY `IDX7i22j43748d8cnciffepm0jk8` (`fcm_enable`),
  CONSTRAINT `FK5kmvrg6uuo55il7lx84mimu4f` FOREIGN KEY (`api_client`) REFERENCES `api_client` (`id`),
  CONSTRAINT `FK9adhg4bm3rvd167xpgg38aqfs` FOREIGN KEY (`login_log`) REFERENCES `login_log` (`id`),
  CONSTRAINT `FKjll8aufysmo6yvf124vsqpd81` FOREIGN KEY (`user`) REFERENCES `user` (`id`),
  CONSTRAINT `FKsca4jypki6xernocxdhewrlgk` FOREIGN KEY (`user_agent`) REFERENCES `user_agent` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2614 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `access_token`
--

LOCK TABLES `access_token` WRITE;
/*!40000 ALTER TABLE `access_token` DISABLE KEYS */;
INSERT INTO `access_token` VALUES (2612,'2023-10-30 11:16:03.530280','2024-10-29 11:16:03.527000','2023-10-30 11:17:18.147875',NULL,0,1,'0f192b8b-199e-4c4c-bc53-fc41ec97210e',1,2794,1,1,NULL,1),(2613,'2023-10-30 11:41:16.965006','2024-10-29 11:41:16.961000','2023-10-30 11:41:33.565792',NULL,0,1,'6af2dd98-a215-4e72-b39a-7ca62ad0ac1c',1,2795,1,1,NULL,1);
/*!40000 ALTER TABLE `access_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `api_client`
--

DROP TABLE IF EXISTS `api_client`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `api_client` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) DEFAULT NULL,
  `created_user` bigint DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `updated_user` bigint DEFAULT NULL,
  `api_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `api_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `by_pass` tinyint(1) DEFAULT '0',
  `status` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_qi9faagnfpyh5wky24ma2hbr6` (`api_token`),
  KEY `IDXohkk0fataetw36doj0cbn6wf3` (`updated_user`),
  KEY `IDXe851maef0ogkl5s4g3l56u4ff` (`created_user`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_client`
--

LOCK TABLES `api_client` WRITE;
/*!40000 ALTER TABLE `api_client` DISABLE KEYS */;
INSERT INTO `api_client` VALUES (1,'2022-04-19 16:39:10.000000',NULL,'2022-04-19 16:39:10.000000',NULL,'default','d3db548b2325f7aa199da97cb48b55535839f9972f696293369d291ee4e8c9ced9e005c7c4333961f13fa65b129e0918d08a9516adbba9c5a537f43ae7a1be10',1,1);
/*!40000 ALTER TABLE `api_client` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `api_client_ip`
--

DROP TABLE IF EXISTS `api_client_ip`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `api_client_ip` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) DEFAULT NULL,
  `created_user` bigint DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `updated_user` bigint DEFAULT NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint(1) DEFAULT '1',
  `api_client` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5pu9gbj8rvr9gdx27uwua7ug9` (`api_client`),
  KEY `IDXnm0gxcgi9ue456vnjcriy6tks` (`updated_user`),
  KEY `IDX9b2hqhmteavbi90n9d839p2b5` (`created_user`),
  CONSTRAINT `FK5pu9gbj8rvr9gdx27uwua7ug9` FOREIGN KEY (`api_client`) REFERENCES `api_client` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `api_client_ip`
--

LOCK TABLES `api_client_ip` WRITE;
/*!40000 ALTER TABLE `api_client_ip` DISABLE KEYS */;
/*!40000 ALTER TABLE `api_client_ip` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_manager`
--

DROP TABLE IF EXISTS `file_manager`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file_manager` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT '0',
  `created_date` datetime(6) DEFAULT NULL,
  `created_user` bigint DEFAULT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_size` bigint DEFAULT NULL,
  `hidden` tinyint(1) DEFAULT '0',
  `locked` tinyint(1) DEFAULT '0',
  `original_file_name` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `readable` tinyint(1) DEFAULT '1',
  `writeable` tinyint(1) DEFAULT '1',
  `file_mime_id` bigint DEFAULT NULL,
  `files_directory_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKiq28e5ahmqo1pc8yniixp0r6w` (`file_mime_id`),
  KEY `FKfntevcv1jorjk5fnxqb4knkg3` (`files_directory_id`),
  KEY `IDXlic6tl97u7idgejjj3jev541y` (`deleted`),
  KEY `IDX3tvwfi9ein6ptfl2tlb4a373q` (`created_user`),
  CONSTRAINT `FKfntevcv1jorjk5fnxqb4knkg3` FOREIGN KEY (`files_directory_id`) REFERENCES `files_directory` (`id`),
  CONSTRAINT `FKiq28e5ahmqo1pc8yniixp0r6w` FOREIGN KEY (`file_mime_id`) REFERENCES `file_mime` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3399 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_manager`
--

LOCK TABLES `file_manager` WRITE;
/*!40000 ALTER TABLE `file_manager` DISABLE KEYS */;
/*!40000 ALTER TABLE `file_manager` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_mime`
--

DROP TABLE IF EXISTS `file_mime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file_mime` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_mime`
--

LOCK TABLES `file_mime` WRITE;
/*!40000 ALTER TABLE `file_mime` DISABLE KEYS */;
INSERT INTO `file_mime` VALUES (1,'image/jpeg'),(2,'image/png');
/*!40000 ALTER TABLE `file_mime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `files_directory`
--

DROP TABLE IF EXISTS `files_directory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `files_directory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) DEFAULT NULL,
  `created_user` bigint DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `updated_user` bigint DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `name` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `files_directory_parent` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKaisbmg4sw7vpvjjjrbedfropt` (`files_directory_parent`),
  KEY `IDXmkmxwfmdb7gbn689c2dhges20` (`updated_user`),
  KEY `IDX6auvrj4vq887v2k2xeloqmydk` (`created_user`),
  CONSTRAINT `FKaisbmg4sw7vpvjjjrbedfropt` FOREIGN KEY (`files_directory_parent`) REFERENCES `files_directory` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `files_directory`
--

LOCK TABLES `files_directory` WRITE;
/*!40000 ALTER TABLE `files_directory` DISABLE KEYS */;
/*!40000 ALTER TABLE `files_directory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `files_directory_path`
--

DROP TABLE IF EXISTS `files_directory_path`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `files_directory_path` (
  `files_directory` bigint NOT NULL,
  `files_directory_parent` bigint NOT NULL,
  `level` smallint DEFAULT '0',
  PRIMARY KEY (`files_directory`,`files_directory_parent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `files_directory_path`
--

LOCK TABLES `files_directory_path` WRITE;
/*!40000 ALTER TABLE `files_directory_path` DISABLE KEYS */;
/*!40000 ALTER TABLE `files_directory_path` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_log`
--

DROP TABLE IF EXISTS `login_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `host_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `login_form` int DEFAULT NULL,
  `user` bigint DEFAULT NULL,
  `login_from` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FK2qpnc9vsk5481p4gnc31yg3dy` (`user`),
  CONSTRAINT `FK2qpnc9vsk5481p4gnc31yg3dy` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2796 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_log`
--

LOCK TABLES `login_log` WRITE;
/*!40000 ALTER TABLE `login_log` DISABLE KEYS */;
INSERT INTO `login_log` VALUES (2794,'2023-10-30 11:16:03.423000','bekaku','192.168.7.228',NULL,1,NULL),(2795,'2023-10-30 11:41:16.870000','bekaku','192.168.7.228',NULL,1,NULL);
/*!40000 ALTER TABLE `login_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT '0',
  `created_date` datetime(6) DEFAULT NULL,
  `created_user` bigint DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `updated_user` bigint DEFAULT NULL,
  `code` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `front_end` tinyint(1) DEFAULT '1',
  `operation_type` tinyint DEFAULT '1',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_a7ujv987la0i7a0o91ueevchc` (`code`),
  KEY `IDXlgcidqr19vydctfjnhr4oxujc` (`deleted`),
  KEY `IDXrdg1tdcdctktlit5t43updt8w` (`updated_user`),
  KEY `IDXhckfu87omenl7lbta38y3fid4` (`created_user`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
/*!40000 ALTER TABLE `permission` DISABLE KEYS */;
INSERT INTO `permission` VALUES (1,1,NULL,NULL,NULL,NULL,'api_client_list',0,1,NULL),(2,0,NULL,NULL,NULL,NULL,'api_client_view',0,1,NULL),(3,0,NULL,NULL,NULL,NULL,'api_client_manage',0,1,NULL),(4,0,NULL,NULL,NULL,NULL,'permission_list',0,1,NULL),(5,0,NULL,NULL,NULL,NULL,'permission_view',0,1,NULL),(6,0,NULL,NULL,NULL,NULL,'permission_manage',0,1,NULL),(7,0,NULL,NULL,NULL,NULL,'role_list',0,1,NULL),(8,0,NULL,NULL,NULL,NULL,'role_view',0,1,NULL),(9,0,NULL,NULL,NULL,NULL,'role_manage',0,1,NULL),(10,0,NULL,NULL,NULL,NULL,'user_list',0,1,NULL),(11,0,NULL,NULL,NULL,NULL,'user_view',0,1,NULL),(12,0,NULL,NULL,NULL,NULL,'user_manage',0,1,NULL),(13,0,'2022-08-03 10:28:04.861721',1,'2022-08-03 10:28:04.861721',1,'file_manager_list',0,1,NULL),(14,0,'2022-08-03 10:28:04.878599',1,'2022-08-03 10:28:04.878599',1,'file_manager_view',0,1,NULL),(15,0,'2022-08-03 10:28:04.896150',1,'2022-08-03 10:28:04.896150',1,'file_manager_manage',0,1,NULL),(16,0,NULL,NULL,NULL,NULL,'backend_login',0,1,NULL),(17,0,NULL,NULL,NULL,NULL,'frontend_login',1,1,NULL);
/*!40000 ALTER TABLE `permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT '0',
  `created_date` datetime(6) DEFAULT NULL,
  `created_user` bigint DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `updated_user` bigint DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `front_end` tinyint(1) DEFAULT '0',
  `name` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name_en` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `IDXqwme6oh71j16j9tjout2w96mh` (`deleted`),
  KEY `IDXkwfp7fdwvytjjstut4n0p05wy` (`updated_user`),
  KEY `IDXctbhhagf9li3p92o5trgw8dmr` (`created_user`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,0,'2023-10-30 09:39:41.000000',1,'2023-05-03 15:15:15.946746',1,1,0,'Developer','Developer'),(2,0,'2022-06-08 16:34:57.366829',1,'2022-09-03 11:16:05.800877',1,1,1,'General user','general user');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permission` (
  `role` bigint NOT NULL,
  `permission` bigint NOT NULL,
  PRIMARY KEY (`role`,`permission`),
  KEY `FK8dbhyr3cvowlp4r0cuc578uqn` (`permission`),
  CONSTRAINT `FK8dbhyr3cvowlp4r0cuc578uqn` FOREIGN KEY (`permission`) REFERENCES `permission` (`id`),
  CONSTRAINT `FKcdu069ajq6b1vc22coycmn4eg` FOREIGN KEY (`role`) REFERENCES `role` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE;
/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;
INSERT INTO `role_permission` VALUES (1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17);
/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_activity_logs`
--

DROP TABLE IF EXISTS `system_activity_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_activity_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action_date_time` datetime DEFAULT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3yfu2bqce92uqdthyhuybd58j` (`user_id`),
  CONSTRAINT `FK3yfu2bqce92uqdthyhuybd58j` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_activity_logs`
--

LOCK TABLES `system_activity_logs` WRITE;
/*!40000 ALTER TABLE `system_activity_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_activity_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT '0',
  `created_date` datetime(6) DEFAULT NULL,
  `created_user` bigint DEFAULT NULL,
  `updated_date` datetime(6) DEFAULT NULL,
  `updated_user` bigint DEFAULT NULL,
  `active` tinyint(1) NOT NULL DEFAULT '1',
  `email` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar_file_id` bigint DEFAULT NULL,
  `cover_file_id` bigint DEFAULT NULL,
  `default_locale` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`),
  UNIQUE KEY `UK_sb8bbouer5wak8vyiiy4pf2bx` (`username`),
  KEY `IDXrmdrlv7oa6wsku2viqcwrxbiq` (`active`),
  KEY `FKpha9pjidm0ib64nfhup05bpkm` (`avatar_file_id`),
  KEY `FKhsc0g1xd4fpys9xc0qt77jre3` (`cover_file_id`),
  KEY `IDX428jy249wds8xo1ynmwu5n67x` (`deleted`),
  KEY `IDXihrjwk6rnqxbsmp3t9k8nln2o` (`updated_user`),
  KEY `IDX9gumqk0p8f2qeuy4u6vbdgk50` (`created_user`),
  CONSTRAINT `FKhsc0g1xd4fpys9xc0qt77jre3` FOREIGN KEY (`cover_file_id`) REFERENCES `file_manager` (`id`),
  CONSTRAINT `FKpha9pjidm0ib64nfhup05bpkm` FOREIGN KEY (`avatar_file_id`) REFERENCES `file_manager` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=297 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,0,NULL,NULL,'2023-10-30 08:55:16.524579',1,1,'admin@mydomain.com','$2a$10$2dKQuOzRiw6hj9GHLltShuz3SZ1MIDqfZltkrtJXN0tN54II.d1je','0d1af063-ed5c-4387-91b2-04292799b06c','admin',NULL,NULL,0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_agent`
--

DROP TABLE IF EXISTS `user_agent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_agent` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX8po4lxsgivw9m6ohl7qi37cxs` (`agent`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_agent`
--

LOCK TABLES `user_agent` WRITE;
/*!40000 ALTER TABLE `user_agent` DISABLE KEYS */;
INSERT INTO `user_agent` VALUES (1,'PostmanRuntime/7.34.0');
/*!40000 ALTER TABLE `user_agent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_login_logs`
--

DROP TABLE IF EXISTS `user_login_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_login_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `login_date` datetime NOT NULL,
  `login_from` tinyint(1) DEFAULT '1',
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDXbywlfkna4hg69ek16bl92k26q` (`login_date`),
  KEY `IDX9yerujh0nyt4c1bk128ksccbx` (`login_from`),
  KEY `FKfsjujenb42ykv3u5eacy94rpv` (`user_id`),
  CONSTRAINT `FKfsjujenb42ykv3u5eacy94rpv` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9784 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_login_logs`
--

LOCK TABLES `user_login_logs` WRITE;
/*!40000 ALTER TABLE `user_login_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_login_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
  `user` bigint NOT NULL,
  `role` bigint NOT NULL,
  PRIMARY KEY (`user`,`role`),
  KEY `FK26f1qdx6r8j1ggkgras9nrc1d` (`role`),
  CONSTRAINT `FK26f1qdx6r8j1ggkgras9nrc1d` FOREIGN KEY (`role`) REFERENCES `role` (`id`),
  CONSTRAINT `FKmow7bmkl6wduuutk26ypkgmm1` FOREIGN KEY (`user`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (1,1);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-10-30  4:46:55
