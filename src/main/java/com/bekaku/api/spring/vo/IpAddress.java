package com.bekaku.api.spring.vo;

import lombok.Data;

@Data
public class IpAddress {

    private String ip;
    private String hostName;

    public IpAddress(String ip, String hostName) {
        this.ip = ip;
        this.hostName = hostName;
    }
}
