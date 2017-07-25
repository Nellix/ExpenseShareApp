package it.mad8.expenseshare.model;

import com.firebase.ui.auth.ui.User;
import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupModel implements Serializable, Model {

    private String name;
    private Map<UserModel, Boolean> users;
    private boolean hasImage;
    private boolean onlyAdminManagesExpences;
    private boolean onlyAdminManagesUsers;
    private UserModel creator;

    public GroupModel(String name, Map<UserModel, Boolean> users, boolean hasImage, boolean onlyAdminManagesExpences, boolean onlyAdminManagesUsers, UserModel creator) {
        this(name, users, hasImage, onlyAdminManagesExpences, onlyAdminManagesUsers);
        if (creator == null)
            throw new IllegalArgumentException("Creator can't be null");
        if (!users.containsKey(creator))
            users.put(creator, true);
        this.creator = creator;
    }

    public GroupModel(String name, Map<UserModel, Boolean> users, boolean hasImage, boolean onlyAdminManagesExpences, boolean onlyAdminManagesUsers) {
        this(name, users, hasImage, onlyAdminManagesExpences);
        this.onlyAdminManagesUsers = onlyAdminManagesUsers;
    }

    public GroupModel(String name, Map<UserModel, Boolean> users, boolean hasImage, boolean onlyAdminManagesExpences) {
        this(name, users, hasImage);
        this.onlyAdminManagesExpences = onlyAdminManagesExpences;
    }

    public GroupModel(String name, Map<UserModel, Boolean> users, boolean hasImage) {
        this(name, users);
        this.hasImage = hasImage;
    }

    public GroupModel(String name, Map<UserModel, Boolean> users) {
        this(name);
        if (users == null)
            throw new IllegalArgumentException("User list can't be null");
        this.users = users;

    }

    public GroupModel(String name) {
        if (name == null)
            throw new IllegalArgumentException("Name can't be null");
        this.name = name;
        this.users = new HashMap<>();
        this.hasImage = false;
        this.onlyAdminManagesExpences = false;
        this.onlyAdminManagesUsers = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<UserModel, Boolean> getUsers() {
        return users;
    }

    public void setUsers(Map<UserModel, Boolean> users) {
        this.users = users;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public boolean isOnlyAdminManagesExpences() {
        return onlyAdminManagesExpences;
    }

    public void setOnlyAdminManagesExpences(boolean onlyAdminManagesExpences) {
        this.onlyAdminManagesExpences = onlyAdminManagesExpences;
    }

    public boolean isOnlyAdminManagesUsers() {
        return onlyAdminManagesUsers;
    }

    public void setOnlyAdminManagesUsers(boolean onlyAdminManagesUsers) {
        this.onlyAdminManagesUsers = onlyAdminManagesUsers;
    }

    public UserModel getCreator() {
        return creator;
    }

    public void setCreator(UserModel creator) {
        this.creator = creator;
    }
}