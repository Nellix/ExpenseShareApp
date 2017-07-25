package it.mad8.expenseshare.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Rosario on 13/06/2017.
 */

public class UserExpenseModel implements Serializable,Model {
    private Calendar creationDate;
    private String name;
    private ExpenseModel.ExpenseStatus status;
    private List<UserExpensePaymentModel> payments;

    public UserExpenseModel (){
        this.name = "";
        this.payments = new ArrayList<UserExpensePaymentModel>();
    }

    public UserExpenseModel(Calendar creationDate, String name, ExpenseModel.ExpenseStatus status, List<UserExpensePaymentModel> payments) {
        this.creationDate = creationDate;
        this.name = name;
        this.status = status;
        this.payments = payments;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ExpenseModel.ExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(ExpenseModel.ExpenseStatus status) {
        this.status = status;
    }

    public List<UserExpensePaymentModel> getPayments() {
        return payments;
    }

    public void setPayments(List<UserExpensePaymentModel> payments) {
        this.payments = payments;
    }
}