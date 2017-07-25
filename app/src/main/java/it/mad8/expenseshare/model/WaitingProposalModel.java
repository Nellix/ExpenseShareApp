package it.mad8.expenseshare.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Rosario on 01/05/2017.
 */

public class WaitingProposalModel implements Serializable, Model {

    private String name;
    private String description;
    private boolean hasImage;
    private float price;
    private UserModel creator;
    private Calendar creationDate;
    private String creatorId;

    public WaitingProposalModel() {
        this.name = "";
        this.description = "";
        this.hasImage = false;
        this.price = 0;
        this.creatorId = "";
        this.creationDate = GregorianCalendar.getInstance();
    }

    public WaitingProposalModel(ProposalModel origin, UserModel creator) {
        this.name = origin.getName();
        this.description = origin.getDescription();
        this.hasImage = origin.isHasImage();
        this.price = origin.getPrice();
        this.creator = creator;
        this.creatorId = creator.getUid();
        this.creationDate = GregorianCalendar.getInstance();
    }

    public WaitingProposalModel(String name, String description, boolean hasImage, float price, UserModel creator, String creatorId, Calendar creationDate) {
        this.name = name;
        this.description = description;
        this.hasImage = hasImage;
        this.price = price;
        this.creator = creator;
        this.creatorId = creatorId;
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

    public UserModel getCreator() {
        return creator;
    }

    public void setCreator(UserModel creator) {
        this.creator = creator;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}