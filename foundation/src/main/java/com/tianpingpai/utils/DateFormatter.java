package com.tianpingpai.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
    public static final String LOGIN_DATE_TWO = "yyyyMMddHHmmss";

    static SimpleDateFormat format = new SimpleDateFormat(LOGIN_DATE_TWO);

    public static String format(java.util.Date date) {
        String result = "";
        try {
            result = format.format(date);
        } catch (Exception e) {
            // log.error(e);
        }
        return result;
    }

    public static final String ORDER_DT = "yyyy-MM-dd HH:mm";

    static SimpleDateFormat formatOrder = new SimpleDateFormat(ORDER_DT);
    /**将毫秒数 转化为 日期 String  若是失败则返回空字符
     * @param milliseconds
     * @return
     */
    public static String formatMS(long milliseconds) {
        String result = "";
        try {
            result = formatOrder.format(new Date(milliseconds));
        } catch (Exception e) {
            // log.error(e);
        }
        return result;
    }
}
