package it.mad8.expenseshare.model.datamapper;

import java.util.List;
import java.util.Map;

import it.mad8.expenseshare.model.ExpenseModel;
import it.mad8.expenseshare.model.RefunderModel;
import it.mad8.expenseshare.model.UserExpensePaymentModel;
import it.mad8.expenseshare.utils.CalendarUtils;

/**
 * Created by Rosario on 13/06/2017.
 */

public class UserExpensePaymentDataMapper implements DataMapper<UserExpensePaymentModel> {
    String description;
    String deadline;

    Map<String, RefunderDataMapper> refunders;
    Float amount;
    String creationDate;
    UserDataMapper creator;

    String creatorId;
    private Float price;

    UserExpensePaymentDataMapper() {
    }


    @Override
    public UserExpensePaymentModel toModel() {
        UserExpensePaymentModel model = new UserExpensePaymentModel();
        model.setDescription(this.description);
        model.setAmount(this.amount);
        model.setCreationDate(CalendarUtils.mapISOStringToCalendar(this.creationDate));
        model.setDeadline(CalendarUtils.mapISOStringToCalendar(this.deadline));
        model.setCreatorId(this.creatorId);
        model.setPrice(this.price);
        if (this.refunders != null) {
            List<RefunderModel> refunderList = model.getRefunders();
            for (String userId : this.refunders.keySet()) {
                RefunderModel refunder = this.refunders.get(userId).toModel();
                refunder.setUserId(userId);
                refunderList.add(refunder);
            }
        }

        if (this.creator != null) {
            model.setCreator(this.creator.toModel());
        }

        return model;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }


    public Map<String, RefunderDataMapper> getRefunders() {
        return refunders;
    }

    public void setRefunders(Map<String, RefunderDataMapper> refunders) {
        this.refunders = refunders;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public UserDataMapper getCreator() {
        return creator;
    }

    public void setCreator(UserDataMapper creator) {
        this.creator = creator;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
