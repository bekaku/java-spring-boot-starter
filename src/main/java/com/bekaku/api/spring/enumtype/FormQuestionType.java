package com.bekaku.api.spring.enumtype;

public enum FormQuestionType {
    TITLE,//0
    FREE_TEXT,//1
    FREE_TEXT_SHORT,//2
    ROW_COLUMN,//3
    LINEAR_SCALE,//4
    RADIO,//5
    DATE,//6
    TIME,//7
    FILE,//8
    YOUTUBE_LINK,//9
    MAP_LAT_LNG;//10

//    @JsonValue
//    public int toValue() {
//        return ordinal();
//    }
}
