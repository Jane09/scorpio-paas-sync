package com.newtank.scorpio.paas.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AriesCustomerUpdate {

    private String real_name;

    private Date birthday;

    private String sex;

    private String id_number;
}
