package com.tianpingpai.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ContractModel implements Serializable,Cloneable {
    private static final long serialVersionUID = -5085499929043108560L;

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("contract_id")
    private String contractId;

    @SerializedName("legal_name")
    private String legalName;

    @SerializedName("legal_idcard")
    private String legalIdcard;

    @SerializedName("remarks")
    private String remarks;

    @SerializedName("status")
    private int status;

    @SerializedName("upload_time")
    private long uploadTime;

    @SerializedName("legal_idcard_pictures")
    private List<String> legalIdcardPictures;

    @SerializedName("contract_pictures")
    private List<String> contractPictures;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getContractPictures() {
        return contractPictures;
    }

    public void setContractPictures(List<String> contractPictures) {
        this.contractPictures = contractPictures;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getLegalIdcard() {
        return legalIdcard;
    }

    public void setLegalIdcard(String legalIdcard) {
        this.legalIdcard = legalIdcard;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public List<String> getLegalIdcardPictures() {
        return legalIdcardPictures;
    }

    public void setLegalIdcardPictures(List<String> legalIdcardPictures) {
        this.legalIdcardPictures = legalIdcardPictures;
    }
}
