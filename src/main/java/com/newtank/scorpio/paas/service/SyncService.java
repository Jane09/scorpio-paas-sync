package com.newtank.scorpio.paas.service;

import com.newtank.scorpio.paas.dao.aries.AriesDao;
import com.newtank.scorpio.paas.dao.hxl.HxlDao;
import com.newtank.scorpio.paas.domain.HxlLeadInBatch;
import com.newtank.scorpio.paas.domain.HxlTempCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

        
        List<HxlTempCustomer> customers = hxlDao.findAllTempCustomers();
        if(customers != null) {
            customers.forEach(customer -> {
                String resId = "";
                String mobile = "";
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
}
