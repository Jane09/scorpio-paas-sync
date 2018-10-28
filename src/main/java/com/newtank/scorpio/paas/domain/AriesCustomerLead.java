package com.newtank.scorpio.paas.domain;


import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AriesCustomerLead {

    private String id;
    private Date acquisition_time;
    private String data_source;
    private String         insurance_test_info;
    private String product_name;
    private String         res_id;
    private String search_keyword;
    private String         survey_info;
    private String customer_id;
    private String         batch_id;
    private Boolean blocked;
    private String         reason;
    private String blocked_type;

}
