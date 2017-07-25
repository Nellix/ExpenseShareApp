package it.mad8.expenseshare.model.datamapper;

import android.app.Notification;

import org.json.JSONException;
import org.json.JSONObject;

import it.mad8.expenseshare.model.NotificationModel;

/**
 * Created by giaco on 22/05/2017.
 */

public class NotificationDataMapper implements DataMapper<NotificationModel> {

    private String type;
    private String payload;

    public NotificationDataMapper() {

    }

    public NotificationDataMapper(String ciao, String mama) {
        this.type = ciao;
        this.payload = mama;
    }


    @Override
    public NotificationModel toModel() {
        NotificationModel model = new NotificationModel();
        try {
            model.setPayload(new JSONObject(payload));
        } catch (JSONException e) {
            model.setPayload(new JSONObject());
        }
        model.setType(NotificationModel.NotificationType.valueOf(type));
        return model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
