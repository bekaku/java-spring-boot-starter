package com.grandats.api.givedeefive.controller.dev;

import com.grandats.api.givedeefive.configuration.I18n;
import com.grandats.api.givedeefive.configuration.MetadataExtractorIntegrator;
import com.grandats.api.givedeefive.controller.api.BaseApiController;
import com.grandats.api.givedeefive.annotation.GenSourceableTable;
import com.grandats.api.givedeefive.model.ApiClient;
import com.grandats.api.givedeefive.model.Permission;
import com.grandats.api.givedeefive.model.Role;
import com.grandats.api.givedeefive.service.ApiClientService;
import com.grandats.api.givedeefive.service.PermissionService;
import com.grandats.api.givedeefive.service.RoleService;
import com.grandats.api.givedeefive.util.AppUtil;
import com.grandats.api.givedeefive.util.ConstantData;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.Metadata;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Iterator;

@RequestMapping(path = "/dev/development")
@RestController
@RequiredArgsConstructor
public class DevelopmentContoller extends BaseApiController {
    private final I18n i18n;
    private final PermissionService permissonService;
    private final ApiClientService apiClientService;
    private final RoleService roleService;

    Logger logger = LoggerFactory.getLogger(DevelopmentContoller.class);

    @Value("${environments.production}")
    boolean isProduction;

    @RequestMapping(value = "/migrateData", method = RequestMethod.POST)
    public ResponseEntity<Object> migrateData() {
        if (!isProduction) {
            logger.info("migrateData");
//            migrateDefaultPermission();
//            migrateApiClient();
//            migrateDevRole();
            return this.responseEntity(apiClientService.findAll(), HttpStatus.OK);
        }

        return this.responseEntity(HttpStatus.OK);
    }

    private void migrateDefaultPermission() {
        /*
        INSERT INTO `permission`  (`code`, front_end) VALUES ('api_client_list',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('api_client_view',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('api_client_manage',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('permission_list',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('permission_view',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('permission_manage',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('role_list',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('role_view',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('role_manage',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('user_list',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('user_view',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('user_manage',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('backend_login',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('graph_config_list',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('graph_config_view',false);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('graph_config_manage',false);

        // frontend
        INSERT INTO `permission`  (`code`, front_end) VALUES ('frontend_login',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_theme_owner_list',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_theme_owner_view',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_theme_owner_manage',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('front_company_view',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('front_company_manage',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_role_list',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_role_view',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_role_manage',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_user_list',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_user_view',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_user_manage',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_notice_list',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_notice_view',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_notice_manage',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_announcement_list',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_announcement_view',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_announcement_manage',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_organization_list',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_organization_view',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('company_organization_manage',true);

        INSERT INTO `permission`  (`code`, front_end) VALUES ('reward_shop_list',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('reward_shop_view',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('reward_shop_manage',true);
        INSERT INTO `permission`  (`code`, front_end) VALUES ('reward_deliver',true);


        INSERT INTO `role`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `front_end`, `name`, `name_en`, `company_id`) VALUES (1, 0, NULL, NULL, '2022-06-02 16:16:08.386441', 1, 1, 0, 'Developer', 'Developer', NULL);
        INSERT INTO `role`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `front_end`, `name`, `name_en`, `company_id`) VALUES (2, 0, '2022-06-08 16:34:57.366829', 1, '2022-06-08 16:34:57.366829', 1, 1, 1, 'general user', 'general user', NULL);
        INSERT INTO `role`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `front_end`, `name`, `name_en`, `company_id`) VALUES (3, 0, '2022-09-03 11:16:46.830155', 1, '2022-09-03 11:16:46.830155', 1, 1, 1, 'Organization admin', 'Organization admin', NULL);

        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 1);
        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 2);
        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 3);
        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 4);
        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 5);
        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 6);
        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 7);
        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 8);
        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 9);
        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 10);
        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 11);
        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 12);
        INSERT INTO `role_permission`(`role`, `permission`) VALUES (1, 13);

        INSERT INTO `user`(`id`, `created_date`, `created_user`, `updated_date`, `updated_user`, `email`, `avatar_file_id`, `password`, `salt`, `active`, `username`) VALUES (1, NULL, NULL, NULL, NULL, 'admin@mydomain.com', NULL, '$2a$10$VvK6.mMEdt9sdEjIFE.g8uN4I33dbV9luiFkhGV773wPIBHLEamhe', '0d1af063-ed5c-4387-91b2-04292799b06c', 1, 'admin');
        INSERT INTO `user_role`(`user`, `role`) VALUES (1, 1);
        INSERT INTO `api_client`(`id`, `created_date`, `created_user`, `updated_date`, `updated_user`, `api_name`, `api_token`, `by_pass`, `status`) VALUES (1, '2022-04-19 16:39:10', NULL, '2022-04-19 16:39:10', NULL, 'default', '10b7b78c-6544-4a04-9839-8f8c10d9445e', 1, 1);


        INSERT INTO `system_configuration` VALUES (1, 0, '2022-08-03 11:37:51.553358', 1, 1.000, 1.000, 1.000, 1.000, 1.000, 1, 1.000, 1.000, 1.000, 1, 1.000, 1.000, 1.000, 1, 1.000, 1.000, 2, 1, 2, '1.1.0', '1.0.13', 15, 0, 1, 1.000, 1.000, 1.500);

        INSERT INTO `de_formula` VALUES (1, 0, '2022-08-03 11:40:33.841454', 1, '2023-03-29 09:42:37.120059', 1, 'DE_1', 'DE-1', 'ค่าความนิยมของตนเอง โดยคำนวณจาก D-get / E-own', 0, NULL, 3, 0, 0);
        INSERT INTO `de_formula` VALUES (2, 0, '2022-08-03 11:41:08.062573', 1, '2023-03-29 09:43:17.962197', 1, 'DE_2', 'DE-2', 'ค่าความมีส่วนร่วมของตนเอง โดยคำนวณจาก D-give / (E-รวมทั้งทีม – E-own)', 0, NULL, 4, 0, 0);
        INSERT INTO `de_formula` VALUES (3, 0, '2022-08-03 11:41:31.910997', 1, '2023-03-29 09:44:40.470123', 1, 'D_GIVE', 'D-GIVE', 'คำนวณค่า D ที่ให้คนอื่น', 1, NULL, 3, 1, 2);
        INSERT INTO `de_formula` VALUES (4, 0, '2022-08-03 11:41:44.902581', 1, '2023-03-29 09:44:40.482798', 1, 'D_GET', 'D-GET', 'คำนวณค่า D ที่ได้รับจากคนอื่น', 1, NULL, 2, 1, 3);
        INSERT INTO `de_formula` VALUES (5, 0, '2022-08-03 11:41:54.257178', 1, '2023-03-29 09:44:40.449138', 1, 'E_OWN', 'E', 'คำนวณค่า E ของตนเอง', 1, NULL, 0, 1, 1);
        INSERT INTO `de_formula` VALUES (7, 0, '2023-03-29 09:38:00.266813', 1, '2023-03-29 09:44:13.026037', 1, 'POST', 'Post', 'เก็บคะแนนโพสต์เพื่อเอาไว้คำนวณค่าอื่นๆ เท่านั้น', 1, NULL, 1, 0, 0);
        INSERT INTO `de_formula` VALUES (8, 0, '2023-03-29 09:40:19.062446', 1, '2023-03-29 09:44:40.512382', 1, 'D_LEARN', 'D_LEARN', 'เป็นตัวแปร ที่เราเข้าไปแสดงตนว่า ได้เรียนรู้จากโพสของผู้อื่น (L1=กดปุ่มได้เรียนรู้ L2=กดปุ่มได้เรียนรู้ พร้อมแสดงวิธีการเรียน', 1, NULL, 4, 1, 4);
        INSERT INTO `de_formula` VALUES (9, 0, '2023-03-29 09:40:49.047600', 1, '2023-03-29 09:44:40.582237', 1, 'D_ACT', 'D_ACT', 'เป็นตัวแปร ที่เราได้ไปสั่งการหรือนำไปใช้งาน และการสรุปข้อมูลในหน้าโพส', 1, NULL, 5, 1, 5);
        INSERT INTO `de_formula` VALUES (10, 0, '2023-03-29 09:41:01.614932', 1, '2023-03-29 09:44:40.638504', 1, 'D_GET_P', 'D_GET_P', 'คะแนน D-Index ต่อคะแนนโพส', 1, NULL, 6, 1, 6);
        INSERT INTO `de_formula` VALUES (11, 0, '2023-03-29 09:41:11.887660', 1, '2023-03-29 09:44:40.673163', 1, 'D_GIVE_P', 'D_GIVE_P', 'คะแนน D-Index ต่อคะแนนโพส', 1, NULL, 7, 1, 7);
        INSERT INTO `de_formula` VALUES (12, 0, '2023-03-29 09:41:21.449604', 1, '2023-03-29 09:44:40.685587', 1, 'D_LEARN_P', 'D_LEARN_P', 'คะแนน D-Index ต่อคะแนนโพส', 1, NULL, 8, 1, 8);
        INSERT INTO `de_formula` VALUES (13, 0, '2023-03-29 09:41:32.980990', 1, '2023-03-29 09:44:40.706848', 1, 'D_ACT_P', 'D_ACT_P', 'คะแนน D-Index ต่อคะแนนโพส', 1, NULL, 9, 1, 9);
        INSERT INTO `de_formula` VALUES (14, 0, '2023-03-29 09:41:42.784266', 1, '2023-03-29 09:44:40.785810', 1, 'D_GET_H', 'D_GET_H', 'คะแนน D-Index ต่อจำนวนคน', 1, NULL, 10, 1, 10);
        INSERT INTO `de_formula` VALUES (15, 0, '2023-03-29 09:41:52.580213', 1, '2023-03-29 09:44:40.799187', 1, 'D_GIVE_H', 'D_GIVE_H', 'คะแนน D-Index ต่อจำนวนคน', 1, NULL, 11, 1, 11);
        INSERT INTO `de_formula` VALUES (16, 0, '2023-03-29 09:42:02.438135', 1, '2023-03-29 09:44:40.812562', 1, 'D_LEARN_H', 'D_LEARN_H', 'คะแนน D-Index ต่อจำนวนคน', 1, NULL, 12, 1, 12);
        INSERT INTO `de_formula` VALUES (17, 0, '2023-03-29 09:42:12.874733', 1, '2023-03-29 09:44:41.036988', 1, 'D_ACT_H', 'D_ACT_H', 'คะแนน D-Index ต่อจำนวนคน', 1, NULL, 13, 1, 13);


        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (1, 0, '2022-08-03 11:46:00.580204', 1, '2022-08-03 11:46:00.580204', 1, 1, 'ธุรกิจการเกษตร ', 'Agribusiness');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (2, 0, '2022-08-03 11:46:12.736977', 1, '2022-08-03 11:46:12.736977', 1, 1, 'อาหารและเครื่องดื่ม', 'Food & Beverage');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (3, 0, '2022-08-03 11:46:22.285980', 1, '2022-08-03 11:46:22.285980', 1, 1, 'แฟชั่น ', 'Fashion');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (4, 0, '2022-08-03 11:46:34.656183', 1, '2022-08-03 11:46:34.656183', 1, 1, 'ของใช้ในครัวเรือนและสำนักงาน', 'Home & Office Products');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (5, 0, '2022-08-03 11:46:46.500385', 1, '2022-08-03 11:46:46.500385', 1, 1, 'ของใช้ส่วนตัวและเวชภัณฑ์', 'Personal Products & Pharmaceuticals');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (6, 0, '2022-08-03 11:46:55.741782', 1, '2022-08-03 11:46:55.741782', 1, 1, 'ธนาคาร', 'Banking');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (7, 0, '2022-08-03 11:47:07.376204', 1, '2022-08-03 11:47:07.376204', 1, 1, 'เงินทุนและหลักทรัพย์', 'Finance & Securities');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (8, 0, '2022-08-03 11:47:17.876588', 1, '2022-08-03 11:47:17.876588', 1, 1, 'ประกันภัยและประกันชีวิต', 'Insurance');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (9, 0, '2022-08-03 11:47:28.812506', 1, '2022-08-03 11:47:28.812506', 1, 1, 'ยานยนต์', 'Automotive');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (10, 0, '2022-08-03 11:47:40.098632', 1, '2022-08-03 11:47:40.098632', 1, 1, 'วัสดุอุตสาหกรรมและเครื่องจักร', 'Industrial Materials & Machine');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (11, 0, '2022-08-03 11:47:50.803602', 1, '2022-08-03 11:47:50.803602', 1, 1, 'บรรจุภัณฑ์', 'Packaging');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (12, 0, '2022-08-03 11:48:02.197796', 1, '2022-08-03 11:48:02.197796', 1, 1, 'กระดาษและวัสดุการพิมพ์', 'Paper & Printing Materials');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (13, 0, '2022-08-03 11:48:14.084197', 1, '2022-08-03 11:48:14.084197', 1, 1, 'ปิโตรเคมีและเคมีภัณฑ์', 'Petrochemicals & Chemicals');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (14, 0, '2022-08-03 11:48:26.485295', 1, '2022-08-03 11:48:26.485295', 1, 1, 'เหล็ก และ ผลิตภัณฑ์โลหะ', 'Steel and Metal Products');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (15, 0, '2022-08-03 11:48:39.245558', 1, '2022-08-03 11:48:39.245558', 1, 1, 'วัสดุก่อสร้าง', 'Construction Materials');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (16, 0, '2022-08-03 11:48:50.285858', 1, '2022-08-03 11:48:50.285858', 1, 1, 'บริการรับเหมาก่อสร้าง', 'Construction Services');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (17, 0, '2022-08-03 11:49:02.965310', 1, '2022-08-03 11:49:02.965310', 1, 1, 'พัฒนาอสังหาริมทรัพย์', 'Property Development');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (18, 0, '2022-08-03 11:49:29.136459', 1, '2022-08-03 11:49:29.136459', 1, 1, 'กองทุนรวมอสังหาริม ทรัพย์และกองทรัสต์เพื่อการลงทุนในอสังหาริมทรัพย์', 'roperty Fund & Real Estate Investment Trusts');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (19, 0, '2022-08-03 11:49:44.150787', 1, '2022-08-03 11:49:44.150787', 1, 1, 'พลังงานและสาธารณูปโภค', 'Energy & Utilities');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (20, 0, '2022-08-03 11:49:53.349443', 1, '2022-08-03 11:49:53.349443', 1, 1, 'เหมืองแร่', 'Mining');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (21, 0, '2022-08-03 11:50:02.501037', 1, '2022-08-03 11:50:02.501037', 1, 1, 'พาณิชย์', 'Commerce');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (22, 0, '2022-08-03 11:50:16.588919', 1, '2022-08-03 11:50:16.588919', 1, 1, 'การแพทย์', 'Health Care Services');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (23, 0, '2022-08-03 11:50:27.725738', 1, '2022-08-03 11:50:27.725738', 1, 1, 'สื่อและสิ่งพิมพ์', 'Media & Publishing');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (24, 0, '2022-08-03 11:50:38.216671', 1, '2022-08-03 11:50:38.216671', 1, 1, 'บริการเฉพาะกิจ', 'Professional Services');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (25, 0, '2022-08-03 11:50:49.939513', 1, '2022-08-03 11:50:49.939513', 1, 1, 'การท่องเที่ยวและสันทนาการ ', 'Tourisms & Leisure');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (26, 0, '2022-08-03 11:51:00.658094', 1, '2022-08-03 11:51:00.658094', 1, 1, 'ขนส่งและโลจิสติกส์', 'Transportation & Logistics');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (27, 0, '2022-08-03 11:51:12.947540', 1, '2022-08-03 11:51:12.947540', 1, 1, 'ชิ้นส่วนอิเล็กทรอนิกส์', 'Electronic Components');
        INSERT INTO `company_type`(`id`, `deleted`, `created_date`, `created_user`, `updated_date`, `updated_user`, `active`, `name`, `name_en`) VALUES (28, 0, '2022-08-03 11:51:24.405444', 1, '2022-08-03 11:51:24.405444', 1, 1, 'เทคโนโลยีสารสนเทศและการสื่อสาร', 'Information & Communication Technology');



        Index
        alter table files_directory_path add INDEX k_files_directory (files_directory);
        alter table files_directory_path add INDEX k_files_directory_parent (files_directory_parent);

        -- CREATE INDEX IDXpostRecommend ON form_answer(recommend(255));
        -- https://database.guide/how-the-match-function-works-in-mysql/
        -- SELECT * FROM table WHERE MATCH(col1, col2,col3,col4, col5) AGAINST ('abc');
        -- ALTER TABLE tab ADD FULLTEXT ft_index_name (col1,col2,col3,col4,col5);

        ALTER TABLE form_answer ADD FULLTEXT IDXFTrecommend(recommend);
        ALTER TABLE post_data ADD FULLTEXT IDXFTpost_content(post_content);
        ALTER TABLE post_data ADD FULLTEXT IDXFTpost_cause(post_cause);
        ALTER TABLE post_data ADD FULLTEXT IDXFTpost_solution(post_solution);
         */
        permissonService.saveAll(Arrays.asList(
                new Permission("api_client_list", false),
                new Permission("api_client_view", false),
                new Permission("api_client_manage", false),
                new Permission("permission_list", false),
                new Permission("permission_view", false),
                new Permission("permission_manage", false),
                new Permission("role_list", false),
                new Permission("role_view", false),
                new Permission("role_manage", false),
                new Permission("user_list", false),
                new Permission("user_view", false),
                new Permission("user_manage", false)
        ));
    }

    private void migrateApiClient() {

        apiClientService.save(new ApiClient("default", true, true));
    }

    private void migrateDevRole() {
        Role role = new Role("developer", "developer", true, false);
        role.getPermissions().addAll(permissonService.findAll());
        roleService.save(role);
    }

    @RequestMapping(value = "/generateSrcV2", method = RequestMethod.GET)
    public ResponseEntity<Object> generateSrcMethodGet() {
        generateProcess();
        return this.responseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/generateSrc", method = RequestMethod.POST)
    public ResponseEntity<Object> generateSrc() {
        generateProcess();
        return this.responseEntity(HttpStatus.OK);
    }

    private void generateProcess() {
        logger.warn("Production : {}", isProduction);
        if (isProduction) {
            throw this.responseError(HttpStatus.UNAUTHORIZED, null, i18n.getMessage("error.productionModeDetect"));
        }

        /*
        for (Namespace namespace : MetadataExtractorIntegrator.INSTANCE
                .getDatabase()
                .getNamespaces()) {

            for (Table table : namespace.getTables()) {
                logger.info("Table {} has the following columns: {}",
                        table,
                        StreamSupport.stream(
                                Spliterators.spliteratorUnknownSize(
                                        table.getColumnIterator(),
                                        Spliterator.ORDERED
                                ),
                                false
                        )
                                .collect(Collectors.toList())
                );
            }
        }
        */

        Metadata metadata = MetadataExtractorIntegrator.INSTANCE.getMetadata();
        String className;
        String packageClassName;
        for (PersistentClass persistentClass : metadata.getEntityBindings()) {
            className = AppUtil.getSimpleClassName(persistentClass.getClassName());
            packageClassName = persistentClass.getClassName();
            Table table = persistentClass.getTable();

            logger.info("capitalizeFirstLetter {} => {}", className, AppUtil.capitalizeFirstLetter(className, true));

            System.out.println("--------------------------");
            logger.info("Entity: {} is mapped to table: {}",
                    packageClassName,
                    table.getName()
            );
            Class<?> aClass = getClassFromName(packageClassName);
            if (aClass != null) {
                GenSourceableTable genSourceableTable = aClass.getAnnotation(GenSourceableTable.class);
                if (genSourceableTable != null) {

                    if (genSourceableTable.createDto()) {
                        logger.error("  createDto : {} ", className);
                        generateDto(className, persistentClass);
                    }
                    if (genSourceableTable.createRepository()) {
                        generateRepository(className);
                    }
                    if (genSourceableTable.createService()) {
                        generateService(className, genSourceableTable.createDto());
                    }
                    if (genSourceableTable.createServiceImpl()) {
                        generateServiceImpl(className, genSourceableTable.createDto());
                    }
                    if (genSourceableTable.createController()) {
                        generateController(className, genSourceableTable.createDto());
                    }
                    if (genSourceableTable.createMapper()) {
                        logger.error("  createMapper : {} ", className);
                    }
                    if (genSourceableTable.createValidator()) {
                        logger.error("  createValidator : {} ", className);
                    }
                    if (genSourceableTable.createPermission()) {
                        if (permissonService.findByCode(table.getName() + "_list").isEmpty()) {
                            permissonService.save(new Permission(table.getName() + "_list", false));
                        }
                        if (permissonService.findByCode(table.getName() + "_view").isEmpty()) {
                            permissonService.save(new Permission(table.getName() + "_view", false));
                        }
                        if (permissonService.findByCode(table.getName() + "_manage").isEmpty()) {
                            permissonService.save(new Permission(table.getName() + "_manage", false));
                        }
//                        permissonService.saveAll(Arrays.asList(
//                                new Permission(table.getName()+"_list", table.getName()+"_list", table.getName()+"_list", false),
//                                new Permission(table.getName()+"_view", table.getName()+"_view", table.getName()+"_view", false),
//                                new Permission(table.getName()+"_manage", table.getName()+"_manage", table.getName()+"_manage", false)
//                        ));
                    }
                }
            } else {
                logger.error("  className : {} NULL", className);
            }
//            for (Iterator propertyIterator = persistentClass.getPropertyIterator();
//                 propertyIterator.hasNext(); ) {
//                Property property = (Property) propertyIterator.next();
//
//                for (Iterator columnIterator = property.getColumnIterator();
//                     columnIterator.hasNext(); ) {
//                    Column column = (Column) columnIterator.next();
//
//                    logger.info("Property: {} is mapped on table column: {} of type: {} uniqe : {}, length : {}, propertyTypeName : {} , isNullable :{}",
//                            property.getName(),
//                            column.getName(),
//                            column.getSqlType(),
//                            column.isUnique(),
//                            column.getLength(),
//                            property.getType().getName(),
//                            column.isNullable()
//                    );
//                }
//            }
        }


//        TableSerializable tableSerializable = LoginLog.class.getAnnotation(TableSerializable.class);
//        logger.info("CreateController : {}", tableSerializable.createController());

//
//        for (Field field : LoginLog.class.getAnnotation(TableSerializable.class)) {
//            javax.persistence.Column column = field.getAnnotation(javax.persistence.Column.class);
//            if (column != null) {
//                System.out.println("Columns: " + column);
//            }
//        }

    }

    private void generateDto(String entityName, PersistentClass persistentClass) {
        //package com.grandats.api.givedeefive.controller.api
        String fileName = entityName + "Dto";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto." + fileName;

        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/dto/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto").append(";\n");
                writer.append("\n");
                writer.append("import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\n");
                writer.append("import com.fasterxml.jackson.annotation.JsonRootName;\n");
                writer.append("import lombok.Data;\n");
                writer.append("import lombok.experimental.Accessors;\n");
                writer.append("\n");
                writer.append("@Data\n");
                writer.append("@JsonRootName(\"").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("\")\n");
//                writer.append("//@AllArgsConstructor\n");
//                writer.append("//@NoArgsConstructor\n");
                writer.append("@JsonIgnoreProperties(ignoreUnknown = true)\n");
                writer.append("@Accessors(chain = true)\n");
                writer.append("public class ").append(fileName).append("{\n");
                writer.append("    private Long id;\n");

                for (Iterator propertyIterator = persistentClass.getPropertyIterator();
                     propertyIterator.hasNext(); ) {
                    Property property = (Property) propertyIterator.next();

                    for (Iterator columnIterator = property.getColumnIterator();
                         columnIterator.hasNext(); ) {
                        Column column = (Column) columnIterator.next();

//                        logger.info("Property: {} is mapped on table column: {} of type: {} uniqe : {}, length : {}, propertyTypeName : {} , isNullable :{}",
//                                property.getName(),
//                                column.getName(),
//                                column.getSqlType(),
//                                column.isUnique(),
//                                column.getLength(),
//                                property.getType().getName(),
//                                column.isNullable()
//                        );
                        if (!property.getName().equals("createdDate") && !property.getName().equals("createdUser") && !property.getName().equals("updatedDate") && !property.getName().equals("updatedUser")) {
                            writer.append("    //private ").append(property.getType().getName()).append(" ").append(property.getName()).append(";\n");
                        }
                    }
                }
                writer.append("}\n");
                writer.close();
                logger.info("Created Class : {} ", className);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void generateRepository(String entityName) {
        //package io.beka.repository
        String fileName = entityName + "Repository";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".repository." + fileName;

        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/repository/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".repository").append(";\n");
                writer.append("\n");
                writer.append("import org.springframework.stereotype.Repository;\n");
                writer.append("import org.springframework.data.jpa.repository.JpaSpecificationExecutor;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".model.").append(entityName).append(";\n");
                writer.append("\n");
                writer.append("@Repository\n");
                writer.append("public interface ").append(fileName).append(" extends BaseRepository<").append(entityName).append(",Long>, JpaSpecificationExecutor<").append(entityName).append("> {\n");
                writer.append("}\n");
                writer.close();
                logger.info("Created Class : {} ", className);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void generateService(String entityName, boolean haveDto) {
        //package io.beka.service
        String fileName = entityName + "Service";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".service." + fileName;
        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/service/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".service").append(";\n");
                if (haveDto) {
                    writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.").append(entityName).append("Dto;\n");
                }
                writer.append("\n");
                writer.append("import com.grandats.api.givedeefive.model.").append(entityName).append(";\n");
                writer.append("\n");
                writer.append("public interface ").append(entityName).append("Service extends BaseService<").append(entityName).append(", ").append(haveDto ? entityName + "Dto" : entityName).append("> {\n");
                writer.append("}\n");
                writer.close();
                logger.info("Created Class : {} ", className);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void generateServiceImpl(String entityName, boolean haveDto) {
        //package com.grandats.api.givedeefive.serviceImpl
        String fileName = entityName + "ServiceImpl";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".serviceImpl." + fileName;

        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/serviceImpl/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".serviceImpl").append(";\n");
                writer.append("\n");
//                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".vo.Paging;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.ResponseListDto;\n");
                if (haveDto) {
                    writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.").append(entityName).append("Dto;\n");
                }
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".model.").append(entityName).append(";\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".repository.").append(entityName).append("Repository;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".service.").append(entityName).append("Service;\n");
//                writer.append("import lombok.AllArgsConstructor;\n");
//                writer.append("import lombok.RequiredArgsConstructor;\n");
                writer.append("import org.modelmapper.ModelMapper;\n");
                writer.append("import org.springframework.data.domain.Pageable;\n");
                writer.append("import org.springframework.data.domain.Page;\n");
                writer.append("import com.grandats.api.givedeefive.specification.SearchSpecification;\n");
                writer.append("import org.springframework.data.jpa.domain.Specification;\n");
//                writer.append("import org.springframework.data.domain.PageRequest;\n");
//                writer.append("import org.springframework.data.domain.Sort;\n");
                writer.append("import org.springframework.stereotype.Service;\n");
                writer.append("import org.springframework.transaction.annotation.Transactional;\n");
                writer.append("import org.springframework.beans.factory.annotation.Autowired;\n");
                writer.append("\n");
                writer.append("import java.util.List;\n");
                writer.append("import java.util.Optional;\n");
                writer.append("import java.util.stream.Collectors;\n");

                writer.append("\n");
                writer.append("@Transactional\n");
//                writer.append("@RequiredArgsConstructor\n");
                writer.append("@Service\n");
                writer.append("public class ").append(entityName).append("ServiceImpl implements ").append(entityName).append("Service {\n");
                writer.append("    @Autowired\n");
                writer.append("    private ").append(entityName).append("Repository ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository;\n");
                writer.append("    @Autowired\n");
                writer.append("    private ModelMapper modelMapper;\n");
                //findAllWithPaging
                writer.append("\n");
                writer.append("    @Transactional(readOnly = true)\n");
                writer.append("    @Override\n");
                writer.append("    public ResponseListDto<").append(haveDto ? entityName + "Dto" : entityName).append("> findAllWithPaging(Pageable pageable) {\n");
                writer.append("        Page<").append(entityName).append("> result = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.findAll(pageable);\n");
                writer.append("        return getListFromResult(result);\n");
                writer.append("    }\n");
                //findAllWithSearch
                writer.append("\n");
                writer.append("    @Transactional(readOnly = true)\n");
                writer.append("    @Override\n");
                writer.append("    public ResponseListDto<").append(haveDto ? entityName + "Dto" : entityName).append("> findAllWithSearch(SearchSpecification<").append(entityName).append("> specification, Pageable pageable) {\n");
                writer.append("        return getListFromResult(findAllPageSearchSpecificationBy(specification, pageable));\n");
                writer.append("    }\n");
                //findAllBy Specification
                writer.append("\n");
                writer.append("    @Transactional(readOnly = true)\n");
                writer.append("    @Override\n");
                writer.append("    public ResponseListDto<").append(haveDto ? entityName + "Dto" : entityName).append("> findAllBy(Specification<").append(entityName).append("> specification, Pageable pageable) {\n");
                writer.append("        return getListFromResult(findAllPageSpecificationBy(specification, pageable));\n");
                writer.append("    }\n");
                //findAllPageSpecificationBy
                writer.append("\n");
                writer.append("    @Transactional(readOnly = true)\n");
                writer.append("    @Override\n");
                writer.append("    public Page<").append(entityName).append("> findAllPageSpecificationBy(Specification<").append(entityName).append("> specification, Pageable pageable) {\n");
                writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.findAll(specification, pageable);\n");
                writer.append("    }\n");
                //findAllPageSearchSpecificationBy
                writer.append("\n");
                writer.append("    @Transactional(readOnly = true)\n");
                writer.append("    @Override\n");
                writer.append("    public Page<").append(entityName).append("> findAllPageSearchSpecificationBy(SearchSpecification<").append(entityName).append("> specification, Pageable pageable) {\n");
                writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.findAll(specification, pageable);\n");
                writer.append("    }\n");

                //getListFromResult
                writer.append("    private ResponseListDto<").append(haveDto ? entityName + "Dto" : entityName).append("> getListFromResult(Page<").append(entityName).append("> result) {\n");
                writer.append("        return new ResponseListDto<>(result.getContent()\n");
                if (haveDto) {
                    writer.append("                .stream()\n");
                    writer.append("                .map(this::convertEntityToDto)\n");
                    writer.append("                .collect(Collectors.toList())\n");
                }
                writer.append("                , result.getTotalPages(), result.getNumberOfElements(), result.isLast());\n");
                writer.append("    }\n");
                //findAll
                writer.append("\n");
                writer.append("    @Transactional(readOnly = true)\n");
                writer.append("    @Override\n");
                writer.append("    public List<").append(entityName).append("> findAll() {\n");
                writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.findAll();\n");
                writer.append("    }\n");
                writer.append("\n");
                //save
                writer.append("\n");
                writer.append("    public ").append(entityName).append(" save(").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.save(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                writer.append("    }\n");
                //update
                writer.append("\n");
                writer.append("    @Override\n");
                writer.append("    public ").append(entityName).append(" update(").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.save(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                writer.append("    }\n");
                //findById
                writer.append("\n");
                writer.append("    @Transactional(readOnly = true)\n");
                writer.append("    @Override\n");
                writer.append("    public Optional<").append(entityName).append("> findById(Long id) {\n");
                writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.findById(id);\n");
                writer.append("    }\n");
                //delete
                writer.append("\n");
                writer.append("    @Override\n");
                writer.append("    public void delete(").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                writer.append("        ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.delete(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                writer.append("    }\n");

                writer.append("\n");
                writer.append("    @Override\n");
                writer.append("    public void deleteById(Long id) {\n");
                writer.append("        ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository.deleteById(id);\n");
                writer.append("    }\n");

                writer.append("\n");
                writer.append("    @Override\n");
                writer.append("    public ").append(haveDto ? entityName + "Dto" : entityName).append(" convertEntityToDto(").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                if (haveDto) {
                    writer.append("        return modelMapper.map(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(", ").append(entityName).append("Dto.class);\n");
                } else {
                    writer.append("return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(";\n");
                }
                writer.append("    }\n");


                writer.append("\n");
                writer.append("    @Override\n");
                writer.append("    public ").append(entityName).append(" convertDtoToEntity(").append(haveDto ? entityName + "Dto " + AppUtil.capitalizeFirstLetter(entityName, true) + "Dto" : entityName + " " + AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                if (haveDto) {
                    writer.append("        return modelMapper.map(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Dto, ").append(entityName).append(".class);\n");
                } else {
                    writer.append("return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(";\n");
                }
                writer.append("    }\n");
                writer.append("\n");
                writer.append("}\n");
                writer.close();
                logger.info("Created Class : {} ", className);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    private void generateController(String entityName, boolean haveDto) {
        //package com.grandats.api.givedeefive.controller.api
        String fileName = entityName + "Controller";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".controller.api." + fileName;

        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/controller/api/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".controller.api").append(";\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".configuration.I18n;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.ResponseListDto;\n");
//                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".vo.Paging;\n");
                if (haveDto) {
                    writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.").append(entityName).append("Dto;\n");
                }

                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".model.").append(entityName).append(";\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".service.").append(entityName).append("Service;\n");
//                writer.append("import lombok.RequiredArgsConstructor;\n");
                writer.append("import org.slf4j.Logger;\n");
                writer.append("import org.slf4j.LoggerFactory;\n");
//                writer.append("import jakarta.servlet.http.HttpServletRequest;\n");
                writer.append("import com.grandats.api.givedeefive.specification.SearchSpecification;\n");
                writer.append("import org.springframework.beans.factory.annotation.Autowired;\n");
                writer.append("import org.springframework.http.HttpStatus;\n");
                writer.append("import org.springframework.http.ResponseEntity;\n");
                writer.append("import org.springframework.web.bind.annotation.*;\n");
                writer.append("import org.springframework.security.access.prepost.PreAuthorize;\n");
//                writer.append("import org.springframework.data.domain.PageRequest;\n");
                writer.append("import org.springframework.data.domain.Pageable;\n");
                writer.append("\n");
                writer.append("import jakarta.validation.Valid;\n");
                writer.append("import java.util.Optional;\n");
                writer.append("\n");
                writer.append("@RequestMapping(path = \"/api/").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("\")\n");
                writer.append("@RestController\n");
//                writer.append("@RequiredArgsConstructor\n");
                writer.append("public class ").append(fileName).append(" extends BaseApiController{\n");
                writer.append("\n");
                writer.append("    @Autowired\n");
                writer.append("    private ").append(entityName).append("Service ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service;\n");
                writer.append("    @Autowired\n");
                writer.append("    private I18n i18n;\n");
                writer.append(" //   Logger logger = LoggerFactory.getLogger(").append(entityName).append("Controller.class);\n");
                writer.append("\n");
                //findall
                writer.append("    @PreAuthorize(\"isHasPermission('").append(AppUtil.camelToSnake(entityName)).append("_list')\")\n");
                writer.append("    @GetMapping\n");

//                writer.append("    public ResponseEntity<Object> findAll(@RequestParam(value = \"page\", defaultValue = \"0\") int page,\n");
//                writer.append("                                          @RequestParam(value = \"limit\", defaultValue = \"20\") int limit) {\n");
//                writer.append("    public ResponseEntity<Object> findAll(Pageable pageable) {\n");
                if (haveDto) {
                    writer.append("    public ResponseListDto<").append(entityName).append("Dto").append("> findAll(Pageable pageable) {\n");
                }else{
                    writer.append("    public ResponseListDto<").append(entityName).append("> findAll(Pageable pageable) {\n");
                }
//                writer.append("        return this.responseEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findAllWithPaging(new Paging(page, limit), ").append(entityName).append(".getSort()), HttpStatus.OK);\n");
//                writer.append("        return this.responseEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findAllWithPaging(!pageable.getSort().isEmpty() ? pageable :\n");
//                writer.append("                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), ").append(entityName).append(".getSort())), HttpStatus.OK);\n");
                writer.append("        SearchSpecification<").append(entityName).append("> specification = new SearchSpecification<>(getSearchCriteriaList());    \n");
//                writer.append("        return this.responseEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findAllWithSearch(specification, getPageable(pageable, ").append(entityName).append(".getSort())), HttpStatus.OK);\n");
                writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findAllWithSearch(specification, getPageable(pageable, ").append(entityName).append(".getSort()));\n");
                writer.append("    }\n");
                //create
                writer.append("\n");
                writer.append("    @PreAuthorize(\"isHasPermission('").append(AppUtil.camelToSnake(entityName)).append("_manage')\")\n");
                writer.append("    @PostMapping\n");
                if (haveDto) {
//                    writer.append("    public ResponseEntity<Object> create(@Valid @RequestBody ").append(entityName).append("Dto dto) {\n");
                    writer.append("    public ").append(entityName).append(" create(@Valid @RequestBody ").append(entityName).append("Dto dto) {\n");
                } else {
                    writer.append("    public ").append(entityName).append(" create(@Valid @RequestBody ").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                }
                if (haveDto) {
                    writer.append("        ").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(" = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertDtoToEntity(dto);\n");
                }
                //validator
//                writer.append("        Optional<Role> roleExist = roleService.findByName(dto.getName());\n");
//                writer.append("        if (roleExist.isPresent()) {\n");
//                writer.append("            throw this.responseError(HttpStatus.BAD_REQUEST, null, i18n.getMessage(\"error.validateDuplicate\", dto.getName()));\n");
//                writer.append("        }\n");

                writer.append("        ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.save(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
//                writer.append("        return this.responseEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(", HttpStatus.OK);\n");
                writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(";\n");
                writer.append("    }\n");
                //update
                writer.append("\n");
                writer.append("    @PreAuthorize(\"isHasPermission('").append(AppUtil.camelToSnake(entityName)).append("_manage')\")\n");
                writer.append("    @PutMapping\n");
                if (haveDto) {
                    writer.append("    public ResponseEntity<Object> update(@Valid @RequestBody ").append(entityName).append("Dto dto) {\n");
                } else {
                    writer.append("    public ResponseEntity<Object> update(@Valid @RequestBody ").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                }
                if (haveDto) {
                    writer.append("        ").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(" = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertDtoToEntity(dto);\n");
                    writer.append("        Optional<").append(entityName).append("> oldData = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findById(dto.getId());\n");
                } else {
                    writer.append("        Optional<").append(entityName).append("> oldData = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findById(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(".getId());\n");
                }

                writer.append("        if (oldData.isEmpty()) {\n");
                writer.append("            throw this.responseErrorNotfound();\n");
                writer.append("        }\n");
                writer.append("        ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.update(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                if (haveDto) {
                    writer.append("        return this.responseEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertEntityToDto(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("), HttpStatus.OK);\n");
                } else {
//                    writer.append("        return this.responseEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(", HttpStatus.OK);\n");
                    writer.append("        return this.reponseUpdatedMessage();\n");
                }

                writer.append("    }\n");

                //findOne
                writer.append("\n");
                writer.append("    @PreAuthorize(\"isHasPermission('").append(AppUtil.camelToSnake(entityName)).append("_view')\")\n");
                writer.append("    @GetMapping(\"/{id}\")\n");
                if (haveDto) {
                    writer.append("    public ").append(entityName).append("Dto").append(" findOne(@PathVariable(\"id\") Long id) {\n");
                }else{
                    writer.append("    public ").append(entityName).append(" findOne(@PathVariable(\"id\") Long id) {\n");
                }
                writer.append("        Optional<").append(entityName).append("> ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(" = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findById(id);\n");
                writer.append("        if (").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(".isEmpty()) {\n");
                writer.append("            throw this.responseErrorNotfound();\n");
                writer.append("        }\n");
                if (haveDto) {
//                    writer.append("        return this.responseEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertEntityToDto(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(".get()), HttpStatus.OK);\n");
                    writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertEntityToDto(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(".get());\n");
                } else {
//                    writer.append("        return this.responseEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(".get(), HttpStatus.OK);\n");
                    writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(".get();\n");
                }
                writer.append("    }\n");
                //delete
                writer.append("\n");
                writer.append("    @PreAuthorize(\"isHasPermission('").append(AppUtil.camelToSnake(entityName)).append("_manage')\")\n");
                writer.append("    @DeleteMapping(\"/{id}\")\n");
                writer.append("    public ResponseEntity<Object> delete(@PathVariable(\"id\") Long id) {\n");
                writer.append("        Optional<").append(entityName).append("> ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(" = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findById(id);\n");
                writer.append("        if (").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(".isEmpty()) {\n");
                writer.append("            throw this.responseErrorNotfound();\n");
                writer.append("        }\n");
//                writer.append("        ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.deleteById(id);\n");
                writer.append("        ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.delete(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(".get());\n");
                writer.append("        return this.reponseDeleteMessage();\n");
                writer.append("    }\n");
                writer.append("}\n");
                writer.close();
                logger.info("Created Class : {} ", className);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }


    private Class<?> getClassFromName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
