package com.tianpingpai.seller.adapter;

public class MineAction {
    public int icon;
    public String title;
    public Class target;
    public String url; //网页的情况

    public boolean isSeparator;

    public static MineAction getSeperator(){
        MineAction action = new MineAction();
        action.isSeparator = true;
        return action;
    }
}
