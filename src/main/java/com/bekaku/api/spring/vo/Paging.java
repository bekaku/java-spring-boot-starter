package com.bekaku.api.spring.vo;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Getter
public class Paging {
    private static final int MAX_LIMIT = 100;

    private int page = 0;// start page with page 0
    private int offset = 0;
    private int limit = 20;
    private String sortmode;
    private String sortfield;

    private final String ASC_MODE="asc";
    private final String DESC_MODE="desc";

    public Paging(int page, int limit, String sort) {
        setLimit(limit);
        setOffset(page);
        setSort(sort);
    }

    private void setSort(String sort) {
        if (sort != null && !sort.isEmpty()) {
            String[] splitParams = sort.toLowerCase().split(",");
            if (splitParams.length == 2) {
                String sortmode = splitParams[1];
                this.sortfield = splitParams[0];
                this.sortmode = sortmode.equals(DESC_MODE) ? DESC_MODE : ASC_MODE;
            }
        }
    }

    private void setOffset(int page) {
        if (page > 0) {
            this.page = (page - 1);
            this.offset = this.page * this.limit;
        }
    }

    private void setLimit(int limit) {
        if (limit > MAX_LIMIT) {
            this.limit = MAX_LIMIT;
        } else if (limit > 0) {
            this.limit = limit;
        }
    }
}

