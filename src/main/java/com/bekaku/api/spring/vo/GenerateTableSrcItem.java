package com.bekaku.api.spring.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GenerateTableSrcItem {
    private String tableName;
    private String propertyName;
    private String sqlName;
    private String sqlType;
    private String propertyType;
    private boolean unique;
    private Long length;
    private boolean nullable;
    private boolean typeTextArea;

}
