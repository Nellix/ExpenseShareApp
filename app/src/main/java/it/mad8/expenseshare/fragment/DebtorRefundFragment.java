package it.mad8.expenseshare.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Locale;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
import it.mad8.expenseshare.model.ExpenseModel;
import it.mad8.expenseshare.model.PaymentModel;
import it.mad8.expenseshare.model.RefunderModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.ExpenseDataMapper;
import it.mad8.expenseshare.model.datamapper.RefunderDataMapper;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;


/**
 * Created by Rosario on 29/05/2017.
 */

public class DebtorRefundFragment extends Fragment {
    public static final String EXPENSE = "expense";
    public static final String USER_MODEL = "USER_MODEL";
    private TextView mItemName;
    private TextView mCreatorName;
    private TextView mCreationDate;
    private ImageView mReceiptImage;
    private TextView mPaymentDescription;
    private TextView mRefundedQuota;
    private CircularImageView mBuyerImage;
    private Button mButtonRefund;
    private Button mButtonRemind;

    private LinearLayout mLayoutToPay;
    private LinearLayout mLayoutPaid;

    private ExpenseModel mExpense;
    private PaymentModel mCurrentPayment;
    private RefunderModel mCurrentRefunder;
    private UserModel currentUserModel;


    public DebtorRefundFragment() {
    }

    public static DebtorRefundFragment newInstance(ExpenseModel e, UserModel currentUser) {
        DebtorRefundFragment fragment = new DebtorRefundFragment();
        Bundle args = new Bundle();

        args.putSerializable(EXPENSE, e);
        args.putSerializable(USER_MODEL, currentUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mExpense = (ExpenseModel) getArguments().getSerializable(EXPENSE);
            currentUserModel = (UserModel) getArguments().getSerializable(USER_MODEL);
        }
        if (currentUserModel == null)
            currentUserModel = ((ExpenseShareApplication) getActivity().getApplication()).getUserModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_refund_debtor, container, false);

        mItemName = (TextView) view.findViewById(R.id.txt_refund_item_name);
        mCreatorName = (TextView) view.findViewById(R.id.txt_creator_name);
        mCreationDate = (TextView) view.findViewById(R.id.txt_payment_creation_date);
        mReceiptImage = (ImageView) view.findViewById(R.id.receipt_image);
        mPaymentDescription = (TextView) view.findViewById(R.id.txt_payment_message);
        mRefundedQuota = (TextView) view.findViewById(R.id.txt_refunded_quota);
        mBuyerImage = (CircularImageView) view.findViewById(R.id.img_buyer);
        mButtonRefund = (Button) view.findViewById(R.id.btn_refund);
        mButtonRemind = (Button) view.findViewById(R.id.btn_remind);

        mLayoutToPay = (LinearLayout) view.findViewById(R.id.layout_to_pay_state);
        mLayoutPaid = (LinearLayout) view.findViewById(R.id.layout_paid_state);

        //// TODO: 27/05/2017  Adattare al caso di expense TimeLasting
        mCurrentPayment = mExpense.getPayments().get(0);
        for (RefunderModel refunder : mCurrentPayment.getRefunders()) {
            if (refunder.getUserId().equals(currentUserModel.getUid())) {
                mCurrentRefunder = refunder;
            }
        }

        mItemName.setText(mExpense.getWaitingProposal().getName());
        mCreatorName.setText(String.format(getString(R.string.added_by_no_time), mCurrentPayment.getCreator().getUsername()));
        mCreationDate.setText(String.format(Locale.getDefault(),getString(R.string.time), mCurrentPayment.getCreationDate()));

        if (mCurrentPayment.getHasReceiptImg()) {
            StorageReference mItemImageRef = FirebaseStorage.getInstance()
                    .getReference(ExpenseDataMapper.PAYMENT_IMG_PATH.replace("{eid}", mExpense.getId())
                            .replace("{pid}", mCurrentPayment.getId()));
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(mItemImageRef)
                    .into(mReceiptImage);
        }
        if(mExpense.getDescription().trim().length() > 0)
        mPaymentDescription.setText(mExpense.getDescription().trim());
        else
        mPaymentDescription.setText(getString(R.string.no_description));


        if (mCurrentRefunder.getStatus().equals(RefunderModel.RefundState.TO_PAY)) {

            mLayoutPaid.setVisibility(View.GONE);
            mRefundedQuota.setText(String.format(Locale.getDefault(), getString(R.string.debt), mCurrentRefunder.getRefundAmount(), mCurrentPayment.getCreator().getUsername()));
            if (mCurrentPayment.getCreator().isHasImage()) {
                StorageReference mBuyerImageRef = FirebaseStorage.getInstance()
                        .getReference(UserDataMapper.USER_IMAGE_PATH.replace("{uid}", currentUserModel.getUid()));
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(mBuyerImageRef)
                        .into(mReceiptImage);
            }

            mButtonRefund.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCurrentRefunder.setStatus(RefunderModel.RefundState.PAID);

                    final DatabaseReference refunderRef = FirebaseDatabase.getInstance().getReference(
                            ExpenseDataMapper.REFUNDER_PATH
                                    .replace("{eid}", mExpense.getId())
                                    .replace("{pid}", mCurrentPayment.getId())
                                    .replace("{uid}", mCurrentRefunder.getUserId())
                    );

                    refunderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            RefunderDataMapper mapper = new RefunderDataMapper(mCurrentRefunder);
                            refunderRef.setValue(mapper);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mLayoutToPay.setVisibility(View.GONE);
                    mLayoutPaid.setVisibility(View.VISIBLE);
                    mButtonRemind.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //// TODO: 29/05/2017 inviare notifica
                        }
                    });
                }
            });
        } else if (mCurrentRefunder.getStatus().equals(RefunderModel.RefundState.PAID)) {
            mLayoutToPay.setVisibility(View.GONE);
            mButtonRemind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //// TODO: 29/05/2017 inviare notifica
                }
            });
        }

        return view;
    }

}



