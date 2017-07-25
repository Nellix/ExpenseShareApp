package it.mad8.expenseshare.model.datamapper;

import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import it.mad8.expenseshare.model.GroupModel;
import it.mad8.expenseshare.model.UserModel;

/**
 * Created by Conte on 11/05/2017.
 */

public class GroupDataMapper implements DataMapper<GroupModel> {

    public static final String GROUP_IMAGE_PATH = "group_images/";
    public static final String GROUPS = "groups";

    private String name;
    private Map<String, Boolean> usersRole;
    private Map<String, UserDataMapper> users;
    private boolean hasImage;


    private boolean onlyAdminManagesExpences;
    private boolean onlyAdminManagesUsers;
    private String creatorId;
    private UserDataMapper creator;

    public GroupDataMapper() {
        //default constructor for firebase
    }

    public GroupDataMapper(GroupModel group) {
        this.creator = new UserDataMapper(group.getCreator());
        this.creatorId = group.getCreator().getUid();
        this.name = group.getName();
        this.usersRole = new HashMap<>();
        this.users = new HashMap<>();

        for (UserModel user : group.getUsers().keySet()) {
            usersRole.put(user.getUid(), group.getUsers().get(user));
            users.put(user.getUid(), new UserDataMapper(user));
        }
        this.hasImage = group.isHasImage();
        this.onlyAdminManagesExpences = group.isOnlyAdminManagesExpences();
        this.onlyAdminManagesUsers = group.isOnlyAdminManagesUsers();
    }

    public GroupModel toModel() {
        GroupModel group = new GroupModel(this.name);
        group.setOnlyAdminManagesExpences(this.onlyAdminManagesExpences);
        group.setOnlyAdminManagesUsers(this.onlyAdminManagesUsers);
        group.setHasImage(this.hasImage);

        Map<UserModel,Boolean> usermap = group.getUsers();
        for(String uid : this.users.keySet()){
            UserModel user = this.users.get(uid).toModel();
            user.setUid(uid);
            usermap.put(user, this.usersRole.get(uid));
        }
        UserModel creator = this.creator.toModel();
        creator.setUid(this.creatorId);
        group.setCreator(creator);
        return group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }

    public Map<String, Boolean> getUsersRole() {
        return usersRole;
    }

    public void setUsersRole(Map<String, Boolean> usersRole) {
        this.usersRole = usersRole;
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

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public UserDataMapper getCreator() {
        return creator;
    }

    public void setCreator(UserDataMapper creator) {
        this.creator = creator;
    }

    public Map<String, UserDataMapper> getUsers() {
        return users;
    }

    public void setUsers(Map<String, UserDataMapper> users) {
        this.users = users;
    }
}
