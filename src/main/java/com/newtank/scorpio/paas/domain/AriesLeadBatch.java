package com.newtank.scorpio.paas.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"create_time","name","path","tenant_id","total"})
public class AriesLeadBatch {
    private String id;
    private Date create_time;
    private String name;
    private String path;
    private Long tenant_id;
    private Integer total;
}
