package it.mad8.expenseshare.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by Rosario on 01/05/2017.
 */

public class ProposalModel implements Serializable, Model {

    private String id;
    private String name;
    private String description;
    private boolean hasImage;
    private float price;
    private UserModel creator;
    private Calendar creationDate;
    private HashMap<String, Integer> users;


    public ProposalModel() {
        this.name = "";
        this.description = "";
        this.hasImage = false;
        this.price = 0;
        this.creationDate = GregorianCalendar.getInstance();
        this.users = new HashMap<>();
    }

    public ProposalModel(String name, String description, boolean hasImage, float price, UserModel creator, Calendar creationDate) {
        this.name = name;
        this.description = description;
        this.hasImage = hasImage;
        this.price = price;
        this.creator = creator;
        this.creationDate = creationDate;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public HashMap<String, Integer> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, Integer> users) {
        this.users = users;
    }

    public UserModel getCreator() {
        return creator;
    }

    public void setCreator(UserModel creator) {
        this.creator = creator;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}