package com.grandats.api.givedeefive.vo;

import lombok.Data;

@Data
public class DirectoryPathVo {

    public DirectoryPathVo(Long id, String name, boolean current, boolean root) {
        this.id = id;
        this.name = name;
        this.current = current;
        this.root = root;
    }

    private Long id;
    private String name;
    private boolean current;
    private boolean root;
}
