package com.bekaku.api.spring.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SearchSpecification<T> implements Specification<T> {

    private final List<SearchCriteria> criteriaList;
    private final String keyword;
    private final List<String> keywordColumns;

    public SearchSpecification(List<SearchCriteria> criteriaList, String keyword, List<String> keywordColumns) {
        this.criteriaList = criteriaList != null ? new ArrayList<>(criteriaList) : new ArrayList<>();
        this.keyword = keyword;
        this.keywordColumns = keywordColumns;
    }

    public void add(SearchCriteria criteria) {
        criteriaList.add(criteria);
    }
    // 1. Create a method to convert a string (e.g., "module.code") into a JPA path and automatically perform a join.
    private Path<?> getPath(Root<T> root, String key) {
        if (!key.contains(".")) {
            // If there is no dot, search within the table normally.
            return root.get(key);
        }

        // If there is a dot, such as in "module.code" or "user.department.name"
        String[] parts = key.split("\\.");

        // Use a LEFT JOIN in case the data is null, so it won't be lost from the results.
        Join<?, ?> join = root.join(parts[0], JoinType.LEFT);

        // Loop for nested joins (multiple joins)
        for (int i = 1; i < parts.length - 1; i++) {
            join = join.join(parts[i], JoinType.LEFT);
        }

        // Returns the last field to be searched (e.g., "code")
        return join.get(parts[parts.length - 1]);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder builder) {

        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();
        //add criteria to predicates
        for (SearchCriteria criteria : criteriaList) {

            // 🔥 เรียกใช้ getPath แทน root.get(criteria.getKey())
            Path<?> path = getPath(root, criteria.getKey());

            if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
                if (criteria.isDate()) {
                    predicates.add(builder.greaterThan(path.as(LocalDate.class), (LocalDate) criteria.getValue()));
                } else {
                    predicates.add(builder.greaterThan(path.as(String.class), criteria.getValue().toString()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
                if (criteria.isDate()) {
                    predicates.add(builder.lessThan(path.as(LocalDate.class), (LocalDate) criteria.getValue()));
                } else {
                    predicates.add(builder.lessThan(path.as(String.class), criteria.getValue().toString()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
                if (criteria.isDate()) {
                    predicates.add(builder.greaterThanOrEqualTo(path.as(LocalDate.class), (LocalDate) criteria.getValue()));
                } else {
                    predicates.add(builder.greaterThanOrEqualTo(path.as(String.class), criteria.getValue().toString()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
                if (criteria.isDate()) {
                    predicates.add(builder.lessThanOrEqualTo(path.as(LocalDate.class), (LocalDate) criteria.getValue()));
                } else {
                    predicates.add(builder.lessThanOrEqualTo(path.as(String.class), criteria.getValue().toString()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
                predicates.add(builder.notEqual(path, criteria.getValue()));

            } else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                predicates.add(builder.equal(path, criteria.getValue()));

            } else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
                if (criteria.isDate()) {
                    predicates.add(builder.equal(path, criteria.getValue()));
                } else {
                    predicates.add(builder.like(
                            builder.lower(path.as(String.class)),
                            "%" + criteria.getValue().toString().toLowerCase() + "%"));
                }

            } else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
                if (criteria.isDate()) {
                    predicates.add(builder.equal(path, criteria.getValue()));
                } else {
                    predicates.add(builder.like(
                            builder.lower(path.as(String.class)),
                            criteria.getValue().toString().toLowerCase() + "%"));
                }

            } else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
                if (criteria.isDate()) {
                    predicates.add(builder.equal(path, criteria.getValue()));
                } else {
                    predicates.add(builder.like(
                            builder.lower(path.as(String.class)),
                            "%" + criteria.getValue().toString().toLowerCase()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.IN)) {
                // Check first if the value passed is a Collection (List/Set).
                if (criteria.getValue() instanceof java.util.Collection) {
                    predicates.add(path.in((java.util.Collection<?>) criteria.getValue()));
                } else {
                    // If a single value is passed
                    predicates.add(path.in(criteria.getValue()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
                // For NOT_IN, we wrap path.in() with builder.not.
                if (criteria.getValue() instanceof java.util.Collection) {
                    predicates.add(builder.not(path.in((java.util.Collection<?>) criteria.getValue())));
                } else {
                    predicates.add(builder.not(path.in(criteria.getValue())));
                }

            } else if (criteria.getOperation().equals(SearchOperation.IS_NULL)) {
                predicates.add(builder.isNull(path));

            } else if (criteria.getOperation().equals(SearchOperation.IS_NOT_NULL)) {
                predicates.add(builder.isNotNull(path));
            }
        }

        // ส่วนของ Global Keyword
        if (keyword != null && !keyword.trim().isEmpty() && keywordColumns != null && !keywordColumns.isEmpty()) {
            List<Predicate> keywordPredicates = new ArrayList<>();
            for (String col : keywordColumns) {
                // Use getPath for Keyword as well to enable searching across tables.
                Path<?> path = getPath(root, col);
                keywordPredicates.add(builder.like(builder.lower(path.as(String.class)), "%" + keyword.toLowerCase() + "%"));
            }
            // Perform an OR operation on the sub-conditions and then insert them into the main predicate (which will be an AND operation with the main condition).
            predicates.add(builder.or(keywordPredicates.toArray(new Predicate[0])));
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
