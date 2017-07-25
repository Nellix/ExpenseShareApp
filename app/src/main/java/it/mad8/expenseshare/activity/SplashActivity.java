package it.mad8.expenseshare.activity;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.utils.NotificationService;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */


public class SplashActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "SplashActivty";
    //firebase auth
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private View mContentView;
    private ProgressDialog mDialog;
    private boolean firstLogin;
    private NotificationCompat.Builder notificationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        mContentView = findViewById(R.id.fullscreen_content);

        mAuth = FirebaseAuth.getInstance();

        firstLogin = false;

        // Set up the user interaction to manually show or hide the system UI.
       /* mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAuth.getCurrentUser() == null) {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.drawable.ic_googleg_color_24dp)
                                    .setTheme(R.style.AuthTheme)
                                    .build(),
                            RC_SIGN_IN);
                } else {
                    goToNextActivity();
                }
            }
        });*/

        mDialog = new ProgressDialog(this);
        mDialog.setMessage(getString(R.string.please_wait));
        mDialog.setCancelable(false);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    firstLogin = false;
                    goToNextActivity();
                } else {
                    startActivityForResult(
                            AuthUI.getInstance().createSignInIntentBuilder()
                                    .setTheme(R.style.AuthTheme)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mDialog.show();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == ResultCodes.OK) {
                firstLogin = true;
                goToNextActivity();

            }
        }
    }

    private void goToNextActivity() {
        final FirebaseUser fbUser = mAuth.getCurrentUser();

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(UserDataMapper.USERS);

        if (firstLogin) {
            UserDataMapper user = new UserDataMapper();
            user.setEmail(fbUser.getEmail());
            Uri uri = fbUser.getPhotoUrl();
            user.setUsername(fbUser.getDisplayName());
            //user.setHasImage();
            databaseReference.child(fbUser.getUid()).setValue(user);
            UserModel model = user.toModel();
            model.setUid(fbUser.getUid());
            ((ExpenseShareApplication) getApplication()).setUserModel(model);
        }

        DatabaseReference currentUserRef = databaseReference.child(fbUser.getUid());
        final Context mContext = this;
        currentUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel user;
                if (firstLogin) {
                    user = ((ExpenseShareApplication) getApplication()).getUserModel();
                } else {
                    GenericTypeIndicator<UserDataMapper> e = new GenericTypeIndicator<UserDataMapper>() {
                    };

                    UserDataMapper userMapper = dataSnapshot.getValue(e);
                    user = userMapper.toModel();
                    user.setUid(dataSnapshot.getKey());
                    ((ExpenseShareApplication) getApplication()).setUserModel(user);
                }

                Intent intent = new Intent(getApplicationContext(), NotificationService.class);
                intent.putExtra(NotificationService.USER_MODEL, user);
                startService(intent);

                Intent launchGroup = new Intent(mContext, MainActivity.class);
                launchGroup.putExtra(MainActivity.USER_MODEL, user);

                mDialog.dismiss();
                startActivity(launchGroup);
                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
