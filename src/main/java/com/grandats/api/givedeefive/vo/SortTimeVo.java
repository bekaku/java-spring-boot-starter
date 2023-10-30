package com.grandats.api.givedeefive.vo;

import lombok.Data;

@Data
public class SortTimeVo {
    public SortTimeVo(String date, String startDate, String endDate, String monthNo, int year) {
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
        this.monthNo = monthNo;
        this.year = year;
    }

    private String date;
    private String startDate;
    private String endDate;
    private String monthNo;
    int year;

}
