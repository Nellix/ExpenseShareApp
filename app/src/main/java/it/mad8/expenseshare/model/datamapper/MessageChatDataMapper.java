package it.mad8.expenseshare.model.datamapper;

import java.io.Serializable;
import java.util.Calendar;

import it.mad8.expenseshare.model.MessageChatModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.utils.CalendarUtils;


/**
 * Created by Aniello Malinconico on 26/05/2017.
 */

public class MessageChatDataMapper implements Serializable, DataMapper<MessageChatModel> {

    private String message;
    private String timestamp;
    private UserDataMapper creator;
    private String creatorId;

    public MessageChatDataMapper(String message, String timestamp, UserDataMapper creator, String creatorId) {
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public UserDataMapper getCreator() {
        return creator;
    }

    public void setCreator(UserDataMapper creator) {
        this.creator = creator;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public MessageChatModel toModel() {
        return new MessageChatModel(message, CalendarUtils.mapISOStringToCalendar(timestamp),creator.toModel(),creatorId);
    }
}
