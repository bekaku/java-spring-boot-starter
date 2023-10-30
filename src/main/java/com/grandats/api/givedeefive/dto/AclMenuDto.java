package com.grandats.api.givedeefive.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AclMenuDto {
    private List<MenuPage> pages = new ArrayList<>();
    private String header;
    private boolean border;
}
