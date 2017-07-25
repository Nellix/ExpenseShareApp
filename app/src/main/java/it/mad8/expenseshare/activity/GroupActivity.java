package it.mad8.expenseshare.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
import it.mad8.expenseshare.adapter.RefundPersonAdapter;
import it.mad8.expenseshare.adapter.UsersAdapter;
import it.mad8.expenseshare.fragment.ChatFragment;
import it.mad8.expenseshare.fragment.ExpensesFragment;
import it.mad8.expenseshare.model.GroupModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.GroupDataMapper;
import it.mad8.expenseshare.model.datamapper.PaymentDataMapper;
import it.mad8.expenseshare.model.datamapper.RefunderDataMapper;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;
import it.mad8.expenseshare.utils.CalendarUtils;


/**
 * Created by Rosario on 06/04/2017.
 */
public class GroupActivity extends AppCompatActivity implements ExpensesFragment.OnFragmentInteractionListener, ChatFragment.OnFragmentInteractionListener {

    public static final String USER_MODEL = "USER_MODEL";
    public static final String GROUP_NAME = "GROUP_NAME";
    public static final String GROUP_ID = "GROUP_ID";
    public static final String NOTIFICATION_ID = "CANCEL_NOTIFICATION_GROUPACTIVITY";
    public static final String FRAGMENT = "FRAGMENT";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * <p>
     * fragments for each of the sections. We use a
     * <p>
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * <p>
     * loaded fragment in memory. If this becomes too memory intensive, it
     * <p>
     * may be best to switch to a
     * <p>
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private GroupPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String groupID;
    private UserModel currentUserModel;

    private Toolbar toolbar;
    private List<UserModel> list;
    private RecyclerView rv;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        list = new ArrayList<>();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new GroupPagerAdapter(getSupportFragmentManager(), this);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String groupName = extras.getString(GROUP_NAME);
            groupID = extras.getString(GROUP_ID);
            currentUserModel = (UserModel) extras.getSerializable(USER_MODEL);
            setTitle(groupName);
        }

        String notificationId = getIntent().getStringExtra(NOTIFICATION_ID);
        String type = getIntent().getStringExtra(FRAGMENT);

        if (notificationId != null) {
            deleteNotification(notificationId);
        }


        if (currentUserModel == null) {
            ExpenseShareApplication app = (ExpenseShareApplication) getApplication();
            if (savedInstanceState == null) {
                if (app.getUserModel() != null) {
                    currentUserModel = app.getUserModel();
                }

            } else {
                currentUserModel = (UserModel) savedInstanceState.getSerializable(USER_MODEL);
                app.setUserModel(currentUserModel);
            }
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        toolbar = (Toolbar) findViewById(R.id.toolbar_group);
        setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.showuserslist);


        if (type != null) {
            if (type.compareTo("CHAT") == 0) {
                mViewPager.setCurrentItem(1);
            } else {
                mViewPager.setCurrentItem(0);
            }
        }


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(USER_MODEL, currentUserModel);
    }


    private void deleteNotification(String notificationID) {
        FirebaseDatabase.getInstance().getReference("users-notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(notificationID).removeValue();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.showuserslist, menu);

        MenuItem inviteUser = menu.findItem(R.id.action_show_users);
        inviteUser.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                showUsers();
                return false;
            }
        });


        return true;
    }

    private void showUsers() {

        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("groups").child(groupID);

        firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GroupDataMapper g = dataSnapshot.getValue(GroupDataMapper.class);
                HashMap<String, UserDataMapper> map = (HashMap<String, UserDataMapper>) g.getUsers();

                list.clear();
                for (String s : map.keySet()) {
                    UserDataMapper user = map.get(s);
                    UserModel u = user.toModel();
                    u.setUid(s);
                    list.add(u);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_showusers, null);

        builder.setView(view);
        final AlertDialog alertDialog1 = builder.create();
        alertDialog1.setTitle("List users");
        alertDialog1.show();

        Button btn_leave = (Button) view.findViewById(R.id.btn_leave);
        btn_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, UserDataMapper> map = new HashMap<>();
                for(int i = 0; i < list.size(); i++){
                    UserModel user = list.get(i);
                    if(!user.equals(currentUserModel)){
                        map.put(user.getUid(),new UserDataMapper(user));
                    }
                }
                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("groups").child(groupID).child("users");
                firebaseDatabase.setValue(map);
                alertDialog1.dismiss();
                finish();
            }
        });

        rv = (RecyclerView) view.findViewById(R.id.rv_dialog_group);
        mAdapter = new UsersAdapter(getApplicationContext(), list);
        rv.setAdapter(mAdapter);
        rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        //      if (id == R.id.action_settings) {
        //        return true;
        //  }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class GroupPagerAdapter extends FragmentPagerAdapter {

        Context mContext;

        GroupPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }


        @Override

        public Fragment getItem(int position) {

            String groupId = getIntent().getExtras().getString("GROUP_ID");
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0)
                return ExpensesFragment.newInstance(groupID, currentUserModel);
            else
                return ChatFragment.newInstance(groupID, currentUserModel);
        }


        @Override

        public int getCount() {
            // Show 2 total pages.
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return mContext.getString(R.string.title_fragment_expenses);
                case 1:
                    return mContext.getString(R.string.title_fragment_chat);
            }
            return null;

        }
    }
}

