package com.grandats.api.givedeefive.util;

import com.grandats.api.givedeefive.specification.SearchCriteria;
import com.grandats.api.givedeefive.specification.SearchOperation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ControllerUtil {
    static Logger logger = LoggerFactory.getLogger(ControllerUtil.class);


    public static List<SearchCriteria> getSearchCriteriaList(HttpServletRequest request) {
        List<SearchCriteria> list = new ArrayList<>();
        String searchParameter = request.getParameter(ConstantData.SEARCH_PARAMETER_ATT);
        if (searchParameter != null) {
            String[] splitParams = searchParameter.split(ConstantData.SEARCH_SEPARATOR_ATT);
            String param;
            for (String splitParam : splitParams) {
                param = splitParam;
                if (param != null) {
                    String[] splitSearch;
                    if (param.contains(ConstantData.SEARCH_SIGN_MATCH)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_MATCH);
                        addSearchCriteriaParam(splitSearch, SearchOperation.MATCH, list);
                    } else if (param.contains(ConstantData.SEARCH_SIGN_GREATER_THAN_EQUA)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_GREATER_THAN_EQUA);
                        addSearchCriteriaParam(splitSearch, SearchOperation.GREATER_THAN_EQUAL, list);
                    } else if (param.contains(ConstantData.SEARCH_SIGN_GREATER_THAN)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_GREATER_THAN);
                        addSearchCriteriaParam(splitSearch, SearchOperation.GREATER_THAN, list);
                    } else if (param.contains(ConstantData.SEARCH_SIGN_LESS_THAN_EQUA)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_LESS_THAN_EQUA);
                        addSearchCriteriaParam(splitSearch, SearchOperation.LESS_THAN_EQUAL, list);
                    } else if (param.contains(ConstantData.SEARCH_SIGN_LESS_THAN)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_LESS_THAN);
                        addSearchCriteriaParam(splitSearch, SearchOperation.LESS_THAN, list);
                    } else if (param.contains(ConstantData.SEARCH_SIGN_NOT_EQUA)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_NOT_EQUA);
                        addSearchCriteriaParam(splitSearch, SearchOperation.NOT_EQUAL, list);
                    } else if (param.contains(ConstantData.SEARCH_SIGN_EQUA)) {
                        splitSearch = param.split(ConstantData.SEARCH_SIGN_EQUA);
                        addSearchCriteriaParam(splitSearch, SearchOperation.EQUAL, list);
                    }

                }
            }
        }
        return list;
    }

    private static void addSearchCriteriaParam(String[] splitSearch, SearchOperation operation, List<SearchCriteria> list) {
        if (splitSearch.length == 2) {
//                        add(new SearchCriteria("code", "api_client", SearchOperation.MATCH));
            // convert boolean criteria
            String s = splitSearch[1].toLowerCase(Locale.ROOT);
            if ("true".equals(s) || "false".equals(s)) {
                list.add(new SearchCriteria(splitSearch[0], Boolean.parseBoolean(s), operation));
            } else if (DateUtil.isValidDate(s, DateTimeFormatter.ISO_LOCAL_DATE)) {
                SearchCriteria criteriaDate = new SearchCriteria(splitSearch[0], DateUtil.parseDate(s, DateTimeFormatter.ISO_LOCAL_DATE), operation);
                criteriaDate.setDate(true);
                list.add(criteriaDate);
            } else {
                list.add(new SearchCriteria(splitSearch[0], splitSearch[1], operation));
            }

        }
    }
}
