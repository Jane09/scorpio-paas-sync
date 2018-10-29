package com.newtank.scorpio.paas.service;

import com.newtank.scorpio.paas.dao.aries.AriesDao;
import com.newtank.scorpio.paas.dao.hxl.HxlDao;
import com.newtank.scorpio.paas.domain.*;
import com.newtank.scorpio.paas.utils.DataUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
@Slf4j
public class SyncService {

    private static final String HIDDEN = "*";
    private static final String MALE = "男";
    private static final String FEMALE = "女";


    private final AriesDao ariesDao;
    private final HxlDao hxlDao;

    @Autowired
    public SyncService(AriesDao ariesDao, HxlDao hxlDao) {
        this.ariesDao = ariesDao;
        this.hxlDao = hxlDao;
    }

    /**
     * logically
     *
     */
    public void syncDataFromHxlToAries(Long tenantId) {
        Map<AriesLeadBatch,List<AriesCustomer>> waitSync = new HashMap<>();
        Map<String,AriesLeadBatch> waitBatches = new HashMap<>();
        //待同步的客户信息
        List<HxlTempCustomer> customers = hxlDao.findAllTempCustomers();
        if(customers != null) {
            customers.forEach(customer -> {

                String resId = customer.getRes_id();
                String mobile = customer.getMobile();
                HxlLeadInBatch batch;
                if(isHide(mobile)) {
                    batch = hxlDao.findLastAssignBatchByResId(resId);
                    //查询明文电话
                    mobile = hxlDao.findMobileByResId(resId);
                }else {
                    batch = hxlDao.findLastAssignBatchByMobile(mobile);
                }
                if(batch == null) {
                    log.error(mobile +" 没有对应的批次数据!");
                }
                if(StringUtils.isEmpty(mobile) || mobile.contains(HIDDEN)) {
                    log.error(mobile +" 没有对应的数据");
                }
                AriesAgent ariesAgent = ariesDao.findByJobNo(customer.getJob_no());
                Date now = new Date();
                if(batch != null && !StringUtils.isEmpty(mobile) && !mobile.contains(HIDDEN)) {
                    AriesLeadBatch wait = transferBatchNo(batch, waitBatches);
                    if(wait != null) {//有批次信息
                        //校验白羊座的数据是否存在
                        AriesCustomer ariesCustomer = ariesDao.findByTenantIdAndMobile(tenantId,mobile);
                        if(ariesCustomer != null){
                            //更新基础信息 和线索信息（黑名单）
                            AriesCustomerUpdate update = new AriesCustomerUpdate();
                            ariesDao.updateCustomer(update);
                            AriesCustomerLead custLead = ariesDao.findByCustomerId(ariesCustomer.getId());
                            if(custLead != null){
                                checkBlocked(custLead,tenantId,mobile);
                                //更新
                                ariesDao.updateCustomerLead(custLead);
                            }else {
                                custLead = new AriesCustomerLead();


                                checkBlocked(custLead,tenantId,mobile);
                                ariesDao.addCustomerLead(custLead);
                            }
                        }else {
                            //新增客户信息和 线索信息（黑名单）
                            ariesCustomer = new AriesCustomer();
                            ariesCustomer.setId(DataUtils.generatePk());
                            ariesCustomer.setAgent_id(ariesAgent.getAgentId());
                            ariesCustomer.setOrg_id(ariesAgent.getOrgId());
                            ariesCustomer.setAssigned(true);
                            ariesCustomer.setBirthday(getBirth(customer.getBirthday()));
                            ariesCustomer.setCreate_at(now);
                            ariesCustomer.setId_number(customer.getId_number());
                            ariesCustomer.setJob(customer.getJob());
                            ariesCustomer.setLast_called_time(getTime(customer.getLast_call_time()));
                            ariesCustomer.setLead_status("MB");
                            ariesCustomer.setMobile(customer.getMobile());
                            ariesCustomer.setNick_name(customer.getName());
                            ariesCustomer.setReal_name(customer.getName());
                            ariesCustomer.setRemark(customer.getRemark());
                            ariesCustomer.setSex(checkSex(customer.getSex()));
                            ariesCustomer.setSticky(false);
                            ariesCustomer.setTenant_id(tenantId);
                            ariesDao.addCustomer(ariesCustomer);


                            AriesCustomerLead custLead = new AriesCustomerLead();
                            custLead.setId(DataUtils.generatePk());
                            custLead.setAcquisition_time(now);
                            custLead.setBatch_id(wait.getId());
                            checkBlocked(custLead,tenantId,mobile);
                            custLead.setCustomer_id(ariesCustomer.getId());
                            custLead.setData_source(customer.getSource());
                            custLead.setProduct_name(customer.getMarket_project());
                            custLead.setRes_id(customer.getRes_id());

                            ariesDao.addCustomerLead(custLead);
                        }
                    }
                }
            });
        }
    }

    private void checkBlocked(AriesCustomerLead lead,Long tenantId, String mobile) {
        Blacklist blacklist = ariesDao.findBlacklistByTenantIdAndMobile(tenantId,mobile);
        if(blacklist != null){
            if(StringUtils.isEmpty(lead.getReason())) {
                lead.setReason(blacklist.getRemark());
            }
            String type = blacklist.getType();
            if(StringUtils.isEmpty(type)) {
                lead.setBlocked_type(BlacklistType.NORMAL.name());
                return;
            }
            type = type.toUpperCase();
            lead.setBlocked_type(type);
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


    /**
     * yyyy-MM-dd
     * @param birth
     * @return
     */
    private Date getBirth(String birth) {
        if(StringUtils.isEmpty(birth)){
            return null;
        }
        birth = birth.replaceAll("/","").replaceAll("-","");
        return DataUtils.parseDate(DataUtils.SHORT_DATE, birth);
    }

    /**
     * yyyy/MM/dd HH:mm:ss
     * @param str
     * @return
     */
    private Date getTime(String str) {
        if(StringUtils.isEmpty(str)){
            return null;
        }
        str = str.replaceAll("/","").replaceAll("-","");
        return DataUtils.parseDate(DataUtils.SLASH_TIME, str);
    }

    private String checkSex(String sex) {
        if(MALE.equals(sex)) {
            return Sex.MALE.name();
        }else if(FEMALE.equals(sex)) {
            return Sex.FEMALE.name();
        }else {
            return Sex.UNKNOWN.name();
        }
    }

    public enum Sex {
        MALE,
        FEMALE,
        UNKNOWN
    }

    public enum BlacklistType {
        BLACK("黑名单用户"),
        RED("红名单:包括政府官员等"),
        NORMAL("正常")
        ;

        @Getter
        private String desc;
        BlacklistType(String desc) {
            this.desc = desc;
        }
    }
}
