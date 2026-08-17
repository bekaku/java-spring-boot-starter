package com.bekaku.api.spring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatabaseQueryResult {
    private int rowCount;
    private List<Map<String, Object>> rows;

    public static DatabaseQueryResult empty() {
        return DatabaseQueryResult.builder()
                .rowCount(0)
                .rows(List.of())
                .build();
    }
}
