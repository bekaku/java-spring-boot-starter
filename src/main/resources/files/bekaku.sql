/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 50539
 Source Host           : localhost:3306
 Source Schema         : springboot

 Target Server Type    : MySQL
 Target Server Version : 50539
 File Encoding         : 65001

 Date: 04/05/2021 16:33:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for access_token
-- ----------------------------
DROP TABLE IF EXISTS `access_token`;
CREATE TABLE `access_token`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NULL DEFAULT NULL,
  `updated_date` datetime NULL DEFAULT NULL,
  `expires_at` datetime NULL DEFAULT NULL,
  `revoked` tinyint(1) NULL DEFAULT 0,
  `service` int(1) NULL DEFAULT 1,
  `token` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `api_client` bigint(20) NULL DEFAULT NULL,
  `login_log` bigint(20) NULL DEFAULT NULL,
  `user` bigint(20) NULL DEFAULT NULL,
  `user_agent` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_1djybee0iap4odfl91gkxoxem`(`token`) USING BTREE,
  INDEX `FK5kmvrg6uuo55il7lx84mimu4f`(`api_client`) USING BTREE,
  INDEX `FK9adhg4bm3rvd167xpgg38aqfs`(`login_log`) USING BTREE,
  INDEX `FKjll8aufysmo6yvf124vsqpd81`(`user`) USING BTREE,
  INDEX `FKsca4jypki6xernocxdhewrlgk`(`user_agent`) USING BTREE,
  CONSTRAINT `FK5kmvrg6uuo55il7lx84mimu4f` FOREIGN KEY (`api_client`) REFERENCES `api_client` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK9adhg4bm3rvd167xpgg38aqfs` FOREIGN KEY (`login_log`) REFERENCES `login_log` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKjll8aufysmo6yvf124vsqpd81` FOREIGN KEY (`user`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKsca4jypki6xernocxdhewrlgk` FOREIGN KEY (`user_agent`) REFERENCES `user_agent` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of access_token
-- ----------------------------
INSERT INTO `access_token` VALUES (1, '2021-04-26 12:01:53', '2021-04-26 12:01:53', '2021-05-26 12:01:53', 0, 1, '2b359d9c-261e-4fc8-89ca-c6b587d2d49f', 1, 1, 1, 1);
INSERT INTO `access_token` VALUES (2, '2021-04-27 09:24:27', '2021-04-27 09:24:27', '2021-05-27 09:24:27', 0, 1, '01bc83fc-0525-404d-ad6f-c304a07da11a', 1, 2, 1, 1);

-- ----------------------------
-- Table structure for api_client
-- ----------------------------
DROP TABLE IF EXISTS `api_client`;
CREATE TABLE `api_client`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `api_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `api_token` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `by_pass` tinyint(1) NULL DEFAULT 0,
  `created_date` datetime NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1,
  `updated_date` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of api_client
-- ----------------------------
INSERT INTO `api_client` VALUES (1, 'default', '41d6c48b-3af8-43fa-a75f-b171d812ba8c', 1, '2021-04-21 10:48:40', 1, '2021-04-21 10:48:43');
INSERT INTO `api_client` VALUES (5, 'edrMobile', 'd46c07e8-4af9-4c35-9ee7-9814233f532a', 1, '2021-05-03 10:56:05', 1, '2021-05-03 11:10:09');

-- ----------------------------
-- Table structure for api_client_ip
-- ----------------------------
DROP TABLE IF EXISTS `api_client_ip`;
CREATE TABLE `api_client_ip`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NULL DEFAULT NULL,
  `updated_date` datetime NULL DEFAULT NULL,
  `ip_address` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1,
  `api_client` bigint(20) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK5pu9gbj8rvr9gdx27uwua7ug9`(`api_client`) USING BTREE,
  CONSTRAINT `FK5pu9gbj8rvr9gdx27uwua7ug9` FOREIGN KEY (`api_client`) REFERENCES `api_client` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of api_client_ip
-- ----------------------------
INSERT INTO `api_client_ip` VALUES (3, '2021-05-03 10:56:05', '2021-05-03 10:56:05', '192.168.7.11', 1, 5);
INSERT INTO `api_client_ip` VALUES (4, '2021-05-03 10:56:05', '2021-05-03 10:56:05', '192.168.7.15', 1, 5);

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_at` datetime NULL DEFAULT NULL,
  `host_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `ip` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `login_form` int(11) NULL DEFAULT NULL,
  `user` bigint(20) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK2qpnc9vsk5481p4gnc31yg3dy`(`user`) USING BTREE,
  CONSTRAINT `FK2qpnc9vsk5481p4gnc31yg3dy` FOREIGN KEY (`user`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of login_log
-- ----------------------------
INSERT INTO `login_log` VALUES (1, '2021-04-26 12:01:53', 'bekaku', '192.168.159.2', 1, 1);
INSERT INTO `login_log` VALUES (2, '2021-04-27 09:24:27', 'bekaku', '192.168.159.2', 1, 1);

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(125) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `module` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (1, 'api_client_list', 'api_client_list', 'AD');
INSERT INTO `permission` VALUES (2, 'api_client_add', 'api_client_list_add', 'AD');
INSERT INTO `permission` VALUES (3, 'api_client_view', 'api_client_view', 'AD');
INSERT INTO `permission` VALUES (4, 'api_client_edit', 'api_client_edit', 'AD');
INSERT INTO `permission` VALUES (5, 'api_client_delete', 'api_client_delete', 'AD');
INSERT INTO `permission` VALUES (6, 'permission_list', 'permission list', 'AD');
INSERT INTO `permission` VALUES (7, 'permission_add', 'permission add', 'AD');
INSERT INTO `permission` VALUES (8, 'permission_view', 'permission view', 'AD');
INSERT INTO `permission` VALUES (9, 'permission_edit', 'permission edit', 'AD');
INSERT INTO `permission` VALUES (10, 'permission_delete', 'permission delete', 'AD');
INSERT INTO `permission` VALUES (11, 'role_list', 'role list', 'AD');
INSERT INTO `permission` VALUES (12, 'role_add', 'role add', 'AD');
INSERT INTO `permission` VALUES (13, 'role_view', 'role view', 'AD');
INSERT INTO `permission` VALUES (14, 'role_edit', 'role edit', 'AD');
INSERT INTO `permission` VALUES (15, 'role_delete', 'role delete', 'AD');
INSERT INTO `permission` VALUES (16, 'user_list', 'user list', 'AD');
INSERT INTO `permission` VALUES (17, 'user_add', 'user add', 'AD');
INSERT INTO `permission` VALUES (18, 'user_view', 'user view', 'AD');
INSERT INTO `permission` VALUES (19, 'user_edit', 'user edit', 'AD');
INSERT INTO `permission` VALUES (20, 'user_delete', 'user delete', 'AD');

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NULL DEFAULT NULL,
  `updated_date` datetime NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `status` tinyint(1) NULL DEFAULT 1,
  `expired_at` date NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_8sewwnpamngi6b1dwaa88askk`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of role
-- ----------------------------
INSERT INTO `role` VALUES (1, '2021-04-28 13:15:25', '2021-04-28 13:31:18', 'developer', 'develop', 1, NULL);

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `role` bigint(20) NOT NULL,
  `permission` bigint(20) NOT NULL,
  PRIMARY KEY (`role`, `permission`) USING BTREE,
  INDEX `FK8dbhyr3cvowlp4r0cuc578uqn`(`permission`) USING BTREE,
  CONSTRAINT `FK8dbhyr3cvowlp4r0cuc578uqn` FOREIGN KEY (`permission`) REFERENCES `permission` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKcdu069ajq6b1vc22coycmn4eg` FOREIGN KEY (`role`) REFERENCES `role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (1, 1);
INSERT INTO `role_permission` VALUES (1, 2);
INSERT INTO `role_permission` VALUES (1, 3);
INSERT INTO `role_permission` VALUES (1, 4);
INSERT INTO `role_permission` VALUES (1, 5);
INSERT INTO `role_permission` VALUES (1, 6);
INSERT INTO `role_permission` VALUES (1, 7);
INSERT INTO `role_permission` VALUES (1, 8);
INSERT INTO `role_permission` VALUES (1, 9);
INSERT INTO `role_permission` VALUES (1, 10);
INSERT INTO `role_permission` VALUES (1, 11);
INSERT INTO `role_permission` VALUES (1, 12);
INSERT INTO `role_permission` VALUES (1, 13);
INSERT INTO `role_permission` VALUES (1, 14);
INSERT INTO `role_permission` VALUES (1, 15);
INSERT INTO `role_permission` VALUES (1, 16);
INSERT INTO `role_permission` VALUES (1, 17);
INSERT INTO `role_permission` VALUES (1, 18);
INSERT INTO `role_permission` VALUES (1, 19);
INSERT INTO `role_permission` VALUES (1, 20);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NULL DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `image` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `salt` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1,
  `updated_date` datetime NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = COMPACT;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '2021-04-22 18:10:20', 'admin@mydomain.com', 'https://static.productionready.io/images/smiley-cyrus.jpg', '$2a$10$oqntqVRd2GtCS8s3S5e23OPn4stBaOA//MwgWBYlNnj9hB2JyHxGS', 'c1878158-e0bb-424e-bba6-b052a1577ada', 1, '2021-04-22 18:10:20', 'admin');

-- ----------------------------
-- Table structure for user_agent
-- ----------------------------
DROP TABLE IF EXISTS `user_agent`;
CREATE TABLE `user_agent`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `agent` varchar(120) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user_agent
-- ----------------------------
INSERT INTO `user_agent` VALUES (1, 'PostmanRuntime/7.26.10');

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `user` bigint(20) NOT NULL,
  `role` bigint(20) NOT NULL,
  PRIMARY KEY (`user`, `role`) USING BTREE,
  INDEX `FK26f1qdx6r8j1ggkgras9nrc1d`(`role`) USING BTREE,
  CONSTRAINT `FK26f1qdx6r8j1ggkgras9nrc1d` FOREIGN KEY (`role`) REFERENCES `role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKmow7bmkl6wduuutk26ypkgmm1` FOREIGN KEY (`user`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Records of user_role
-- ----------------------------
INSERT INTO `user_role` VALUES (1, 1);

SET FOREIGN_KEY_CHECKS = 1;
