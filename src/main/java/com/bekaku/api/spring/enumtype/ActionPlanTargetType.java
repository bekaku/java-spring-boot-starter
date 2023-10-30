package com.bekaku.api.spring.enumtype;

public enum ActionPlanTargetType {
    VALUE(0),
    PERCENTAGE(1),
    TIME(2),
    SIMPLE(3);

    private int id;

    ActionPlanTargetType(int id) {
        this.id = id;
    }
    //ActionPlanTargetType e = ActionPlanTargetType.values()[1]

    public static ActionPlanTargetType findByName(String name) {
        ActionPlanTargetType result = null;
        for (ActionPlanTargetType direction : values()) {
            if (direction.name().equalsIgnoreCase(name)) {
                result = direction;
                break;
            }
        }
        return result;
    }
}
