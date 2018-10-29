package com.newtank.scorpio.paas.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Blacklist {

    private String type = "BLACK";

    private String remark;

    private String callState = "DISABLE";

    private Boolean universal = false;
}
