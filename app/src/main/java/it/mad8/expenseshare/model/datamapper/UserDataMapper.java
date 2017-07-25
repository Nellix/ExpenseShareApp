package it.mad8.expenseshare.model.datamapper;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

import it.mad8.expenseshare.model.UserModel;

/**
 * Created by Aniello Malinconico on 08/05/2017.
 */

public class UserDataMapper implements Serializable, DataMapper<UserModel> {

    @Exclude
    public static final String USERS = "users";
    @Exclude
    public static final String USER_IMAGE_PATH="users/";


    private String username;
    private String email;
    private boolean hasImage;


    public UserDataMapper() {
        //default constructor for firebase
    }

    public UserDataMapper(String username, String email, boolean hasImage) {
        this.username = username;
        this.email = email;
        this.hasImage = hasImage;
    }

    public UserDataMapper(UserModel user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.hasImage = user.isHasImage();
    }

    @Override
    public UserModel toModel() {
        UserModel user = new UserModel();
        user.setEmail(this.email);
        user.setUsername(this.username);
        user.setHasImage(this.hasImage);
        return user;
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

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

}
