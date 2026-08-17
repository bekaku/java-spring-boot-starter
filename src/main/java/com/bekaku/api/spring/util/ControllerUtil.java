package com.bekaku.api.spring.util;

import com.bekaku.api.spring.specification.SearchCriteria;
import com.bekaku.api.spring.specification.SearchOperation;
import com.bekaku.api.spring.specification.SearchSpecification;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ControllerUtil {

    // Regex group: (1)field (2)operator (3)value
    // Supports field names with underscores or dots, e.g., user.name
    private static final Pattern SEARCH_PATTERN = Pattern.compile("^([a-zA-Z0-9_.]+)(>=|<=|!=|>|<|=|:)(.+)$");

    public static <T> SearchSpecification<T> buildSpecification(HttpServletRequest request, List<String> keywordColumns) {

//        log.info("_q: {}", request.getParameter(ConstantData.SEARCH_PARAMETER_ATT));
        List<SearchCriteria> criteria = getSearchCriteriaList(request);
        String keyword = request.getParameter(ConstantData.KEYWORD_PARAMETER_ATT);
        return new SearchSpecification<>(criteria, keyword, keywordColumns);
    }

    public static List<SearchCriteria> getSearchCriteriaList(HttpServletRequest request) {
        String searchParameter = request.getParameter(ConstantData.SEARCH_PARAMETER_ATT);
        return getSearchCriteriaList(searchParameter);
    }

    public static List<SearchCriteria> getSearchCriteriaList(String searchParameter) {
        List<SearchCriteria> list = new ArrayList<>();
        if (searchParameter == null || searchParameter.trim().isEmpty()) {
            return list;
        }

        String[] splitParams = searchParameter.split(ConstantData.SEARCH_SEPARATOR_ATT);
        for (String param : splitParams) {
            if (param == null || param.trim().isEmpty()) continue;

            Matcher matcher = SEARCH_PATTERN.matcher(param);
            if (matcher.find()) {
                String field = matcher.group(1);
                String operatorStr = matcher.group(2);
                String value = matcher.group(3);

                SearchOperation operation = mapToSearchOperation(operatorStr);
                if (operation != null) {
                    addSearchCriteriaParam(field, value, operation, list);
                }
            } else {
                log.warn("Invalid search parameter format: {}", param);
            }
        }
        return list;
    }

    private static SearchOperation mapToSearchOperation(String operatorStr) {
        switch (operatorStr) {
            case ":":
                return SearchOperation.MATCH;
            case "=":
                return SearchOperation.EQUAL;
            case "!=":
                return SearchOperation.NOT_EQUAL;
            case ">":
                return SearchOperation.GREATER_THAN;
            case ">=":
                return SearchOperation.GREATER_THAN_EQUAL;
            case "<":
                return SearchOperation.LESS_THAN;
            case "<=":
                return SearchOperation.LESS_THAN_EQUAL;
            default:
                return null;
        }
    }

    private static void addSearchCriteriaParam(String field, String value, SearchOperation operation, List<SearchCriteria> list) {
        String s = value.toLowerCase(Locale.ROOT);

        // 1. ตรวจสอบว่าเป็น Array/List (มีเครื่องหมาย ,) หรือไม่
        if (value.contains(",")) {
            // แตกค่าด้วยคอมมา และลบช่องว่าง (trim) ออกจากแต่ละค่า
            String[] arrayValues = value.split(",");
            List<Object> parsedValues = new ArrayList<>();

            for (String val : arrayValues) {
                String cleanVal = val.trim();
                String lowerCleanVal = cleanVal.toLowerCase(Locale.ROOT);

                // พยายาม Parse ค่าข้างใน Array ให้เป็น Boolean หรือ Date ถ้ารองรับ
                if ("true".equals(lowerCleanVal) || "false".equals(lowerCleanVal)) {
                    parsedValues.add(Boolean.parseBoolean(lowerCleanVal));
                } else if (DateUtil.isValidDate(cleanVal, DateTimeFormatter.ISO_LOCAL_DATE)) {
                    parsedValues.add(DateUtil.parseDate(cleanVal, DateTimeFormatter.ISO_LOCAL_DATE));
                } else {
                    parsedValues.add(cleanVal);
                }
            }

            // หมายเหตุ: อาจจะต้องเพิ่ม SearchOperation.IN เข้าไปใน Enum ของคุณ
            // เพื่อใช้ระบุว่าค่านี้เป็น List ที่ต้องใช้ Query แบบ IN(...)
            list.add(new SearchCriteria(field, parsedValues, SearchOperation.IN));
        }
        // 2. convert boolean criteria (กรณีไม่ใช่ Array)
        else if ("true".equals(s) || "false".equals(s)) {
            list.add(new SearchCriteria(field, Boolean.parseBoolean(s), operation));
        }
        // 3. convert date criteria (กรณีไม่ใช่ Array)
        else if (DateUtil.isValidDate(value, DateTimeFormatter.ISO_LOCAL_DATE)) {
            SearchCriteria criteriaDate = new SearchCriteria(field, DateUtil.parseDate(value, DateTimeFormatter.ISO_LOCAL_DATE), operation);
            criteriaDate.setDate(true);
            list.add(criteriaDate);
        }
        // 4. string / other fallback (กรณีไม่ใช่ Array)
        else {
            list.add(new SearchCriteria(field, value, operation));
        }
    }

//    private static void addSearchCriteriaParam(String field, String value, SearchOperation operation, List<SearchCriteria> list) {
//        String s = value.toLowerCase(Locale.ROOT);
//
//        // convert boolean criteria
//        if ("true".equals(s) || "false".equals(s)) {
//            list.add(new SearchCriteria(field, Boolean.parseBoolean(s), operation));
//        }
//        // convert date criteria
//        else if (DateUtil.isValidDate(value, DateTimeFormatter.ISO_LOCAL_DATE)) {
//            SearchCriteria criteriaDate = new SearchCriteria(field, DateUtil.parseDate(value, DateTimeFormatter.ISO_LOCAL_DATE), operation);
//            criteriaDate.setDate(true); // เซ็ต flag ว่าเป็น Date ตามโค้ดเดิมของคุณ
//            list.add(criteriaDate);
//        }
//        // string / other fallback
//        else {
//            list.add(new SearchCriteria(field, value, operation));
//        }
//    }
}
