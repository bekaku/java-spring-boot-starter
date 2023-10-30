package com.bekaku.api.spring.vo;

import lombok.Data;

@Data
public class LinkPreview {
    private String domain;
    private String url;
    private String title;
    private String desc;
    private String image;
    private String imageAlt;

    public LinkPreview(String domain, String url, String title, String desc, String image, String imageAlt) {
        this.domain = domain;
        this.url = url;
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.imageAlt = imageAlt;
    }
}
