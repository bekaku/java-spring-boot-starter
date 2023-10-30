package com.bekaku.api.spring.dto;

import lombok.Data;

import java.util.List;

@Data
public class ResponseListDto<T> {
    private List<T> dataList;
    private int totalPages;
    private int totalElements;
    private boolean isLast;

    public ResponseListDto(List<T> dataList, int totalPages, int totalElements, boolean isLast) {
        this.dataList = dataList;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.isLast = isLast;
    }
}
