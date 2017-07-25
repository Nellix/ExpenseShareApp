package it.mad8.expenseshare.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import it.mad8.expenseshare.model.datamapper.RefunderDataMapper;

/**
 * Created by Rosario on 13/06/2017.
 */

public class UserExpensePaymentModel implements Model,Serializable {
    String description;
    String id;
    String creatorId;
    Calendar deadline;
    Float amount;
    List<RefunderModel> refunders;
    Calendar creationDate;
    UserModel creator;
    private float price;

    public UserExpensePaymentModel(){
        this.description = "";
        this.amount = new Float(0);
        this.refunders = new ArrayList<RefunderModel>() ;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(Calendar deadline) {
        this.deadline = deadline;
    }


    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public List<RefunderModel> getRefunders() {
        return refunders;
    }

    public void setRefunders(List<RefunderModel> refunders) {
        this.refunders = refunders;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public UserModel getCreator() {
        return creator;
    }

    public void setCreator(UserModel creator) {
        this.creator = creator;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
