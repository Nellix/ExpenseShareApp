package it.mad8.expenseshare.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
import it.mad8.expenseshare.fragment.BalanceFragment;
import it.mad8.expenseshare.fragment.GroupsFragment;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.utils.NotificationService;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GroupsFragment.OnFragmentInteractionListener, BalanceFragment.OnFragmentInteractionListener {
    public static final String FIRST_LOGIN = "FIRST_LOGIN";
    public static final String USER_MODEL = "USER_MODEL";

    public static final int REQUEST_CAMERA = 1, SELECT_FILE = 0;
    public static final String CANCEL_NOTIFICATION = "CANCEL_NOTIFICATION";

    private TextView mProfileName;
    private TextView mProfileEmail;
    private View mNavHeaderVIew;

    private CircularImageView mProfileImage;
    private StorageReference mProfileImageRef;

    private boolean imageSelected;
    private Bitmap bitmap_user;


    private FirebaseUser currentUser;
    private UserModel currentUserModel;


    public static final int Request_Code = 100;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(USER_MODEL, currentUserModel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Toolbar toolbar = new Toolbar(this);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.groups_title));
        ExpenseShareApplication app = (ExpenseShareApplication) getApplication();
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String notificationID = extras.getString(CANCEL_NOTIFICATION);
                if(notificationID!=null)
                {
                    deleteNotification(notificationID);
                    notificationID = null;
                }
                if (app.getUserModel() != null) {
                    currentUserModel = app.getUserModel();
                } else {
                    currentUserModel = (UserModel) extras.getSerializable(USER_MODEL);
                    app.setUserModel(currentUserModel);
                }
            } else {
                currentUserModel = app.getUserModel();
            }
        } else {
            currentUserModel = (UserModel) savedInstanceState.getSerializable(USER_MODEL);
            app.setUserModel(currentUserModel);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            Fragment fragment = GroupsFragment.newInstance(currentUserModel);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_layout, fragment).commit();

        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mNavHeaderVIew = navigationView.getHeaderView(0);

        mProfileName = (TextView) mNavHeaderVIew.findViewById(R.id.profile_name);
        mProfileEmail = (TextView) mNavHeaderVIew.findViewById(R.id.profile_email);
        mProfileImage = (CircularImageView) mNavHeaderVIew.findViewById(R.id.profile_img);
        mProfileImage.setImageResource(R.drawable.ic_person_white_24dp);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });


        if (currentUser != null) {
            mProfileName.setText(
                    TextUtils.isEmpty(currentUser.getDisplayName()) ? "No display name" : currentUser.getDisplayName()
            );
            mProfileEmail.setText(
                    TextUtils.isEmpty(currentUser.getEmail()) ? "No email" : currentUser.getEmail()
            );
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(UserDataMapper.USERS).child(currentUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

            //tempo di richiesta 3-4 sec quindi si dovrebbe far partire caricamento...

            @Override
            public void onDataChange(DataSnapshot snapshot) {
                UserDataMapper u = snapshot.getValue(UserDataMapper.class);
             if (u.isHasImage()) {
                    mProfileImageRef = FirebaseStorage.getInstance().getReference(UserDataMapper.USER_IMAGE_PATH + currentUser.getUid());
                    Glide.with(MainActivity.this)
                            .using(new FirebaseImageLoader())
                            .load(mProfileImageRef)
                            .into(mProfileImage);
                }

            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: ", firebaseError.getMessage());
            }
        });


    }

    private void deleteNotification(String notificationID) {
        FirebaseDatabase.getInstance().getReference("users-notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(notificationID).removeValue();

    }

    void selectImage() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Choose Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);

                } else if (items[which].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            Intent intent = new Intent(getApplicationContext(),NotificationService.class);
                            stopService(intent);
                            startActivity(new Intent(MainActivity.this, SplashActivity.class));
                            finish();
                        }
                    });
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_groups) {
            Fragment fragment = GroupsFragment.newInstance(currentUserModel);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_layout, fragment)
                    .commit();

        } else if (id == R.id.nav_balance) {
            Fragment fragment = BalanceFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.main_layout, fragment)
                    .commit();

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_info) {

        } else if (id == R.id.nav_invite) {
            OnInviteClicked();
        } else if (id == R.id.nav_logout) {
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            // user is now signed out
                            startActivity(new Intent(MainActivity.this, SplashActivity.class));
                            finish();
                        }
                    });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Logger log = Logger.getLogger("Main Activty");
        log.log(Level.WARNING, "OnFragmentInteraction", uri);
    }


    private void OnInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder("Join friends ")
                .setMessage("Hey there , this is a nice app ...")
                .setDeepLink(Uri.parse("https://google.it"))
                .setCallToActionText("Invitation CTA")
                .build();
        startActivityForResult(intent, Request_Code);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                imageSelected = true;
                Bundle bundle = data.getExtras();
                bitmap_user = (Bitmap) bundle.get("data");
                mProfileImage.setImageBitmap(bitmap_user);
            } else if (requestCode == SELECT_FILE) {
                imageSelected = true;
                Uri selectedImage = data.getData();
                mProfileImage.setImageURI(selectedImage);
                BitmapDrawable bi = (BitmapDrawable) mProfileImage.getDrawable();
                bitmap_user = bi.getBitmap();

            }

        }

        if (imageSelected) {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            StorageReference groupImageRef = FirebaseStorage.getInstance().getReference(UserDataMapper.USER_IMAGE_PATH).child(firebaseUser.getUid());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap_user.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] dataBitmap = baos.toByteArray();

            UploadTask uploadTask = groupImageRef.putBytes(dataBitmap);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(getApplicationContext(), R.string.image_add_unsuccess, Toast.LENGTH_SHORT).show();
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL
                    UserModel user = new UserModel(currentUser.getDisplayName(), currentUser.getEmail());
                    user.setHasImage(true);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(UserDataMapper.USERS);
                    databaseReference.child(currentUser.getUid()).setValue(new UserDataMapper(user));
                    mProfileImage.setImageBitmap(bitmap_user);
                    Toast.makeText(getApplicationContext(), R.string.image_add_success, Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
