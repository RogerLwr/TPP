package com.tianpingpai.buyer.model;
import com.google.gson.annotations.SerializedName;
public class Promotion {
    @SerializedName("id")
    private int id;
    @SerializedName("type")
    private int type;
    @SerializedName("title")
    private String title;
    @SerializedName("rule")
    private String rule;
    @SerializedName("label")
    private String label;
    @SerializedName("bgcolor")
    private String bgcolor;
    @SerializedName("status")
    private int status;

    public String getBgcolor() {
        return bgcolor;
    }

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setType(int type){
        this.type = type;
    }
    public int getType(){
        return this.type;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getTitle(){
        return this.title;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
}
