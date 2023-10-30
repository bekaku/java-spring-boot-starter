package com.grandats.api.givedeefive.specification;

import com.grandats.api.givedeefive.util.DateUtil;
import com.grandats.api.givedeefive.util.ConstantData;
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
    Logger logger = LoggerFactory.getLogger(SearchSpecification.class);

    private HttpServletRequest request;

    //    public SearchSpecification(HttpServletRequest request) {
    public SearchSpecification(List<SearchCriteria> list) {
//        this.request = request;
        this.list = list;
//        initSearchParam();
    }

    public void add(SearchCriteria criteria) {
        list.add(criteria);
    }

    @Deprecated
    public void initSearchParam() {
        String searchParameter = this.request.getParameter(ConstantData.SEARCH_PARAMETER_ATT);
        if (searchParameter != null) {
            String[] splitParams = searchParameter.split(ConstantData.SEARCH_SEPARATOR_ATT);
            String param;
            for (String splitParam : splitParams) {
                param = splitParam;
                if (param != null) {
                    String[] splitSearch;
                    if (param.contains(ConstantData.SEARCH_SIGN_MATCH)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_MATCH);
                        addParam(splitSearch, SearchOperation.MATCH);
                    } else if (param.contains(ConstantData.SEARCH_SIGN_GREATER_THAN_EQUA)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_GREATER_THAN_EQUA);
                        addParam(splitSearch, SearchOperation.GREATER_THAN_EQUAL);
                    } else if (param.contains(ConstantData.SEARCH_SIGN_GREATER_THAN)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_GREATER_THAN);
                        addParam(splitSearch, SearchOperation.GREATER_THAN);
                    } else if (param.contains(ConstantData.SEARCH_SIGN_LESS_THAN_EQUA)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_LESS_THAN_EQUA);
                        addParam(splitSearch, SearchOperation.LESS_THAN_EQUAL);
                    } else if (param.contains(ConstantData.SEARCH_SIGN_LESS_THAN)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_LESS_THAN);
                        addParam(splitSearch, SearchOperation.LESS_THAN);
                    } else if (param.contains(ConstantData.SEARCH_SIGN_NOT_EQUA)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_NOT_EQUA);
                        addParam(splitSearch, SearchOperation.NOT_EQUAL);
                    } else if (param.contains(ConstantData.SEARCH_SIGN_EQUA)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_EQUA);
                        addParam(splitSearch, SearchOperation.EQUAL);
                    }

                }
            }
        }

//        add(new SearchCriteria("code", "api_client", SearchOperation.MATCH));
    }

    @Deprecated
    private void addParam(String[] splitSearch, SearchOperation operation) {
        if (splitSearch.length == 2) {
//                        add(new SearchCriteria("code", "api_client", SearchOperation.MATCH));
            // convert boolean criteria
            String s = splitSearch[1].toLowerCase(Locale.ROOT);
            if ("true".equals(s) || "false".equals(s)) {
                add(new SearchCriteria(splitSearch[0], Boolean.parseBoolean(s), operation));
            } else if (DateUtil.isValidDate(s, DateTimeFormatter.ISO_LOCAL_DATE)) {
                add(new SearchCriteria(splitSearch[0], DateUtil.parseDate(s, DateTimeFormatter.ISO_LOCAL_DATE), operation));
            } else {
                add(new SearchCriteria(splitSearch[0], splitSearch[1], operation));
            }

        }
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
        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
