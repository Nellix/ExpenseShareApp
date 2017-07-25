package it.mad8.expenseshare.model.datamapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.mad8.expenseshare.model.ExpenseModel;
import it.mad8.expenseshare.model.ProposalModel;
import it.mad8.expenseshare.model.PaymentModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.utils.CalendarUtils;

/**
 * Created by Rosario on 06/05/2017.
 */

public class ExpenseDataMapper implements DataMapper<ExpenseModel> {

    public static final String WAITING_PROPOSAL_PATH = "expenses/{eid}/waiting";
    public static final String PROPOSAL_PATH = "expenses/{eid}/proposals/{pid}";
    public static final String REFUNDER_PATH = "expenses/{eid}/payments/{pid}/refunders/{uid}";
    public static final String PAYMENT_IMG_PATH = "expenses/{eid}/payments/{pid}";

    private String name;
    private String groupId;
    private String description;
    private String creatorId;
    private UserDataMapper creator;
    private String creationDate;
    private String lastModificationDate;
    private Map<String, UserDataMapper> users;
    private Map<String, ProposalDataMapper> proposals;
    private Map<String, PaymentDataMapper> payments;
    private ExpenseModel.ExpenseStatus status;
    private String proposalDeadline;
    private Boolean hasProposalState;
    private Boolean isOneTime;
    private ExpenseModel.RefundPartition refundPartition;
    private ExpenseModel.VotationCriteria votationCriteria;
    private WaitingProposalDataMapper waitingProposal;

    public ExpenseDataMapper() {

    }

    public ExpenseDataMapper(ExpenseModel model) {
        this.name = model.getName();
        this.groupId = model.getGroupId();
        this.description = model.getDescription();
        this.creatorId = model.getCreatorId();
        this.creator = new UserDataMapper(model.getCreator());
        this.creationDate = CalendarUtils.mapCalendarToISOString(model.getCreationDate());
        this.lastModificationDate = CalendarUtils.mapCalendarToISOString(model.getLastModificationDate());
        this.hasProposalState = model.getHasProposalState();
        this.proposalDeadline = CalendarUtils.mapCalendarToISOString(model.getProposalDeadline());
        this.status = model.getStatus();
        this.isOneTime = model.isOneTime();
        this.refundPartition = model.getRefundPartition();
        this.votationCriteria = model.getVotationCriteria();
        this.waitingProposal = new WaitingProposalDataMapper(model.getWaitingProposal());

        this.users = new HashMap<>();
        for (UserModel user : model.getUsers()) {
            this.users.put(user.getUid(), new UserDataMapper(user));
        }
        this.proposals = new HashMap<>();
        for (ProposalModel proposal : model.getProposals()) {
            this.proposals.put(proposal.getId(), new ProposalDataMapper(proposal));
        }
        this.payments = new HashMap<>();
        for (PaymentModel payment : model.getPayments()) {
            this.payments.put(payment.getId(), new PaymentDataMapper(payment));
        }
    }

    public ExpenseDataMapper(String name,
                             String groupId,
                             String description,
                             String creatorId,
                             UserDataMapper creator,
                             String creationDate,
                             String lastModificationDate,
                             Map<String, UserDataMapper> users,
                             Map<String, ProposalDataMapper> proposals,
                             WaitingProposalDataMapper waitingProposal,
                             ExpenseModel.ExpenseStatus status,
                             String proposalDeadline,
                             Map<String, PaymentDataMapper> payments,
                             Boolean hasProposalState,
                             Boolean isOneTime,
                             ExpenseModel.RefundPartition refundPartition,
                             ExpenseModel.VotationCriteria votationCriteria) {
        this.name = name;
        this.groupId = groupId;
        this.description = description;
        this.creatorId = creatorId;
        this.creator = creator;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.hasProposalState = hasProposalState;
        this.proposalDeadline = proposalDeadline;
        this.status = status;
        this.isOneTime = isOneTime;
        this.refundPartition = refundPartition;
        this.votationCriteria = votationCriteria;
        this.waitingProposal = waitingProposal;

        this.users = users;
        this.proposals = proposals;
        this.payments = payments;


    }

    @Override
    public ExpenseModel toModel() {
        ExpenseModel expense = new ExpenseModel();

        expense.setName(this.name);
        expense.setGroupId(this.groupId);
        expense.setDescription(this.description);
        expense.setCreatorId(this.creatorId);
        UserModel creator = this.creator.toModel();
        creator.setUid(this.creatorId);
        expense.setCreator(creator);
        expense.setCreationDate(CalendarUtils.mapISOStringToCalendar(this.creationDate));
        expense.setLastModificationDate(CalendarUtils.mapISOStringToCalendar(this.lastModificationDate));
        expense.setHasProposalState(this.hasProposalState);
        if (this.proposalDeadline != null) {
            expense.setProposalDeadline(CalendarUtils.mapISOStringToCalendar(this.proposalDeadline));
        }
        expense.setStatus(this.status);
        expense.setOneTime(this.isOneTime);
        expense.setRefundPartition(this.refundPartition);
        expense.setVotationCriteria(this.votationCriteria);

        if (this.waitingProposal != null) {
            expense.setWaitingProposal(this.waitingProposal.toModel());
        }


        convertUsers(expense.getUsers());
        convertProposals(expense.getProposals());
        convertPayments(expense.getPayments());

        return expense;
    }


    private void convertUsers(List<UserModel> users) {
        if (this.users != null) {
            for (String id : this.users.keySet()) {
                UserModel user = this.users.get(id).toModel();
                user.setUid(id);
                users.add(user);
            }
        }

    }

    private void convertProposals(List<ProposalModel> proposals) {
        if (this.proposals != null) {
            for (String pid : this.proposals.keySet()) {
                ProposalModel prop = this.proposals.get(pid).toModel();
                prop.setId(pid);
                proposals.add(prop);
            }
        }
    }

    private void convertPayments(List<PaymentModel> payments) {
        if (this.payments != null) {
            for (String paymentId : this.payments.keySet()) {
                PaymentModel newPayment = this.payments.get(paymentId).toModel();
                newPayment.setId(paymentId);
                payments.add(newPayment);
            }

        }
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

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(String lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public Map<String, ProposalDataMapper> getProposals() {
        return proposals;
    }

    public ExpenseModel.ExpenseStatus getStatus() {
        return status;
    }

    public void setStatus(ExpenseModel.ExpenseStatus status) {
        this.status = status;
    }

    public String getProposalDeadline() {
        return proposalDeadline;
    }

    public void setProposalDeadline(String proposalDeadline) {
        this.proposalDeadline = proposalDeadline;
    }

    public void setProposals(Map<String, ProposalDataMapper> proposals) {
        this.proposals = proposals;
    }

    public Map<String, PaymentDataMapper> getPayments() {
        return payments;
    }

    public void setPayments(Map<String, PaymentDataMapper> payments) {
        this.payments = payments;
    }

    public Boolean getHasProposalState() {
        return hasProposalState;
    }

    public void setHasProposalState(Boolean hasProposalState) {
        this.hasProposalState = hasProposalState;
    }

    public Boolean getOneTime() {
        return isOneTime;
    }

    public void setOneTime(Boolean oneTime) {
        isOneTime = oneTime;
    }

    public ExpenseModel.RefundPartition getRefundPartition() {
        return refundPartition;
    }

    public void setRefundPartition(ExpenseModel.RefundPartition refundPartition) {
        this.refundPartition = refundPartition;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Map<String, UserDataMapper> getUsers() {
        return users;
    }

    public void setUsers(Map<String, UserDataMapper> users) {
        this.users = users;
    }

    public ExpenseModel.VotationCriteria getVotationCriteria() {
        return votationCriteria;
    }

    public void setVotationCriteria(ExpenseModel.VotationCriteria votationCriteria) {
        this.votationCriteria = votationCriteria;
    }

    public WaitingProposalDataMapper getWaitingProposal() {
        return waitingProposal;
    }

    public void setWaitingProposal(WaitingProposalDataMapper waitingProposal) {
        this.waitingProposal = waitingProposal;
    }

    public UserDataMapper getCreator() {
        return creator;
    }

    public void setCreator(UserDataMapper creator) {
        this.creator = creator;
    }
}


