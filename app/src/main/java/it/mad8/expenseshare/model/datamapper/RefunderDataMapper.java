package it.mad8.expenseshare.model.datamapper;

import java.io.Serializable;

import it.mad8.expenseshare.model.RefunderModel;

/**
 * Created by Rosario on 07/05/2017.
 */


public class RefunderDataMapper implements Serializable, DataMapper<RefunderModel> {

    private float refundAmount;
    private RefunderModel.RefundState status;
    //private boolean hasImage;
    private UserDataMapper user;
    public RefunderDataMapper() {
    }

    public RefunderDataMapper(float refundAmount, RefunderModel.RefundState status, UserDataMapper user) {
        this.refundAmount = refundAmount;
        this.status = status;
        //this.hasImage = hasImage;
        this.user = user;
    }

    public RefunderDataMapper (RefunderModel model){
        this.refundAmount = model.getRefundAmount();
        this.status = model.getStatus();
        this.user = new UserDataMapper(model.getUser());
    }

    public RefunderModel toModel() {
        RefunderModel model = new RefunderModel();
        model.setRefundAmount(this.refundAmount);
        model.setStatus(this.status);
        //model.setHasImage(this.hasImage);
        model.setUser(this.user.toModel());
        return model;
    }

    public float getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(float refundAmount) {
        this.refundAmount = refundAmount;
    }

    public RefunderModel.RefundState getStatus() {
        return status;
    }

    public void setStatus(RefunderModel.RefundState status) {
        this.status = status;
    }

    public UserDataMapper getUser() {
        return user;
    }

    public void setUser(UserDataMapper user) {
        this.user = user;
    }

    /*public boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }*/
}
