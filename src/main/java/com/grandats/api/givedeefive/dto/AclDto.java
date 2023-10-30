package com.grandats.api.givedeefive.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AclDto {
    private List<String> permissions = new ArrayList<>();
    private List<AclMenuDto> menus = new ArrayList<>();

    private List<String> frontendPermissions = new ArrayList<>();
}
