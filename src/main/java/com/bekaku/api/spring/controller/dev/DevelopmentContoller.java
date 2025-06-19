package com.bekaku.api.spring.controller.dev;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.configuration.MetadataExtractorIntegrator;
import com.bekaku.api.spring.controller.api.BaseApiController;
import com.bekaku.api.spring.annotation.GenSourceableTable;
import com.bekaku.api.spring.enumtype.DevFrontendTheme;
import com.bekaku.api.spring.model.ApiClient;
import com.bekaku.api.spring.model.Permission;
import com.bekaku.api.spring.model.Role;
import com.bekaku.api.spring.service.ApiClientService;
import com.bekaku.api.spring.service.PermissionService;
import com.bekaku.api.spring.service.RoleService;
import com.bekaku.api.spring.util.AppUtil;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.FileUtil;
import com.bekaku.api.spring.vo.GenerateTableSrcItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@Slf4j
@RequestMapping(path = "/dev/development")
@RestController
@RequiredArgsConstructor
public class DevelopmentContoller extends BaseApiController {
    private final I18n i18n;
    private final PermissionService permissonService;
    private final ApiClientService apiClientService;
    private final RoleService roleService;


    @Value("${environments.production}")
    boolean isProduction;

    @Value("${app.front-end.theme}")
    String frontendTheme;

    private List<GenerateTableSrcItem> propertyList;
    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_STRING = "string";
    private static final String TYPE_TEXT = "text";
    private static final String DOT = ".";
    private static final String TYPE_LOCAL_DATETIME = "LocalDateTime";
    private static final String TYPE_LOCAL_DATE = "LocalDate";
    private static final String TYPE_BIG_DECIMAL = "big_decimal";
    private static final String TYPE_FLOAT = "float";
    private static final String TYPE_INTEGER = "integer";
    private static final String TYPESCRIPT_STRING = "string";
    private static final String TYPESCRIPT_NUMBER = "number";
    private static final String TYPESCRIPT_BOOLEAN = "boolean";
    private static final String TYPESCRIPT_UNDEFINED = "undefined";
    private static final String TYPESCRIPT_NULL = "null";
    private static final String TYPESCRIPT_OR_SIGN = "|";

    @RequestMapping(value = "/migrateData", method = RequestMethod.POST)
    public ResponseEntity<Object> migrateData() {
        if (!isProduction) {
            log.info("migrateData");
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
        generateProcess(DevFrontendTheme.QUASAR);
        return this.responseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/generateSrc", method = RequestMethod.POST)
    public ResponseEntity<Object> generateSrc() {
        DevFrontendTheme theme = DevFrontendTheme.QUASAR;
        if (frontendTheme != null) {
            theme = DevFrontendTheme.valueOf(frontendTheme);
        }
        generateProcess(theme);
        return this.responseEntity(HttpStatus.OK);
    }

    private void setPropertyList(Table table, PersistentClass persistentClass) {
        propertyList = new ArrayList<>();
        for (Iterator propertyIterator = persistentClass.getProperties().listIterator();
             propertyIterator.hasNext(); ) {
            Property property = (Property) propertyIterator.next();
            for (Iterator columnIterator = property.getColumns().iterator();
                 columnIterator.hasNext(); ) {
                Column column = (Column) columnIterator.next();
                propertyList.add(GenerateTableSrcItem.builder()
                        .tableName(null)
                        .propertyName(property.getName())
                        .sqlName(column.getName())
                        .sqlType(column.getSqlType())
                        .propertyType(property.getType().getName())
                        .unique(column.isUnique())
                        .length(column.getLength())
                        .nullable(column.isNullable())
                        .typeTextArea(isTypeTextArea(column.getSqlType(), property.getType().getName()))
                        .build());
//                log.info("Property: {} is mapped on table column: {} of type: {} uniqe : {}, length : {}, propertyTypeName : {} , isNullable :{}",
//                        property.getName(),
//                        column.getName(),
//                        column.getSqlType(),
//                        column.isUnique(),
//                        column.getLength(),
//                        property.getType().getName(),
//                        column.isNullable()
//                );
            }
        }
    }

    private void generateProcess(DevFrontendTheme theme) {
        log.warn("Production : {}, theme: {}", isProduction, theme);
        if (isProduction) {
            throw this.responseError(HttpStatus.UNAUTHORIZED, null, i18n.getMessage("error.productionModeDetect"));
        }

        /*
        for (Namespace namespace : MetadataExtractorIntegrator.INSTANCE
                .getDatabase()
                .getNamespaces()) {

            for (Table table : namespace.getTables()) {
                log.info("Table {} has the following columns: {}",
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
            setPropertyList(table, persistentClass);
            log.info("--------------------------");
            log.info("capitalizeFirstLetter {} => {}", className, AppUtil.capitalizeFirstLetter(className, true));
            log.info("-Entity: {} is mapped to table: {}", packageClassName, table.getName());
            Class<?> aClass = getClassFromName(packageClassName);
            if (aClass != null) {
                GenSourceableTable genSourceableTable = aClass.getAnnotation(GenSourceableTable.class);
                if (genSourceableTable != null) {

                    if (genSourceableTable.createDto()) {
                        generateDto(className, persistentClass);
                        generateDtoMapper(className, persistentClass);
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
//                    if (genSourceableTable.createMapper()) {
//                        log.error("  createMapper : {} ", className);
//                    }
//                    if (genSourceableTable.createValidator()) {
//                        log.error("  createValidator : {} ", className);
//                    }
                    if (genSourceableTable.createFrontend()) {

//                        if(theme==DevFrontendTheme.DEFAULT){
//                            generateFrontend(className, persistentClass);
//                        }
                        switch (theme) {
                            case QUASAR -> generateFrontend(className, persistentClass);
                            case NUXT_QUASAR -> generateFrontendNuxt3Quasar(className, persistentClass);
                        }

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
                log.error("  className : {} NULL", className);
            }
//            for (Iterator propertyIterator = persistentClass.getPropertyIterator();
//                 propertyIterator.hasNext(); ) {
//                Property property = (Property) propertyIterator.next();
//
//                for (Iterator columnIterator = property.getColumnIterator();
//                     columnIterator.hasNext(); ) {
//                    Column column = (Column) columnIterator.next();
//
//                    log.info("Property: {} is mapped on table column: {} of type: {} uniqe : {}, length : {}, propertyTypeName : {} , isNullable :{}",
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
//        log.info("CreateController : {}", tableSerializable.createController());

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
            log.warn("---generateDto : {} ", fileName);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/dto/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto").append(";\n");
                writer.append("\n");
                writer.append("import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\n");
                writer.append("import com.fasterxml.jackson.annotation.JsonRootName;\n");
                writer.append("import com.fasterxml.jackson.annotation.JsonFormat;\n");
                writer.append("import java.math.BigDecimal;\n");
                writer.append("import java.time.LocalDate;\n");
                writer.append("import java.time.LocalDateTime;\n");
                writer.append("import lombok.Getter;\n");
                writer.append("import lombok.Setter;\n");
                writer.append("import lombok.experimental.Accessors;\n");
                writer.append("import jakarta.validation.constraints.DecimalMax;\n");
                writer.append("import jakarta.validation.constraints.DecimalMin;\n");
                writer.append("import jakarta.validation.constraints.NotEmpty;\n");
                writer.append("import jakarta.validation.constraints.Size;\n");
                writer.append("import jakarta.validation.constraints.Positive;\n");
                writer.append("import jakarta.validation.constraints.Max;\n");
                writer.append("import jakarta.validation.constraints.Min;\n");
                writer.append("import jakarta.validation.constraints.NotNull;\n");

                writer.append("\n");
                writer.append("@Getter\n");
                writer.append("@Setter\n");
                writer.append("@JsonRootName(\"").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("\")\n");
//                writer.append("//@AllArgsConstructor\n");
//                writer.append("//@NoArgsConstructor\n");
                writer.append("@JsonIgnoreProperties(ignoreUnknown = true)\n");
                writer.append("@Accessors(chain = true)\n");
                writer.append("public class ").append(fileName).append(" extends DtoId {\n");
//                writer.append("    private Long id;\n");
                for (GenerateTableSrcItem src : propertyList) {
                    String propertyName = src.getPropertyName();
                    if (!exceptField(propertyName)) {
                        String type = getJavaType(src.getPropertyType());
                        boolean isNullable = src.isNullable();
                        Long size = src.getLength();
                        boolean isRefClass = isObjectLink(src.getPropertyType());
                        if (type != null) {
                            if (src.getPropertyType().equals(TYPE_LOCAL_DATETIME)) {
                                writer.append("    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = \"yyyy-MM-dd HH:mm:ss\")\n");
                            } else if (src.getPropertyType().equals(TYPE_LOCAL_DATE)) {
                                writer.append("    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = \"yyyy-MM-dd\")\n");
                            }

                            if (!isNullable) {
                                if (!isRefClass) {
                                    writer.append("    @NotEmpty(message = \"{error.NotEmpty}\")\n");
                                } else {
                                    writer.append("    //@NotEmpty(message = \"{error.NotEmpty}\")\n");
                                }

                            }
                            if (size != null && !src.getPropertyType().equals(TYPE_BIG_DECIMAL) && !src.getPropertyType().equals(TYPE_TEXT) && !src.getPropertyType().equals(TYPE_BOOLEAN)) {
                                if (src.getPropertyType().equals(TYPE_LOCAL_DATE)) {
                                    writer.append("    @Size(max = 10, message = \"{error.SizeLimitMaxFormat}\")\n");
                                } else if (src.getPropertyType().equals(TYPE_LOCAL_DATETIME)) {
                                    writer.append("    @Size(max = 19, message = \"{error.SizeLimitMaxFormat}\")\n");
                                } else {
                                    writer.append("    @Size(max = ").append(String.valueOf(size)).append(", message = \"{error.SizeLimitMaxFormat}\")\n");
                                }
                            } else if (src.getPropertyType().equals(TYPE_BIG_DECIMAL)) {
                                writer.append("    @DecimalMax(value = \"999999999999.0\", message = \"{error.DecimalMax.message}\")\n");
                                writer.append("    @DecimalMin(value = \"0.0\", message = \"{error.DecimalMin.message}\")\n");
                            }

                            if (isRefClass) {
                                writer.append("    //private ").append(type).append(" ").append(propertyName).append(";\n");
                            } else {
                                writer.append("    private ").append(type).append(" ").append(propertyName).append(";\n");
                            }
                            writer.append("\n");
                        }
                    }
                }
                writer.append("}\n");
                writer.close();
                logCretedFile(className);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }
    private void generateDtoMapper(String entityName, PersistentClass persistentClass) {
        String fileName = entityName + "Mapper";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".mapper." + fileName;

        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            log.warn("---generateDtoMapper : {} ", fileName);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/mapper/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".mapper").append(";\n");
                writer.append("\n");
                writer.append("import org.mapstruct.Mapper;\n");
                writer.append("import org.mapstruct.Mapping;\n");
                writer.append("import org.mapstruct.Mappings;\n");
                writer.append("import org.mapstruct.ReportingPolicy;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.").append(entityName).append("Dto;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".model.").append(entityName).append(";\n");
                writer.append("\n");
                writer.append("@Mapper(componentModel = \"spring\",unmappedTargetPolicy = ReportingPolicy.IGNORE)\n");
                writer.append("public interface ").append(fileName).append(" {\n");
                writer.append("    ").append(entityName).append("Dto toDto(").append(entityName).append(" entity);\n");
                writer.append("    ").append(entityName).append(" toEntity(").append(entityName).append("Dto dto);\n");
                writer.append("}\n");
                writer.close();
                logCretedFile(className);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void generateRepository(String entityName) {
        //package io.beka.repository
        String fileName = entityName + "Repository";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".repository." + fileName;

        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            log.warn("---generateRepository : {} ", fileName);
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
                logCretedFile(className);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void generateService(String entityName, boolean haveDto) {
        //package io.beka.service
        String fileName = entityName + "Service";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".service." + fileName;
        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            log.warn("---generateService : {} ", fileName);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/service/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".service").append(";\n");
                if (haveDto) {
                    writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.").append(entityName).append("Dto;\n");
                }
                writer.append("\n");
                writer.append("import com.bekaku.api.spring.model.").append(entityName).append(";\n");
                writer.append("\n");
                writer.append("public interface ").append(entityName).append("Service extends BaseService<").append(entityName).append(", ").append(haveDto ? entityName + "Dto" : entityName).append("> {\n");
                writer.append("}\n");
                writer.close();
                logCretedFile(className);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void logCretedFile(String name) {
        log.info("Created Class : {} ", name);
    }

    private void generateServiceImpl(String entityName, boolean haveDto) {
        //package com.bekaku.api.spring.serviceImpl
        String fileName = entityName + "ServiceImpl";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".serviceImpl." + fileName;

        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            log.warn("---generateServiceImpl : {} ", fileName);
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ConstantData.DEFAULT_PROJECT_ROOT_PATH + "/serviceImpl/" + fileName + ".java", false));
                writer.append("package ").append(ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".serviceImpl").append(";\n");
                writer.append("\n");
//                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".vo.Paging;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.ResponseListDto;\n");
                if (haveDto) {
                    writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".dto.").append(entityName).append("Dto;\n");
                    writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".mapper.").append(entityName).append("Mapper;\n");
                }
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".model.").append(entityName).append(";\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".repository.").append(entityName).append("Repository;\n");
                writer.append("import " + ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".service.").append(entityName).append("Service;\n");
//                writer.append("import lombok.AllArgsConstructor;\n");
                writer.append("import lombok.RequiredArgsConstructor;\n");
//                writer.append("import org.modelmapper.ModelMapper;\n");
                writer.append("import org.springframework.data.domain.Pageable;\n");
                writer.append("import org.springframework.data.domain.Page;\n");
                writer.append("import com.bekaku.api.spring.specification.SearchSpecification;\n");
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
                writer.append("@RequiredArgsConstructor\n");
                writer.append("@Service\n");
                writer.append("public class ").append(entityName).append("ServiceImpl implements ").append(entityName).append("Service {\n");
//                writer.append("    @Autowired\n");
                writer.append("    private final ").append(entityName).append("Repository ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Repository;\n");
                if (haveDto) {
//                  writer.append("    @Autowired\n");
                    writer.append("    private final ").append(AppUtil.capitalizeFirstLetter(entityName, false)).append("Mapper modelMapper;\n");
                }
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
                writer.append("                , result.getTotalPages(), result.getTotalElements(), result.isLast());\n");
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
//                    writer.append("        return modelMapper.map(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(", ").append(entityName).append("Dto.class);\n");
                    writer.append("        return modelMapper.toDto(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                } else {
                    writer.append("return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(";\n");
                }
                writer.append("    }\n");


                writer.append("\n");
                writer.append("    @Override\n");
                writer.append("    public ").append(entityName).append(" convertDtoToEntity(").append(haveDto ? entityName + "Dto " + AppUtil.capitalizeFirstLetter(entityName, true) + "Dto" : entityName + " " + AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                if (haveDto) {
//                    writer.append("        return modelMapper.map(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Dto, ").append(entityName).append(".class);\n");
                    writer.append("        return modelMapper.toEntity(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Dto);\n");
                } else {
                    writer.append("return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(";\n");
                }
                writer.append("    }\n");
                writer.append("\n");
                writer.append("}\n");
                writer.close();
                logCretedFile(className);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    private void generateController(String entityName, boolean haveDto) {
        //package com.bekaku.api.spring.controller.api
        String fileName = entityName + "Controller";
        String className = ConstantData.DEFAULT_PROJECT_ROOT_PACKAGE + ".controller.api." + fileName;

        boolean isExist = getClassFromName(className) != null;
        if (!isExist) {
            log.warn("---generateController : {} ", fileName);
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
//                writer.append("import org.slf4j.Logger;\n");
//                writer.append("import org.slf4j.LoggerFactory;\n");
                writer.append("import lombok.extern.slf4j.Slf4j;\n");
//                writer.append("import jakarta.servlet.http.HttpServletRequest;\n");
                writer.append("import com.bekaku.api.spring.specification.SearchSpecification;\n");
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
                writer.append("@Slf4j\n");
                writer.append("@RequestMapping(path = \"/api/").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("\")\n");
                writer.append("@RestController\n");
                writer.append("@RequiredArgsConstructor\n");
                writer.append("public class ").append(fileName).append(" extends BaseApiController{\n");
                writer.append("\n");
//                writer.append("    @Autowired\n");
                writer.append("    private final ").append(entityName).append("Service ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service;\n");
//                writer.append("    @Autowired\n");
                writer.append("    private final I18n i18n;\n");
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
                } else {
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
                    writer.append("    public ").append(entityName).append("Dto create(@Valid @RequestBody ").append(entityName).append("Dto dto) {\n");
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
                if (haveDto) {
                    writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertEntityToDto(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                } else {
                    writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(";\n");
                }
                writer.append("    }\n");

                //update
                writer.append("\n");
                writer.append("    @PreAuthorize(\"isHasPermission('").append(AppUtil.camelToSnake(entityName)).append("_manage')\")\n");
                writer.append("    @PutMapping(\"/{id}\")\n");
                if (haveDto) {
                    writer.append("    public ").append(entityName).append("Dto update(@PathVariable(\"id\") Long id, @Valid @RequestBody ").append(entityName).append("Dto dto) {\n");
                } else {
                    writer.append("    public ").append(entityName).append(" update(@PathVariable(\"id\") Long id, @Valid @RequestBody ").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(") {\n");
                }
                if (haveDto) {
                    writer.append("        ").append(entityName).append(" ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(" = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertDtoToEntity(dto);\n");
                } else {
//                    writer.append("        Optional<").append(entityName).append("> oldData = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findById(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(".getId());\n");
                }
                writer.append("        Optional<").append(entityName).append("> oldData = ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.findById(id);\n");

                writer.append("        if (oldData.isEmpty()) {\n");
                writer.append("            throw this.responseErrorNotfound();\n");
                writer.append("        }\n");
                writer.append("        ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.update(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                if (haveDto) {
                    writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append("Service.convertEntityToDto(").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(");\n");
                } else {
                    writer.append("        return ").append(AppUtil.capitalizeFirstLetter(entityName, true)).append(";\n");
                }
//                writer.append("        return this.reponseUpdatedMessage();\n");
                writer.append("    }\n");

                //findOne
                writer.append("\n");
                writer.append("    @PreAuthorize(\"isHasPermission('").append(AppUtil.camelToSnake(entityName)).append("_view')\")\n");
                writer.append("    @GetMapping(\"/{id}\")\n");
                if (haveDto) {
                    writer.append("    public ").append(entityName).append("Dto findOne(@PathVariable(\"id\") Long id) {\n");
                } else {
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
                writer.append("        return this.responseDeleteMessage();\n");
                writer.append("    }\n");


                writer.append("}\n");
                writer.close();
                logCretedFile(className);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    //Start nuxt3+quasar
    private void generateFrontendNuxt3Quasar(String entityName, PersistentClass persistentClass) {
        Table table = persistentClass.getTable();
        String tableName = table.getName();
        String tableNameKebabCase = tableName.replace("_", "-");
        String dirName = ConstantData.DEFAULT_FRONTEND_GENERATE_DIRECTORY + "/nuxt3-quasar/" + tableNameKebabCase;
        String listName = dirName + "/index.vue";
        String dirFormName = dirName + "/[crud]";
        String formName = dirFormName + "/[id].vue";
        String apiName = dirName + "/" + entityName + "Service.ts";
        log.info("generateFrontendNuxt3Quasar > TableName :{},  listName :{}, formName :{}, apiName :{}", tableName, listName, formName, apiName);

        if (!FileUtil.folderExist(dirName)) {
            FileUtil.folderCreate(dirName);
            log.warn("created folder :{} ", dirName);
        }
        if (!FileUtil.folderExist(dirFormName)) {
            FileUtil.folderCreate(dirFormName);
            log.warn("dirFormName :{} ", dirFormName);
        }
        if (!FileUtil.fileExists(listName)) {
            log.warn("listName :{}, created", listName);
            generateNux3QuasarFrontList(listName, entityName, tableName);
        }
        if (!FileUtil.fileExists(formName)) {
            log.warn("formName :{}, created", formName);
            generateNux3QuasarFrontForm(formName, entityName, tableName);
        }
        if (!FileUtil.fileExists(apiName)) {
            log.warn("apiName :{}, created", apiName);
            generateNux3QuasarFrontService(apiName, entityName, tableName);
        }
    }

    private void generateNux3QuasarFrontList(String filePathName, String entityName, String tableName) {
        String entityNameLowerFirst = AppUtil.capitalizeFirstLetter(entityName, true);
        String upperTableName = AppUtil.upperLowerCaseString(tableName, false);
        String tableNameKebabCase = tableName.replace("_", "-");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePathName, false));
            writer.append("<script setup lang=\"ts\">\n");

            String breadcrumbsName = entityName + "FormBreadcrumb";
            //breadcrumbs
            writer.append("/* move this variable to /app/libs/appBreadcrumbs.ts \n");
            writer.append("export const ").append(breadcrumbsName).append(": Breadcrumb[] = [\n");
            writer.append("  {\n");
            writer.append("    label: 'model.").append(entityNameLowerFirst).append(".table',\n");
//            writer.append("    to: '/").append(tableNameKebabCase).append("',\n");
            writer.append("    to: '/").append(tableNameKebabCase).append("',\n");
//            writer.append("    to: `/${AdminRootPath}/").append(tableNameKebabCase).append("`,\n");
            writer.append("    icon: biFileEarmark,\n");
            writer.append("    translateLabel: true\n");
            writer.append("  },\n");
            writer.append("  ...crudDetailFn('").append(tableNameKebabCase).append("')\n");
            writer.append("];\n");
            writer.append("*/\n");
            writer.append("\n");


            //Model
            writer.append("/* move this interface to /app/types/models.ts \n");
            writer.append("export interface ").append(entityName).append(" extends Id {\n");
            for (GenerateTableSrcItem src : propertyList) {
                String propertyName = src.getPropertyName();
                String propertyTypeName = src.getPropertyType();
                if (!exceptField(propertyName)) {
                    String typeScriptType = getTypscriptType(propertyTypeName);
                    boolean isNullable = src.isNullable();
                    String undefinedSign = isNullable ? "?" : "";
                    String typeScriptTypeFinal = !AppUtil.isEmpty(undefinedSign) ? typeScriptType + " " + TYPESCRIPT_OR_SIGN + " " + TYPESCRIPT_NULL : typeScriptType;
                    writer.append(" ").append(propertyName).append(undefinedSign).append(": ").append(typeScriptTypeFinal).append("\n");
                }
            }
            writer.append("}\n");
            writer.append("*/\n");
            writer.append("\n");

            //Permission
            writer.append("/* move this object to /app/libs/appPermissions.ts \n");
            writer.append("export const ").append(entityName).append("Permission= {\n");
            writer.append("  view: '").append(tableName).append("_view',\n");
            writer.append("  list: '").append(tableName).append("_list',\n");
            writer.append("  manage: '").append(tableName).append("_manage'\n");
            writer.append("}\n");
            writer.append("*/\n");
            writer.append("\n");

            //message
            writer.append("/* move this message object to /app/i18n/th/model.ts and /app/i18n/en/model.ts under model:{}  \n");
            writer.append("    ,").append(entityNameLowerFirst).append(": {\n");
            writer.append("      table: '").append(entityNameLowerFirst).append("',\n");
            for (GenerateTableSrcItem src : propertyList) {
                String propertyName = src.getPropertyName();
                if (!exceptField(propertyName)) {
                    writer.append("      ").append(propertyName).append(": '").append(propertyName).append("',\n");
                }
            }
            writer.append("    }\n");
            writer.append("*/\n");
            writer.append("import { biFileEarmark } from '@quasar/extras/bootstrap-icons';\n");
            writer.append("import { CrudListDataType, ICrudListHeaderOptionSearchType, type ICrudListHeader } from '~/types/common';\n");
            writer.append("import type { ").append(entityName).append(" } from '~/types/models';\n");
            writer.append("import { ").append(entityName).append("Permission } from '~/libs/appPermissions';\n");

            writer.append("definePageMeta({ \n");
            writer.append("  pageName: 'model.").append(entityNameLowerFirst).append(".table', \n");
            writer.append("  requiresPermission: [").append(entityName).append("Permission.list], \n");
            writer.append("}) \n");
            writer.append("useInitPage(); \n");
            writer.append("const { t }=useLang(); \n");
            writer.append("const headerItems: ICrudListHeader[] = [\n");
            for (GenerateTableSrcItem src : propertyList) {
                String propertyName = src.getPropertyName();
                if (!exceptField(propertyName)) {

                    String crudListType = getCrudListType(src.getPropertyType());
                    String crudListSearchType = getCrudListSearchType(src.getPropertyType());
                    String crudListSearchOperation = getCrudListSearchOperation(src.getPropertyType());
                    boolean isClassRef = isObjectLink(src.getPropertyType());
                    if (!isClassRef) {

                        writer.append("  {\n");
                        writer.append("    label: 'model.").append(entityNameLowerFirst).append(".").append(propertyName).append("',\n");
                        writer.append("    column: '").append(propertyName).append("',\n");
                        writer.append("    type: CrudListDataType.").append(crudListType).append(",\n");
                        writer.append("    options: {\n");
                        writer.append("      searchable: true,\n");
                        writer.append("      sortable: true,\n");
                        writer.append("      fillable: true,\n");
                        if (crudListSearchType != null) {
                            writer.append("      searchType: ICrudListHeaderOptionSearchType.").append(crudListSearchType).append(",\n");
                            writer.append("      searchModel: '',\n");
                            writer.append("      searchOperation: '").append(crudListSearchOperation).append("'\n");
                        }
                        writer.append("    }\n");
                        writer.append("  },\n");
                    }
                }
            }
            //base tool
            writer.append("  {\n");
            writer.append("    label: 'base.tool',\n");
            writer.append("    type: CrudListDataType.BASE_TOOL,\n");
            writer.append("    options: {\n");
            writer.append("      fillable: true,\n");
            writer.append("      editButton: true,\n");
            writer.append("      deleteButton: true,\n");
            writer.append("      copyButton: true,\n");
            writer.append("      viewButton: true,\n");
            writer.append("      align: 'center'\n");
            writer.append("    }\n");
            writer.append("  },\n");

            writer.append("];\n");
            writer.append("const {\n");
            writer.append("  dataList,\n");
            writer.append("  loading,\n");
            writer.append("  firstLoaded,\n");
            writer.append("  pages,\n");
            writer.append("  sort,\n");
            writer.append("  onPageNoChange,\n");
            writer.append("  onItemPerPageChange,\n");
            writer.append("  onSort,\n");
            writer.append("  onSortMode,\n");
            writer.append("  onReload,\n");
            writer.append("  onAdvanceSearch,\n");
            writer.append("  onItemDelete,\n");
            writer.append("  onNewForm,\n");
            writer.append("  onItemClick,\n");
            writer.append("  onItemCopy,\n");
            writer.append("  crudName,\n");
            writer.append("  onKeywordSearch,\n");
            writer.append("  headers\n");
            writer.append("} = useCrudList<").append(entityName).append(">(\n");
            writer.append("  {\n");
            writer.append("    crudName: '").append(tableName).append("',\n");
            writer.append("    apiEndpoint: '/api',\n");
            writer.append("    headers: headerItems,\n");
            writer.append("    defaultSort: {\n");
            writer.append("      column: 'id',\n");
            writer.append("      mode: 'desc'\n");
            writer.append("    }\n");
            writer.append("  },\n");
            writer.append(");\n");
            writer.append("</script>\n");

//            for (GenerateTableSrcItem src : propertyList) {
//                String propertyName = src.getPropertyName();
//                String propertyTypeName = src.getPropertyType();
//                if (!exceptField(propertyName)) {
//                    log.info("Property: {}  of SqlType: {}, isUnique : {}, length : {}, propertyTypeName : {} , isNullable :{}, isTypeTextArea :{}",
//                            src.getPropertyName(),
//                            src.getSqlType(),
//                            src.isUnique(),
//                            src.getLength(),
//                            src.getPropertyType(),
//                            src.isNullable(),
//                            src.isTypeTextArea()
//                    );
//                }
//            }

            writer.append("<template>\n");
            writer.append("  <BasePage>\n");
            writer.append("    <BaseCrudList\n");
            writer.append("      :icon=\"biFileEarmark\" \n");
            writer.append("      :title=\"t('model.").append(entityNameLowerFirst).append(".table')\"\n");
            writer.append("      :crud-name=\"crudName\" \n");
            writer.append("      :loading=\"loading\" \n");
            writer.append("      :first-loaded=\"firstLoaded\" \n");
            writer.append("      :pages=\"pages\" \n");
            writer.append("      :headers=\"headers\" \n");
            writer.append("      :sort=\"sort\" \n");
            writer.append("      :list=\"dataList\" \n");
            writer.append("      show-search-text-box \n");
            writer.append("      :view-permission=\"[").append(entityName).append("Permission.view]\"\n");
            writer.append("      :manage-permission=\"[").append(entityName).append("Permission.manage]\"\n");
            writer.append("      @on-item-click=\"onItemClick\" \n");
            writer.append("      @on-item-copy=\"onItemCopy\" \n");
            writer.append("      @on-page-no-change=\"onPageNoChange\" \n");
            writer.append("      @on-items-perpage-change=\"onItemPerPageChange\" \n");
            writer.append("      @on-sort=\"onSort\" \n");
            writer.append("      @on-sort-mode=\"onSortMode\" \n");
            writer.append("      @on-reload=\"onReload\" \n");
            writer.append("      @on-advance-search=\"onAdvanceSearch\" \n");
            writer.append("      @on-keyword-search=\"onKeywordSearch\" \n");
            writer.append("      @on-item-delete=\"onItemDelete\" \n");
            writer.append("      @on-new-form=\"onNewForm\" \n");
            writer.append("    >\n");
            writer.append("    <!-- Slot \n");
            writer.append("    <template #additionalBaseTool/> \n");
            writer.append("    <template #baseTool=\"{index, item}\"/> \n");
            writer.append("    <template #belowInnerSearchExtra/> \n");
            writer.append("    <template #belowSearchExtra/> \n");
            writer.append("    <template #extraBeforeInnerToolbar/> \n");
            writer.append("    <template #extraInnerToolbar/> \n");
            writer.append("    <template #extraToolbar/> \n");
            writer.append("    <template #headerCard/> \n");
            writer.append("    <template #paging/> \n");
            writer.append("    <template #table/> \n");
            writer.append("    <template #tbody=\"{fillableHeaders, list}\"/> \n");
            writer.append("    --> \n");
            writer.append("    </BaseCrudList>\n");
            writer.append("  </BasePage>\n");
            writer.append("</template>\n");
            writer.close();
            logCretedFile(filePathName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void generateNux3QuasarFrontForm(String filePathName, String entityName, String tableName) {
        String entityNameLowerFirst = AppUtil.capitalizeFirstLetter(entityName, true);
        String upperTableName = AppUtil.upperLowerCaseString(tableName, false);
        String tableNameKebabCase = tableName.replace("_", "-");
        try {

            BufferedWriter writer = new BufferedWriter(new FileWriter(filePathName, false));

            writer.append("<script setup lang=\"ts\">\n");
            writer.append("import { biFileEarmark } from '@quasar/extras/bootstrap-icons';\n");
            writer.append("import { ").append(entityName).append("FormBreadcrumb } from '~/libs/appBreadcrumbs';\n");
            writer.append("import { ").append(entityName).append("Permission } from '~/libs/appPermissions';\n");
            writer.append("import type { ").append(entityName).append(" } from '~/types/models';\n");

            writer.append("const { t } = useLang();\n");
            writer.append("const { required, requiredNotMinusNumberOrFloat } = useValidation();\n");

            writer.append("definePageMeta({ \n");
            writer.append("  pageName: 'model.").append(entityNameLowerFirst).append(".table', \n");
            writer.append("  requiresPermission: [").append(entityName).append("Permission.view], \n");
            writer.append("  breadcrumbs: ").append(entityName).append("FormBreadcrumb,\n");
            writer.append("}) \n");
            writer.append("useInitPage(); \n");

            //initial entity
            writer.append("const entity: ").append(entityName).append(" =  Object.freeze<").append(entityName).append(">({\n");
            writer.append("  id: null,\n");
            for (GenerateTableSrcItem src : propertyList) {
                String propertyName = src.getPropertyName();
                String propertyTypeName = src.getPropertyType();
                if (!exceptField(propertyName)) {
                    boolean typeScriptType = isTypeTextArea(src.getSqlType(), propertyTypeName);
                    boolean isNullable = src.isNullable();
                    String defaultValue = getTypscriptTypeDefaultValue(propertyTypeName);
                    writer.append("  ").append(propertyName).append(": ").append(defaultValue).append(",\n");
                }
            }
            writer.append("});\n");

            writer.append("const {\n");
            writer.append("  crudAction,\n");
            writer.append("  crudEntity,\n");
            writer.append("  crudName,\n");
            writer.append("  isEditMode,\n");
            writer.append("  loading,\n");
            writer.append("  onBack,\n");
            writer.append("  onDelete,\n");
            writer.append("  onEnableEditForm,\n");
            writer.append("  onSubmit,\n");
            writer.append("} = useCrudForm<").append(entityName).append(">(\n");
            writer.append("  {\n");
            writer.append("    crudName: '").append(tableName).append("',\n");
//            writer.append("    backLink: `/${AdminRootPath}/").append(tableNameKebabCase).append("`,\n");
//            writer.append("    backLink: '/").append(tableNameKebabCase).append("',\n");
            writer.append("    apiEndpoint: '/api',\n");
            writer.append("  },\n");
            writer.append("  entity\n");
            writer.append(");\n");
            writer.append("\n");

            writer.append("</script>\n");
            writer.append("\n");

            writer.append("<template>\n");
            writer.append("  <BasePage>\n");
            writer.append("    <BaseCrudForm\n");
            writer.append("      :icon=\"biFileEarmark\"\n");
            writer.append("      :title=\"t('model.").append(entityNameLowerFirst).append(".table')\"\n");
            writer.append("      :crud-name=\"crudName\"\n");
            writer.append("      :crud-action=\"crudAction\"\n");
            writer.append("      :crud-entity=\"crudEntity\"\n");
            writer.append("      :full-width=\"false\"\n");
            writer.append("      :list-permission=\"[").append(entityName).append("Permission.list]\"\n");
            writer.append("      :manage-permission=\"[").append(entityName).append("Permission.manage]\"\n");
            writer.append("      :loading=\"loading\"\n");
            writer.append("      @on-back=\"onBack\"\n");
            writer.append("      @on-submit=\"onSubmit\"\n");
            writer.append("      @on-delete=\"onDelete\"\n");
            writer.append("      @on-edit-enable=\"onEnableEditForm\"\n");
            writer.append("    >\n");
            //start crudFromContent slot
            writer.append("      <template #crudFromContent>\n");
            //start row
            writer.append("        <div class=\"row\">\n");
            for (GenerateTableSrcItem src : propertyList) {
                String propertyName = src.getPropertyName();
                String propertyTypeName = src.getPropertyType();
                if (!exceptField(propertyName)) {
                    String typeScriptType = getTypscriptType(propertyTypeName);
                    boolean isNullable = src.isNullable();
                    String defaultValue = getTypscriptTypeDefaultValue(propertyTypeName);
                    boolean isTextAreaType = isTypeTextArea(src.getSqlType(), propertyTypeName);
                    boolean isNumberType = propertyTypeName.equals(TYPE_FLOAT) || propertyTypeName.equals(TYPE_BIG_DECIMAL) || propertyTypeName.equals(TYPE_INTEGER);
                    Long limitText = src.getLength();
                    boolean isRefClass = isObjectLink(propertyTypeName);
                    //start col
                    writer.append("          <div class=\"col-12 col-md-4 q-pa-md\">\n");
                    if (isRefClass) {
                        //if reference to other Object class
                        writer.append("          <!-- type ").append(propertyTypeName).append(" -->\n");
                        writer.append("          <!-- TODO implement object link -->\n");
                    } else if (propertyTypeName.equals(TYPE_STRING) || isNumberType) {
                        writer.append("              <BaseInput\n");
                        writer.append("                v-model=\"crudEntity.").append(propertyName).append("\" :edit-mode=\"isEditMode\" :readonly=\"loading\" \n");
                        writer.append("                :label=\"t('model.").append(entityNameLowerFirst).append(".").append(propertyName).append("')\"\n");
                        if (isTextAreaType) {
                            writer.append("                type=\"textarea\"\n");
                            if (!isNullable) {
                                writer.append("                :rules=\"[required]\"\n");
                            }
                        } else if (propertyTypeName.equals(TYPE_STRING)) {
                            writer.append("                type=\"text\"\n");
                            if (!isNullable) {
                                writer.append("                :rules=\"[required]\"\n");
                            }
                        } else {
                            writer.append("                type=\"number\"\n");
                            writer.append("                :min=\"0\"\n");
                            if (!isNullable) {
                                writer.append("                :rules=\"[requiredNotMinusNumberOrFloat]\"\n");
                            }
                        }


                        if (!isTextAreaType && limitText != null) {
                            writer.append("                 :maxlength=\"").append(String.valueOf(limitText)).append("\"\n");
                            writer.append("                 counter\n");
                        }
                        if (!isNullable) {
                            writer.append("                 required\n");
                        }
                        writer.append("                />\n");
                        /*
                        if (!isNullable) {
                            writer.append("                <template #hint>\n");
//                        writer.append("                          {{ t('helper.requireMinimumLetter', { no: 4 }) }}\n");
                            writer.append("                  <span class=\"text-negative\">*</span>\n");
                            writer.append("                </template>\n");
                        }

                        writer.append("            </BaseInput>\n");
                         */
                    } else if (propertyTypeName.equals(TYPE_BOOLEAN)) {
                        writer.append("             <BaseChekbox v-model=\"crudEntity.").append(propertyName).append("\" :edit-mode=\"isEditMode\" \n");
                        writer.append("              :label=\"t('model.").append(entityNameLowerFirst).append(".").append(propertyName).append("')\"\n");
                        writer.append("              />\n");
                    } else if (propertyTypeName.equals(TYPE_LOCAL_DATE)) {
                        writer.append("            <BaseDatePicker\n");
                        writer.append("              v-model=\"crudEntity.").append(propertyName).append("\"\n");
                        writer.append("              :edit-mode=\"editMode\" \n");
                        writer.append("              :label=\"t(model.").append(entityNameLowerFirst).append(".").append(propertyName).append(")\"\n");
                        if (!isNullable) {
                            writer.append("             required\n");
                        }
                        writer.append("            />\n");
                    } else if (propertyTypeName.equals(TYPE_LOCAL_DATETIME)) {
                        writer.append("            <BaseTimePicker\n");
                        writer.append("              v-model=\"crudEntity.").append(propertyName).append("\"\n");
                        writer.append("              :edit-mode=\"editMode\" \n");
                        writer.append("              :label=\"t(model.").append(entityNameLowerFirst).append(".").append(propertyName).append(")\"\n");
                        if (!isNullable) {
                            writer.append("             required\n");
                        }
                        writer.append("            />\n");
                    }


                    writer.append("          </div>\n");//end col
                }
            }
            writer.append("\n");
            writer.append("        </div>\n");//end row
            writer.append("      </template>\n");//end crudFromContent slot
            writer.append("    </BaseCrudForm>\n");//end crud-api-form
            writer.append("  </BasePage>\n");//end page
            writer.append("</template>\n");
            writer.append("\n");
            writer.close();
            logCretedFile(filePathName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void generateNux3QuasarFrontService(String filePathName, String entityName, String tableName) {
        String entityNameLowerFirst = AppUtil.capitalizeFirstLetter(entityName, true);
        String upperTableName = AppUtil.upperLowerCaseString(tableName, false);
        String tableNameKebabCase = tableName.replace("_", "-");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePathName, false));

            //api service
            writer.append("// move this file to /app/api \n");
            writer.append("import type { ").append(entityName).append(" } from '~/types/models';\n");
            writer.append("import type { ResponseMessage, IApiListResponse } from '~/types/common';\n");
            writer.append("export default () => {\n");
            writer.append("  const { callAxios } = useAxios();\n");
            writer.append("  const findAll = async (q: string): Promise<IApiListResponse<").append(entityName).append("> | null> => {\n");
            writer.append("    return await callAxios<IApiListResponse<").append(entityName).append(">>({\n");
            writer.append("      API: `/api/").append(entityNameLowerFirst).append("${q}`,\n");
            writer.append("      method: 'GET',\n");
            writer.append("    });\n");
            writer.append("  };\n");
            writer.append("  const findById = async (id: number): Promise<").append(entityName).append(" | null> => {\n");
            writer.append("    return await callAxios<").append(entityName).append(">({\n");
            writer.append("      API: `/api/").append(entityNameLowerFirst).append("/${id}`,\n");
            writer.append("      method: 'GET',\n");
            writer.append("    });\n");
            writer.append("  };\n");
            writer.append("  const crudCreate = async (request: ").append(entityName).append("): Promise<").append(entityName).append(" | null> => {\n");
            writer.append("    return await callAxios<").append(entityName).append(">({\n");
            writer.append("      API: '/api/").append(entityNameLowerFirst).append("',\n");
            writer.append("      method: 'POST',\n");
            writer.append("      body: {\n");
            writer.append("        ").append(entityNameLowerFirst).append(": request,\n");
            writer.append("      },\n");
            writer.append("    });\n");
            writer.append("  };\n");
            writer.append("  const crudUpdate = async (id: number, request: ").append(entityName).append("): Promise<").append(entityName).append(" | null> => {\n");
            writer.append("    return await callAxios<").append(entityName).append(">({\n");
            writer.append("      API: `/api/").append(entityNameLowerFirst).append("/${id}`,\n");
            writer.append("      method: 'PUT',\n");
            writer.append("      body: {\n");
            writer.append("        ").append(entityNameLowerFirst).append(": request,\n");
            writer.append("      },\n");
            writer.append("    });\n");
            writer.append("  };\n");
            writer.append("  const deleteById = async (id: number): Promise<ResponseMessage | null> => {\n");
            writer.append("    return await callAxios<ResponseMessage>({\n");
            writer.append("      API: `/api/").append(entityNameLowerFirst).append("/${id}`,\n");
            writer.append("      method: 'DELETE',\n");
            writer.append("    });\n");
            writer.append("  };\n");
            writer.append("  return {\n");
            writer.append("    findAll,\n");
            writer.append("    findById,\n");
            writer.append("    crudCreate,\n");
            writer.append("    crudUpdate,\n");
            writer.append("    deleteById\n");
            writer.append("  };\n");
            writer.append("};\n");
            writer.close();
            logCretedFile(filePathName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    //End nuxt3+quasar

    //Start Default theme Quasar SSR
    private void generateFrontend(String entityName, PersistentClass persistentClass) {
        Table table = persistentClass.getTable();
        String tableName = table.getName();
        String tableNameKebabCase = tableName.replace("_", "-");
        String dirName = ConstantData.DEFAULT_FRONTEND_GENERATE_DIRECTORY + "/default/" + tableNameKebabCase;
        String listName = dirName + "/index.vue";
        String formName = dirName + "/view.vue";
        String apiName = dirName + "/" + entityName + "Service.ts";
        log.info("generateFrontend > TableName :{},  listName :{}, formName :{}, apiName :{}", tableName, listName, formName, apiName);

        if (!FileUtil.folderExist(dirName)) {
            FileUtil.folderCreate(dirName);
            log.warn("created folder :{} ", dirName);
        }
        if (!FileUtil.fileExists(listName)) {
            log.warn("listName :{}, created", listName);
            generateFrontList(listName, entityName, tableName);
        }
        if (!FileUtil.fileExists(formName)) {
            log.warn("formName :{}, created", formName);
            generateFrontForm(formName, entityName, tableName);
        }
        if (!FileUtil.fileExists(apiName)) {
            log.warn("apiName :{}, created", apiName);
            generateFrontService(apiName, entityName, tableName);
        }
    }

    private void generateFrontForm(String filePathName, String entityName, String tableName) {
        String entityNameLowerFirst = AppUtil.capitalizeFirstLetter(entityName, true);
        String upperTableName = AppUtil.upperLowerCaseString(tableName, false);
        String tableNameKebabCase = tableName.replace("_", "-");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePathName, false));

            writer.append("<script setup lang=\"ts\">\n");
            writer.append("import { useLang } from '@/composables/useLang';\n");
            writer.append("import { useCrudForm } from '@/composables/useCrudForm';\n");
            writer.append("import { ").append(entityName).append(" } from '@/types/models';\n");
            writer.append("import { useAppMeta } from '@/composables/useAppMeta';\n");
            writer.append("import CrudApiForm from '@/components/base/CrudApiForm.vue';\n");
            writer.append("import { useValidation } from '@/composables/useValidation';\n");
            writer.append("import { biPencil, biFileEarmark } from '@quasar/extras/bootstrap-icons';\n");
            writer.append("import { ").append(tableName).append("Permission } from '@/utils/appPermissionList';\n");
            writer.append("import BasePage from '@/components/base/BasePage.vue';\n");
            writer.append("import BaseCheckbox from '@/components/base/BaseCheckbox.vue';\n");
            writer.append("import BaseInput from '@/components/base/BaseInput.vue';\n");
            writer.append("import BaseDatePicker from '@/components/base/BaseDatePicker.vue';\n");
            writer.append("import BaseTimePicker from '@/components/base/BaseTimePicker.vue';\n");
            writer.append("import { getCurrentDateByFormat } from '@/utils/dateUtil';\n");
//            writer.append("import { AdminRootPath } from '@/utils/Constant';\n");
            writer.append("\n");

            writer.append("const { t } = useLang();\n");
            writer.append("const { required, requiredNotMinusNumberOrFloat } = useValidation();\n");

            //initial entity
            writer.append("const entity: ").append(entityName).append(" = Object.freeze({\n");
            writer.append("  id: null,\n");
            for (GenerateTableSrcItem src : propertyList) {
                String propertyName = src.getPropertyName();
                String propertyTypeName = src.getPropertyType();
                if (!exceptField(propertyName)) {
                    boolean typeScriptType = isTypeTextArea(src.getSqlType(), propertyTypeName);
                    boolean isNullable = src.isNullable();
                    String defaultValue = getTypscriptTypeDefaultValue(propertyTypeName);
                    writer.append("  ").append(propertyName).append(": ").append(defaultValue).append(",\n");
                }
            }
            writer.append("});\n");

            writer.append("const {\n");
            writer.append("  crudName,\n");
            writer.append("  crudAction,\n");
            writer.append("  loading,\n");
            writer.append("  onBack,\n");
            writer.append("  onSubmit,\n");
            writer.append("  onDelete,\n");
            writer.append("  crudEntity\n");
            writer.append("} = useCrudForm<").append(entityName).append(">(\n");
            writer.append("  {\n");
            writer.append("    crudName: '").append(tableName).append("',\n");
//            writer.append("    backLink: `/${AdminRootPath}/").append(tableNameKebabCase).append("`,\n");
            writer.append("    backLink: '/").append(tableNameKebabCase).append("',\n");
            writer.append("    backToPreviousPath: true,\n");
            writer.append("    apiEndpoint: '/api',\n");
            writer.append("    fectchDataOnLoad: true,\n");
            writer.append("    methodPutIncludeId: true\n");
            writer.append("  },\n");
            writer.append("  entity\n");
            writer.append(");\n");
            writer.append("\n");
            writer.append("useAppMeta({\n");
            writer.append("  additionalTitle: t('crudAction.' + crudAction.value)\n");
            writer.append("});\n");
            writer.append("</script>\n");
            writer.append("\n");
            writer.append("<template>\n");
            writer.append("  <BasePage>\n");
            writer.append("    <crud-api-form\n");
            writer.append("      :icon=\"biFileEarmark\"\n");
            writer.append("      :title=\"t('model.").append(entityNameLowerFirst).append(".table')\"\n");
            writer.append("      :crud-name=\"crudName\"\n");
            writer.append("      :crud-action=\"crudAction\"\n");
            writer.append("      :list-permission=\"").append(upperTableName).append(".list\"\n");
            writer.append("      :manage-permission=\"").append(upperTableName).append(".manage\"\n");
            writer.append("      :loading=\"loading\"\n");
            writer.append("      is-frontend\n");
            writer.append("      @on-back=\"onBack\"\n");
            writer.append("      @on-submit=\"onSubmit\"\n");
            writer.append("      @on-delete=\"onDelete\"\n");
            writer.append("    >\n");
            //start crudFromContent slot
            writer.append("      <template #crudFromContent>\n");
            //start row
            writer.append("        <div class=\"row\">\n");
            for (GenerateTableSrcItem src : propertyList) {
                String propertyName = src.getPropertyName();
                String propertyTypeName = src.getPropertyType();
                if (!exceptField(propertyName)) {
                    String typeScriptType = getTypscriptType(propertyTypeName);
                    boolean isNullable = src.isNullable();
                    String defaultValue = getTypscriptTypeDefaultValue(propertyTypeName);
                    boolean isTextAreaType = isTypeTextArea(src.getSqlType(), propertyTypeName);
                    boolean isNumberType = propertyTypeName.equals(TYPE_FLOAT) || propertyTypeName.equals(TYPE_BIG_DECIMAL) || propertyTypeName.equals(TYPE_INTEGER);
                    Long limitText = src.getLength();
                    boolean isRefClass = isObjectLink(propertyTypeName);
                    //start col
                    writer.append("          <div class=\"col-12 col-md-4 q-pa-md\">\n");
                    if (isRefClass) {
                        //if reference to other Object class
                        writer.append("          <!-- type ").append(propertyTypeName).append(" -->\n");
                        writer.append("          <!-- TODO implement object link -->\n");
                    } else if (propertyTypeName.equals(TYPE_STRING) || isNumberType) {
                        writer.append("              <BaseInput\n");
                        writer.append("                v-model=\"crudEntity.").append(propertyName).append("\"\n");
                        writer.append("                :label=\"t('model.").append(entityNameLowerFirst).append(".").append(propertyName).append("')\"\n");
                        if (isTextAreaType) {
                            writer.append("                type=\"textarea\"\n");
                            if (!isNullable) {
                                writer.append("                :rules=\"[required]\"\n");
                            }
                        } else if (propertyTypeName.equals(TYPE_STRING)) {
                            writer.append("                type=\"text\"\n");
                            if (!isNullable) {
                                writer.append("                :rules=\"[required]\"\n");
                            }
                        } else {
                            writer.append("                type=\"number\"\n");
                            writer.append("                min=\"0\"\n");
                            if (!isNullable) {
                                writer.append("                :rules=\"[requiredNotMinusNumberOrFloat]\"\n");
                            }
                        }


                        if (!isTextAreaType && limitText != null) {
                            writer.append("                 maxlength=\"").append(String.valueOf(limitText)).append("\"\n");
                            writer.append("                 counter\n");
                        }
                        writer.append("                >\n");
                        if (!isNullable) {
                            writer.append("                <template v-slot:hint>\n");
//                        writer.append("                          {{ t('helper.requireMinimumLetter', { no: 4 }) }}\n");
                            writer.append("                  <span class=\"text-negative\">*</span>\n");
                            writer.append("                </template>\n");
                        }

                        writer.append("            </BaseInput>\n");
                    } else if (propertyTypeName.equals(TYPE_BOOLEAN)) {
                        writer.append("             <BaseChekbox v-model=\"crudEntity.").append(propertyName).append("\"\n");
                        writer.append("              :label=\"t('model.").append(entityNameLowerFirst).append(".").append(propertyName).append("')\"\n");
                        writer.append("              />\n");
                    } else if (propertyTypeName.equals(TYPE_LOCAL_DATE)) {
                        writer.append("            <BaseDatePicker\n");
                        writer.append("              v-model=\"crudEntity.").append(propertyName).append("\"\n");
                        writer.append("              :label=\"t(model.").append(entityNameLowerFirst).append(".").append(propertyName).append(")\"\n");
                        if (!isNullable) {
                            writer.append("             required\n");
                        }
                        writer.append("            />\n");
                    } else if (propertyTypeName.equals(TYPE_LOCAL_DATETIME)) {
                        writer.append("            <BaseTimePicker\n");
                        writer.append("              v-model=\"crudEntity.").append(propertyName).append("\"\n");
                        writer.append("              :edit-mode=\"editMode\" \n");
                        writer.append("              :label=\"t(model.").append(entityNameLowerFirst).append(".").append(propertyName).append(")\"\n");
                        if (!isNullable) {
                            writer.append("             required\n");
                        }
                        writer.append("            />\n");
                    }
                    writer.append("          </div>\n");//end col
                }
            }
            writer.append("\n");
            writer.append("        </div>\n");//end row
            writer.append("      </template>\n");//end crudFromContent slot
            writer.append("    </crud-api-form>\n");//end crud-api-form
            writer.append("  </BasePage>\n");//end page
            writer.append("</template>\n");
            writer.append("\n");
            writer.close();
            logCretedFile(filePathName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void generateFrontList(String filePathName, String entityName, String tableName) {
        String entityNameLowerFirst = AppUtil.capitalizeFirstLetter(entityName, true);
        String upperTableName = AppUtil.upperLowerCaseString(tableName, false);
        String tableNameKebabCase = tableName.replace("_", "-");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePathName, false));
            writer.append("<script setup lang=\"ts\">\n");

            String breadcrumbsName = entityName + "Breadcrumb";
            //breadcrumbs
            writer.append("/* move this variable to /src/breadcrumbs/AppBreadcrumbs.ts or /src/breadcrumbs/AdminBreadcrumbs.ts or /src/BackendBreadcrumbs/BackendBreadcrumbs.ts \n");
            writer.append("export const ").append(breadcrumbsName).append(": Breadcrumb[] = [\n");
            writer.append("  {\n");
            writer.append("    label: 'model.").append(entityNameLowerFirst).append(".table',\n");
//            writer.append("    to: '/").append(tableNameKebabCase).append("',\n");
            writer.append("    to: '/").append(tableNameKebabCase).append("',\n");
//            writer.append("    to: `/${AdminRootPath}/").append(tableNameKebabCase).append("`,\n");
            writer.append("    icon: biFileEarmark,\n");
            writer.append("    translateLabel: true\n");
            writer.append("  },\n");
            writer.append("  ...detailItemFn('").append(tableNameKebabCase).append("')\n");
            writer.append("];\n");
            writer.append("*/\n");
            writer.append("\n");

            //router
            writer.append("/* move this object to /src/router/routes.ts, /src/router/adminRoutes.ts or /src/router/backendRoutes.ts or /src/router/frontendRoutes.ts \n");
            writer.append("      {\n");
            writer.append("        path: '").append(tableNameKebabCase).append("',\n");
            writer.append("        children: [\n");
            writer.append("          {\n");
            writer.append("            path: '',\n");
            writer.append("            meta: { pageName: 'model.").append(entityNameLowerFirst).append(".table', permission: [").append(entityName).append("Permission.list] },\n");
            writer.append("            component: () => import('@/pages/").append(tableNameKebabCase).append("/index.vue')\n");
            writer.append("          },\n");
            writer.append("          {\n");
            writer.append("            path: ':crud/:id/',\n");
            writer.append("            meta: {\n");
            writer.append("              pageName: 'model.").append(entityNameLowerFirst).append(".table',\n");
            writer.append("              permission: [").append(entityName).append("Permission.view],\n");
            writer.append("              breadcrumbs: ").append(entityName).append("\n");
            writer.append("            },\n");
            writer.append("            component: () => import('@/pages/").append(tableNameKebabCase).append("/view.vue')\n");
            writer.append("          }\n");
            writer.append("        ]\n");
            writer.append("      }\n");
            writer.append("*/\n");
            writer.append("\n");

            //Model
            writer.append("/* move this interface to /src/types/models.ts \n");
            writer.append("export interface ").append(entityName).append(" extends Id {\n");
            for (GenerateTableSrcItem src : propertyList) {
                String propertyName = src.getPropertyName();
                String propertyTypeName = src.getPropertyType();
                if (!exceptField(propertyName)) {
                    String typeScriptType = getTypscriptType(propertyTypeName);
                    boolean isNullable = src.isNullable();
                    String undefinedSign = isNullable ? "?" : "";
                    String typeScriptTypeFinal = !AppUtil.isEmpty(undefinedSign) ? typeScriptType + " " + TYPESCRIPT_OR_SIGN + " " + TYPESCRIPT_NULL : typeScriptType;
                    writer.append(" ").append(propertyName).append(undefinedSign).append(": ").append(typeScriptTypeFinal).append(";\n");
                }
            }
            writer.append("}\n");
            writer.append("*/\n");
            writer.append("\n");

            //Permission
            writer.append("/* move this object to /src/utils/appPermissionList.ts \n");
            writer.append("export const ").append(entityName).append("Permission= {\n");
            writer.append("  view: '").append(tableName).append("_view',\n");
            writer.append("  list: '").append(tableName).append("_list',\n");
            writer.append("  manage: '").append(tableName).append("_manage'\n");
            writer.append("}\n");
            writer.append("*/\n");
            writer.append("\n");

            //message
            writer.append("/* move this message object to /src/i18n/th/model.ts and /src/i18n/en/model.ts under model:{}  \n");
            writer.append("    ,").append(entityNameLowerFirst).append(": {\n");
            writer.append("      table: '").append(entityNameLowerFirst).append("',\n");
            for (GenerateTableSrcItem src : propertyList) {
                String propertyName = src.getPropertyName();
                if (!exceptField(propertyName)) {
                    writer.append("      ").append(propertyName).append(": '").append(propertyName).append("',\n");
                }
            }
            writer.append("    }\n");
            writer.append("*/\n");


            writer.append("import { ref } from 'vue';\n");
            writer.append("import { useLang } from '@/composables/useLang';\n");
            writer.append("import { useCrudList } from '@/composables/useCrudList';\n");
            writer.append("import { ").append(entityName).append(" } from '@/types/models';\n");
            writer.append("import { CrudListDataType, ICrudListHeader, ICrudListHeaderOptionSearchType } from '@/types/common';\n");
            writer.append("import CrudApiList from '@/components/base/CrudApiList.vue';\n");
            writer.append("import BasePage from '@/components/base/BasePage.vue';\n");
            writer.append("import { biFileEarmark } from '@quasar/extras/bootstrap-icons';\n");
            writer.append("import { ").append(entityName).append("Permission } from '@/utils/appPermissionList';\n");
            writer.append("import { useAppMeta } from '@/composables/useAppMeta';\n");
            writer.append("const { setTitle } = useAppMeta({ manualSet: true });\n");
            writer.append("const { t } = useLang();\n");
            writer.append("const headers = ref<ICrudListHeader[]>([\n");
            for (GenerateTableSrcItem src : propertyList) {
                String propertyName = src.getPropertyName();
                if (!exceptField(propertyName)) {

                    String crudListType = getCrudListType(src.getPropertyType());
                    String crudListSearchType = getCrudListSearchType(src.getPropertyType());
                    String crudListSearchOperation = getCrudListSearchOperation(src.getPropertyType());
                    boolean isClassRef = isObjectLink(src.getPropertyType());
                    if (!isClassRef) {

                        writer.append("  {\n");
                        writer.append("    label: 'model.").append(entityNameLowerFirst).append(".").append(propertyName).append("',\n");
                        writer.append("    column: '").append(propertyName).append("',\n");
                        writer.append("    type: CrudListDataType.").append(crudListType).append(",\n");
                        writer.append("    options: {\n");
                        writer.append("      searchable: true,\n");
                        writer.append("      sortable: true,\n");
                        writer.append("      fillable: true,\n");
                        if (crudListSearchType != null) {
                            writer.append("      searchType: ICrudListHeaderOptionSearchType.").append(crudListSearchType).append(",\n");
                            writer.append("      searchModel: '',\n");
                            writer.append("      searchOperation: '").append(crudListSearchOperation).append("'\n");
                        }
                        writer.append("    }\n");
                        writer.append("  },\n");
                    }
                }
            }
            //base tool
            writer.append("  {\n");
            writer.append("    label: 'base.tool',\n");
            writer.append("    type: CrudListDataType.BASE_TOOL,\n");
            writer.append("    options: {\n");
            writer.append("      fillable: true,\n");
            writer.append("      editButton: true,\n");
            writer.append("      deleteButton: true,\n");
            writer.append("      copyButton: true,\n");
            writer.append("      align: 'center'\n");
            writer.append("    }\n");
            writer.append("  },\n");

            writer.append("]);\n");
            writer.append("const {\n");
            writer.append("  crudName,\n");
            writer.append("  loading,\n");
            writer.append("  sort,\n");
            writer.append("  filteredList,\n");
            writer.append("  onItemCopy,\n");
            writer.append("  onPageNoChange,\n");
            writer.append("  onItemPerPageChange,\n");
            writer.append("  onSort,\n");
            writer.append("  onSortMode,\n");
            writer.append("  onReload,\n");
            writer.append("  filterText,\n");
            writer.append("  onAdvanceSearch,\n");
            writer.append("  onItemDelete,\n");
            writer.append("  fristLoad,\n");
            writer.append("  pages,\n");
            writer.append("  onItemClick,\n");
            writer.append("  onNewForm,\n");
            writer.append("  onKeywordSearch\n");
            writer.append("} = useCrudList<").append(entityName).append(">(\n");
            writer.append("  {\n");
            writer.append("    crudName: '").append(tableName).append("',\n");
            writer.append("    apiEndpoint: '/api',\n");
            writer.append("    fetchListOnload: true,\n");
            writer.append("    defaultSort: {\n");
            writer.append("      column: 'id',\n");
            writer.append("      mode: 'desc'\n");
            writer.append("    }\n");
            writer.append("  },\n");
            writer.append("  headers\n");
            writer.append(");\n");
            writer.append("</script>\n");

//            for (GenerateTableSrcItem src : propertyList) {
//                String propertyName = src.getPropertyName();
//                String propertyTypeName = src.getPropertyType();
//                if (!exceptField(propertyName)) {
//                    log.info("Property: {}  of SqlType: {}, isUnique : {}, length : {}, propertyTypeName : {} , isNullable :{}, isTypeTextArea :{}",
//                            src.getPropertyName(),
//                            src.getSqlType(),
//                            src.isUnique(),
//                            src.getLength(),
//                            src.getPropertyType(),
//                            src.isNullable(),
//                            src.isTypeTextArea()
//                    );
//                }
//            }

            writer.append("\n");
            writer.append("<template>\n");
            writer.append("  <BasePage>\n");
            writer.append("    <crud-api-list\n");
            writer.append("      :icon=\"biFileEarmark\"\n");
            writer.append("      :title=\"t('model.").append(entityNameLowerFirst).append(".table')\"\n");
            writer.append("      :crud-name=\"crudName\"\n");
            writer.append("      :loading=\"loading\"\n");
            writer.append("      :frist-load=\"fristLoad\"\n");
            writer.append("      :pages=\"pages\"\n");
            writer.append("      :headers=\"headers\"\n");
            writer.append("      :sort=\"sort\"\n");
            writer.append("      :list=\"filteredList\"\n");
            writer.append("      :show-search-text-box=\"false\"\n");
//            writer.append("      :is-frontend=\"true\"\n");
            writer.append("      :view-permission=\"").append(entityName).append("Permission.view\"\n");
            writer.append("      :manage-permission=\"").append(entityName).append("Permission.manage\"\n");
            writer.append("      @on-item-click=\"onItemClick\"\n");
            writer.append("      @on-item-copy=\"onItemCopy\"\n");
            writer.append("      @on-page-no-change=\"onPageNoChange\"\n");
            writer.append("      @on-items-perpage-change=\"onItemPerPageChange\"\n");
            writer.append("      @on-sort=\"onSort\"\n");
            writer.append("      @on-sort-mode=\"onSortMode\"\n");
            writer.append("      @on-reload=\"onReload\"\n");
            writer.append("      @update-search=\"filterText = $event\"\n");
            writer.append("      @on-advance-search=\"onAdvanceSearch\"\n");
            writer.append("      @on-keyword-search=\"onKeywordSearch\"\n");
            writer.append("      @on-item-delete=\"onItemDelete\"\n");
            writer.append("      @on-new-form=\"onNewForm\"\n");
            writer.append("    >\n");
            writer.append("    </crud-api-list>\n");
            writer.append("  </BasePage>\n");
            writer.append("</template>\n");
            writer.append("\n");
            writer.close();
            logCretedFile(filePathName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void generateFrontService(String filePathName, String entityName, String tableName) {
        String entityNameLowerFirst = AppUtil.capitalizeFirstLetter(entityName, true);
        String upperTableName = AppUtil.upperLowerCaseString(tableName, false);
        String tableNameKebabCase = tableName.replace("_", "-");
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePathName, false));

            //api service
            writer.append("// move this file to /src/api \n");

            writer.append("import { useAxios } from '@/composables/useAxios';\n");
            writer.append("import { ").append(entityName).append(", IApiListResponse } from '@/types/models';\n");
            writer.append("import { ResponseMessage } from '@/types/common';\n");
            writer.append("export default () => {\n");
            writer.append("  const { callAxios } = useAxios();\n");
            writer.append("  const findAll = async (q: string): Promise<IApiListResponse<").append(entityName).append("> | null> => {\n");
            writer.append("    return await callAxios<ApiListResponse<").append(entityName).append(">>({\n");
            writer.append("      API: `/api/").append(entityNameLowerFirst).append("${q}`,\n");
            writer.append("      method: 'GET',\n");
            writer.append("    });\n");
            writer.append("  };\n");
            writer.append("  const findById = async (id: number): Promise<").append(entityName).append(" | null> => {\n");
            writer.append("    return await callAxios<").append(entityName).append(">({\n");
            writer.append("      API: `/api/").append(entityNameLowerFirst).append("/${id}`,\n");
            writer.append("      method: 'GET',\n");
            writer.append("    });\n");
            writer.append("  };\n");
            writer.append("  const crudCreate = async (request: ").append(entityName).append("): Promise<").append(entityName).append(" | null> => {\n");
            writer.append("    return await callAxios<").append(entityName).append(">({\n");
            writer.append("      API: '/api/").append(entityNameLowerFirst).append("',\n");
            writer.append("      method: 'POST',\n");
            writer.append("      body: {\n");
            writer.append("        ").append(entityNameLowerFirst).append(": request,\n");
            writer.append("      },\n");
            writer.append("    });\n");
            writer.append("  };\n");
            writer.append("  const crudUpdate = async (id: number, request: ").append(entityName).append("): Promise<").append(entityName).append(" | null> => {\n");
            writer.append("    return await callAxios<").append(entityName).append(">({\n");
            writer.append("      API: `/api/").append(entityNameLowerFirst).append("/${id}`,\n");
            writer.append("      method: 'PUT',\n");
            writer.append("      body: {\n");
            writer.append("        ").append(entityNameLowerFirst).append(": request,\n");
            writer.append("      },\n");
            writer.append("    });\n");
            writer.append("  };\n");
            writer.append("  const deleteById = async (id: number): Promise<ResponseMessage | null> => {\n");
            writer.append("    return await callAxios<ResponseMessage>({\n");
            writer.append("      API: `/api/").append(entityNameLowerFirst).append("/${id}`,\n");
            writer.append("      method: 'DELETE',\n");
            writer.append("    });\n");
            writer.append("  };\n");
            writer.append("  return {\n");
            writer.append("    findAll,\n");
            writer.append("    findById,\n");
            writer.append("    crudCreate,\n");
            writer.append("    crudUpdate,\n");
            writer.append("    deleteById\n");
            writer.append("  };\n");
            writer.append("};\n");
            writer.close();
            logCretedFile(filePathName);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    //End Default theme Quasar SSR

    private boolean exceptField(String propertyName) {
        return propertyName.equals("createdDate") || propertyName.equals("createdUser") || propertyName.equals("updatedDate") || propertyName.equals("updatedUser") || propertyName.equals("deleted");
    }

    private String getTypscriptType(String propertyType) {
        if (isObjectLink(propertyType)) {
            return getClassNameFromPackageString(propertyType);
        } else if (propertyType.equals(TYPE_BOOLEAN)) {
            return TYPESCRIPT_BOOLEAN;
        } else if (propertyType.equals(TYPE_STRING) || propertyType.equals(TYPE_LOCAL_DATE) || propertyType.equals(TYPE_LOCAL_DATETIME)) {
            return TYPESCRIPT_STRING;
        } else if (propertyType.equals(TYPE_FLOAT) || propertyType.equals(TYPE_BIG_DECIMAL) || propertyType.equals(TYPE_INTEGER)) {
            return TYPESCRIPT_NUMBER;
        }
        return TYPESCRIPT_UNDEFINED;
    }

    private String getCrudListType(String propertyType) {
        if (propertyType.equals(TYPE_BOOLEAN)) {
            return "STATUS";
        } else if (propertyType.equals(TYPE_STRING) || isObjectLink(propertyType)) {
            return "TEXT";
        } else if (propertyType.equals(TYPE_LOCAL_DATE)) {
            return "DATE";
        } else if (propertyType.equals(TYPE_LOCAL_DATETIME)) {
            return "DATE_TIME";
        } else if (propertyType.equals(TYPE_FLOAT) || propertyType.equals(TYPE_BIG_DECIMAL) || propertyType.equals(TYPE_INTEGER)) {
            return "NUMBER_FORMAT";
        }
        return "TEXT";
    }

    private String getCrudListSearchType(String propertyType) {
        if (propertyType.equals(TYPE_BOOLEAN)) {
            return "BOOLEAN";
        } else if (propertyType.equals(TYPE_STRING) || isObjectLink(propertyType)) {
            return "TEXT";
        } else if (propertyType.equals(TYPE_LOCAL_DATE)) {
            return "DATE";
        } else if (propertyType.equals(TYPE_LOCAL_DATETIME)) {
            return "DATE_TIME";
        } else if (propertyType.equals(TYPE_FLOAT) || propertyType.equals(TYPE_BIG_DECIMAL) || propertyType.equals(TYPE_INTEGER)) {
            return "NUMBER";
        }
        return null;
    }

    private String getCrudListSearchOperation(String propertyType) {
        if (propertyType.equals(TYPE_BOOLEAN)) {
            return "=";
        } else if (propertyType.equals(TYPE_STRING) || isObjectLink(propertyType)) {
            return ":";
        } else if (propertyType.equals(TYPE_LOCAL_DATE) || propertyType.equals(TYPE_LOCAL_DATETIME)) {
            return ">=";
        } else if (propertyType.equals(TYPE_FLOAT) || propertyType.equals(TYPE_BIG_DECIMAL) || propertyType.equals(TYPE_INTEGER)) {
            return ">=";
        }
        return "=";
    }

    private String getJavaType(String propertyType) {
        if (isObjectLink(propertyType)) {
            return getClassNameFromPackageString(propertyType);
        } else if (propertyType.equals(TYPE_BOOLEAN)) {
            return TYPE_BOOLEAN;
        } else if (propertyType.equals(TYPE_STRING)) {
            return AppUtil.capitalizeFirstLetter(TYPE_STRING, false);
        } else if (propertyType.equals(TYPE_LOCAL_DATE)) {
            return TYPE_LOCAL_DATE;
        } else if (propertyType.equals(TYPE_LOCAL_DATETIME)) {
            return TYPE_LOCAL_DATETIME;
        } else if (propertyType.equals(TYPE_FLOAT)) {
            return AppUtil.capitalizeFirstLetter(TYPE_FLOAT, false);
        } else if (propertyType.equals(TYPE_BIG_DECIMAL)) {
            return "BigDecimal";
        } else if (propertyType.equals(TYPE_INTEGER)) {
            return "int";
        }
        return null;
    }

    private String getTypscriptTypeDefaultValue(String propertyType) {
        if (isObjectLink(propertyType)) {
            return TYPESCRIPT_NULL;
        } else if (propertyType.equals(TYPE_BOOLEAN)) {
            return "true";
        } else if (propertyType.equals(TYPE_STRING) || propertyType.equals(TYPE_LOCAL_DATETIME)) {
            return "''";
        } else if (propertyType.equals(TYPE_LOCAL_DATE)) {
            return "getCurrentDateByFormat()";
        } else if (propertyType.equals(TYPE_FLOAT) || propertyType.equals(TYPE_BIG_DECIMAL) || propertyType.equals(TYPE_INTEGER)) {
            return "0";
        }
        return TYPESCRIPT_NULL;
    }

    private boolean isTypeTextArea(String getSqlType, String propertyType) {
        return getSqlType != null && propertyType.equals(TYPE_STRING) && AppUtil.findStringInString(getSqlType, TYPE_TEXT);
    }

    private boolean isObjectLink(String propertyType) {
        return AppUtil.findStringInString(propertyType, DOT);
    }

    private String getClassNameFromPackageString(String fullClassName) {
        if (!isObjectLink(fullClassName)) {
            return null;
        }
        // Split the full class name by the dot (.)
        String[] parts = fullClassName.split("\\.");
        return parts[parts.length - 1];
    }

    private Class<?> getClassFromName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}

