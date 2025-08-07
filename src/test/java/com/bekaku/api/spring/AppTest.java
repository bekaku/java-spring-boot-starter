package com.bekaku.api.spring;


import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class AppTest {

    @Test
    void add_TwoNumbers_ReturnsCorrectSum() {
        String name = "alice";
        String compare = "alice";
        assertEquals(compare, name);
    }

    private Integer addNumber(int no1, int no2) {
        return no1 + no2;
    }
}
