package it.mad8.expenseshare.model;

import java.io.Serializable;
import java.util.Calendar;
import it.mad8.expenseshare.model.ExpenseModel.ExpenseStatus;

/**
 * Created by Rosario on 30/04/2017.
 */
public class UserGroupExpenseModel implements Serializable, Model {

    private String expenseId;
    private String name;
    private ExpenseStatus status;
    private Calendar creationDate;
    private Calendar lastModificationDate;
    private double cost;

    public UserGroupExpenseModel() {
    }

    public UserGroupExpenseModel(String expenseId, String name, ExpenseStatus status, Calendar creationDate, Calendar lastModificationDate, double cost) {
        this.expenseId = expenseId;
        this.name = name;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(ExpenseStatus status) {
        this.status = status;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public Calendar getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Calendar lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(String expenseId) {
        this.expenseId = expenseId;
    }
}