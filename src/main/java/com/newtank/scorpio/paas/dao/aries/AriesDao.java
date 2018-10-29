package com.newtank.scorpio.paas.dao.aries;

import com.newtank.scorpio.paas.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AriesDao {

    AriesLeadBatch findBatchById(String id);

    AriesLeadBatch findBatchByName(String name);

    void addLeadBatch(@Param("leadBatch") AriesLeadBatch leadBatch);

    void addCustomer(@Param("customer") AriesCustomer customer);

    void addCustomerLead(@Param("lead")AriesCustomerLead lead);


    void updateCustomer(@Param("customer") AriesCustomerUpdate customer);

    void updateCustomerLead(@Param("lead") AriesCustomerLead ariesCustomerLead);

    AriesCustomerLead findByCustomerId(String customerId);

    AriesAgent findByJobNo(String jobNo);

    AriesCustomer findByTenantIdAndMobile(@Param("tenantId") Long tenantId, @Param("mobile") String mobile);


    Blacklist findBlacklistByTenantIdAndMobile(@Param("tenantId") Long tenantId, @Param("mobile") String mobile);
}
