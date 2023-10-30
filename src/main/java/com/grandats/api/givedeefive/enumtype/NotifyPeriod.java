package com.grandats.api.givedeefive.enumtype;

public enum NotifyPeriod {
    NO_PROGRESS(0),//0
    EVERY_1WEEK(1),//1
    EVERY_2WEEK(2),//2
    EVERY_3WEEK(3),//3
    EVERY_4WEEK(4),//4
    EVERY_5WEEK(5),//5
    EVERY_6WEEK(6);//6

    private int id;

    NotifyPeriod(int id) {
        this.id = id;
    }

    //NotifyPeriod e = NotifyPeriod.values()[1]
}
