package com.newtank.scorpio.paas.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AriesLeadBatch {
    private String id;
    private Date create_time;
    private String name;
    private String path;
    private Long tenant_id;
    private Integer total;
}
