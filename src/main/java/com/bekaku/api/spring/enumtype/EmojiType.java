package com.bekaku.api.spring.enumtype;

public enum EmojiType {
    LIKE(0),//0
    FIGHTING(1),//1
    LAUGH(2),//2
    WOW(3),//3
    CARE(4),//4
    SAD(5);//5

    private int emojiId;
    EmojiType(int emojiId) {
        this.emojiId = emojiId;
    }

    //EmojiType e = EmojiType.values()[1]
}
