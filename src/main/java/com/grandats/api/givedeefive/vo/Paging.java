package com.grandats.api.givedeefive.vo;

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

    public Paging(int page, int limit, String sort) {
        setLimit(limit);
        setOffset(page);
        setSort(sort);
    }

    private void setSort(String sort) {
        if (sort != null && !sort.isEmpty()) {
            String[] splitParams = sort.toLowerCase().split(",");
            if (splitParams.length == 2) {
                this.sortfield = splitParams[0];
                this.sortmode = splitParams[1];
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

