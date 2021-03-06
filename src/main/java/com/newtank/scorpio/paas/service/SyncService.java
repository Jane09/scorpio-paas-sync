package com.newtank.scorpio.paas.service;

import com.alibaba.fastjson.JSON;
import com.newtank.scorpio.paas.dao.aries.AriesDao;
import com.newtank.scorpio.paas.dao.hxl.HxlDao;
import com.newtank.scorpio.paas.domain.*;
import com.newtank.scorpio.paas.utils.DataUtils;
import com.newtank.scorpio.paas.utils.RemarkConvertor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author zhoujian
 */
@Service
@Slf4j
public class SyncService {

    private static final String HIDDEN = "*";
    private static final String MALE = "男";
    private static final String FEMALE = "女";


    private final AriesDao ariesDao;
    private final HxlDao hxlDao;

    @Value("${switch.update}")
    private Boolean update;

    @Autowired
    public SyncService(AriesDao ariesDao, HxlDao hxlDao) {
        this.ariesDao = ariesDao;
        this.hxlDao = hxlDao;
    }

    /**
     * logically
     *
     */
    public void syncDataFromHxlToAries(final Long tenantId) {
        Map<String,AriesLeadBatch> waitBatches = new HashMap<>();
        //待同步的客户信息
        List<HxlTempCustomer> customers = hxlDao.findAllTempCustomers();
        if(customers != null) {
            customers.forEach(customer -> {
                boolean skip = false;
                String resId = customer.getRes_id();
                String mobile = customer.getMobile();
                HxlLeadInBatch batch;
                if(isHide(mobile)) {
                    batch = hxlDao.findLastAssignBatchByResId(resId);
                    //查询明文电话
                    mobile = hxlDao.findMobileByResId(resId);
                    if(StringUtils.isEmpty(mobile)) {
                        log.info("ResId：{} 找不到对应的线索信息",resId);
                        skip = true;
                    }
                }else {
                    batch = hxlDao.findLastAssignBatchByMobile(mobile);
                }
                if(!skip && batch == null) {
                    log.error(resId +" 没有对应的批次数据!");
                    skip = true;
                }
                if(!skip) {
                    AriesAgent ariesAgent = ariesDao.findByJobNo(customer.getJob_no());
                    if(ariesAgent == null){
                        log.error("工号：{} 对应的坐席不存在", customer.getJob_no());
                    }else {
                        Date now = new Date();
                        if(!StringUtils.isEmpty(mobile) && !mobile.contains(HIDDEN)) {
                            AriesLeadBatch wait = transferBatchNo(batch, waitBatches);
                            if(wait != null) {
                                //更新批次信息
                                AriesLeadBatch old = ariesDao.findBatchById(wait.getId());
                                if(old == null) {
                                    old = ariesDao.findBatchByName(wait.getName());
                                }
                                if(old == null){
                                    log.info("添加批次数据： {}", JSON.toJSONString(wait));
                                    ariesDao.addLeadBatch(wait);
                                }
                                //校验白羊座的数据是否存在
                                AriesCustomer ariesCustomer = ariesDao.findByTenantIdAndMobile(tenantId,mobile);
                                if(ariesCustomer != null){
                                    //only sync data which does not exists.
                                    if(update) {
                                        log.info("电话： {} 对应的客户已经存在，更新数据",mobile);
                                        AriesCustomerUpdate update = new AriesCustomerUpdate();
                                        update.setId(ariesCustomer.getId());
                                        update.setReal_name(customer.getName());
                                        update.setBirthday(getBirth(customer.getBirthday()));
                                        update.setId_number(customer.getId_number());
                                        ariesDao.updateCustomer(update);

                                        //更新线索表
                                        AriesCustomerLead custLead = ariesDao.findByCustomerId(ariesCustomer.getId());
                                        if(custLead != null){
                                            checkBlocked(custLead,tenantId,mobile);
                                            custLead.setBatch_id(wait.getId());
                                            ariesDao.updateCustomerLead(custLead);
                                        }else {
                                            custLead = new AriesCustomerLead();
                                            custLead.setId(DataUtils.generatePk());
                                            custLead.setAcquisition_time(now);
                                            custLead.setBatch_id(wait.getId());
                                            checkBlocked(custLead,tenantId,mobile);
                                            custLead.setCustomer_id(ariesCustomer.getId());
                                            custLead.setData_source(customer.getSource());
                                            custLead.setRes_id(customer.getRes_id());
                                            ariesDao.addCustomerLead(custLead);
                                        }
                                    }else {
                                        log.info("电话： {} 对应的客户已经存在，跳过",mobile);
                                    }
                                }else {
                                    log.info("新增用户：{}",mobile);

                                    Map<String,String> remarks = RemarkConvertor.getRemarkMap(customer.getRemark());
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
                                    ariesCustomer.setMobile(mobile);
                                    ariesCustomer.setNick_name(customer.getName());
                                    ariesCustomer.setReal_name(customer.getName());
                                    ariesCustomer.setRemark(customer.getRemark());
                                    if(StringUtils.isEmpty(customer.getSex())) {
                                        if(remarks.containsKey("sex")) {
                                            ariesCustomer.setSex(checkSex(remarks.get("sex")));
                                        }
                                    }else {
                                        ariesCustomer.setSex(checkSex(customer.getSex()));
                                    }

                                    ariesCustomer.setSticky(false);
                                    ariesCustomer.setTenant_id(tenantId);
                                    ariesCustomer.setIs_deal_made(false);
                                    ariesDao.addCustomer(ariesCustomer);


                                    AriesCustomerLead custLead = new AriesCustomerLead();
                                    custLead.setId(DataUtils.generatePk());
                                    custLead.setAcquisition_time(now);
                                    custLead.setBatch_id(wait.getId());
                                    checkBlocked(custLead,tenantId,mobile);
                                    custLead.setCustomer_id(ariesCustomer.getId());
                                    custLead.setData_source(customer.getSource());
                                    if(remarks.containsKey("product")) {
                                        custLead.setProduct_name(remarks.get("product"));
                                    }
                                    custLead.setRes_id(customer.getRes_id());

                                    List<AriesCustomAddition> exts = new ArrayList<>();
                                    if(remarks.containsKey("appoint")) {
                                        //预约时间
                                        AriesCustomAddition appoint = new AriesCustomAddition();
                                        appoint.setInfo_key("appoint_time");
                                        appoint.setInfo_name("预约时间");
                                        appoint.setInfo_value(remarks.get("appoint"));
                                        appoint.setCreate_at(now);
                                        exts.add(appoint);
                                    }

                                    if(remarks.containsKey("qa")) {
                                        //问卷调查
                                        AriesCustomAddition appoint = new AriesCustomAddition();
                                        appoint.setInfo_key("psq_info");
                                        appoint.setInfo_name("问卷信息");
                                        appoint.setInfo_value(remarks.get("qa"));
                                        appoint.setCreate_at(now);
                                        exts.add(appoint);
                                    }
                                    if(exts.size() >0){
                                        //添加扩展信息
//                                        ariesDao.addAdditions(exts);
                                    }

                                    ariesDao.addCustomerLead(custLead);
                                }
                            }
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


    private Date getBirth(String birth) {
        if(StringUtils.isEmpty(birth)){
            return null;
        }
        if(birth.contains("/")){
            String[] bs = birth.split("/");
            birth = bs[0]+fmt24(bs[1])+fmt24(bs[2]);
        }else if(birth.contains("-")){
            String[] bs = birth.split("-");
            birth = bs[0]+fmt24(bs[1])+fmt24(bs[2]);
        }
        return DataUtils.parseDate(birth,DataUtils.SHORT_DATE);
    }

    private String fmt24(String str) {
        if(str.length() == 1){
            return "0"+str;
        }
        return str;
    }

    private Date getTime(String str) {
        if(StringUtils.isEmpty(str)){
            return null;
        }
        str = str.replaceAll("/","").replaceAll("-","");
        return DataUtils.parseDate(str,DataUtils.SLASH_TIME);
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
