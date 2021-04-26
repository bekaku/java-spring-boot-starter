package io.beka.validator;

import java.lang.reflect.Field;

public class BaseValidator<T> {

    public BaseValidator(T t) {
        System.out.println("BaseValidator: Constructor");

        for (Field field : t.getClass().getDeclaredFields()) {
            javax.persistence.Column column = field.getAnnotation(javax.persistence.Column.class);
            if (column != null) {
                System.out.println("Columns: " + column);
            }
        }
    }

}
