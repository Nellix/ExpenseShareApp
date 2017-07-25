package it.mad8.expenseshare.model;

import java.io.Serializable;

/**
 * Created by giaco on 26/05/2017.
 */

public class ContactModel implements Serializable {

    private boolean selected;
    private UserModel user;

    public ContactModel(UserModel user){
        this(user, false);
    }

    public ContactModel(UserModel user, boolean selected){
        this.setSelected(selected);
        this.setUser(user);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserModel) {
            UserModel c = (UserModel) obj;
            if (this.getUser().equals(c))
                return true;
        } else if(obj instanceof ContactModel){
            ContactModel c = (ContactModel) obj;
            if (this.getUser().equals(c.getUser()))
                return true;
        }

        return false;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
