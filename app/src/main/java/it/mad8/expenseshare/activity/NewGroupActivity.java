package it.mad8.expenseshare.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import it.mad8.expenseshare.R;
import it.mad8.expenseshare.adapter.EndlessRecyclerViewScrollListener;
import it.mad8.expenseshare.adapter.ContactsAdapter;
import it.mad8.expenseshare.adapter.SelectedContactsAdapter;
import it.mad8.expenseshare.model.ContactModel;
import it.mad8.expenseshare.model.GroupModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.GroupDataMapper;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;


public class NewGroupActivity extends AppCompatActivity implements SelectedContactsAdapter.OnItemRemovedListener, ContactsAdapter.OnItemSelectedListener, ContactsAdapter.OnItemDeselectedListener {
    private static final String TAG = "NewGroupActivity";
    public static final int Request_Code = 100;
    public static final int PAGE_SIZE = 10;
    public static final String EMAIL = "email";
    public static final String USER_MODEL = "USER_MODEL";
    public static final int REQUEST_CAMERA = 1, SELECT_FILE = 0;

    private List<ContactModel> contacts;
    private List<UserModel> selectedContacts;
    private ProgressDialog loading_spinner;
    private Button btn_addGroupDialog;
    private EditText et_groupName;
    private TextInputLayout ti_groupName;
    private CircularImageView civ_groupImage;
    private Bitmap bitmap_group;

    private ContactsAdapter contactsAdapter;
    private SelectedContactsAdapter selectedContactsAdapter;

    private MaterialSearchView searchView;
    private UserModel currentUserModel;
    private TextView tv_users;
    private TextView tv_noSelected;

    private boolean imageSelected;

    private boolean isQuerySearch;
    private String q;
    private int currentPage;
    private int searchPage;

    final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(UserDataMapper.USERS);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                currentUserModel = (UserModel) extras.getSerializable(USER_MODEL);
            }
        }

        isQuerySearch = false;
        currentPage = 0;
        searchPage = 0;
        contacts = new CopyOnWriteArrayList<>();
        selectedContacts = new CopyOnWriteArrayList<>();

        setContentView(R.layout.activity_new_group);

        tv_noSelected = (TextView) findViewById(R.id.no_selected);
        RecyclerView rvContacts = (RecyclerView) findViewById(R.id.my_recycler_view);
        RecyclerView rvSelectedContacts = (RecyclerView) findViewById(R.id.selectedContacts);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        FloatingActionButton btn_addGroup = (FloatingActionButton) findViewById(R.id.fab);
        loading_spinner = new ProgressDialog(this);

        //loading_spinner.setVisibility(View.GONE);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rvContacts.setHasFixedSize(true);
        LinearLayoutManager contactsLayoutManager = new LinearLayoutManager(this);
        contactsAdapter = new ContactsAdapter(contacts);
        contactsAdapter.setDeselectedListener(this);
        contactsAdapter.setSelectedListener(this);
        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(contactsLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                loadMoreData();
            }
        };
        rvContacts.setLayoutManager(contactsLayoutManager);
        rvContacts.addOnScrollListener(scrollListener);
        rvContacts.setAdapter(contactsAdapter);

        rvSelectedContacts.setHasFixedSize(true);
        LinearLayoutManager selectedContactsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        selectedContactsAdapter = new SelectedContactsAdapter(selectedContacts);
        selectedContactsAdapter.setItemRemovedListener(this);
        rvSelectedContacts.setLayoutManager(selectedContactsLayoutManager);
        rvSelectedContacts.setAdapter(selectedContactsAdapter);

        loadData();

        imageSelected = false;
        searchView.setVisibility(View.GONE);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                if (text.trim().equals(q)) {
                    return true;
                }
                q = text.trim();
                if (q.length() > 0) {

                    clearContacts();

                    isQuerySearch = true;
                    searchPage = 0;
                    loadData();
                    return true;
                } else {
                    clearContacts();
                    isQuerySearch = false;
                    currentPage = 0;
                    loadData();
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                searchView.setVisibility(View.VISIBLE);
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                clearContacts();
                isQuerySearch = false;
                currentPage = 0;
                loadData();
            }
        });

        btn_addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewGroupActivity.this);
                View view = getLayoutInflater().inflate(R.layout.dialog_custom, null);

                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                alertDialog.setTitle(getString(R.string.new_group_dialog_title));
                alertDialog.show();

                btn_addGroupDialog = (Button) view.findViewById(R.id.btn_addGroup_dialog);
                //et_groupMessage = (EditText) view.findViewById(R.id.et_message);
                ti_groupName = (TextInputLayout) view.findViewById(R.id.ti_group_name);
                et_groupName = ti_groupName.getEditText();
                civ_groupImage = (CircularImageView) view.findViewById(R.id.img_group_dialog);
                RecyclerView rv_customDialog = (RecyclerView) view.findViewById(R.id.rv_customdialog);
                tv_users = (TextView) view.findViewById(R.id.tv_users_selected);


                et_groupName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus)
                            if (((EditText) v).getText().length() == 0) {
                                ti_groupName.setError(getString(R.string.error_empty));
                            } else {
                                ti_groupName.setError("");
                            }
                    }
                });

                rv_customDialog.setHasFixedSize(true);
                rv_customDialog.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                rv_customDialog.setAdapter(new SelectedContactsAdapter(selectedContacts, false));

                tv_users.setText(String.format(Locale.getDefault(), getString(R.string.users_joining_number), selectedContacts.size()));

                civ_groupImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectImage();
                    }
                });

                btn_addGroupDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (et_groupName.getText().toString().trim().length() == 0) {
                            return;
                        }
                        List<UserModel> users = new ArrayList<>(selectedContacts);
                        addGroup(users);
                        alertDialog.dismiss();
                        onBackPressed();

                    }
                });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                imageSelected = true;
                Bundle bundle = data.getExtras();
                bitmap_group = (Bitmap) bundle.get("data");
                civ_groupImage.setImageBitmap(bitmap_group);
            } else if (requestCode == SELECT_FILE) {
                imageSelected = true;
                Uri selectedImage = data.getData();
                civ_groupImage.setImageURI(selectedImage);
                BitmapDrawable bi = (BitmapDrawable) civ_groupImage.getDrawable();
                bitmap_group = bi.getBitmap();

            }

        }


        if (requestCode == Request_Code) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                /*for (String id : ids) {
                    //  Log.d(TAG, "onActivityResult: sent invitation " + id);
                    // Toast.makeText(getApplicationContext(),"Inviato a "+id,Toast.LENGTH_SHORT).show();
                    // System.out.println("MainActivity.onActivityResult "+ id);
                }*/
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }

    public void addGroup(List<UserModel> sel_contacts) {

        final DatabaseReference databaseReference_GroupsChild = FirebaseDatabase.getInstance().getReference(GroupDataMapper.GROUPS);
        String groupName = et_groupName.getText().toString().trim();

        Map<UserModel, Boolean> users = new HashMap<>();

        for (UserModel user : sel_contacts)
            users.put(user, false);

        //prepare to save a group
        final GroupModel model = new GroupModel(groupName, users, imageSelected, false, false, currentUserModel);
        final GroupDataMapper gm = new GroupDataMapper(model);
        final String group_id = databaseReference_GroupsChild.push().getKey();


        //save a image
        if (imageSelected) {
            //first upload image then create a groupe
            StorageReference groupImageRef = FirebaseStorage.getInstance().getReference(GroupDataMapper.GROUP_IMAGE_PATH).child(group_id);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap_group.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = groupImageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), R.string.group_add_unsuccess, Toast.LENGTH_SHORT).show();
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL

                    databaseReference_GroupsChild.child(group_id).setValue(gm);
                    Toast.makeText(getApplicationContext(), R.string.group_add_success, Toast.LENGTH_SHORT).show();

                }
            });

        } else {
            databaseReference_GroupsChild.child(group_id).setValue(gm);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        MenuItem inviteUser = menu.findItem(R.id.action_invite);
        inviteUser.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                onInviteClicked();
                return false;
            }
        });


        searchView.setMenuItem(item);


        return true;
    }


    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder("Join friends ")
                .setMessage("Hey there , this is a nice app ...")
                .setDeepLink(Uri.parse("https://google.it"))
                .setCallToActionText("Invitation CTA")
                .build();
        startActivityForResult(intent, Request_Code);
    }


    public void setLoading(boolean loading) {
        if (loading)
            loading_spinner.show();
        else
            loading_spinner.dismiss();
    }

    private void loadData() {
        final int page = this.isQuerySearch ? this.searchPage : this.currentPage;
        setLoading(true);
        Log.d(TAG, "Query: " + (isQuerySearch ? "Search" : "Normal"));
        Log.d(TAG, "Query page: " + page);
        Query currentQuery = ref.orderByChild(EMAIL);
        if (isQuerySearch)
            currentQuery = currentQuery.startAt(q).endAt(q + "\uf8ff");
        currentQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Toast.makeText(NewGroupActivity.this, "No more questions", Toast.LENGTH_SHORT).show();
                    if (isQuerySearch) {
                        searchPage--;
                    } else {
                        currentPage--;
                    }
                }
                int index = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (index < page * PAGE_SIZE || index >= (page + 1) * PAGE_SIZE) {
                        index++;
                        continue;
                    }
                    index++;
                    UserDataMapper mapped = postSnapshot.getValue(UserDataMapper.class);
                    UserModel user = mapped.toModel();
                    user.setUid(postSnapshot.getKey());
                    if (user.equals(currentUserModel))
                        continue;
                    contacts.add(new ContactModel(user, selectedContacts.contains(user)));

                    Log.d(TAG, "Added User: " + user.getUsername());
                    if (user.isHasImage())
                        Log.d(TAG, "With Image: " + user.getUid());
                    contactsAdapter.notifyItemInserted(contacts.size() - 1);
                }
                setLoading(false);
                //contactsAdapter.notifyItemRangeInserted(contactsBefore - 1, contacts.size() - contactsBefore);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadMoreData() {
        if (isQuerySearch)
            searchPage++;
        else
            currentPage++;
        loadData();
    }

    public void clearContacts() {
        if (contacts.size() > 0) {
            contacts.clear();
            contactsAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onItemRemove(UserModel removedUser, int position) {
        int pos = contacts.indexOf(new ContactModel(removedUser, false));
        ContactModel contact = contacts.get(pos);
        contact.setSelected(false);
        contactsAdapter.notifyItemChanged(pos);
        if (selectedContacts.size() == 0) {
            tv_noSelected.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemSelected(UserModel user, int position) {
        if(selectedContacts.size() == 0){
            tv_noSelected.setVisibility(View.GONE);
        }
        selectedContacts.add(user);
        selectedContactsAdapter.notifyItemInserted(selectedContacts.size() - 1);
    }

    @Override
    public void onItemDeselected(UserModel user, int position) {
        int pos = selectedContacts.indexOf(user);
        selectedContacts.remove(pos);
        selectedContactsAdapter.notifyItemRemoved(pos);
        if(selectedContacts.size() == 0){
            tv_noSelected.setVisibility(View.VISIBLE);
        }
    }
}
