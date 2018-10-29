package com.newtank.scorpio.paas.utils;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class RemarkConvertor {

    private static final String SEMICOLON = "；";
    private static final String COLON = "：";
    private static final String SEX_CN = "性别";
    private static final String AT_CN = "预约时间";
    private static final String PD_CN = "产品名称";
    private static final String QA_CN = "问卷";

    public static void main(String[] args) {
        String remark = "性别：女；预约时间：2018-10-21 11:22:35；产约名称：富德生命全心全意意外险；问卷：请问您是否有子女？ [13岁以上]；请问您和家人常以哪种方式出游？[自驾车]；请问您更倾向于哪种商业保障？ [医疗保障]；\n" +
                "     \n" +
                "     说?";
        System.out.println(JSON.toJSONString(parseRemark(remark)));
    }

    /**
     * 性别：女；预约时间：2018-10-21 11:22:35；产约名称：富德生命全心全意意外险；问卷：请问您是否有子女？ [13岁以上]；请问您和家人常以哪种方式出游？[自驾车]；请问您更倾向于哪种商业保障？ [医疗保障]；
     *
     * 说?
     * @param remark
     * @return
     */
    private static String[][] parseRemark(String remark) {
        if(remark.contains(SEMICOLON)) {
            String[] rs = remark.split(SEMICOLON);
            int len = rs.length;
            String[][] rt = new String[len][2];
            int index = 0;
            for(String r: rs) {
                if(r.contains(COLON)) {
                    String[] kv = r.split(COLON);
                    rt[index][0] = kv[0];
                    rt[index][1] = kv[1];
                }else {
                    rt[index][0] = "";
                    rt[index][1] = "";
                }
                index ++;
            }
            return rt;
        }
        return null;
    }

    /**
     * 获取remark里面的数据类型
     * @param remark
     * @return
     */
    public static Map<String,String> getRemarkMap(String remark) {
        Map<String,String> map = new HashMap<>();
        String[][] rss = parseRemark(remark);
        for(String[] rs: rss) {
            if(SEX_CN.equalsIgnoreCase(rs[0])) {
                map.put("sex",rs[1]);
            }else if(AT_CN.equalsIgnoreCase(rs[0])) {
                map.put("appoint",rs[1]);
            }else if(PD_CN.equalsIgnoreCase(rs[0])) {
                map.put("product",rs[1]);
            }else if(QA_CN.equalsIgnoreCase(rs[0])) {
                map.put("qa", rs[1]);
            }
        }
        return map;
    }
}
