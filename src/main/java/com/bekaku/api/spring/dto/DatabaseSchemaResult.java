package com.bekaku.api.spring.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DatabaseSchemaResult {

    private String schema;

    private String tableName;

    private String definition;

    private Double score;
}
