package it.mad8.expenseshare.model;

import java.io.Serializable;

/**
 * Created by giaco on 17/04/2017.
 */

public class RefunderModel implements Serializable, Model {

    private float refundAmount;
    private RefundState status;
    //private boolean hasImage;
    private UserModel user;
    private String userId;

    public RefunderModel (){}

    public RefunderModel(float refundAmount, RefundState status, UserModel ussr, boolean hasImage) {
        this.refundAmount = refundAmount;
        this.status = status;
        //this.hasImage = hasImage;
        this.user = ussr;

    }

    public float getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(float refundAmount) {
        this.refundAmount = refundAmount;
    }

    public RefundState getStatus() {
        return status;
    }

    public void setStatus(RefundState status) {
        this.status = status;
    }


    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    /*public boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }*/

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public enum RefundState{
        TO_PAY,PAID,CONFIRMED,BUYER
    }
}
