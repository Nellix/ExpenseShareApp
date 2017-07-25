package it.mad8.expenseshare.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
//import it.mad8.expenseshare.utils.DataProvider;
import it.mad8.expenseshare.adapter.UsersAdapter;
import it.mad8.expenseshare.model.ExpenseModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.utils.CalendarUtils;
import it.mad8.expenseshare.model.datamapper.ExpenseDataMapper;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;
import it.mad8.expenseshare.model.datamapper.WaitingProposalDataMapper;

import static it.mad8.expenseshare.activity.NewGroupActivity.REQUEST_CAMERA;
import static it.mad8.expenseshare.activity.NewGroupActivity.SELECT_FILE;


public class NewExpenseFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String GROUP_ID = "GROUP_ID";
    private static final String USER_MODEL = "USER_MODEL";

    //private final String[] SPINNERLIST = {ExpenseModel.RefundPartition.PROPORTIONAL.toString(), ExpenseModel.RefundPartition.CUSTOM.toString()};
    //private final String[] SPINNERVOTATION = {ExpenseModel.VotationCriteria.MAJORITY_OF_VOTES.toString(), ExpenseModel.VotationCriteria.MAJORITY_OF_PARTICIPANT.toString(), ExpenseModel.VotationCriteria.UNANIMITY.toString()};

    private ExpenseModel expense;

    private TextInputLayout ti_name;
    private EditText et_name;
    private TextInputLayout ti_description;
    private EditText et_description;

    private CheckBox cb_proposal;

    private LinearLayout linearLayout;
    private ImageView iv_waitingImage;
    private TextInputLayout ti_waitingName;
    private EditText et_waitingName;
    private TextInputLayout ti_waitingPrice;
    private EditText et_waitingPrice;
    private TextInputLayout ti_waitingDescription;
    private EditText et_waitingDescription;

    //private MaterialBetterSpinner materialBetterSpinner;
    //private Button datePicker;
    //private Calendar dateSelected;
    private String groupId;
    private List<UserModel> users;
    private UserModel currentUserModel;

    private boolean hasChosenImage;

    public static NewExpenseFragment newInstance(String groupId, UserModel user) {
        NewExpenseFragment fragment = new NewExpenseFragment();
        Bundle args = new Bundle();
        args.putString(GROUP_ID, groupId);
        args.putSerializable(USER_MODEL, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(GROUP_ID);
            currentUserModel = (UserModel) getArguments().getSerializable(USER_MODEL);
        }
        if (currentUserModel == null) {
            currentUserModel = ((ExpenseShareApplication) getActivity().getApplication()).getUserModel();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_expense, container, false);
        users = new ArrayList<>();
        users = getListUsers();

        ti_name = (TextInputLayout) v.findViewById(R.id.ti_new_expense_name);
        et_name = ti_name.getEditText();
        ti_description = (TextInputLayout) v.findViewById(R.id.ti_new_expense_description);
        et_description = ti_description.getEditText();

        cb_proposal = (CheckBox) v.findViewById((R.id.cb_proposal_step));

        linearLayout = (LinearLayout) v.findViewById(R.id.ll_waiting);
        iv_waitingImage = (ImageView) v.findViewById(R.id.img_image_waiting);
        ti_waitingName = (TextInputLayout) v.findViewById(R.id.ti_waiting_name);
        et_waitingName = ti_waitingName.getEditText();
        ti_waitingDescription = (TextInputLayout) v.findViewById(R.id.ti_waiting_description);
        et_waitingDescription = ti_waitingDescription.getEditText();
        ti_waitingPrice = (TextInputLayout) v.findViewById(R.id.ti_waiting_price);
        et_waitingPrice = ti_waitingPrice.getEditText();

        //datePicker = (Button) v.findViewById(R.id.btn_datePicker);
        //materialBetterSpinner = (MaterialBetterSpinner) v.findViewById(R.id.spinner_expense);


        et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    if (((EditText) v).getText().length() == 0) {
                        ti_name.setError(getString(R.string.error_empty));
                    } else {
                        ti_name.setError("");
                    }
            }
        });
        et_waitingName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    if (((EditText) v).getText().length() == 0) {
                        ti_waitingName.setError(getString(R.string.error_empty));
                    } else {
                        ti_waitingName.setError("");
                    }
            }
        });
        et_waitingPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    if (((EditText) v).getText().length() == 0) {
                        ti_waitingPrice.setError(getString(R.string.error_empty));
                    } else {
                        ti_waitingPrice.setError("");
                    }
            }
        });


        iv_waitingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        iv_waitingImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                hasChosenImage = false;
                                iv_waitingImage.setImageResource(R.drawable.image_placeholder);
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


        //ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(),
        //        android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        //materialBetterSpinner.setAdapter(arrayAdapter);
        //materialBetterSpinner.setOnItemClickListener(this);
        //datePicker.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        setDateTimeField();
        //    }
        //});


        cb_proposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb_proposal.isChecked())
                    linearLayout.setVisibility(View.VISIBLE);
                else
                    linearLayout.setVisibility(View.GONE);
            }
        });

        Button btn_add_expense = (Button) v.findViewById(R.id.btn_add_expense);
        btn_add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!checkRequired())
                    return;
                String expName = et_name.getText().toString();
                String description = et_description.getText().toString();
                String creatorId = currentUserModel.getUid();

                ExpenseDataMapper newExpense = new ExpenseDataMapper();


                if (cb_proposal.isChecked()) {
                    /*if (dateSelected != null) {
                        String proposalDeadline = CalendarUtils.mapCalendarToISOString(dateSelected);
                        newExpense.setProposalDeadline(proposalDeadline);
                        newExpense.setHasProposalState(false);
                    }*/
                    newExpense.setHasProposalState(true);
                    newExpense.setStatus(ExpenseModel.ExpenseStatus.PROPOSAL);
                } else {

                    String paymentName = et_waitingName.getText().toString();
                    String paymentDescription = NewExpenseFragment.this.et_waitingDescription.getText().toString();
                    Float paymentPrice = Float.parseFloat(et_waitingPrice.getText().toString());

                    newExpense.setStatus(ExpenseModel.ExpenseStatus.WAITING);

                    WaitingProposalDataMapper waitingProposal = new WaitingProposalDataMapper();
                    waitingProposal.setName(paymentName);
                    waitingProposal.setDescription(paymentDescription);
                    waitingProposal.setCreatorId(creatorId);
                    waitingProposal.setCreator(new UserDataMapper(currentUserModel));
                    waitingProposal.setPrice(paymentPrice);
                    waitingProposal.setCreationDate(CalendarUtils.now());
                    waitingProposal.setHasImage(hasChosenImage);
                    newExpense.setHasProposalState(false);
                    newExpense.setWaitingProposal(waitingProposal);
                }


                newExpense.setName(expName);

                newExpense.setGroupId(groupId);

                newExpense.setDescription(description);
                newExpense.setCreatorId(creatorId);
                newExpense.setCreator(new UserDataMapper(currentUserModel));
                Map<String, UserDataMapper> mappedUsers = new HashMap<>();
                for (UserModel user : users) {
                    mappedUsers.put(user.getUid(), new UserDataMapper(user));
                }
                newExpense.setUsers(mappedUsers);

                newExpense.setCreationDate(CalendarUtils.now());
                newExpense.setLastModificationDate(CalendarUtils.now());

                newExpense.setOneTime(true);
                newExpense.setRefundPartition(ExpenseModel.RefundPartition.PROPORTIONAL);


                loadToDB(newExpense);


                //TODO Effettuare controlli più accurati sui valori acquisiti
/*
                if ((expName.isEmpty()) || (description.isEmpty()) || (price.isEmpty())) {
                    Snackbar.make(v, "Error! Please fill every field.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                //    if (cb_proposal.isChecked())
                //        initialStatus = ExpenseModel.ExpenseStatus.PROPOSAL;
                //    else
               //         initialStatus = ExpenseModel.ExpenseStatus.WAITING;
                    double priceValue = Double.valueOf(price);
                    //ExpenseModel e = new ExpenseModel(id, expName, description, false, initialStatus);
                    //DataProvider.getInstance().addExpense(e);
                    getActivity().finish();
                }
      */

                getActivity().finish();
            }
        });
        return v;
    }

    private boolean checkRequired() {
        if (et_name.getText().toString().trim().length() == 0) {
            return false;
        }

        if(!cb_proposal.isChecked()) {
            if (et_waitingName.getText().toString().trim().length() == 0) {
                return false;
            }

            if (et_waitingPrice.getText().toString().trim().length() == 0) {
                return false;
            }
        }

        return true;
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
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            hasChosenImage = true;
            if (requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                iv_waitingImage.setImageBitmap(bitmap);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImage = data.getData();
                iv_waitingImage.setImageURI(selectedImage);
                //BitmapDrawable bi = (BitmapDrawable) iv_waitingImage.getDrawable();
                //bitmap = bi.getBitmap();

            }

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selected = String.valueOf(parent.getItemAtPosition(position));

        if (selected.compareTo("CUSTOM") == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View view2 = getActivity().getLayoutInflater().inflate(R.layout.dialog_custom_refund, null);
            RecyclerView rv_users = (RecyclerView) view2.findViewById(R.id.rv_users);
            rv_users.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            rv_users.setLayoutManager(mLayoutManager);


            RecyclerView.Adapter adapter = new UsersAdapter(getContext(), users);
            rv_users.setAdapter(adapter);
            adapter.notifyDataSetChanged();


            builder.setView(view2);
            final AlertDialog alertDialog = builder.create();
            alertDialog.setTitle("Set partitions");
            alertDialog.show();


        }
    }

    private List<UserModel> getListUsers() {

        final List<UserModel> users = new ArrayList<>();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("groups").child(groupId);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> groupModel = (HashMap<String, Object>) dataSnapshot.getValue();
                HashMap<String, Boolean> list = (HashMap<String, Boolean>) groupModel.get("users");
                for (String s : list.keySet()) {

                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("users").child(s);
                    final String finalS = s;
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap<String, Object> user = (HashMap<String, Object>) dataSnapshot.getValue();
                            //
                            users.add(new UserModel((String) user.get("username"), (String) user.get("email"), finalS, (boolean) user.get("hasImage")));

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return users;
    }


    void loadToDB(ExpenseDataMapper ex) {
        DatabaseReference createRef = FirebaseDatabase.getInstance().getReference().child("expenses");
        String id = createRef.push().getKey();
        if (hasChosenImage) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReference(ExpenseDataMapper.WAITING_PROPOSAL_PATH.replace("{eid}", id));

            BitmapDrawable bitmapDrawable = ((BitmapDrawable) iv_waitingImage.getDrawable());
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] imageInByte = stream.toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
            imageRef.putStream(bis);
        }

        createRef.child(id).setValue(ex);
        Toast.makeText(getContext(), "New Expense saved", Toast.LENGTH_SHORT).show();
    }

    /*private void setDateTimeField() {
        Calendar newCalendar = Calendar.getInstance();
        dateSelected = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateSelected.set(year, monthOfYear, dayOfMonth, 0, 0);
                // datePicker.setText(CalendarUtils.fromCalendar(dateSelected));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }*/
}
