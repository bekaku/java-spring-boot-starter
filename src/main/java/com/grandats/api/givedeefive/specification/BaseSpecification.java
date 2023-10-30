package com.grandats.api.givedeefive.specification;

import com.grandats.api.givedeefive.util.ControllerUtil;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BaseSpecification {
    private final HttpServletRequest request;
    private final HashMap<String, Object> searchCriteria = new HashMap<>();

    public BaseSpecification(HttpServletRequest request) {
        this.request = request;
        setSearchCriteriaMap();
    }

    public Predicate getSearchPredicate(CriteriaBuilder builder, List<SearchSepecificationKey> keys, boolean isOr) {
        List<Predicate> predicates = new ArrayList<>();
        for (SearchSepecificationKey s : keys) {
            Expression<String> x = s.getExpression();
            if (searchCriteria.containsKey(s.getKey())) {
                SearchCriteria criteria = (SearchCriteria) searchCriteria.get(s.getKey());
                if (criteria.getOperation().equals(SearchOperation.GREATER_THAN)) {
                    predicates.add(builder.greaterThan(x, criteria.getValue().toString()));
                } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN)) {
                    predicates.add(builder.lessThan(x, criteria.getValue().toString()));
                } else if (criteria.getOperation().equals(SearchOperation.GREATER_THAN_EQUAL)) {
                    predicates.add(builder.greaterThanOrEqualTo(x, criteria.getValue().toString()));
                } else if (criteria.getOperation().equals(SearchOperation.LESS_THAN_EQUAL)) {
                    predicates.add(builder.lessThanOrEqualTo(x, criteria.getValue().toString()));
                } else if (criteria.getOperation().equals(SearchOperation.NOT_EQUAL)) {
                    predicates.add(builder.notEqual(x, criteria.getValue()));
                } else if (criteria.getOperation().equals(SearchOperation.EQUAL)) {
                    predicates.add(builder.equal(x, criteria.getValue()));
                } else if (criteria.getOperation().equals(SearchOperation.MATCH)) {
                    predicates.add(builder.like(builder.lower(x), "%" + criteria.getValue().toString().toLowerCase() + "%"));
                } else if (criteria.getOperation().equals(SearchOperation.MATCH_END)) {
                    predicates.add(builder.like(
                            builder.lower(x), criteria.getValue().toString().toLowerCase() + "%"));
                } else if (criteria.getOperation().equals(SearchOperation.MATCH_START)) {
                    predicates.add(builder.like(
                            builder.lower(x), "%" + criteria.getValue().toString().toLowerCase()));
                } else if (criteria.getOperation().equals(SearchOperation.IN)) {
                    predicates.add(builder.in(x).value((String) criteria.getValue()));
                } else if (criteria.getOperation().equals(SearchOperation.IS_NULL)) {
                    predicates.add(builder.isNull(x));
                } else if (criteria.getOperation().equals(SearchOperation.IS_NOT_NULL)) {
                    predicates.add(builder.isNotNull(x));
                }
            }
        }
        return predicates.size() > 0 ? isOr ?
                builder.or(predicates.toArray(new Predicate[0])) : builder.and(predicates.toArray(new Predicate[0]))
                : null;
    }

    public void setSearchCriteriaMap() {
        List<SearchCriteria> list = ControllerUtil.getSearchCriteriaList(request);
        for (SearchCriteria criteria : list) {
            searchCriteria.put(criteria.getKey(), criteria);
        }
    }

    public HashMap<String, Object> getSearchCriteria() {
        return searchCriteria;
    }
}
