package it.mad8.expenseshare;

import android.app.Application;


import it.mad8.expenseshare.model.UserModel;

/**
 * Created by giaco on 12/05/2017.
 */

public class ExpenseShareApplication extends Application {
    private UserModel user;

    public ExpenseShareApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void setUserModel(UserModel user){
        this.user = user;
    }

    public UserModel getUserModel(){
        return user;
    }
}
