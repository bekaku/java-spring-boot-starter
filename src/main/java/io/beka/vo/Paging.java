package io.beka.vo;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Getter
public class Paging {
    private static final int MAX_LIMIT = 100;

    private int page = 0;
    private int offset = 0;
    private int limit = 20;

    public Paging(int page, int limit) {
        setLimit(limit);
        setOffset(page);

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

