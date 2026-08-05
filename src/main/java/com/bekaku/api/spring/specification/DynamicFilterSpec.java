package com.bekaku.api.spring.specification;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicFilterSpec<T> implements Specification<T> {

    private final String q;
    private final String keyword;
    private final List<String> searchColumns;

    public DynamicFilterSpec(String q, String keyword, List<String> searchColumns) {
        this.q = q;
        this.keyword = keyword;
        this.searchColumns = searchColumns;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        // 1. จัดการ _q (Filter แบบระบุคอลัมน์)
        if (q != null && !q.trim().isEmpty()) {
            String[] filters = q.split(",");
            Pattern pattern = Pattern.compile("^([a-zA-Z0-9_]+)(>=|<=|!=|>|<|=|:)(.+)$");

            for (String filter : filters) {
                Matcher matcher = pattern.matcher(filter);
                if (matcher.find()) {
                    String field = matcher.group(1);
                    String operator = matcher.group(2);
                    String value = matcher.group(3);

                    Path<Object> path = root.get(field);
                    Class<?> type = path.getJavaType();

                    // แปลง String เป็น Type ของ Field ใน Database
                    Object parsedValue = parseValue(type, value);

                    switch (operator) {
                        case ":":
                            predicates.add(cb.like(cb.lower(root.get(field).as(String.class)), "%" + value.toLowerCase() + "%"));
                            break;
                        case "=":
                            predicates.add(cb.equal(root.get(field), parsedValue));
                            break;
                        case "!=":
                            predicates.add(cb.notEqual(root.get(field), parsedValue));
                            break;
                        case ">":
                            predicates.add(cb.greaterThan(root.<Comparable>get(field), (Comparable) parsedValue));
                            break;
                        case ">=":
                            predicates.add(cb.greaterThanOrEqualTo(root.<Comparable>get(field), (Comparable) parsedValue));
                            break;
                        case "<":
                            predicates.add(cb.lessThan(root.<Comparable>get(field), (Comparable) parsedValue));
                            break;
                        case "<=":
                            predicates.add(cb.lessThanOrEqualTo(root.<Comparable>get(field), (Comparable) parsedValue));
                            break;
                    }
                }
            }
        }

        // 2. จัดการ _keyword (Global Search)
        if (keyword != null && !keyword.trim().isEmpty() && searchColumns != null && !searchColumns.isEmpty()) {
            List<Predicate> keywordPredicates = new ArrayList<>();
            for (String col : searchColumns) {
                keywordPredicates.add(cb.like(cb.lower(root.get(col).as(String.class)), "%" + keyword.toLowerCase() + "%"));
            }
            // นำเงื่อนไข OR ของแต่ละคอลัมน์มารวมกัน
            predicates.add(cb.or(keywordPredicates.toArray(new Predicate[0])));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    // Helper สำหรับแปลง String จาก URL เป็น Data Type ที่ถูกต้องของ Entity
    private Object parseValue(Class<?> type, String value) {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) return Boolean.parseBoolean(value);
        if (value.equalsIgnoreCase("null")) return null;
        if (type == Integer.class || type == int.class) return Integer.parseInt(value);
        if (type == Long.class || type == long.class) return Long.parseLong(value);
        if (type == Double.class || type == double.class) return Double.parseDouble(value);
        if (type == LocalDateTime.class) return LocalDateTime.parse(value);
        return value; // Default to String
    }
}
