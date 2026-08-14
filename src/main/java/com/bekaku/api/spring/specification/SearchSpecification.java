package com.bekaku.api.spring.specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
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

    private Path<?> getPath(Root<T> root, String key) {
        if (!key.contains(".")) {
            return root.get(key);
        }

        String[] parts = key.split("\\.");
        Join<?, ?> join = root.join(parts[0], JoinType.LEFT);

        for (int i = 1; i < parts.length - 1; i++) {
            join = join.join(parts[i], JoinType.LEFT);
        }

        return join.get(parts[parts.length - 1]);
    }

    /**
     * Safely converts any Path (including numeric/bigint fields) to an Expression<String>
     * to avoid PostgreSQL 'lower(bigint) does not exist' error.
     */
    private Expression<String> getAsString(CriteriaBuilder builder, Path<?> path) {
        if (path.getJavaType() != null && String.class.isAssignableFrom(path.getJavaType())) {
            return path.as(String.class);
        }
        // Force SQL string concatenation ('' || column) to coerce non-string types to text in PostgreSQL
        return builder.concat("", path.as(String.class));
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : criteriaList) {

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
                            builder.lower(getAsString(builder, path)),
                            "%" + criteria.getValue().toString().toLowerCase() + "%"));
                }

            } else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
                if (criteria.isDate()) {
                    predicates.add(builder.equal(path, criteria.getValue()));
                } else {
                    predicates.add(builder.like(
                            builder.lower(getAsString(builder, path)),
                            criteria.getValue().toString().toLowerCase() + "%"));
                }

            } else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
                if (criteria.isDate()) {
                    predicates.add(builder.equal(path, criteria.getValue()));
                } else {
                    predicates.add(builder.like(
                            builder.lower(getAsString(builder, path)),
                            "%" + criteria.getValue().toString().toLowerCase()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.IN)) {
                if (criteria.getValue() instanceof java.util.Collection) {
                    predicates.add(path.in((java.util.Collection<?>) criteria.getValue()));
                } else {
                    predicates.add(path.in(criteria.getValue()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
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

        // Global Keyword Search
        if (keyword != null && !keyword.trim().isEmpty() && keywordColumns != null && !keywordColumns.isEmpty()) {
            List<Predicate> keywordPredicates = new ArrayList<>();
            for (String col : keywordColumns) {
                Path<?> path = getPath(root, col);
                keywordPredicates.add(builder.like(
                        builder.lower(getAsString(builder, path)),
                        "%" + keyword.toLowerCase() + "%"
                ));
            }
            predicates.add(builder.or(keywordPredicates.toArray(new Predicate[0])));
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}