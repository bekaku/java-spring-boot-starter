package com.bekaku.api.spring.specification;

import jakarta.persistence.criteria.Expression;
import lombok.Data;

@Data
public class SearchSepecificationKey {
    public SearchSepecificationKey(String key, Expression<String> expression) {
        this.key = key;
        this.expression = expression;
    }

    String key;
    Expression<String> expression;
}
