package it.mad8.expenseshare.model.datamapper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import it.mad8.expenseshare.model.RefunderModel;
import it.mad8.expenseshare.model.PaymentModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.utils.CalendarUtils;

/**
 * Created by Rosario on 06/05/2017.
 */

public class PaymentDataMapper implements DataMapper<PaymentModel> {
    private String deadline;
    private String description;
    private Boolean hasImage;
    private Boolean hasReceiptImg;
    private Float price;
    private String creatorId;
    private UserDataMapper creator;
    private String creationDate;
    private Boolean isCompletetlyRefunded;
    private HashMap <String,RefunderDataMapper> refunders;

    public PaymentDataMapper () {}

    public PaymentDataMapper(String deadline, String description, Boolean hasImage, Boolean hasReceiptImg, Float price, String creatorId, UserDataMapper creator, String creationDate, Boolean isCompletetlyRefunded, HashMap<String, RefunderDataMapper> refunders) {
        this.deadline = deadline;
        this.description = description;
        this.hasImage = hasImage;
        this.hasReceiptImg = hasReceiptImg;
        this.price = price;
        this.creatorId = creatorId;
        this.setCreator(creator);
        this.creationDate = creationDate;
        this.isCompletetlyRefunded = isCompletetlyRefunded;
        this.refunders = refunders;
    }

    public PaymentDataMapper(PaymentModel payment) {
        this.deadline = CalendarUtils.mapCalendarToISOString(payment.getDeadline());
        this.description = payment.getDescription();
        this.hasImage = payment.getHasImage();
        this.hasReceiptImg = payment.getHasReceiptImg();
        this.price = payment.getPrice();
        this.creatorId =  payment.getCreator().getUid();
        this.creator = new UserDataMapper(payment.getCreator());
        this.creationDate = CalendarUtils.mapCalendarToISOString(payment.getCreationDate());
        this.isCompletetlyRefunded = payment.getCompletetlyRefunded();

        this.refunders = new HashMap<>();
        for(RefunderModel refunder: payment.getRefunders()){
            this.refunders.put(refunder.getUserId(), new RefunderDataMapper(refunder));
        }
    }

    @Override
    public PaymentModel toModel(){
        PaymentModel payment = new PaymentModel();
        payment.setPrice(this.price);
        payment.setDescription(this.description);
        payment.setCompletetlyRefunded(this.isCompletetlyRefunded);
        payment.setHasReceiptImg(this.hasReceiptImg);
        payment.setDeadline(CalendarUtils.mapISOStringToCalendar(this.deadline));
        payment.setCreationDate(CalendarUtils.mapISOStringToCalendar(this.creationDate));
        payment.setHasImage(this.hasImage);
        UserModel creator = this.getCreator().toModel();
        creator.setUid(this.creatorId);
        payment.setCreator(creator);

        if(this.getRefunders() != null) {
            List<RefunderModel> refunderList =  payment.getRefunders();
            for (String userId : this.getRefunders().keySet()) {
                RefunderModel refunder = this.getRefunders().get(userId).toModel();
                refunder.setUserId(userId);
                refunderList.add(refunder);
            }
        }

        return payment;
    }



    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getCompletetlyRefunded() {
        return isCompletetlyRefunded;
    }

    public void setCompletetlyRefunded(Boolean completetlyRefunded) {
        isCompletetlyRefunded = completetlyRefunded;
    }

    public HashMap<String, RefunderDataMapper> getRefunders() {
        return refunders;
    }

    public void setRefunders(HashMap<String, RefunderDataMapper> refunders) {
        this.refunders = refunders;
    }

    public Boolean getHasReceiptImg() {
        return hasReceiptImg;
    }

    public void setHasReceiptImg(Boolean hasReceiptImg) {
        this.hasReceiptImg = hasReceiptImg;
    }

    public UserDataMapper getCreator() {
        return creator;
    }

    public void setCreator(UserDataMapper creator) {
        this.creator = creator;
    }
}
