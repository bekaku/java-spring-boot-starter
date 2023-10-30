package com.grandats.api.givedeefive.validator;

import com.grandats.api.givedeefive.configuration.I18n;
import com.grandats.api.givedeefive.exception.BaseResponseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
//public class BaseValidator<T> {
public class BaseValidator extends BaseResponseException {

    @Autowired
    private I18n i18n;

    @Autowired
    private HttpServletRequest request;
    private List<String> errors = new ArrayList<>();


    public boolean isNew() {
        return request.getMethod().equalsIgnoreCase(RequestMethod.POST.name());
    }

    public void addError(String error) {
        errors.add(error);
    }

    public void addErrorDuplicate(String data) {
        errors.add(i18n.getMessage("error.validateDuplicate", data));
    }

    public void addErrorNotFound() {
        errors.add(i18n.getMessage("error.dataNotfound"));
    }

    public void addErrorRequireField(String data) {
        errors.add(i18n.getMessage("error.validateRequireField", data));
    }

    public void checkValidate() {
        if (this.errors.size() > 0) {
            List<String> err = this.errors;
            this.errors = new ArrayList<>();
            throw this.responseError(HttpStatus.BAD_REQUEST, null, err);
        }
    }
//    public BaseValidator(T t) {
//        System.out.println("BaseValidator: Constructor");
//        for (Field field : t.getClass().getDeclaredFields()) {
//            javax.persistence.Column column = field.getAnnotation(javax.persistence.Column.class);
//            if (column != null) {
//                System.out.println("Columns: " + column);
//            }
//        }
//    }

}
