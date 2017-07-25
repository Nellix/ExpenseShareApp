package it.mad8.expenseshare.model.datamapper;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

import it.mad8.expenseshare.model.UserGroupExpenseModel;
import it.mad8.expenseshare.model.ExpenseModel.ExpenseStatus;
import it.mad8.expenseshare.utils.CalendarUtils;

/**
 * Created by giaco on 12/05/2017.
 */

public class UserGroupExpenseMapper implements Serializable, DataMapper<UserGroupExpenseModel> {

    private String name;
    private String status;
    private String creationDate;
    private String lastModificationDate;
    private double cost;

    public UserGroupExpenseMapper() {

    }

    @Override
    public UserGroupExpenseModel toModel() {
        UserGroupExpenseModel model = new UserGroupExpenseModel();
        model.setCost(getCost());
        model.setName(getName());
        switch (getStatus()) {
            case "PROPOSAL":
                model.setStatus(ExpenseStatus.PROPOSAL);
                break;
            case "WAITING":
                model.setStatus(ExpenseStatus.WAITING);
                break;
            case "REFUND":
                model.setStatus(ExpenseStatus.REFUND);
                break;
            case "CLOSED":
                model.setStatus(ExpenseStatus.CLOSED);
                break;
        }
        model.setCreationDate(CalendarUtils.mapISOStringToCalendar(getCreationDate()));
        model.setLastModificationDate(CalendarUtils.mapISOStringToCalendar(getLastModificationDate()));
        return model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
