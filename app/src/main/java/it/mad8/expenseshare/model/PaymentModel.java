package it.mad8.expenseshare.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Rosario on 06/05/2017.
 */

public class PaymentModel implements Serializable, Model {

    private Calendar deadline;
    private String description;
    private boolean hasImage;
    private float price;
    private UserModel creator;
    private Calendar creationDate;
    private boolean isCompletetlyRefunded;
    private ArrayList<RefunderModel> refunders;
    private boolean hasReceiptImg;
    private String id;

    public PaymentModel (){

        this.deadline = GregorianCalendar.getInstance();
        this.creationDate = GregorianCalendar.getInstance();
        this.description = "";
        this.hasImage = false;
        this.price = 0;
        this.id = "";
        this.isCompletetlyRefunded = false;
        this.hasReceiptImg = false;
        this.refunders = new ArrayList<>();
    }

    public PaymentModel(Calendar deadline, String description, Boolean hasImage, Float price, Calendar creationDate, Boolean isCompletetlyRefunded, ArrayList<RefunderModel> refunders, Boolean hasReceiptImg, UserModel creator ) {
        this.deadline = deadline;
        this.description = description;
        this.hasImage = hasImage;
        this.price = price;
        this.creationDate = creationDate;
        this.isCompletetlyRefunded = isCompletetlyRefunded;
        this.refunders = refunders;
        this.hasReceiptImg = hasReceiptImg;
        this.creator = creator;
    }


    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public UserModel getCreator() {
        return creator;
    }

    public void setCreator(UserModel creator) {
        this.creator = creator;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public boolean getCompletetlyRefunded() {
        return isCompletetlyRefunded;
    }

    public void setCompletetlyRefunded(boolean completetlyRefunded) {
        isCompletetlyRefunded = completetlyRefunded;
    }

    public ArrayList<RefunderModel> getRefunders() {
        return refunders;
    }

    public void setRefunders(ArrayList<RefunderModel> refunders) {
        this.refunders = refunders;
    }

    public boolean getHasReceiptImg() {
        return hasReceiptImg;
    }

    public void setHasReceiptImg(boolean hasReceiptImg) {
        this.hasReceiptImg = hasReceiptImg;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public Float getRefundedQuota(){
        float quota = 0;

        for(RefunderModel refunder : this.refunders){
            if(! refunder.getUserId().equals(this.creator.getUid())) {
                if (refunder.getStatus() != RefunderModel.RefundState.CONFIRMED) {
                    quota += refunder.getRefundAmount();
                }
            }
        }
        return quota;

    }


}
