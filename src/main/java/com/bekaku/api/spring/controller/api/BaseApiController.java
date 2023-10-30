package com.bekaku.api.spring.controller.api;

import com.bekaku.api.spring.configuration.I18n;
import com.bekaku.api.spring.specification.SearchCriteria;
import com.bekaku.api.spring.vo.Paging;
import com.bekaku.api.spring.exception.BaseResponseException;
import com.bekaku.api.spring.util.ConstantData;
import com.bekaku.api.spring.util.ControllerUtil;
import com.bekaku.api.spring.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class BaseApiController extends BaseResponseException {

    Logger logger = LoggerFactory.getLogger(BaseApiController.class);
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private I18n i18n;

    public ResponseEntity<Object> responseEntity(@Nullable Object o, HttpStatus status) {
        return new ResponseEntity<>(o, status);
    }
   public ResponseEntity<Object> responseEntity(@Nullable Object o) {
        return new ResponseEntity<>(o, HttpStatus.OK);
    }
    public ResponseEntity<Object> reponseDeleteMessage() {
        return this.responseServerMessage(i18n.getMessage("success.deleteSuccesfull"), HttpStatus.OK);
    }
    public ResponseEntity<Object> reponseCreatedMessage() {
        return this.responseServerMessage(i18n.getMessage("success.insertSuccesfull"), HttpStatus.OK);
    }
    public ResponseEntity<Object> reponseUpdatedMessage() {
        return this.responseServerMessage(i18n.getMessage("success.updateSuccesfull"), HttpStatus.OK);
    }
    public ResponseEntity<Object> reponseSuccessMessage() {
        return this.responseServerMessage(i18n.getMessage("success"), HttpStatus.OK);
    }
    public ResponseEntity<Object> responseEntity(@Nullable Object o, HttpStatus status, String viewPermission, String managePermission) {
        return new ResponseEntity<>(o, status);
    }

    public ResponseEntity<Object> responseServerMessage(@Nullable String o) {
        return responseServerMessage(o, HttpStatus.OK);
    }
    public ResponseEntity<Object> responseServerMessage(@Nullable String o, HttpStatus status) {
        return responseEntity(new HashMap<String, Object>() {{
            put(ConstantData.SERVER_MESSAGE, o);
            put(ConstantData.SERVER_STATUS, status);
            put(ConstantData.SERVER_TIMESTAMP, DateUtil.getLocalDateTimeNow());
        }}, HttpStatus.OK);
    }

    public ResponseEntity<Object> responseServerMessage(@Nullable String o, HttpStatus status, boolean success) {
        return responseEntity(new HashMap<String, Object>() {{
            put(ConstantData.SERVER_MESSAGE, o);
            put(ConstantData.SERVER_STATUS, status);
            put(ConstantData.SERVER_SUCCESS, success);
            put(ConstantData.SERVER_TIMESTAMP, DateUtil.getLocalDateTimeNow());
        }}, HttpStatus.OK);
    }

    public ResponseEntity<Object> responseEntity(HttpStatus status) {
        return new ResponseEntity<>(status);
    }

    public void initSearchParam() {
        logger.info("initSearchParam Params {}", request.getParameter("search"));
    }

    public Optional<String> getParameter(String parameterName) {
        return Optional.ofNullable(request.getParameter(parameterName));
    }

    public Pageable getPageable(Pageable pageable, Sort defaultSort) {
        return !pageable.getSort().isEmpty() ? pageable : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), defaultSort);
    }

    public Paging getPaging(Pageable pageable) {
        Pageable p = pageable.isPaged() ? pageable : null;
        String sortString = null;
        if (p != null) {
            if (!pageable.getSort().isEmpty()) {
                Sort sort = pageable.getSort();
                for (Sort.Order order : sort) {
                    sortString = order.getProperty() + "," + order.getDirection();
                }
            }
            return new Paging(p.getPageNumber(), p.getPageSize(), sortString);
        }
        return null;
    }

    public HashMap<String, Object> getSearchCriteriaMap() {
        List<SearchCriteria> list = getSearchCriteriaList();
        HashMap<String, Object> search = new HashMap<>();
        for (SearchCriteria criteria : list) {
            search.put(criteria.getKey(), criteria);
        }
        return search;
    }

    public List<SearchCriteria> getSearchCriteriaList() {
        return ControllerUtil.getSearchCriteriaList(request);
    }

}
