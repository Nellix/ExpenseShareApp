package it.mad8.expenseshare.model.datamapper;

import java.util.List;
import java.util.Map;

import it.mad8.expenseshare.model.ExpenseModel;
import it.mad8.expenseshare.model.PaymentModel;
import it.mad8.expenseshare.model.UserExpenseModel;
import it.mad8.expenseshare.model.UserExpensePaymentModel;
import it.mad8.expenseshare.utils.CalendarUtils;

/**
 * Created by Rosario on 13/06/2017.
 */

public class UserExpenseDataMapper implements DataMapper<UserExpenseModel> {

    private String name;
    private String creationDate;
    private ExpenseModel.ExpenseStatus status;
    private Map<String,UserExpensePaymentDataMapper> payments;

    public UserExpenseDataMapper () {}



    public UserExpenseModel toModel() {
        UserExpenseModel model = new UserExpenseModel();
        model.setName(this.name);
        model.setStatus(this.status);
        model.setCreationDate(CalendarUtils.mapISOStringToCalendar(this.creationDate));
        convertPayments(model.getPayments());

        return model;
    }


    private void convertPayments( List <UserExpensePaymentModel> payments) {
        if (this.payments != null) {
            for (String paymentId : this.payments.keySet()) {
                UserExpensePaymentModel newPayment = this.payments.get(paymentId).toModel();
                newPayment.setId(paymentId);
                payments.add(newPayment);
            }

        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public ExpenseModel.ExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(ExpenseModel.ExpenseStatus status) {
        this.status = status;
    }

    public Map<String, UserExpensePaymentDataMapper> getPayments() {
        return payments;
    }

    public void setPayments(Map<String, UserExpensePaymentDataMapper> payments) {
        this.payments = payments;
    }
}
