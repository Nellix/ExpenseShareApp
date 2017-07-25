package it.mad8.expenseshare.model.datamapper;

import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.WaitingProposalModel;
import it.mad8.expenseshare.utils.CalendarUtils;

/**
 * Created by Rosario on 05/05/2017.
 */

public class WaitingProposalDataMapper {
    private String name;
    private String description;
    private boolean hasImage;
    private Float price;
    private UserDataMapper creator;
    private String creatorId;
    private String creationDate;

    public WaitingProposalDataMapper() {
    }

    public WaitingProposalDataMapper(String name, String description, boolean hasImage, Float price, UserDataMapper creator, String creatorId, String creationDate) {
        this.name = name;
        this.description = description;
        this.hasImage = hasImage;
        this.price = price;
        this.creator = creator;
        this.creatorId = creatorId;
        this.creationDate = creationDate;
    }

    public WaitingProposalDataMapper(WaitingProposalModel waitingProposal) {

        this.name = waitingProposal.getName();
        this.description = waitingProposal.getDescription();
        this.hasImage = waitingProposal.isHasImage();
        this.price = waitingProposal.getPrice();
        this.creator = new UserDataMapper(waitingProposal.getCreator());
        this.creatorId = waitingProposal.getCreator().getUid();
        this.creationDate = CalendarUtils.mapCalendarToISOString(waitingProposal.getCreationDate());
    }

    public WaitingProposalModel toModel() {

        WaitingProposalModel model = new WaitingProposalModel();

        model.setCreatorId(this.creatorId);
        UserModel c = this.creator.toModel();
        c.setUid(creatorId);
        model.setCreator(c);
        model.setDescription(this.description);
        model.setHasImage(this.hasImage);
        model.setName(this.name);
        model.setPrice(this.price);
        model.setCreationDate(CalendarUtils.mapISOStringToCalendar(this.creationDate));

        return model;
    }

    public Float getPrice() {
        return this.price;
    }

    public void setPrice(Float price) {
        this.price = price;
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

    public UserDataMapper getCreator() {
        return creator;
    }

    public void setCreator(UserDataMapper creator) {
        this.creator = creator;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }
}
