package it.mad8.expenseshare.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.fragment.NewExpenseFragment;
import it.mad8.expenseshare.model.UserModel;

public class NewExpenseActivity extends SingleFragmentActivity {

    public static String GROUP_ID = "GROUP_ID";
    public static String USER_MODEL = "USER_MODEL";
    private UserModel currentUserModel;
    private String groupId;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getIntent().getExtras() != null) {
            groupId = getIntent().getStringExtra(GROUP_ID);
            currentUserModel = (UserModel) getIntent().getSerializableExtra(USER_MODEL);
        }
        ExpenseShareApplication app = (ExpenseShareApplication) getApplication();
        if (savedInstanceState == null) {
            if (app.getUserModel() != null) {
                currentUserModel = app.getUserModel();
            }

        } else {
            currentUserModel = (UserModel) savedInstanceState.getSerializable(USER_MODEL);
            app.setUserModel(currentUserModel);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected Fragment createFragment() {
        return NewExpenseFragment.newInstance(groupId, currentUserModel);
    }
}
