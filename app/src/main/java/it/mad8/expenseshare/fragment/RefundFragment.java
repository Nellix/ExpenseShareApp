package it.mad8.expenseshare.fragment;

import android.app.Activity;
import android.app.Notification;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import it.mad8.expenseshare.R;
//import it.mad8.expenseshare.adapter.Refund_lv_adapter;
import it.mad8.expenseshare.adapter.RefundPersonAdapter;
import it.mad8.expenseshare.model.ExpenseModel;
import it.mad8.expenseshare.model.NotificationModel;
import it.mad8.expenseshare.model.RefunderModel;
import it.mad8.expenseshare.model.PaymentModel;
import it.mad8.expenseshare.model.UserGroupExpenseModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.NotificationDataMapper;
//import it.mad8.expenseshare.utils.CircularCounter;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class RefundFragment extends Fragment implements RefundPersonAdapter.RefundListener {

    public static final String EXPENSE = "expense";
    public static final String USER_MODEL = "USER_MODEL";
    RecyclerView mRefunder;
    PieChart mChart;
    String[] titles;
    String[] descriptions;

    private ExpenseModel expense;
    private UserModel currentUserModel;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private RefundPersonAdapter mAdapter;

    // TODO: Rename and change types of parameters
    public static RefundFragment newInstance(ExpenseModel e, UserModel currentUserModel) {
        RefundFragment fragment = new RefundFragment();
        Bundle args = new Bundle();

        args.putSerializable(EXPENSE, e);
        args.putSerializable(USER_MODEL, currentUserModel);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RefundFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            expense = (ExpenseModel) getArguments().getSerializable(EXPENSE);
            currentUserModel = (UserModel) getArguments().getSerializable(USER_MODEL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_refund, container, false);

        mRefunder = (RecyclerView) view.findViewById(R.id.refund_person_list);

        // TODO: 07/05/2017  Manage timeLasting Expenses case
        mAdapter = new RefundPersonAdapter(expense.getId(), expense.getPayments().get(0), this);
        mRefunder.setAdapter(mAdapter);
        mRefunder.setLayoutManager(new LinearLayoutManager(getContext()));

        mChart = (PieChart) view.findViewById(R.id.refund_chart);
        mChart.setUsePercentValues(false);
        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);
        mChart.setTransparentCircleRadius(25f);
        mChart.setHoleRadius(23f);
        mChart.setDrawCenterText(false);
        mChart.setRotationEnabled(false);
        mChart.setRotationAngle(0);
        mChart.setHighlightPerTapEnabled(false);
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(12f);
        updateData();

        return view;
    }

    private void updateData() {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();


        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.

        PaymentModel mPayment = expense.getPayments().get(0);


        entries.add(new PieEntry((mPayment.getPrice() - mPayment.getRefundedQuota()), getResources().getString(R.string.refunded)));
        entries.add(new PieEntry(mPayment.getRefundedQuota(), getResources().getString(R.string.to_refund)));

        PieDataSet dataSet = new PieDataSet(entries, "Refund State");

        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        //dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(getResources().getColor(R.color.positive_balance));
        colors.add(getResources().getColor(R.color.negative_balance));
        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new LargeValueFormatter("€"));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        //data.setValueTypeface(mTfLight);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    @Override
    public void onConfirmRefund(RefunderModel refunder) {
        //    RefunderModel ref = expense.getRefunders().get(expense.getRefunders().indexOf(refunder));
        //    expense.getSelectedExpense().setRefund(expense.getSelectedExpense().getRefund() + refunder.getRefundAmount());
        //    ref.setStatus(RefunderModel.RefundState.REFUNDED);
        //mAdapter.swap(expense.getRefunders());
        mAdapter.notifyDataSetChanged();
        updateData();
    }

    @Override
    public void onRemindRefund(final RefunderModel refunder) {
        Toast.makeText(getContext(), "Remember", Toast.LENGTH_SHORT).show();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(expense.getGroupId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
                String groupname = map.get("name").toString();

                NotificationDataMapper notificationDataMapper = createNotification(groupname);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users-notifications").child(refunder.getUserId());
                databaseReference.push().setValue(notificationDataMapper);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private NotificationDataMapper createNotification(String groupName) {
        NotificationDataMapper notificationDataMapper = new NotificationDataMapper();
        notificationDataMapper.setPayload(createJsonPayloadNot(groupName));
        notificationDataMapper.setType(NotificationModel.NotificationType.PAYMENT_REMINDER.name());
        return notificationDataMapper;
    }

    private String createJsonPayloadNot(String groupName) {

        JSONObject jsonObject = new JSONObject();


        try {
            jsonObject.put("groupId", expense.getGroupId());
            jsonObject.put("groupName", groupName);
            jsonObject.put("expenseId", expense.getId());
            jsonObject.put("expenseName", expense.getName());
            jsonObject.put("paymentDescription", expense.getPayments().get(0).getDescription());
            jsonObject.put("price", (long) (expense.getWaitingProposal().getPrice() / expense.getUsers().size()));
            jsonObject.put("creatorName", expense.getPayments().get(0).getCreator().getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return jsonObject.toString();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
