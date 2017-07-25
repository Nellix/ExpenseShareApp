package it.mad8.expenseshare.model;

import java.util.Calendar;


public class BalanceModel {
    private String name;
    private Calendar date;
    private Float amount;

    public BalanceModel(String name, Calendar date, Float amount) {
        this.name = name;
        this.date = date;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public Calendar getDate() {
        return date;
    }

    public Float getAmount() {
        return amount;
    }
}
