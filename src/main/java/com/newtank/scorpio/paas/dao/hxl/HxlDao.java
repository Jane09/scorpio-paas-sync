package com.newtank.scorpio.paas.dao.hxl;

import com.newtank.scorpio.paas.domain.HxlLeadInBatch;
import com.newtank.scorpio.paas.domain.HxlTempCustomer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HxlDao {

    List<HxlTempCustomer> findAllTempCustomers();

    HxlLeadInBatch findLastAssignBatchByMobile(@Param("mobile") String mobile);

    HxlLeadInBatch findLastAssignBatchByResId(@Param("resId") String resId);

    String findMobileByResId(String resId);
}
