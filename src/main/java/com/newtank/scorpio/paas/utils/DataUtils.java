package com.newtank.scorpio.paas.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DataUtils {

    private static final String BATCH_FMT = "yyyyMMdd_HHmmss_S";
    private static final String SEQ_FMT = "yyyyMMdd-HHmmssS";

    private static String fmtDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    private static Date parseDate(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException(dateStr +" 日期字符串非法");
        }
    }


    /**
     * 白羊座批次号
     * @param dateStr 时间字符串
     */
    public synchronized static String generateSeqNo(String dateStr) {
        String result = fmtDate(parseDate(dateStr,SEQ_FMT), BATCH_FMT);
        int index = result.lastIndexOf("_");
        String end = result.substring(index+1);
        int len = end.length();
        if(len ==1) {
            end = "00"+end;
        }else if(len == 2) {
            end = "0"+end;
        }
        return result.substring(0,index+1)+end;
    }
}
