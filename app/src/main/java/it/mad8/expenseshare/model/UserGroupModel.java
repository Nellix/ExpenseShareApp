package it.mad8.expenseshare.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Aniello Malinconico on 08/05/2017.
 */

public class UserGroupModel implements Serializable,Model {


    private int notifications;
    private String name;
    private String lastModification;
    private Calendar lastModificationDate;
    private boolean hasImage;
    private String groupId;


    public UserGroupModel(){
        //default constructor for firebase
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


    public Calendar getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Calendar lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public boolean getHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public String getLastModification() {
        return lastModification;
    }

    public void setLastModification(String lastModification) {
        this.lastModification = lastModification;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
