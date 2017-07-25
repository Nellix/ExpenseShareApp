package it.mad8.expenseshare.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Aniello Malinconico on 08/05/2017.
 */

public class MessageChatModel implements Model,Serializable{

    private String message;
    private Calendar timestamp;
    private UserModel creator;
    private String creatorId;

    public MessageChatModel() {
    }

    public MessageChatModel(String message, Calendar timestamp, UserModel creator, String creatorId) {
        this.message = message;
        this.timestamp = timestamp;
        this.creator = creator;
        this.creatorId = creatorId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
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
}
