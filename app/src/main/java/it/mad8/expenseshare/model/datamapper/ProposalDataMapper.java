package it.mad8.expenseshare.model.datamapper;

import java.io.Serializable;
import java.util.HashMap;

import it.mad8.expenseshare.model.ProposalModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.utils.CalendarUtils;

/**
 * Created by Rosario on 05/05/2017.
 */

public class ProposalDataMapper implements Serializable, DataMapper<ProposalModel> {
    private String name;
    private String description;
    private boolean hasImage;
    private float price;
    private String creatorId;
    private UserDataMapper creator;
    private String creationDate;
    private HashMap<String, Integer> users;

    public ProposalDataMapper() {
    }

    public ProposalDataMapper(String name, String description, boolean hasImage, Float price, String creatorID, UserDataMapper creator, String creationDate, HashMap<String, Integer> users) {
        this.name = name;
        this.description = description;
        this.hasImage = hasImage;
        this.price = price;
        this.creatorId = creatorID;
        this.creator = creator;
        this.users = users;
        this.creationDate = creationDate;
    }

    public ProposalDataMapper(ProposalModel model) {
        this.name = model.getName();
        this.description = model.getDescription();
        this.hasImage = model.getHasImage();
        this.price = model.getPrice();
        this.creatorId = model.getCreator().getUid();
        this.creator = new UserDataMapper(model.getCreator());
        this.creationDate = CalendarUtils.mapCalendarToISOString(model.getCreationDate());

        this.users = new HashMap<>();

        if (model.getUsers() != null) {
            for (String userId : model.getUsers().keySet()) {
                this.users.put(userId, model.getUsers().get(userId));
            }
        }
    }

    @Override
    public ProposalModel toModel() {

        ProposalModel model = new ProposalModel();

        UserModel creator = this.creator.toModel();
        creator.setUid(this.creatorId);
        model.setCreator(creator);
        model.setDescription(this.description);
        model.setHasImage(this.hasImage);
        model.setName(this.name);
        model.setPrice(this.price);

        if (this.creationDate != null) {
            model.setCreationDate(CalendarUtils.mapISOStringToCalendar(this.creationDate));
        }

        if (this.users != null) {
            HashMap<String, Integer> modelUsers = model.getUsers();
            for (String userId : this.users.keySet()) {
                modelUsers.put(userId, this.users.get(userId));
            }
        }

        return model;
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

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public HashMap<String, Integer> getUsers() {
        return users;
    }

    public void setUsers(HashMap<String, Integer> users) {
        this.users = users;
    }

    public UserDataMapper getCreator() {
        return creator;
    }

    public void setCreator(UserDataMapper creator) {
        this.creator = creator;
    }
}
