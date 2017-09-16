package com.tianpingpai.crm.parser;

import com.google.gson.annotations.SerializedName;
import com.tianpingpai.parser.HttpResult;
import com.tianpingpai.parser.ListResult;

import java.util.ArrayList;

public class ClaimListResult<T> extends ListResult<T> {
//    @SerializedName("result")
//    private Page<T> page;
//    @SerializedName("statusCode")
//    private int code;
//    @SerializedName("statusDesc")
//    private String desc;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @SerializedName("claim_num")
    private int num ;

//    @Override
//    public int getCode() {
//        return code;
//    }
//
//    @Override
//    public void setCode(int code) {
//        this.code = code;
//    }
//
//    @Override
//    public String getDesc() {
//        return desc;
//    }
//
//    @Override
//    public void setDesc(String desc) {
//        this.desc = desc;
//    }
//
//    public boolean hasMorePage() {
//        if (page == null) {
//            return false;
//        }
//        return !page.lastPage;
//    }
//
//    public boolean isSuccess() {
//        return code == 0;
//    }
//
//    public ArrayList<T> getModels() {
//        if (page != null) {
//            return page.models;
//        }
//        return null;
//    }
//
//    public static class Page<T> {
//        @SerializedName("pageIndex")
//        private int pageIndex;
//        @SerializedName("totalPageCount")
//        private int totalPageNumber;
//
//        // private int nextPage;
//
//        @SerializedName("lastPage")
//        private boolean lastPage;
//
//        public boolean isLastPage() {
//            return lastPage;
//        }
//
//        public void setLastPage(boolean lastPage) {
//            this.lastPage = lastPage;
//        }
//
//        @SerializedName("pageItems")
//        private ArrayList<T> models;
//
//        public int getPageIndex() {
//            return pageIndex;
//        }
//
//        public void setPageIndex(int pageIndex) {
//            this.pageIndex = pageIndex;
//        }
//
//        public int getTotalPageNumber() {
//            return totalPageNumber;
//        }
//
//        public void setTotalPageNumber(int totalPageNumber) {
//            this.totalPageNumber = totalPageNumber;
//        }
//    }
//
//    public void setModels(ArrayList<T> models) {
//        page.models = models;
//    }
//
//    public Page<T> getPage() {
//        return page;
//    }
//
//    public void setPage(Page<T> page) {
//        this.page = page;
//    }
//
//    public int getNextPage() {
//        return page.getPageIndex() + 1;
//    }

}
