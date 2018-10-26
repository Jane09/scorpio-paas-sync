package com.newtank.scorpio.paas.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class HxlLeadInBatch {

    private String id;
    private String name;
    private String seq_no;
    private Date create_time;
    private Integer leads;
    private String path;
}
