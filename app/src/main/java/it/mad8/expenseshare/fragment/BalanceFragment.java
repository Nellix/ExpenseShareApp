package it.mad8.expenseshare.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
import it.mad8.expenseshare.adapter.BalanceAdapter;
import it.mad8.expenseshare.model.BalanceModel;
import it.mad8.expenseshare.model.ExpenseModel;
import it.mad8.expenseshare.model.PaymentModel;
import it.mad8.expenseshare.model.RefunderModel;
import it.mad8.expenseshare.model.UserExpenseModel;
import it.mad8.expenseshare.model.UserExpensePaymentModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.ExpenseDataMapper;
import it.mad8.expenseshare.model.datamapper.UserExpenseDataMapper;
import it.mad8.expenseshare.utils.CalendarUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BalanceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BalanceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BalanceFragment extends Fragment {
    private BarChart mChart;
    private TextView mCurrentMonth;
    private TextView mIncomingAmount;
    private TextView mOutgoingAmount;
    private RecyclerView mBalanceDetailsList;
    private UserModel mCurrentUser;

    private DatabaseReference mDbRef;

    private BalanceAdapter mAdapter;
    private List<BalanceModel> mItemList;

    private float totalIncomingAmount = 0;
    private float totalOutgoingAmount = 0;

    private OnFragmentInteractionListener mListener;

    public BalanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BalanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BalanceFragment newInstance() {
        BalanceFragment fragment = new BalanceFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mCurrentMonth = (TextView) rootView.findViewById(R.id.tv_current_month);
        mIncomingAmount = (TextView) rootView.findViewById(R.id.tv_incoming_amount);
        mOutgoingAmount = (TextView) rootView.findViewById(R.id.tv_outgoing_amount);
        mBalanceDetailsList = (RecyclerView) rootView.findViewById(R.id.past_expenses_list);

        mItemList = new ArrayList<BalanceModel>();

        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        String m = new DateFormatSymbols().getMonths()[currentMonth];

        mCurrentMonth.setText(m);

        mCurrentUser = ((ExpenseShareApplication) getActivity().getApplication()).getUserModel();

        mDbRef = FirebaseDatabase.getInstance().getReference().child("users-expenses")
                .child(mCurrentUser.getUid());
        mDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot exp : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<UserExpenseDataMapper> e = new GenericTypeIndicator<UserExpenseDataMapper>() {
                    };

                    UserExpenseDataMapper ense = exp.getValue(e);


                    UserExpenseModel expense = exp.getValue(e).toModel();


                    if (expense.getStatus().equals(ExpenseModel.ExpenseStatus.REFUND)) {
                        UserExpensePaymentModel payment = expense.getPayments().get(0);
                        List<RefunderModel> refunders = payment.getRefunders();

                        if (payment.getCreatorId().equals(mCurrentUser.getUid())) {
                            //Current user is the "buyer" for this payment
                            totalOutgoingAmount += -payment.getPrice();
                            mItemList.add(new BalanceModel(expense.getName(), payment.getCreationDate(), -payment.getPrice()));
                            for (RefunderModel refunder : refunders) {


                                //totalIncomingAmount += payment.getAmount();

                                if (refunder.getStatus().equals(RefunderModel.RefundState.PAID)) {
                                    mItemList.add(new BalanceModel(expense.getName(), payment.getCreationDate(), refunder.getRefundAmount()));
                                    totalIncomingAmount += refunder.getRefundAmount();
                                }
                            }
                        } else {
                            //Current user is one of the "refunders" for this payment
                            for (RefunderModel refunder : refunders) {
                                if (refunder.getUserId().equals(mCurrentUser.getUid())) {
                                    if (refunder.getStatus().equals(RefunderModel.RefundState.PAID)) {
                                        mItemList.add(new BalanceModel(expense.getName(), payment.getCreationDate(), refunder.getRefundAmount()));
                                        totalOutgoingAmount += refunder.getRefundAmount();
                                    }
                                }
                            }
                        }
                    }
                }
                mIncomingAmount.setText(String.format(Locale.getDefault(), "%1$,.2f €", totalIncomingAmount));
                mOutgoingAmount.setText(String.format(Locale.getDefault(), "%1$,.2f €", totalOutgoingAmount));


                mBalanceDetailsList.setAdapter(new BalanceAdapter(mItemList));
                mBalanceDetailsList.setLayoutManager(new LinearLayoutManager(getContext()));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
