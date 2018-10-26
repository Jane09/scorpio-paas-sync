package com.newtank.scorpio.paas.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AriesCustomer {

    private String id;
    private Long agent_id;
    private Boolean         assigned;
    private Date         birthday;
    private Date         create_at;
    private String id_location;
    private String id_number;
    private Boolean is_deal_made;
    private String         job;
    private Date last_called_time;
    private Date         last_deal_made_time;
    private String         lead_status;
    private String location;
    private String mobile;
    private String         mobile_location;
    private String nick_name;
    private String         org_id;
    private String real_name;
    private Date         recycle_date;
    private Integer recycle_period;
    private String         remark;
    private String sex;
    private Boolean         sticky;
    private Long tenant_id;
    private String sale_state;
    private Date         last_recycle_at;

}
