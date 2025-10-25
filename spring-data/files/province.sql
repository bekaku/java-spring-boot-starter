/*
 Navicat Premium Dump SQL

 Source Server         : postgres-tutorial
 Source Server Type    : PostgreSQL
 Source Server Version : 180000 (180000)
 Source Host           : localhost:5432
 Source Catalog        : spring_starter_postgres
 Source Schema         : public

 Target Server Type    : PostgreSQL
 Target Server Version : 180000 (180000)
 File Encoding         : 65001

 Date: 25/10/2025 15:36:31
*/


-- ----------------------------
-- Table structure for province
-- ----------------------------
DROP TABLE IF EXISTS "public"."province";
CREATE TABLE "public"."province" (
  "id" int8 NOT NULL,
  "deleted" bool,
  "created_date" timestamp(6),
  "created_user" int8,
  "updated_date" timestamp(6),
  "updated_user" int8,
  "name" varchar(255) COLLATE "pg_catalog"."default",
  "name_en" varchar(255) COLLATE "pg_catalog"."default",
  "code" varchar(2) COLLATE "pg_catalog"."default"
)
;

-- ----------------------------
-- Records of province
-- ----------------------------
INSERT INTO "public"."province" VALUES (1, 'f', NULL, NULL, NULL, NULL, 'กรุงเทพมหานคร', 'Bangkok', NULL);
INSERT INTO "public"."province" VALUES (2, 'f', NULL, NULL, NULL, NULL, 'สมุทรปราการ', 'Samut Prakan', NULL);
INSERT INTO "public"."province" VALUES (3, 'f', NULL, NULL, NULL, NULL, 'นนทบุรี', 'Nonthaburi', NULL);
INSERT INTO "public"."province" VALUES (4, 'f', NULL, NULL, NULL, NULL, 'ปทุมธานี', 'Pathum Thani', NULL);
INSERT INTO "public"."province" VALUES (5, 'f', NULL, NULL, NULL, NULL, 'พระนครศรีอยุธยา', 'Phra Nakhon Si Ayutthaya', NULL);
INSERT INTO "public"."province" VALUES (6, 'f', NULL, NULL, NULL, NULL, 'อ่างทอง', 'Ang Thong', NULL);
INSERT INTO "public"."province" VALUES (7, 'f', NULL, NULL, NULL, NULL, 'ลพบุรี', 'Lopburi', NULL);
INSERT INTO "public"."province" VALUES (8, 'f', NULL, NULL, NULL, NULL, 'สิงห์บุรี', 'Sing Buri', NULL);
INSERT INTO "public"."province" VALUES (9, 'f', NULL, NULL, NULL, NULL, 'ชัยนาท', 'Chai Nat', NULL);
INSERT INTO "public"."province" VALUES (10, 'f', NULL, NULL, NULL, NULL, 'สระบุรี', 'Saraburi', NULL);
INSERT INTO "public"."province" VALUES (11, 'f', NULL, NULL, NULL, NULL, 'ชลบุรี', 'Chon Buri', NULL);
INSERT INTO "public"."province" VALUES (12, 'f', NULL, NULL, NULL, NULL, 'ระยอง', 'Rayong', NULL);
INSERT INTO "public"."province" VALUES (13, 'f', NULL, NULL, NULL, NULL, 'จันทบุรี', 'Chanthaburi', NULL);
INSERT INTO "public"."province" VALUES (14, 'f', NULL, NULL, NULL, NULL, 'ตราด', 'Trat', NULL);
INSERT INTO "public"."province" VALUES (15, 'f', NULL, NULL, NULL, NULL, 'ฉะเชิงเทรา', 'Chachoengsao', NULL);
INSERT INTO "public"."province" VALUES (16, 'f', NULL, NULL, NULL, NULL, 'ปราจีนบุรี', 'Prachin Buri', NULL);
INSERT INTO "public"."province" VALUES (17, 'f', NULL, NULL, NULL, NULL, 'นครนายก', 'Nakhon Nayok', NULL);
INSERT INTO "public"."province" VALUES (18, 'f', NULL, NULL, NULL, NULL, 'สระแก้ว', 'Sa Kaeo', NULL);
INSERT INTO "public"."province" VALUES (19, 'f', NULL, NULL, NULL, NULL, 'นครราชสีมา', 'Nakhon Ratchasima', NULL);
INSERT INTO "public"."province" VALUES (20, 'f', NULL, NULL, NULL, NULL, 'บุรีรัมย์', 'Buri Ram', NULL);
INSERT INTO "public"."province" VALUES (21, 'f', NULL, NULL, NULL, NULL, 'สุรินทร์', 'Surin', NULL);
INSERT INTO "public"."province" VALUES (22, 'f', NULL, NULL, NULL, NULL, 'ศรีสะเกษ', 'Si Sa Ket', NULL);
INSERT INTO "public"."province" VALUES (23, 'f', NULL, NULL, NULL, NULL, 'อุบลราชธานี', 'Ubon Ratchathani', NULL);
INSERT INTO "public"."province" VALUES (24, 'f', NULL, NULL, NULL, NULL, 'ยโสธร', 'Yasothon', NULL);
INSERT INTO "public"."province" VALUES (25, 'f', NULL, NULL, NULL, NULL, 'ชัยภูมิ', 'Chaiyaphum', NULL);
INSERT INTO "public"."province" VALUES (26, 'f', NULL, NULL, NULL, NULL, 'อำนาจเจริญ', 'Amnat Charoen', NULL);
INSERT INTO "public"."province" VALUES (27, 'f', NULL, NULL, NULL, NULL, 'หนองบัวลำภู', 'Nong Bua Lam Phu', NULL);
INSERT INTO "public"."province" VALUES (28, 'f', NULL, NULL, NULL, NULL, 'ขอนแก่น', 'Khon Kaen', NULL);
INSERT INTO "public"."province" VALUES (29, 'f', NULL, NULL, NULL, NULL, 'อุดรธานี', 'Udon Thani', NULL);
INSERT INTO "public"."province" VALUES (30, 'f', NULL, NULL, NULL, NULL, 'เลย', 'Loei', NULL);
INSERT INTO "public"."province" VALUES (31, 'f', NULL, NULL, NULL, NULL, 'หนองคาย', 'Nong Khai', NULL);
INSERT INTO "public"."province" VALUES (32, 'f', NULL, NULL, NULL, NULL, 'มหาสารคาม', 'Maha Sarakham', NULL);
INSERT INTO "public"."province" VALUES (33, 'f', NULL, NULL, NULL, NULL, 'ร้อยเอ็ด', 'Roi Et', NULL);
INSERT INTO "public"."province" VALUES (34, 'f', NULL, NULL, NULL, NULL, 'กาฬสินธุ์', 'Kalasin', NULL);
INSERT INTO "public"."province" VALUES (35, 'f', NULL, NULL, NULL, NULL, 'สกลนคร', 'Sakon Nakhon', NULL);
INSERT INTO "public"."province" VALUES (36, 'f', NULL, NULL, NULL, NULL, 'นครพนม', 'Nakhon Phanom', NULL);
INSERT INTO "public"."province" VALUES (37, 'f', NULL, NULL, NULL, NULL, 'มุกดาหาร', 'Mukdahan', NULL);
INSERT INTO "public"."province" VALUES (38, 'f', NULL, NULL, NULL, NULL, 'เชียงใหม่', 'Chiang Mai', NULL);
INSERT INTO "public"."province" VALUES (39, 'f', NULL, NULL, NULL, NULL, 'ลำพูน', 'Lamphun', NULL);
INSERT INTO "public"."province" VALUES (40, 'f', NULL, NULL, NULL, NULL, 'ลำปาง', 'Lampang', NULL);
INSERT INTO "public"."province" VALUES (41, 'f', NULL, NULL, NULL, NULL, 'อุตรดิตถ์', 'Uttaradit', NULL);
INSERT INTO "public"."province" VALUES (42, 'f', NULL, NULL, NULL, NULL, 'แพร่', 'Phrae', NULL);
INSERT INTO "public"."province" VALUES (43, 'f', NULL, NULL, NULL, NULL, 'น่าน', 'Nan', NULL);
INSERT INTO "public"."province" VALUES (44, 'f', NULL, NULL, NULL, NULL, 'พะเยา', 'Phayao', NULL);
INSERT INTO "public"."province" VALUES (45, 'f', NULL, NULL, NULL, NULL, 'เชียงราย', 'Chiang Rai', NULL);
INSERT INTO "public"."province" VALUES (46, 'f', NULL, NULL, NULL, NULL, 'แม่ฮ่องสอน', 'Mae Hong Son', NULL);
INSERT INTO "public"."province" VALUES (47, 'f', NULL, NULL, NULL, NULL, 'นครสวรรค์', 'Nakhon Sawan', NULL);
INSERT INTO "public"."province" VALUES (48, 'f', NULL, NULL, NULL, NULL, 'อุทัยธานี', 'Uthai Thani', NULL);
INSERT INTO "public"."province" VALUES (49, 'f', NULL, NULL, NULL, NULL, 'กำแพงเพชร', 'Kamphaeng Phet', NULL);
INSERT INTO "public"."province" VALUES (50, 'f', NULL, NULL, NULL, NULL, 'ตาก', 'Tak', NULL);
INSERT INTO "public"."province" VALUES (51, 'f', NULL, NULL, NULL, NULL, 'สุโขทัย', 'Sukhothai', NULL);
INSERT INTO "public"."province" VALUES (52, 'f', NULL, NULL, NULL, NULL, 'พิษณุโลก', 'Phitsanulok', NULL);
INSERT INTO "public"."province" VALUES (53, 'f', NULL, NULL, NULL, NULL, 'พิจิตร', 'Phichit', NULL);
INSERT INTO "public"."province" VALUES (54, 'f', NULL, NULL, NULL, NULL, 'เพชรบูรณ์', 'Phetchabun', NULL);
INSERT INTO "public"."province" VALUES (55, 'f', NULL, NULL, NULL, NULL, 'ราชบุรี', 'Ratchaburi', NULL);
INSERT INTO "public"."province" VALUES (56, 'f', NULL, NULL, NULL, NULL, 'กาญจนบุรี', 'Kanchanaburi', NULL);
INSERT INTO "public"."province" VALUES (57, 'f', NULL, NULL, NULL, NULL, 'สุพรรณบุรี', 'Suphan Buri', NULL);
INSERT INTO "public"."province" VALUES (58, 'f', NULL, NULL, NULL, NULL, 'นครปฐม', 'Nakhon Pathom', NULL);
INSERT INTO "public"."province" VALUES (59, 'f', NULL, NULL, NULL, NULL, 'สมุทรสาคร', 'Samut Sakhon', NULL);
INSERT INTO "public"."province" VALUES (60, 'f', NULL, NULL, NULL, NULL, 'สมุทรสงคราม', 'Samut Songkhram', NULL);
INSERT INTO "public"."province" VALUES (61, 'f', NULL, NULL, NULL, NULL, 'เพชรบุรี', 'Phetchaburi', NULL);
INSERT INTO "public"."province" VALUES (62, 'f', NULL, NULL, NULL, NULL, 'ประจวบคีรีขันธ์', 'Prachuap Khiri Khan', NULL);
INSERT INTO "public"."province" VALUES (63, 'f', NULL, NULL, NULL, NULL, 'นครศรีธรรมราช', 'Nakhon Si Thammarat', NULL);
INSERT INTO "public"."province" VALUES (64, 'f', NULL, NULL, NULL, NULL, 'กระบี่', 'Krabi', NULL);
INSERT INTO "public"."province" VALUES (65, 'f', NULL, NULL, NULL, NULL, 'พังงา', 'Phangnga', NULL);
INSERT INTO "public"."province" VALUES (66, 'f', NULL, NULL, NULL, NULL, 'ภูเก็ต', 'Phuket', NULL);
INSERT INTO "public"."province" VALUES (67, 'f', NULL, NULL, NULL, NULL, 'สุราษฎร์ธานี', 'Surat Thani', NULL);
INSERT INTO "public"."province" VALUES (68, 'f', NULL, NULL, NULL, NULL, 'ระนอง', 'Ranong', NULL);
INSERT INTO "public"."province" VALUES (69, 'f', NULL, NULL, NULL, NULL, 'ชุมพร', 'Chumphon', NULL);
INSERT INTO "public"."province" VALUES (70, 'f', NULL, NULL, NULL, NULL, 'สงขลา', 'Songkhla', NULL);
INSERT INTO "public"."province" VALUES (71, 'f', NULL, NULL, NULL, NULL, 'สตูล', 'Satun', NULL);
INSERT INTO "public"."province" VALUES (72, 'f', NULL, NULL, NULL, NULL, 'ตรัง', 'Trang', NULL);
INSERT INTO "public"."province" VALUES (73, 'f', NULL, NULL, NULL, NULL, 'พัทลุง', 'Phatthalung', NULL);
INSERT INTO "public"."province" VALUES (74, 'f', NULL, NULL, NULL, NULL, 'ปัตตานี', 'Pattani', NULL);
INSERT INTO "public"."province" VALUES (75, 'f', NULL, NULL, NULL, NULL, 'ยะลา', 'Yala', NULL);
INSERT INTO "public"."province" VALUES (76, 'f', NULL, NULL, NULL, NULL, 'นราธิวาส', 'Narathiwat', NULL);
INSERT INTO "public"."province" VALUES (77, 'f', NULL, NULL, NULL, NULL, 'บึงกาฬ', 'Bueng Kan', NULL);

-- ----------------------------
-- Primary Key structure for table province
-- ----------------------------
ALTER TABLE "public"."province" ADD CONSTRAINT "province_pkey" PRIMARY KEY ("id");
