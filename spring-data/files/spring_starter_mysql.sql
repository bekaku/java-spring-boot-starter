/*
 Navicat Premium Data Transfer

 Source Server         : localhost-docker-mysql8
 Source Server Type    : MySQL
 Source Server Version : 80300
 Source Host           : 127.0.0.1:3308
 Source Schema         : spring_starter

 Target Server Type    : MySQL
 Target Server Version : 80300
 File Encoding         : 65001

 Date: 26/08/2025 17:40:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for access_token
-- ----------------------------
DROP TABLE IF EXISTS `access_token`;
CREATE TABLE `access_token`  (
  `id` bigint NOT NULL,
  `created_date` datetime(6) NULL DEFAULT NULL,
  `expires_at` datetime(6) NULL DEFAULT NULL,
  `fcm_enable` bit(1) NULL DEFAULT NULL,
  `fcm_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `lastest_active` datetime(6) NULL DEFAULT NULL,
  `logouted_date` datetime(6) NULL DEFAULT NULL,
  `revoked` bit(1) NOT NULL,
  `service` tinyint NOT NULL,
  `token` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `api_client` bigint NULL DEFAULT NULL,
  `app_user` bigint NULL DEFAULT NULL,
  `login_log` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK1djybee0iap4odfl91gkxoxem`(`token`) USING BTREE,
  UNIQUE INDEX `UKt05x1jr0mk2n2se3ogxi1rt59`(`login_log`) USING BTREE,
  INDEX `IDXkf29lomp4g8kwqr49239nfpjo`(`revoked`) USING BTREE,
  INDEX `IDX7i22j43748d8cnciffepm0jk8`(`fcm_enable`) USING BTREE,
  INDEX `IDXi8vvu91hco9k5ymwafnff27jo`(`fcm_token`) USING BTREE,
  INDEX `IDXs2vq59h0rbe4abafu72vay7bl`(`lastest_active`) USING BTREE,
  INDEX `FK5kmvrg6uuo55il7lx84mimu4f`(`api_client`) USING BTREE,
  INDEX `FKa5o1n8cul4rf2wihkmh6agkwi`(`app_user`) USING BTREE,
  CONSTRAINT `FK5kmvrg6uuo55il7lx84mimu4f` FOREIGN KEY (`api_client`) REFERENCES `api_client` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK9adhg4bm3rvd167xpgg38aqfs` FOREIGN KEY (`login_log`) REFERENCES `login_log` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKa5o1n8cul4rf2wihkmh6agkwi` FOREIGN KEY (`app_user`) REFERENCES `app_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of access_token
-- ----------------------------
INSERT INTO `access_token` VALUES (350947664067563520, '2025-08-26 17:20:43.335700', '2025-09-02 17:20:43.330000', b'1', NULL, '2025-08-26 17:39:43.846767', NULL, b'0', 1, '0198e5e4-f20c-733b-8845-c499587620c3', 350921408848597000, 350921080799498240, 350947663711047680);

-- ----------------------------
-- Table structure for api_client
-- ----------------------------
DROP TABLE IF EXISTS `api_client`;
CREATE TABLE `api_client`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) NULL DEFAULT NULL,
  `created_user` bigint NULL DEFAULT NULL,
  `updated_date` datetime(6) NULL DEFAULT NULL,
  `updated_user` bigint NULL DEFAULT NULL,
  `api_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `api_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `by_pass` tinyint(1) NULL DEFAULT 0,
  `status` tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK_qi9faagnfpyh5wky24ma2hbr6`(`api_token`) USING BTREE,
  INDEX `IDXohkk0fataetw36doj0cbn6wf3`(`updated_user`) USING BTREE,
  INDEX `IDXe851maef0ogkl5s4g3l56u4ff`(`created_user`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of api_client
-- ----------------------------
INSERT INTO `api_client` VALUES (350921408848597000, '2025-08-26 16:39:10.000000', 350921408848597000, '2025-08-26 15:36:46.000000', 350921408848597000, 'default', '0198e501-1193-7ac8-80d4-70faab88f9bb', 1, 1);

-- ----------------------------
-- Table structure for api_client_ip
-- ----------------------------
DROP TABLE IF EXISTS `api_client_ip`;
CREATE TABLE `api_client_ip`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) NULL DEFAULT NULL,
  `created_user` bigint NULL DEFAULT NULL,
  `updated_date` datetime(6) NULL DEFAULT NULL,
  `updated_user` bigint NULL DEFAULT NULL,
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1,
  `api_client` bigint NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK5pu9gbj8rvr9gdx27uwua7ug9`(`api_client`) USING BTREE,
  INDEX `IDXnm0gxcgi9ue456vnjcriy6tks`(`updated_user`) USING BTREE,
  INDEX `IDX9b2hqhmteavbi90n9d839p2b5`(`created_user`) USING BTREE,
  CONSTRAINT `FK5pu9gbj8rvr9gdx27uwua7ug9` FOREIGN KEY (`api_client`) REFERENCES `api_client` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of api_client_ip
-- ----------------------------

-- ----------------------------
-- Table structure for app_role
-- ----------------------------
DROP TABLE IF EXISTS `app_role`;
CREATE TABLE `app_role`  (
  `id` bigint NOT NULL,
  `deleted` bit(1) NULL DEFAULT NULL,
  `created_date` datetime(6) NULL DEFAULT NULL,
  `created_user` bigint NULL DEFAULT NULL,
  `updated_date` datetime(6) NULL DEFAULT NULL,
  `updated_user` bigint NULL DEFAULT NULL,
  `active` bit(1) NULL DEFAULT NULL,
  `name` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `IDXrmhyitswekfi6kp10q7stq5ac`(`deleted`) USING BTREE,
  INDEX `IDXgrvp22cs4h9terj94b281fll5`(`updated_user`) USING BTREE,
  INDEX `IDXd93y6baq0w10c9de2kd8fp7bf`(`created_user`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_role
-- ----------------------------
INSERT INTO `app_role` VALUES (350888314967953409, b'0', '2025-08-26 15:46:43.000000', 350921080799498240, '2025-08-26 15:47:19.000000', 350921080799498240, b'1', 'Developer');

-- ----------------------------
-- Table structure for app_user
-- ----------------------------
DROP TABLE IF EXISTS `app_user`;
CREATE TABLE `app_user`  (
  `id` bigint NOT NULL,
  `deleted` bit(1) NULL DEFAULT NULL,
  `created_date` datetime(6) NULL DEFAULT NULL,
  `created_user` bigint NULL DEFAULT NULL,
  `updated_date` datetime(6) NULL DEFAULT NULL,
  `updated_user` bigint NULL DEFAULT NULL,
  `active` bit(1) NOT NULL,
  `default_locale` tinyint NULL DEFAULT NULL,
  `email` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `avatar_file_id` bigint NULL DEFAULT NULL,
  `cover_file_id` bigint NULL DEFAULT NULL,
  `salt` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UK1j9d9a06i600gd43uu3km82jw`(`email`) USING BTREE,
  UNIQUE INDEX `UK3k4cplvh82srueuttfkwnylq0`(`username`) USING BTREE,
  INDEX `IDXago4re6d8ldeib4w1ceru2mwy`(`active`) USING BTREE,
  INDEX `IDXo7jki9ikxwuc4m0542gnhj4dq`(`deleted`) USING BTREE,
  INDEX `IDXlpwkjxebftjm60wu46pck58p4`(`updated_user`) USING BTREE,
  INDEX `IDX7dnv5tcu6inbpsg2biiwky9ih`(`created_user`) USING BTREE,
  INDEX `FKk6uvdlrab91uwu4lfsw9hndcd`(`avatar_file_id`) USING BTREE,
  INDEX `FKf6hgftbo89mgus3gpsy29wj99`(`cover_file_id`) USING BTREE,
  CONSTRAINT `FKf6hgftbo89mgus3gpsy29wj99` FOREIGN KEY (`cover_file_id`) REFERENCES `file_manager` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKk6uvdlrab91uwu4lfsw9hndcd` FOREIGN KEY (`avatar_file_id`) REFERENCES `file_manager` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_user
-- ----------------------------
INSERT INTO `app_user` VALUES (350921080799498240, b'0', '2025-08-26 15:41:06.000000', 350921080799498240, '2025-08-26 15:41:10.000000', 350921080799498240, b'1', 0, 'admin@mydomain.com', '$2a$10$2dKQuOzRiw6hj9GHLltShuz3SZ1MIDqfZltkrtJXN0tN54II.d1je', 'admin', NULL, NULL, '0198e5cb-ff5f-77a1-a9d5-be35d5c197b6');

-- ----------------------------
-- Table structure for app_user_role
-- ----------------------------
DROP TABLE IF EXISTS `app_user_role`;
CREATE TABLE `app_user_role`  (
  `app_user` bigint NOT NULL,
  `app_role` bigint NOT NULL,
  PRIMARY KEY (`app_user`, `app_role`) USING BTREE,
  INDEX `FKcprhx6mpypdwshju5p7pi971y`(`app_role`) USING BTREE,
  CONSTRAINT `FK3xcgg4e44bx37j6oa7p1lfgp8` FOREIGN KEY (`app_user`) REFERENCES `app_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKcprhx6mpypdwshju5p7pi971y` FOREIGN KEY (`app_role`) REFERENCES `app_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of app_user_role
-- ----------------------------
INSERT INTO `app_user_role` VALUES (350921080799498240, 350888314967953409);

-- ----------------------------
-- Table structure for audit_log
-- ----------------------------
DROP TABLE IF EXISTS `audit_log`;
CREATE TABLE `audit_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `details` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `entity_id` bigint NULL DEFAULT NULL,
  `entity_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ip_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `timestamp` datetime(6) NULL DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 348750657739558912 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of audit_log
-- ----------------------------

-- ----------------------------
-- Table structure for file_manager
-- ----------------------------
DROP TABLE IF EXISTS `file_manager`;
CREATE TABLE `file_manager`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NULL DEFAULT 0,
  `created_date` datetime(6) NULL DEFAULT NULL,
  `created_user` bigint NULL DEFAULT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `file_size` bigint NULL DEFAULT NULL,
  `hidden` tinyint(1) NULL DEFAULT 0,
  `locked` tinyint(1) NULL DEFAULT 0,
  `original_file_name` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `readable` tinyint(1) NULL DEFAULT 1,
  `writeable` tinyint(1) NULL DEFAULT 1,
  `file_mime_id` bigint NULL DEFAULT NULL,
  `files_directory_id` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKiq28e5ahmqo1pc8yniixp0r6w`(`file_mime_id`) USING BTREE,
  INDEX `FKfntevcv1jorjk5fnxqb4knkg3`(`files_directory_id`) USING BTREE,
  INDEX `IDXlic6tl97u7idgejjj3jev541y`(`deleted`) USING BTREE,
  INDEX `IDX3tvwfi9ein6ptfl2tlb4a373q`(`created_user`) USING BTREE,
  CONSTRAINT `FKfntevcv1jorjk5fnxqb4knkg3` FOREIGN KEY (`files_directory_id`) REFERENCES `files_directory` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKiq28e5ahmqo1pc8yniixp0r6w` FOREIGN KEY (`file_mime_id`) REFERENCES `file_mime` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 3456 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_manager
-- ----------------------------

-- ----------------------------
-- Table structure for file_mime
-- ----------------------------
DROP TABLE IF EXISTS `file_mime`;
CREATE TABLE `file_mime`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_mime
-- ----------------------------

-- ----------------------------
-- Table structure for files_directory
-- ----------------------------
DROP TABLE IF EXISTS `files_directory`;
CREATE TABLE `files_directory`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_date` datetime(6) NULL DEFAULT NULL,
  `created_user` bigint NULL DEFAULT NULL,
  `updated_date` datetime(6) NULL DEFAULT NULL,
  `updated_user` bigint NULL DEFAULT NULL,
  `active` tinyint(1) NULL DEFAULT 1,
  `name` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `files_directory_parent` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FKaisbmg4sw7vpvjjjrbedfropt`(`files_directory_parent`) USING BTREE,
  INDEX `IDXmkmxwfmdb7gbn689c2dhges20`(`updated_user`) USING BTREE,
  INDEX `IDX6auvrj4vq887v2k2xeloqmydk`(`created_user`) USING BTREE,
  CONSTRAINT `FKaisbmg4sw7vpvjjjrbedfropt` FOREIGN KEY (`files_directory_parent`) REFERENCES `files_directory` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of files_directory
-- ----------------------------

-- ----------------------------
-- Table structure for files_directory_path
-- ----------------------------
DROP TABLE IF EXISTS `files_directory_path`;
CREATE TABLE `files_directory_path`  (
  `files_directory` bigint NOT NULL,
  `files_directory_parent` bigint NOT NULL,
  `level` int NOT NULL,
  PRIMARY KEY (`files_directory`, `files_directory_parent`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of files_directory_path
-- ----------------------------

-- ----------------------------
-- Table structure for login_log
-- ----------------------------
DROP TABLE IF EXISTS `login_log`;
CREATE TABLE `login_log`  (
  `id` bigint NOT NULL,
  `created_at` datetime(6) NULL DEFAULT NULL,
  `device_id` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `host_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `login_from` tinyint NULL DEFAULT NULL,
  `app_user` bigint NULL DEFAULT NULL,
  `user_agent` bigint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `IDX1g886n9ijc3v1kn2ja05c61gx`(`device_id`) USING BTREE,
  INDEX `FKidqrwi0ocgnexw3vyu9d8gk7n`(`app_user`) USING BTREE,
  INDEX `FKqegw0bjfp1kh6o349sbls6qm3`(`user_agent`) USING BTREE,
  CONSTRAINT `FKidqrwi0ocgnexw3vyu9d8gk7n` FOREIGN KEY (`app_user`) REFERENCES `app_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKqegw0bjfp1kh6o349sbls6qm3` FOREIGN KEY (`user_agent`) REFERENCES `user_agent` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of login_log
-- ----------------------------
INSERT INTO `login_log` VALUES (350947663711047680, '2025-08-26 17:20:43.583000', '59fa4d75-6237-4564-a3d8-bf4cf670805d', 'bekaku', '192.168.7.39', 0, 350921080799498240, 350947663622967296);

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` bigint NOT NULL,
  `code` varchar(125) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `module` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `operation_type` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `UKa7ujv987la0i7a0o91ueevchc`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permission
-- ----------------------------
INSERT INTO `permission` VALUES (350897401642356736, 'api_client_list', NULL, NULL, 0);
INSERT INTO `permission` VALUES (350897732065431552, 'api_client_view', NULL, NULL, 0);
INSERT INTO `permission` VALUES (350898678438825984, 'api_client_manage', NULL, NULL, 0);
INSERT INTO `permission` VALUES (350898898232938496, 'permission_list', NULL, NULL, 0);
INSERT INTO `permission` VALUES (350898930604576768, 'permission_view', NULL, NULL, 0);
INSERT INTO `permission` VALUES (350898947750891521, 'permission_manage', NULL, NULL, 0);
INSERT INTO `permission` VALUES (350898969737433089, 'app_role_list', NULL, NULL, 0);
INSERT INTO `permission` VALUES (350898990360825856, 'app_role_view', NULL, NULL, 0);
INSERT INTO `permission` VALUES (350899010636091393, 'app_role_manage', NULL, NULL, 0);
INSERT INTO `permission` VALUES (350899032308060160, 'app_user_list', NULL, NULL, 0);
INSERT INTO `permission` VALUES (350899050880438273, 'app_user_view', NULL, NULL, 0);
INSERT INTO `permission` VALUES (350899073227689985, 'app_user_manage', NULL, NULL, 0);
INSERT INTO `permission` VALUES (350945166250479600, 'login', NULL, NULL, 0);

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `app_role` bigint NOT NULL,
  `permission` bigint NOT NULL,
  PRIMARY KEY (`app_role`, `permission`) USING BTREE,
  INDEX `FK8dbhyr3cvowlp4r0cuc578uqn`(`permission`) USING BTREE,
  CONSTRAINT `FK8dbhyr3cvowlp4r0cuc578uqn` FOREIGN KEY (`permission`) REFERENCES `permission` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FKc13ryj6yfrhcvdak6k4fngtyf` FOREIGN KEY (`app_role`) REFERENCES `app_role` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permission
-- ----------------------------
INSERT INTO `role_permission` VALUES (350888314967953409, 350897401642356736);
INSERT INTO `role_permission` VALUES (350888314967953409, 350897732065431552);
INSERT INTO `role_permission` VALUES (350888314967953409, 350898678438825984);
INSERT INTO `role_permission` VALUES (350888314967953409, 350898898232938496);
INSERT INTO `role_permission` VALUES (350888314967953409, 350898930604576768);
INSERT INTO `role_permission` VALUES (350888314967953409, 350898947750891521);
INSERT INTO `role_permission` VALUES (350888314967953409, 350898969737433089);
INSERT INTO `role_permission` VALUES (350888314967953409, 350898990360825856);
INSERT INTO `role_permission` VALUES (350888314967953409, 350899010636091393);
INSERT INTO `role_permission` VALUES (350888314967953409, 350899032308060160);
INSERT INTO `role_permission` VALUES (350888314967953409, 350899050880438273);
INSERT INTO `role_permission` VALUES (350888314967953409, 350899073227689985);
INSERT INTO `role_permission` VALUES (350888314967953409, 350945166250479600);

-- ----------------------------
-- Table structure for user_agent
-- ----------------------------
DROP TABLE IF EXISTS `user_agent`;
CREATE TABLE `user_agent`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `IDX8po4lxsgivw9m6ohl7qi37cxs`(`agent`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_agent
-- ----------------------------
INSERT INTO `user_agent` VALUES (350947663622967296, 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36');

SET FOREIGN_KEY_CHECKS = 1;
