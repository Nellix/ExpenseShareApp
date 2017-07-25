package it.mad8.expenseshare.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by giaco on 17/04/2017.
 */


public class UserModel implements Serializable, Model {

    private String username;
    private String email;
    private String uid;
    private boolean hasImage;

    public UserModel() {
        this.username = "";
        this.email = "";
        this.uid = "";
        this.hasImage = false;
    }

    public UserModel(String username) {
        this.username = username;
    }

    public UserModel(String username, String email) {
        setEmail(email);
        setUsername(username);

    }

    public UserModel(String username, String email, String uid, boolean hasImage) {
        this.username = username;
        this.email = email;
        this.uid = uid;
        this.hasImage = hasImage;

    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UserModel) {
            UserModel c = (UserModel) o;
            if (this.getUid().equals(c.getUid()))
                return true;
        }

        return false;
    }
}

