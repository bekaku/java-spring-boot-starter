package com.grandats.api.givedeefive.dto;

import lombok.Data;

@Data
public class AppVersionDto {
    public AppVersionDto(int codeVersion, String appVersionIos, String appVersionAndroid, boolean fourceUpdate, boolean puaseUpdate) {
        this.codeVersion = codeVersion;
        this.appVersionIos = appVersionIos;
        this.appVersionAndroid = appVersionAndroid;
        this.fourceUpdate = fourceUpdate;
        this.puaseUpdate = puaseUpdate;
    }

    boolean fourceUpdate;
    boolean puaseUpdate;
    private int codeVersion;
    private String appVersionIos;
    private String appVersionAndroid;
}
