package com.newtank.scorpio.paas.dao.aries;

import com.newtank.scorpio.paas.domain.AriesCustomer;
import com.newtank.scorpio.paas.domain.AriesCustomerLead;
import com.newtank.scorpio.paas.domain.AriesLeadBatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AriesDao {

    List<AriesCustomer> findAll();

    void addLeadBatch(@Param("leadBatch") AriesLeadBatch leadBatch);

    void addCustomer(@Param("customer") AriesCustomer customer);

    void addCustomerLead(@Param("lead")AriesCustomerLead lead);
}
