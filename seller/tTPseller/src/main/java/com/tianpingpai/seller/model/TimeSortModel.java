package com.tianpingpai.seller.model;

import java.io.Serializable;

/**
 * 用户实体类
 *
 * @author Administrator
 */
public class TimeSortModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public String time_sort;  // 默认 "" 按照更新时间，传BY_CREATED是按照创建时间

    private String timeSortName;

    public String getTime_sort() {
        return time_sort;
    }

    public void setTime_sort(String time_sort) {
        this.time_sort = time_sort;
    }

    public String getTimeSortName() {
        return timeSortName;
    }

    public void setTimeSortName(String timeSortName) {
        this.timeSortName = timeSortName;
    }
}
