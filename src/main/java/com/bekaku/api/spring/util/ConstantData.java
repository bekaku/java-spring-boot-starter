package com.bekaku.api.spring.util;

import java.util.List;

public class ConstantData {
    public static final String AUTHORIZATION = "Authorization";
    public static final String ACCEPT_APIC_LIENT = "Accept-Apiclient";
    public static final String USER_AGENT = "User-Agent";
    public static final String ACCEPT_LANGUGE = "Accept-Language";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String X_REAL_IP = "X-Real-IP";
    public static final String HOST_NAME = "Host";

    public static final String SEARCH_SEPARATOR_ATT = ",";
    public static final String SEARCH_PARAMETER_ATT = "_q";
    public static final String SEARCH_SIGN_MATCH = ":";
    public static final String SEARCH_SIGN_GREATER_THAN = ">";
    public static final String SEARCH_SIGN_GREATER_THAN_EQUA = ">=";
    public static final String SEARCH_SIGN_LESS_THAN = "<";
    public static final String SEARCH_SIGN_LESS_THAN_EQUA = "<=";
    public static final String SEARCH_SIGN_EQUA = "=";
    public static final String SEARCH_SIGN_NOT_EQUA = "!=";

    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_DELETE = "DELETE";

    public static final int REQUEST_FROM_WEB = 1;
    public static final int REQUEST_FROM_IOS = 2;
    public static final int REQUEST_FROM_ANDROID = 3;

    public static final int FEED_POST_LEVEL_ORGANIZATION = 1;
    public static final int FEED_POST_LEVEL_PUBLIC = 2;

    public static final String UNDER_SCORE = "_";
    public static final String MIDDLE_SCORE = "-";
    public static final String DOT = ".";
    public static final String COLON = ":";
    public static final String BACK_SLACK = "/";

    public static final String DEFAULT_PROJECT_ROOT_PATH = "src/main/java/com/bekaku/api/spring";
    public static final String DEFAULT_PROJECT_ROOT_PACKAGE = "com.bekaku.api.spring";

    public static final String IMAGE = "image";
    public static final String IMAGES = "images";
    public static final String FILES = "files";

    public static final String FILES_UPLOAD_ATT = "_filesUploadName";

    public static final String SORT_MODE_ASC = "asc";
    public static final String SORT_MODE_desc = "desc";
    public static final String SERVER_STATUS = "status";
    public static final String SERVER_MESSAGE = "message";
    public static final String SERVER_SUCCESS = "success";
    public static final String DATA_ATT = "data";
    public static final String SERVER_TIMESTAMP = "timestamp";

    public static final int PERMISSION_OPERATION_TYPE_CRUD = 1;
    public static final int PERMISSION_OPERATION_TYPE_REPORT = 2;
    public static final int PERMISSION_OPERATION_TYPE_OTHER = 3;

    public static final Long SYSTEM_CONFIGURATION_ID = 1L;

    public static final String WEB_SOCKET_ENDPOINT = "/_websocket";
    public static final String SECURED_CHAT_HISTORY = "/secured/history";
    public static final String SECURED_CHAT = "/secured/chat";
    public static final String SECURED_CHAT_ROOM = "/secured/room";
    public static final String SECURED_CHAT_SPECIFIC_USER = "/secured/user/queue/specific-user";
    public static final String AM = "AM";
    public static final String PM = "PM";
    public static final String FIME_MEME_IMAGE_JPEG = "image/jpeg";

    public static final String MONDAY = "MONDAY";
    public static final String THURSDAY = "THURSDAY";
    public static final String FRIST_JANUARY_MONTH_DATE = "-01-01";
    public static final String FRIST_FABRIARY_MONTH_DATE = "-02-01";
    public static final String FRIST_MARCH_MONTH_DATE = "-03-01";
    public static final String FRIST_APRIL_MONTH_DATE = "-04-01";
    public static final String FRIST_MAY_MONTH_DATE = "-05-01";
    public static final String FRIST_JUNE_MONTH_DATE = "-06-01";
    public static final String FRIST_JULY_MONTH_DATE = "-07-01";
    public static final String FRIST_AUGUST_MONTH_DATE = "-08-01";
    public static final String FRIST_SEPTEMBER_MONTH_DATE = "-09-01";
    public static final String FRIST_OCTOBER_MONTH_DATE = "-10-01";
    public static final String FRIST_NOVEMBER_MONTH_DATE = "-11-01";
    public static final String FRIST_DECEMBER_MONTH_DATE = "-12-01";

    public static final double COMMENT_REACTION_WEIGHT = 0.5;
    public static final String CHART_TYPE_SCATTER = "scatter";
    public static final String CHART_TYPE_LINE = "line";
    public static final String CHART_FILL_TYPE_IMAGE = "image";
    public static final String CHART_FILL_TYPE_SOLID = "solid";
    public static final String COLOR_TRANSPARENT = "transparent";
    public static final String COLOR_PRIMARY = "#00aba9";
    public static final String COLOR_WARNING = "#ff8a06";
    public static final String COLOR_SUCCESS = "#2dd36f";
    public static final String COLOR_DANGER = "#ec1c27";
    public static final String COLOR_ACTIVE = "#2196f3";
    public static final String COLOR_ADVANCE = "#00bcd4";
    public static final String APP_ICON_PATH = "/icons/ss";
    public static final String NOTIFY_DATA_FUNCTION_CODE_ATT = "functionCode";
    public static final String NOTIFY_DATA_FUNCTION_ID_ATT = "functionId";
    public static final String NOTIFY_DATA_NOTIFY_ID_ATT = "notificatoinId";
    public static final List<Integer> MANAGEMENT_ORG_LEVEL = List.of(0, 1);
    public static final String AT_SIGN = "@";

    public static String CONTENT_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final int ONLINE_MINUTES_CLAIM = 5;
    public static final int[][] USER_LEVEL_CHART_TABLE = {
            {0, 0, 0, 0, 0, 0},//Row 0
            {0, 1, 1, 2, 3, 3},//Row 1
            {0, 1, 2, 3, 3, 4},//Row 2
            {0, 2, 3, 3, 4, 5},//Row 3
            {0, 3, 3, 4, 5, 5},//Row 4
            {0, 3, 4, 5, 5, 5},//Row 5
    };

    public static final int USERLEVEL_MAX_X = 5;
    public static final int USERLEVEL_MAX_Y = 5;
    public static final int CONTENT_LIMIT_LENGTH = 45;

    public static final String COOKIE_JWT_REFRESH_TOKEN = "_device_jid";
    public static final String EMAIL_TEMPLATE_FORGOT = "mail-forgot-password-template.html";
    public static final String EMAIL_TEMPLATE = "mail-template.html";

}
