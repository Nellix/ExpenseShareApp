package it.mad8.expenseshare.model.datamapper;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

import it.mad8.expenseshare.model.UserGroupModel;
import it.mad8.expenseshare.utils.CalendarUtils;

/**
 * Created by giaco on 15/05/2017.
 */

public class UserGroupDataMapper implements Serializable, DataMapper<UserGroupModel> {
    @Exclude
    public static final String USERS_GROUPS = "users-groups";


    private int notifications;

    private String name;

    private List<String> lastModification;

    private String lastModificationDate;

    private Boolean hasImage;

    public UserGroupDataMapper() {
        //default constructor for firebase
    }

    @Override
    public UserGroupModel toModel() {
        UserGroupModel model = new UserGroupModel();

        model.setName(this.name);
        model.setHasImage(this.hasImage);
        if (this.lastModification != null)
            model.setLastModification(this.lastModification.get(0));
        else
            model.setLastModification("");
        model.setLastModificationDate(CalendarUtils.mapISOStringToCalendar(this.lastModificationDate));
        model.setNotifications(this.notifications);

        return model;
    }

    public int getNotifications() {
        return notifications;
    }

    public void setNotifications(int notifications) {
        this.notifications = notifications;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public Boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(Boolean hasImage) {
        this.hasImage = hasImage;
    }

    public List<String> getLastModification() {
        return lastModification;
    }

    public void setLastModification(List<String> lastModification) {
        this.lastModification = lastModification;
    }
}
