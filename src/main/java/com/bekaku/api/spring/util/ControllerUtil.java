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

        // 1. ดึง Filter ปรกติ (_q)
        List<SearchCriteria> criteria = getSearchCriteriaList(request);

        // 2. ดึง Global Keyword (_keyword)
        String keyword = request.getParameter(ConstantData.KEYWORD_PARAMETER_ATT);

        // 3. คืนค่าเป็น Specification ที่พร้อมใช้งาน
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

        // convert boolean criteria
        if ("true".equals(s) || "false".equals(s)) {
            list.add(new SearchCriteria(field, Boolean.parseBoolean(s), operation));
        }
        // convert date criteria
        else if (DateUtil.isValidDate(value, DateTimeFormatter.ISO_LOCAL_DATE)) {
            SearchCriteria criteriaDate = new SearchCriteria(field, DateUtil.parseDate(value, DateTimeFormatter.ISO_LOCAL_DATE), operation);
            criteriaDate.setDate(true); // เซ็ต flag ว่าเป็น Date ตามโค้ดเดิมของคุณ
            list.add(criteriaDate);
        }
        // string / other fallback
        else {
            list.add(new SearchCriteria(field, value, operation));
        }
    }
}
