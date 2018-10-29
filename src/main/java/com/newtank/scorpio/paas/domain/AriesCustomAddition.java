package com.newtank.scorpio.paas.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AriesCustomAddition {

    private String id;
    private Date create_at;
    private String info_key;
    private String         info_name;
    private String info_value;
    private String         customer_id;

}
