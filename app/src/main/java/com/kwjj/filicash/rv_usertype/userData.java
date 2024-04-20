package com.kwjj.filicash.rv_usertype;

public class userData {
    String userType;
    String discount;
    Boolean isDriverNeeded;
    String id;

    public userData(String userType, String discount, Boolean isDriverNeeded, String id) {
        this.userType = userType;
        this.discount = discount;
        this.isDriverNeeded = isDriverNeeded;
        this.id = id;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public Boolean getDriverNeeded() {
        return isDriverNeeded;
    }

    public void setDriverNeeded(Boolean driverNeeded) {
        isDriverNeeded = driverNeeded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
