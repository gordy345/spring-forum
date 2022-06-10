-- MySQL dump 10.13  Distrib 8.0.22, for macos10.15 (x86_64)
--
-- Host: 127.0.0.1    Database: forum_dev
-- ------------------------------------------------------
-- Server version	8.0.27

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `comments`
--

# DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `comments`
(
    `id`      bigint NOT NULL AUTO_INCREMENT,
    `version` bigint DEFAULT NULL,
    `text`    longtext,
    `user_id` bigint DEFAULT NULL,
    `post_id` bigint DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK8omq0tc18jd43bu5tjh6jvraq` (`user_id`),
    KEY `FKh4c7lvsc298whoyd4w9ta25cr` (`post_id`),
    CONSTRAINT `FK8omq0tc18jd43bu5tjh6jvraq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
    CONSTRAINT `FKh4c7lvsc298whoyd4w9ta25cr` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `comments`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

# DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `posts`
(
    `id`      bigint NOT NULL AUTO_INCREMENT,
    `version` bigint       DEFAULT NULL,
    `text`    longtext,
    `title`   varchar(255) DEFAULT NULL,
    `user_id` bigint       DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `FK5lidm6cqbc7u4xhqpxm898qme` (`user_id`),
    CONSTRAINT `FK5lidm6cqbc7u4xhqpxm898qme` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `posts`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag_post`
--

# DROP TABLE IF EXISTS `tag_post`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tag_post`
(
    `tag_id`  bigint NOT NULL,
    `post_id` bigint NOT NULL,
    KEY `tag_id` (`tag_id`),
    KEY `post_id` (`post_id`),
    CONSTRAINT `tag_post_ibfk_1` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`),
    CONSTRAINT `tag_post_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag_post`
--

LOCK TABLES `tag_post` WRITE;
/*!40000 ALTER TABLE `tag_post`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `tag_post`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tags`
--

# DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `tags`
(
    `id`      bigint NOT NULL AUTO_INCREMENT,
    `version` bigint       DEFAULT NULL,
    `tag`     varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tags`
--

LOCK TABLES `tags` WRITE;
/*!40000 ALTER TABLE `tags`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `tags`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

# DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `users`
(
    `id`           bigint NOT NULL AUTO_INCREMENT,
    `version`      bigint          DEFAULT NULL,
    `email`        varchar(255)    DEFAULT NULL,
    `gender`       varchar(255)    DEFAULT NULL,
    `is_moderator` bit(1)          DEFAULT NULL,
    `name`         varchar(255)    DEFAULT NULL,
    `phone_number` varchar(255)    DEFAULT NULL,
    `enabled`      bit(1)          DEFAULT NULL,
    `country`      varchar(255)    DEFAULT NULL,
    `language`     varchar(255)    DEFAULT NULL,
    `image_url`    varchar(255)    DEFAULT NULL,
    `password`     varchar(255)    DEFAULT NULL,
    `rating`       bigint NOT NULL DEFAULT '0',
    `name_color`   varchar(255)    DEFAULT 'BLACK',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `users`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `verification_token`
--

# DROP TABLE IF EXISTS `verification_token`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE IF NOT EXISTS `verification_token`
(
    `id`          bigint NOT NULL AUTO_INCREMENT,
    `value`       varchar(255) DEFAULT NULL,
    `version`     bigint       DEFAULT NULL,
    `expiry_date` date         DEFAULT NULL,
    `user_id`     bigint NOT NULL,
    PRIMARY KEY (`id`),
    KEY `FK_UserToken` (`user_id`),
    CONSTRAINT `FK_UserToken` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verification_token`
--

LOCK TABLES `verification_token` WRITE;
/*!40000 ALTER TABLE `verification_token`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `verification_token`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

CREATE USER 'dev_user' IDENTIFIED BY 'dev';

GRANT USAGE ON *.* TO `dev_user`@`%`;
GRANT SELECT, INSERT, UPDATE, DELETE ON `forum_dev`.`comments` TO `dev_user`@`%`;
GRANT SELECT, INSERT, UPDATE, DELETE ON `forum_dev`.`posts` TO `dev_user`@`%`;
GRANT SELECT, INSERT, UPDATE, DELETE ON `forum_dev`.`tag_post` TO `dev_user`@`%`;
GRANT SELECT, INSERT, UPDATE, DELETE ON `forum_dev`.`tags` TO `dev_user`@`%`;
GRANT SELECT, INSERT, UPDATE, DELETE ON `forum_dev`.`users` TO `dev_user`@`%`;
GRANT SELECT, INSERT, UPDATE, DELETE ON `forum_dev`.`verification_token` TO `dev_user`@`%`;


-- Dump completed on 2022-06-09 13:24:43