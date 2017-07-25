package it.mad8.expenseshare.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Aniello Malinconico on 26/05/2017.
 */

public class NotificationSwipeBroadcastReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String CANCEL_NOTIFICATION = "CANCEL_NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(CANCEL_NOTIFICATION)) {
            String notificationId = intent.getStringExtra(NOTIFICATION_ID);
            if (notificationId != null)
                FirebaseDatabase.getInstance().getReference("users-notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(notificationId).removeValue();
        }

    }
}
