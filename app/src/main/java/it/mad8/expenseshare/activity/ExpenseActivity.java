package it.mad8.expenseshare.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.logging.Logger;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
import it.mad8.expenseshare.fragment.DebtorRefundFragment;
import it.mad8.expenseshare.fragment.PlaceholderFragment;
import it.mad8.expenseshare.fragment.ProposalFragment;
import it.mad8.expenseshare.fragment.RefundFragment;
import it.mad8.expenseshare.fragment.WaitingFragment;
import it.mad8.expenseshare.model.ExpenseModel;
import it.mad8.expenseshare.model.RefunderModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.ExpenseDataMapper;


public class ExpenseActivity extends AppCompatActivity implements WaitingFragment.OnFragmentInteractionListener, ProposalFragment.OnFragmentInteractionListener {

    public static final String USER_MODEL = "USER_MODEL";
    public static final String EXPENSE_ID = "EXPENSE_ID";
    public static final String NOTIFICATION_ID = "CANCEL_NOTIFICATION_EXPENSEACTIVITY";
    public static final String FRAGMENT = "FRAGMENT";
    public static final String EXPENSE_ACTIVITY = "EXPENSE ACTIVITY";
    private ExpenseModel mExpense;
    private ExpenseDataMapper mMapper;
    private Toolbar toolbar;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String mExpenseId;
    private UserModel currentUserModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        Intent intent = this.getIntent();
        Bundle extra = intent.getBundleExtra("Bundle");
        if (extra != null) {
            mExpenseId = extra.getString(EXPENSE_ID);
            currentUserModel = (UserModel) extra.getSerializable(USER_MODEL);
        } else {
            mExpenseId = intent.getStringExtra(EXPENSE_ID);
        }

        String notificationID = intent.getStringExtra(NOTIFICATION_ID);
        final String fragmentType = intent.getStringExtra(FRAGMENT);

        if (notificationID != null) {
            deleteNotification(notificationID);
        }

        final DatabaseReference expenseDbRef = FirebaseDatabase.getInstance().getReference()
                .child("expenses")
                .child(mExpenseId);
        expenseDbRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<ExpenseDataMapper> e = new GenericTypeIndicator<ExpenseDataMapper>() {
                        };

                        mMapper = dataSnapshot.getValue(e);
                        mExpense = mMapper.toModel();
                        mExpense.setId(mExpenseId);
                        toolbar.setTitle(mExpense.getName());
                        mSectionsPagerAdapter.notifyDataSetChanged();
                        mViewPager.setAdapter(mSectionsPagerAdapter);

                        if (fragmentType != null) {
                            if (fragmentType.compareTo("WAITING") == 0)
                                if (mExpense.getHasProposalState())
                                    mViewPager.setCurrentItem(1);
                                else
                                    mViewPager.setCurrentItem(0);
                            else if (fragmentType.compareTo("REFUND") == 0)
                                if (mExpense.getHasProposalState())
                                    mViewPager.setCurrentItem(2);
                                else
                                    mViewPager.setCurrentItem(1);
                            else
                                mViewPager.setCurrentItem(0);
                        } else {
                            if (mExpense.getStatus() == ExpenseModel.ExpenseStatus.WAITING)
                                if (mExpense.getHasProposalState())
                                    mViewPager.setCurrentItem(1);
                                else
                                    mViewPager.setCurrentItem(0);
                            else if (mExpense.getStatus() == ExpenseModel.ExpenseStatus.REFUND)
                                if (mExpense.getHasProposalState())
                                    mViewPager.setCurrentItem(2);
                                else
                                    mViewPager.setCurrentItem(1);
                            else
                                mViewPager.setCurrentItem(0);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container_main_exp);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs_main_exp);
        tabLayout.setupWithViewPager(mViewPager);

    }


    private void deleteNotification(String notificationID) {
        FirebaseDatabase.getInstance().getReference("users-notifications")
                .child(currentUserModel.getUid())
                .child(notificationID)
                .removeValue();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //  getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //   if (id == R.id.action_settings) {
        //     return true;
        //}

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFragmentInteraction(ExpenseModel expense) {
        mSectionsPagerAdapter.setUpdateOnce();
        //this.mExpense = expense;
        mSectionsPagerAdapter.notifyDataSetChanged();
        if (mExpense.getStatus() == ExpenseModel.ExpenseStatus.WAITING)
            if (mExpense.getHasProposalState())
                mViewPager.setCurrentItem(1);
            else
                mViewPager.setCurrentItem(0);
        else if (mExpense.getStatus() == ExpenseModel.ExpenseStatus.REFUND)
            if (mExpense.getHasProposalState())
                mViewPager.setCurrentItem(2);
            else
                mViewPager.setCurrentItem(1);
        else
            mViewPager.setCurrentItem(0);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        boolean updateOnce = false;

        boolean upToDate = false;
        int previous = 0;

        public void setUpdateOnce() {
            updateOnce = true;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //    FragmentManager fm = getSupportFragmentManager();
            // fm.beginTransaction();
            //   android.app.FragmentManager fm = getFragmentManager();
            //  fm.putFragment(null,"",ProposalFragment.newInstance());

            //ADD fragment manager

            switch (position) {
                case 0:
                    if (mExpense.getHasProposalState()) {
                        return ProposalFragment.newInstance(mExpense, currentUserModel);
                    } else {
                        return WaitingFragment.newInstance(mExpense, currentUserModel);
                    }
                case 1:
                    if (mExpense.getHasProposalState()) {
                        return WaitingFragment.newInstance(mExpense, currentUserModel);
                    } else {
                        RefunderModel.RefundState status;
                        ExpenseShareApplication app = ((ExpenseShareApplication) getApplication());
                        UserModel currentUserModel = app.getUserModel();
                        //// TODO: 27/05/2017 Adattare al caso di Expense TimeLasting
                        for (RefunderModel refunder : mExpense.getPayments().get(0).getRefunders()) {
                            if (refunder.getUser().equals(currentUserModel)) {
                                status = refunder.getStatus();
                                if (status.equals(RefunderModel.RefundState.BUYER))
                                    return RefundFragment.newInstance(mExpense, currentUserModel);
                                else if (status.equals(RefunderModel.RefundState.TO_PAY) || status.equals(RefunderModel.RefundState.PAID))
                                    return DebtorRefundFragment.newInstance(mExpense, currentUserModel);
                            }
                        }
                    }
                case 2:
                    ExpenseShareApplication app = ((ExpenseShareApplication) getApplication());
                    UserModel currentUserModel = app.getUserModel();
                    //// TODO: 27/05/2017 Adattare al caso di Expense TimeLasting
                    /*for (RefunderModel refunder : mExpense.getPayments().get(0).getRefunders()) {
                        if (refunder.getUser().equals(currentUserModel)) {
                            RefunderModel.RefundState status = refunder.getStatus();
                            if (status.equals(RefunderModel.RefundState.BUYER))
                                return RefundFragment.newInstance(mExpense, currentUserModel);
                            else if (status.equals(RefunderModel.RefundState.TO_PAY) || status.equals(RefunderModel.RefundState.PAID))
                                return DebtorRefundFragment.newInstance(mExpense, currentUserModel);
                        }
                    }*/
                    if (mExpense.getPayments().get(0).getCreator().equals(currentUserModel)) {
                        return RefundFragment.newInstance(mExpense, currentUserModel);
                    } else {
                        return DebtorRefundFragment.newInstance(mExpense, currentUserModel);
                    }
                default:
                    return PlaceholderFragment.newInstance(position + 1);
            }

        }

        @Override
        public void notifyDataSetChanged() {
            upToDate = true;
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {

            if (updateOnce) {
                updateOnce = false;
                notifyDataSetChanged();
            }

            int count = 0;

            if (mExpense.getStatus() == ExpenseModel.ExpenseStatus.PROPOSAL) {
                count = 1;
            } else if (mExpense.getStatus() == ExpenseModel.ExpenseStatus.WAITING) {
                if (mExpense.getHasProposalState()) {
                    count = 2;
                } else
                    count = 1;
            } else if (mExpense.getStatus() == ExpenseModel.ExpenseStatus.REFUND) {
                if (mExpense.getHasProposalState()) {
                    count = 3;
                } else
                    count = 2;
            }

            /*if (previous != count ) {
                upToDate = false;
                notifyDataSetChanged();
                return previous;
            }
            if(upToDate) {
                previous = count;
                return count;
            }
            previous = count;*/
            //Log.d(EXPENSE_ACTIVITY, "Fragment count:" + count);
            return count;


        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    if (mExpense.getHasProposalState())
                        return getString(R.string.proposal_label);
                    else
                        return getString(R.string.waiting_label);
                case 1:
                    if (mExpense.getHasProposalState())
                        return getString(R.string.waiting_label);
                    else
                        return getString(R.string.refund_label);
                case 2:
                    return getString(R.string.refund_label);
                default:
                    return null;
            }
        }

        public ExpenseModel getExpense() {
            return mExpense;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }
}
