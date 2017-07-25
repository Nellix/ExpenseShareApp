package it.mad8.expenseshare.model;

/**
 * Created by Rosario on 01/05/2017.
 */


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseModel implements Serializable, Model {

    private String name;
    private String groupId;
    private String id;
    private String description;
    private boolean hasReceiptImg;
    private String creatorId;
    private UserModel creator;
    private Calendar creationDate;
    private Calendar lastModificationDate;
    private ArrayList<ProposalModel> proposals;
    private ExpenseStatus status;
    private Calendar proposalDeadline;
    private  List <PaymentModel> payments;
    private boolean hasProposalState;
    private boolean isOneTime;
    private RefundPartition refundPartition;
    private VotationCriteria votationCriteria;
    private WaitingProposalModel waitingProposal;
    private ArrayList<UserModel> users;

    public enum ExpenseStatus {
        PROPOSAL, WAITING, REFUND, CLOSED
    }

    public enum RefundPartition {
        PROPORTIONAL, CUSTOM
    }

    public enum VotationCriteria {
        MAJORITY_OF_PARTICIPANT, MAJORITY_OF_VOTES, UNANIMITY;
    }


    public ExpenseModel() {
        this.name = "";
        this.groupId = "";
        this.description = "";
        this.creatorId = "";
        this.id = "";

        this.creationDate = GregorianCalendar.getInstance();
        this.lastModificationDate = GregorianCalendar.getInstance();
        this.proposalDeadline = GregorianCalendar.getInstance();

        this.users = new ArrayList<>();
        this.proposals = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.waitingProposal = new WaitingProposalModel();

    }

    public ExpenseModel(String name,
                        String groupId,
                        String description,
                        boolean hasReceiptImg,
                        String creator,
                        Calendar creationDate,
                        Calendar lastModificationDate,
                        ArrayList<ProposalModel> proposals,
                        WaitingProposalModel waitingProposal,
                        ExpenseStatus status,
                        Calendar proposalDeadline,
                        List <PaymentModel> payments,
                        boolean hasProposalState,
                        boolean isOneTime,
                        RefundPartition refundPartition,
                        VotationCriteria votationCriteria,
                        ArrayList<UserModel> users) {
        this.name = name;
        this.groupId = groupId;
        this.description = description;
        this.hasReceiptImg = hasReceiptImg;
        this.creatorId = creator;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.proposals = proposals;
        this.status = status;
        this.proposalDeadline = proposalDeadline;
        this.payments = payments;
        this.hasProposalState = hasProposalState;
        this.waitingProposal = waitingProposal;
        this.isOneTime = isOneTime;
        this.refundPartition = refundPartition;
        this.votationCriteria = votationCriteria;
        this.users = users;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public ArrayList<UserModel> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserModel> users) {
        this.users = users;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getHasReceiptImg() {
        return hasReceiptImg;
    }

    public void setHasReceiptImg(boolean hasReceiptImg) {
        this.hasReceiptImg = hasReceiptImg;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public UserModel getCreator() {
        return creator;
    }

    public void setCreator(UserModel creatorId) {
        this.creator = creatorId;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public Calendar getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(Calendar lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public ArrayList<ProposalModel> getProposals() {
        return proposals;
    }

    public void setProposals(ArrayList<ProposalModel> proposals) {
        this.proposals = proposals;
    }

    public ExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(ExpenseStatus status) {
        this.status = status;
    }

    public Calendar getProposalDeadline() {
        return proposalDeadline;
    }

    public void setProposalDeadline(Calendar proposalDeadline) {
        this.proposalDeadline = proposalDeadline;
    }

    public List<PaymentModel> getPayments() {
        return payments;
    }

    public void setPayments(List<PaymentModel> payments) {
        this.payments = payments;
    }

    public boolean getHasProposalState() {
        return hasProposalState;
    }

    public void setHasProposalState(boolean hasProposalState) {
        this.hasProposalState = hasProposalState;
    }

    public boolean isOneTime() {
        return isOneTime;
    }

    public void setOneTime(boolean oneTime) {
        isOneTime = oneTime;
    }

    public RefundPartition getRefundPartition() {
        return refundPartition;
    }

    public void setRefundPartition(RefundPartition refundPartition) {
        this.refundPartition = refundPartition;
    }

    public VotationCriteria getVotationCriteria() {
        return votationCriteria;
    }

    public void setVotationCriteria(VotationCriteria votationCriteria) {
        this.votationCriteria = votationCriteria;
    }

    public WaitingProposalModel getWaitingProposal() {
        return waitingProposal;
    }

    public void setWaitingProposal(WaitingProposalModel waitingProposal) {
        this.waitingProposal = waitingProposal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}






