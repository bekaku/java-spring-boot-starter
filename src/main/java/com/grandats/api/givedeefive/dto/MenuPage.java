package com.grandats.api.givedeefive.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MenuPage {
    private List<MenuPageItem> items = new ArrayList<>();
}
