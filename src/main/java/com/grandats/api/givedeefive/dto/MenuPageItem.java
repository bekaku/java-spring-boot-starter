package com.grandats.api.givedeefive.dto;

import lombok.Data;

@Data
public class MenuPageItem {
    private String iconText;
    private String color;
    private String icon;
    private String to;
    private String title;
    private String permission;
    private boolean border;
    private boolean noActiveLink;
}
