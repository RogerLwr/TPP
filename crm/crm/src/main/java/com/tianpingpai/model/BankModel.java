package com.tianpingpai.model;

import com.google.gson.annotations.SerializedName;
import com.tianpingpai.ui.Binding;

import java.io.Serializable;
import java.util.List;

public class BankModel implements Serializable,Cloneable {
    private static final long serialVersionUID = -5085499929043108560L;

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("card_no")
    private String cardNo;

    @SerializedName("name")
    private String name;

    @SerializedName("is_public")
    private int isPublic;

    @SerializedName("bank")
    private String bank;

    @SerializedName("upload_time")
    private Long uploadTime;

    @SerializedName("status")
    private int status;

    @SerializedName("contract_id")
    private String contractId;

    @SerializedName("opening_bank_address")
    private String openingBankAddress;

    @SerializedName("bank_province")
    private String bankProvince;

    @SerializedName("bank_city")
    private String bankCity;

    @SerializedName("ID_no")
    private String IDno;

    @SerializedName("bankcard_pictures")
    private List<String> bankcardPictures ;

    @SerializedName("idcard_pictures")
    private List<String> idcardPictures ;

    @SerializedName("contract_pictures")
    private List<String> contractPictures ;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getIDno() {
        return IDno;
    }

    public void setIDno(String IDno) {
        this.IDno = IDno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public Long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getOpeningBankAddress() {
        return openingBankAddress;
    }

    public void setOpeningBankAddress(String openingBankAddress) {
        this.openingBankAddress = openingBankAddress;
    }

    public String getBankProvince() {
        return bankProvince;
    }

    public void setBankProvince(String bankProvince) {
        this.bankProvince = bankProvince;
    }

    public String getBankCity() {
        return bankCity;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity;
    }

    public List<String> getBankcardPictures() {
        return bankcardPictures;
    }

    public void setBankcardPictures(List<String> bankcardPictures) {
        this.bankcardPictures = bankcardPictures;
    }

    public List<String> getIdcardPictures() {
        return idcardPictures;
    }

    public void setIdcardPictures(List<String> idcardPictures) {
        this.idcardPictures = idcardPictures;
    }

    public List<String> getContractPictures() {
        return contractPictures;
    }

    public void setContractPictures(List<String> contractPictures) {
        this.contractPictures = contractPictures;
    }

    @Override
    public String toString() {
        return "BankModel{" +
                "id=" + id +
                ", userId=" + userId +
                ", cardNo='" + cardNo + '\'' +
                ", name='" + name + '\'' +
                ", isPublic=" + isPublic +
                ", bank='" + bank + '\'' +
                ", uploadTime=" + uploadTime +
                ", status=" + status +
                ", contractId='" + contractId + '\'' +
                ", openingBankAddress='" + openingBankAddress + '\'' +
                ", bankProvince='" + bankProvince + '\'' +
                ", bankCity='" + bankCity + '\'' +
                ", IDno='" + IDno + '\'' +
                ", bankcardPictures=" + bankcardPictures +
                ", idcardPictures=" + idcardPictures +
                ", contractPictures=" + contractPictures +
                '}';
    }
}
