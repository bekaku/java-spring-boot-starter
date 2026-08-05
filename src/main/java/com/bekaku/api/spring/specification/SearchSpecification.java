package com.bekaku.api.spring.specification;

import com.bekaku.api.spring.util.DateUtil;
import com.bekaku.api.spring.util.ConstantData;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchSpecification<T> implements Specification<T> {

    private List<SearchCriteria> list;
    private final String keyword;
    private final List<String> keywordColumns;

    public SearchSpecification(List<SearchCriteria> list, String keyword, List<String> keywordColumns) {
        this.list = list;
        this.keyword = keyword;
        this.keywordColumns = keywordColumns;
    }

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> cq, CriteriaBuilder builder) {

        //create a new predicate list
        List<Predicate> predicates = new ArrayList<>();
        //add add criteria to predicates
        for (SearchCriteria criteria : list) {

            if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
                if (criteria.isDate()) {
                    predicates.add(builder.greaterThan(root.get(criteria.getKey()), (LocalDate) criteria.getValue()));
                } else {
                    predicates.add(builder.greaterThan(root.get(criteria.getKey()), criteria.getValue().toString()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
                if (criteria.isDate()) {
                    predicates.add(builder.lessThan(root.get(criteria.getKey()), (LocalDate) criteria.getValue()));
                } else {
                    predicates.add(builder.lessThan(
                            root.get(criteria.getKey()), criteria.getValue().toString()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
                if (criteria.isDate()) {
                    predicates.add(builder.greaterThanOrEqualTo(root.get(criteria.getKey()), (LocalDate) criteria.getValue()));
                } else {
                    predicates.add(builder.greaterThanOrEqualTo(
                            root.get(criteria.getKey()), criteria.getValue().toString()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
                if (criteria.isDate()) {
                    predicates.add(builder.lessThanOrEqualTo(root.get(criteria.getKey()), (LocalDate) criteria.getValue()));
                } else {
                    predicates.add(builder.lessThanOrEqualTo(
                            root.get(criteria.getKey()), criteria.getValue().toString()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
                predicates.add(builder.notEqual(
                        root.get(criteria.getKey()), criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                predicates.add(builder.equal(root.get(criteria.getKey()), criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
                if (criteria.isDate()) {
                    predicates.add(builder.equal(root.get(criteria.getKey()), criteria.getValue()));
                } else {
                    predicates.add(builder.like(
                            builder.lower(root.get(criteria.getKey())),
                            "%" + criteria.getValue().toString().toLowerCase() + "%"));
                }

            } else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
                if (criteria.isDate()) {
                    predicates.add(builder.equal(root.get(criteria.getKey()), criteria.getValue()));
                } else {
                    predicates.add(builder.like(
                            builder.lower(root.get(criteria.getKey())),
                            criteria.getValue().toString().toLowerCase() + "%"));
                }

            } else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
                if (criteria.isDate()) {
                    predicates.add(builder.equal(root.get(criteria.getKey()), criteria.getValue()));
                } else {
                    predicates.add(builder.like(
                            builder.lower(root.get(criteria.getKey())),
                            "%" + criteria.getValue().toString().toLowerCase()));
                }

            } else if (criteria.getOperation().equals(SearchOperation.IN)) {
                predicates.add(builder.in(root.get(criteria.getKey())).value(criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.NOT_IN)) {
                predicates.add(builder.not(root.get(criteria.getKey())).in(criteria.getValue()));
            } else if (criteria.getOperation().equals(SearchOperation.IS_NULL)) {
                predicates.add(builder.isNull(root.get(criteria.getKey())));
            } else if (criteria.getOperation().equals(SearchOperation.IS_NOT_NULL)) {
                predicates.add(builder.isNotNull(root.get(criteria.getKey())));
            }
        }
        if (keyword != null && !keyword.trim().isEmpty() && keywordColumns != null && !keywordColumns.isEmpty()) {
            List<Predicate> keywordPredicates = new ArrayList<>();
            for (String col : keywordColumns) {
                // สร้างเงื่อนไข LIKE %keyword% สำหรับแต่ละคอลัมน์
                keywordPredicates.add(builder.like(builder.lower(root.get(col).as(String.class)), "%" + keyword.toLowerCase() + "%"));
            }
            // นำเงื่อนไขย่อยมาทำ OR กัน แล้วยัดเข้า Predicate หลัก (ซึ่งจะเป็น AND กับเงื่อนไขข้อ 1)
            predicates.add(builder.or(keywordPredicates.toArray(new Predicate[0])));
        }
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
