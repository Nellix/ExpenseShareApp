package it.mad8.expenseshare.activity;

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
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
import it.mad8.expenseshare.model.ProposalModel;
import it.mad8.expenseshare.model.UserModel;


import static it.mad8.expenseshare.activity.NewGroupActivity.REQUEST_CAMERA;
import static it.mad8.expenseshare.activity.NewGroupActivity.SELECT_FILE;

/**
 * Created by Rosario on 14/05/2017.
 */

public class NewProposalActivity extends AppCompatActivity {

    public static final String MEMBERS_LIST = "MEMBERS_LIST";
    public static final String PROPOSAL = "PROPOSAL";
    public static final String PROPOSAL_IMAGE = "PROPOSAL_IMAGE";

    private ArrayList<UserModel> membersList;

    private Button btn_addProposal;
    private TextInputLayout ti_propName;
    private TextInputLayout ti_propDescription;
    private TextInputLayout ti_propPrice;
    private EditText et_propName;
    private EditText et_propDescription;
    private EditText et_propPrice;
    private ImageView iv_propImage;

    private boolean hasImage = false;
    private Context mContext = this;
    private UserModel currentUserModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_proposal);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                membersList = (ArrayList<UserModel>) extras.getSerializable(MEMBERS_LIST);
            }
        }

        currentUserModel = ((ExpenseShareApplication) getApplication()).getUserModel();


        btn_addProposal = (Button) findViewById(R.id.btn_add_proposal);
        ti_propName = (TextInputLayout) findViewById(R.id.ti_new_prop_name);
        et_propName = ti_propName.getEditText(); //= (EditText) findViewById(R.id.et_new_prop_name);
        et_propName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    if (((EditText) v).getText().length() == 0) {
                        ti_propName.setError(getString(R.string.error_empty));
                    } else {
                        ti_propName.setError("");
                    }
            }
        });

        ti_propDescription = (TextInputLayout) findViewById(R.id.ti_new_prop_description);
        et_propDescription = ti_propDescription.getEditText();//(EditText) findViewById(R.id.et_new_prop_description);

        ti_propPrice = (TextInputLayout) findViewById(R.id.ti_new_prop_price);
        et_propPrice = ti_propPrice.getEditText();//(EditText) findViewById(R.id.et_new_prop_proposal);
        et_propPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    if (((EditText) v).getText().length() == 0) {
                        ti_propPrice.setError(getString(R.string.error_empty));
                    } else {
                        ti_propPrice.setError("");
                    }
            }
        });

        iv_propImage = (ImageView) findViewById(R.id.img_new_proposal_fragment);
        iv_propImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        iv_propImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                hasImage = false;
                                iv_propImage.setImageResource(R.drawable.image_placeholder);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                        dialog.dismiss();
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(R.string.remove_image_question).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();
                return true;
            }
        });


        btn_addProposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProposalModel newProposal = new ProposalModel();

                if (et_propName.getText().toString().trim().length() > 0) {
                    newProposal.setName(et_propName.getText().toString().trim());
                } else {
                    ti_propName.setError(getString(R.string.error_empty));
                    return;
                }

                if (et_propPrice.getText().toString().trim().length() > 0) {
                    newProposal.setPrice(Float.parseFloat(et_propPrice.getText().toString().trim()));
                } else {
                    ti_propPrice.setError(getString(R.string.error_empty));
                    return;
                }

                if (et_propDescription.getText().toString().trim().length() > 0) {
                    newProposal.setDescription(et_propDescription.getText().toString().trim());
                } else {
                    newProposal.setDescription("");
                }

                if (hasImage) {
                    //Add image to storage
                    newProposal.setHasImage(true);
                } else {
                    newProposal.setHasImage(false);
                }

                if (membersList != null) {
                    HashMap<String, Integer> newProposalUsers = new HashMap<>();
                    for (UserModel user : membersList) {
                        newProposalUsers.put(user.getUid(), 0);
                    }
                    newProposal.setUsers(newProposalUsers);
                }

                newProposal.setCreationDate(GregorianCalendar.getInstance());
                newProposal.setCreator(currentUserModel);

                Bundle resultBundle = new Bundle();
                resultBundle.putSerializable(PROPOSAL, newProposal);
                if (hasImage) {
                    BitmapDrawable bitmapDrawable = ((BitmapDrawable) iv_propImage.getDrawable());
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    resultBundle.putParcelable(PROPOSAL_IMAGE, bitmap);
                }
                Intent i = new Intent();
                i.putExtras(resultBundle);
                setResult(Activity.RESULT_OK, i);

                finish();
            }
        });

    }

    void selectImage() {
        final CharSequence[] items = {getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            hasImage = true;
            if (requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                Bitmap bitmap = (Bitmap) bundle.get("data");
                iv_propImage.setImageBitmap(bitmap);
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImage = data.getData();
                iv_propImage.setImageURI(selectedImage);
            }

        }
    }


}





