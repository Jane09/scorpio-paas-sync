package com.newtank.scorpio.paas.service;

import com.newtank.scorpio.paas.dao.aries.AriesDao;
import com.newtank.scorpio.paas.dao.hxl.HxlDao;
import com.newtank.scorpio.paas.domain.AriesCustomer;
import com.newtank.scorpio.paas.domain.AriesLeadBatch;
import com.newtank.scorpio.paas.domain.HxlLeadInBatch;
import com.newtank.scorpio.paas.domain.HxlTempCustomer;
import com.newtank.scorpio.paas.utils.DataUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SyncService {

    private static final String HIDDEN = "*";

    private final AriesDao ariesDao;
    private final HxlDao hxlDao;

    @Autowired
    public SyncService(AriesDao ariesDao, HxlDao hxlDao) {
        this.ariesDao = ariesDao;
        this.hxlDao = hxlDao;
    }

    public void syncDataFromHxlToAries() {

        Map<AriesLeadBatch,List<AriesCustomer>> waitSync = new HashMap<>();
        
        List<HxlTempCustomer> customers = hxlDao.findAllTempCustomers();
        if(customers != null) {
            customers.forEach(customer -> {
                String resId = customer.getRes_id();
                String mobile = customer.getMobile();
                HxlLeadInBatch batch;
                if(isHide(mobile)) {
                    batch = hxlDao.findLastAssignBatchByResId(resId);
                }else {
                    batch = hxlDao.findLastAssignBatchByMobile(mobile);
                }
                if(batch != null) {

                }
            });
        }
    }


    private boolean isHide(String mobile) {
        return mobile.contains(HIDDEN);
    }

    private String transferBatchNo(HxlLeadInBatch batch) throws ParseException {
        String seqNo = batch.getSeq_no();
        String name = batch.getName();
        String batchNo = DataUtils.generateSeqNo(seqNo);

        return batchNo;
    }
}
