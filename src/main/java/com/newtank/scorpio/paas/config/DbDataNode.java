package com.newtank.scorpio.paas.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DbDataNode {

    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
