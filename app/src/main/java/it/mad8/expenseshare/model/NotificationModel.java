package it.mad8.expenseshare.model;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by giaco on 22/05/2017.
 */

public class NotificationModel implements Serializable, Model {

    private NotificationType type;
    private String id;
    private JSONObject payload;


    public NotificationModel(){

    }

    public NotificationModel(String id, NotificationType type, JSONObject payload){
        this.id = id;
        this.type = type;
        this.payload = payload;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public JSONObject getPayload() {
        return payload;
    }

    public void setPayload(JSONObject payload) {
        this.payload = payload;
    }


    public enum NotificationType {
        NEW_GROUP, NEW_EXPENSE, PAYMENT_REMINDER, PAYMENT_DONE, PAYMENT_SUCCESS, NEW_PROPOSAL, PROPOSAL_CHOSEN, NEW_PAYMENT, NEW_MESSAGE
    }
}
