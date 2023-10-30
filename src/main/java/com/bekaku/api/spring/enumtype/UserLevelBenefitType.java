package com.bekaku.api.spring.enumtype;

public enum UserLevelBenefitType {
    PASSIVE(0),
    LEARNER(1),
    ACTIVE(2),
    ADVANCE(3),
    MASTER(4);

    private int id;

    UserLevelBenefitType(int id) {
        this.id = id;
    }
}
