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
import org.springframework.util.StringUtils;
import sun.swing.StringUIClientPropertyKey;

import java.text.ParseException;
import java.util.*;

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
        Map<String,AriesLeadBatch> waitBatches = new HashMap<>();

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
                    AriesLeadBatch wait = transferBatchNo(batch, waitBatches);
                    if(wait != null) {//有批次信息
                        //校验白羊座的数据是否存在

                    }
                }
            });
        }
    }


    private boolean isHide(String mobile) {
        return mobile.contains(HIDDEN);
    }

    private AriesLeadBatch transferBatchNo(HxlLeadInBatch batch,  Map<String,AriesLeadBatch> waitBatches) {
        String seqNo = batch.getSeq_no();
        String name = batch.getName();
        String batchId = ariesDao.getBatchIdByName(name);
        if(!StringUtils.isEmpty(batch)) {
            return null;
        }
        batchId = ariesDao.getBatchIdBySeqNo(seqNo);
        if(!StringUtils.isEmpty(batch)) {
            return null;
        }
        String batchNo = DataUtils.generateSeqNo(seqNo);
        AriesLeadBatch waitBatch = new AriesLeadBatch();
        waitBatch.setId(batchNo);
        waitBatch.setCreate_time(batch.getCreate_time());
        waitBatch.setName(name);
        waitBatch.setPath(batch.getPath());
        waitBatch.setTenant_id(1L);
        waitBatch.setTotal(batch.getLeads());
        waitBatches.put(batchNo,waitBatch);
        return waitBatch;
    }
}
