package it.mad8.expenseshare.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
import it.mad8.expenseshare.activity.ExpenseActivity;
import it.mad8.expenseshare.model.ExpenseModel;
import it.mad8.expenseshare.model.PaymentModel;
import it.mad8.expenseshare.model.RefunderModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.WaitingProposalModel;
import it.mad8.expenseshare.utils.CalendarUtils;
import it.mad8.expenseshare.model.datamapper.ExpenseDataMapper;
import it.mad8.expenseshare.model.datamapper.PaymentDataMapper;
import it.mad8.expenseshare.model.datamapper.RefunderDataMapper;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;

import static it.mad8.expenseshare.model.ExpenseModel.ExpenseStatus.REFUND;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WaitingFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WaitingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WaitingFragment extends Fragment {

    public static final String EXPENSE = "expense";
    public static final String USER_MODEL = "USER_MODEL";
    Button mBtnPay;

    TextView mItemName;
    TextView mItemCreator;
    TextView mItemCreation;
    TextView mItemPrice;
    TextView mItemDescription;
    ImageView mItemImage;
    Button btn_payment;
    EditText et_paymentDescription;

    Boolean receiptImgSet = false;


    ExpenseModel mExpense;
    private UserModel currentUserModel;
    private WaitingProposalModel mWaitingProposal;


    private OnFragmentInteractionListener mListener;
    private Button btn_paymentImage;
    private ImageView iv_paymentImage;
    private int REQUEST_CAMERA;
    private int SELECT_FILE;
    private Bitmap bitmap_group;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WaitingFragment.
     */
    public static WaitingFragment newInstance(ExpenseModel e, UserModel currentUserModel) {
        WaitingFragment fragment = new WaitingFragment();
        Bundle args = new Bundle();

        args.putSerializable(EXPENSE, e);
        args.putSerializable(USER_MODEL, currentUserModel);
        fragment.setArguments(args);
        return fragment;
    }

    public WaitingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mExpense = (ExpenseModel) getArguments().getSerializable(EXPENSE);
            currentUserModel = (UserModel) getArguments().getSerializable(USER_MODEL);
        }

        currentUserModel = ((ExpenseShareApplication) getActivity().getApplication()).getUserModel();

        REQUEST_CAMERA = 1;
        SELECT_FILE = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_waiting, container, false);


        mItemName = (TextView) view.findViewById(R.id.txt_item_name);
        mItemCreator = (TextView) view.findViewById(R.id.txt_item_creator);
        mItemCreation = (TextView) view.findViewById(R.id.txt_item_creation);
        mItemDescription = (TextView) view.findViewById(R.id.txt_item_description);
        mItemPrice = (TextView) view.findViewById(R.id.txt_item_price);
        mItemImage = (ImageView) view.findViewById(R.id.img_item_image);
        mWaitingProposal = mExpense.getWaitingProposal();

        mItemName.setText(mExpense.getName());
        mItemCreator.setText(String.format(getString(R.string.added_by_no_time), mWaitingProposal.getCreator().getUsername()));
        mItemCreation.setText(String.format(Locale.getDefault(), getString(R.string.time), mExpense.getCreationDate()));
        if (mWaitingProposal.isHasImage()) {
            StorageReference mItemImageRef = FirebaseStorage.getInstance()
                    .getReference("/expenses/" + mExpense.getId() + "/waiting");
            Glide.with(this)
                    .using(new FirebaseImageLoader())
                    .load(mItemImageRef)
                    .into(mItemImage);
        }
        ;
        mItemPrice.setText(String.format(Locale.getDefault(), "%1$,.2f €", mWaitingProposal.getPrice()));

        if (mWaitingProposal.getDescription().length() > 0)
            mItemDescription.setText(mWaitingProposal.getDescription());
        else
            mItemDescription.setText(R.string.no_description);

        mBtnPay = (Button) view.findViewById(R.id.btn_item_pay);

        if (mExpense.getPayments().size() != 0) {
            mBtnPay.setClickable(false);
            mBtnPay.setVisibility(View.INVISIBLE);
        } else {
            mBtnPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: aggiungenre activity
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View view = getLayoutInflater(null).inflate(R.layout.dialog_payment, null);

                    builder.setView(view);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.setTitle(getString(R.string.dialog_pay));
                    alertDialog.show();

                    TextInputLayout ti_description = (TextInputLayout) view.findViewById(R.id.ti_payment_description);
                    et_paymentDescription = ti_description.getEditText();

                    btn_payment = (Button) view.findViewById(R.id.btn_payment_add);
                    btn_paymentImage = (Button) view.findViewById(R.id.btn_payment_image);
                    iv_paymentImage = (ImageView) view.findViewById(R.id.iv_payment_image);

                    iv_paymentImage.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            //Yes button clicked
                                            receiptImgSet = false;
                                            iv_paymentImage.setImageResource(R.drawable.image_placeholder);
                                            iv_paymentImage.setVisibility(View.GONE);
                                            btn_paymentImage.setVisibility(View.VISIBLE);
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                    dialog.dismiss();
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setMessage(R.string.remove_image_question).setPositiveButton(R.string.yes, dialogClickListener)
                                    .setNegativeButton(R.string.no, dialogClickListener).show();
                            return true;
                        }
                    });

                    btn_paymentImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectImage();
                        }
                    });

                    btn_payment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //String deadline, String description, Boolean hasImage, Boolean hasReceiptImg, Float price, String creator, String creationDate, Boolean isCompletetlyRefunded, HashMap<String, RefunderDataMapper> refunders
                            HashMap<String, RefunderDataMapper> map = getRefundersMap();
                            ArrayList<RefunderModel> list = new ArrayList<>();
                            for (String userId : map.keySet()) {
                                RefunderModel refunder = map.get(userId).toModel();
                                refunder.setUserId(userId);
                                list.add(refunder);
                            }

                            Calendar deadline = Calendar.getInstance();
                            deadline.add(Calendar.DAY_OF_YEAR, 7);

                            PaymentModel newPayment = new PaymentModel(deadline, et_paymentDescription.getText().toString(), false, mWaitingProposal.getPrice(), Calendar.getInstance(), false, list, false, currentUserModel);
                            payOnDB(newPayment);

                            alertDialog.dismiss();
                            getActivity().onBackPressed();
                            //mBtnPay.setClickable(false);
                            //mBtnPay.setVisibility(View.INVISIBLE);
                            //updateActivty();
                        }
                    });


                }
            });

        }
        return view;
    }

    private HashMap<String, RefunderDataMapper> getRefundersMap() {
        HashMap<String, RefunderDataMapper> map = new HashMap<>();
        for (UserModel u : mExpense.getUsers()) {
            RefunderDataMapper refunderDataMapper;
            UserDataMapper user = new UserDataMapper(u.getUsername(), u.getEmail(), u.isHasImage());

            if (!u.equals(currentUserModel)) {
                if (mExpense.getRefundPartition().equals(ExpenseModel.RefundPartition.PROPORTIONAL)) {
                    refunderDataMapper = new RefunderDataMapper(mWaitingProposal.getPrice() / mExpense.getUsers().size(), RefunderModel.RefundState.TO_PAY, user);
                    map.put(u.getUid(), refunderDataMapper);
                } else {
                    //TODO gestire il caso in cui la politica è "CUSTOM"
                }
            }
        }
        return map;
    }

    private void payOnDB(final PaymentModel newPayment) {

        final DatabaseReference expenseReference = FirebaseDatabase.getInstance().getReference("expenses").child(mExpense.getId());

        DatabaseReference paymentsReference = expenseReference.child("payments");
        String newPaymentId = paymentsReference.push().getKey();

        newPayment.setId(newPaymentId);

        mExpense.getPayments().add(newPayment);

        if (receiptImgSet) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference(ExpenseDataMapper.PAYMENT_IMG_PATH
                    .replace("{eid}", mExpense.getId())
                    .replace("{pid}", newPaymentId));
            BitmapDrawable bitmapDrawable = ((BitmapDrawable) iv_paymentImage.getDrawable());
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
            imageRef.putStream(bis);
        }


        mExpense.setStatus(REFUND);
        expenseReference.setValue(new ExpenseDataMapper(mExpense));

    }


    void selectImage() {
        final CharSequence[] items = {getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.choose_image);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals(getString(R.string.camera))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[which].equals(getString(R.string.gallery))) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, getString(R.string.select_file)), SELECT_FILE);
                } else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                bitmap_group = (Bitmap) bundle.get("data");
                iv_paymentImage.setImageBitmap(bitmap_group);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImage = data.getData();
                iv_paymentImage.setImageURI(selectedImage);
            }
            btn_paymentImage.setVisibility(View.GONE);
            iv_paymentImage.setVisibility(View.VISIBLE);
            receiptImgSet = true;
        } else {
            iv_paymentImage.setVisibility(View.GONE);
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void updateActivty() {
        if (mListener != null) {
            mListener.onFragmentInteraction(mExpense);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ExpenseActivity a;

        if (context instanceof ExpenseActivity) {
            a = (ExpenseActivity) context;
            mListener = a;
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(ExpenseModel expense);
    }

}
