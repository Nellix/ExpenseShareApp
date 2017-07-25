package it.mad8.expenseshare.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import it.mad8.expenseshare.R;
import it.mad8.expenseshare.activity.ExpenseActivity;
import it.mad8.expenseshare.activity.GroupActivity;
import it.mad8.expenseshare.model.NotificationModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.NotificationDataMapper;

/**
 * Created by Aniello Malinconico on 22/05/2017.
 */

public class NotificationService extends Service {

    public static final String KEY_NOTIFICATION_NEW_GROUP = "NOTIFICATION_NEW_GROUP";
    public static final String KEY_NOTIFICATION = "NOTIFICATION";
    public static final String KEY_NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";
    public static final String USER_MODEL = "USER_MODEL";
    public static final String KEY_NOTIFICATION_NEW_EXPENSE = "NOTIFICATION_NEW_EXPENSE";
    private DatabaseReference query;
    int uniqueID;
    private UserModel currentUserModel;


    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        currentUserModel = (UserModel) intent.getSerializableExtra(USER_MODEL);

        query = FirebaseDatabase.getInstance().getReference("users-notifications").child(currentUserModel.getUid());
        if (query != null)
            getNotifications();

        NotificationCompat.Builder notificationMessage = new android.support.v7.app.NotificationCompat.Builder(this)
                .setGroupSummary(true).setGroup(KEY_NOTIFICATION);

        notificationMessage.setAutoCancel(true);
        notificationMessage.setColor(ContextCompat.getColor(this, R.color.colorPrimary));
        notificationMessage.setSmallIcon(R.mipmap.expense_share_icon);
        notificationMessage.setTicker(getString(R.string.notification_welcome));
        notificationMessage.setContentTitle(getString(R.string.notification_welcome));
        notificationMessage.setWhen(System.currentTimeMillis());

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        uniqueID = 0;
        notificationManager.notify(uniqueID, notificationMessage.build());
        uniqueID++;

        return super.onStartCommand(intent, flags, startId);
    }

    private void getNotifications() {

        query.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    if (dataSnapshot.getValue() != null) {
                        NotificationDataMapper value = dataSnapshot.getValue(NotificationDataMapper.class);
                        NotificationModel notificationModel = value.toModel();
                        notificationModel.setId(dataSnapshot.getKey());
                        DispatchNotification(notificationModel);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void DispatchNotification(NotificationModel notificationModel) {

        JSONObject payload = notificationModel.getPayload();
        try {
            String notificationID = notificationModel.getId();
            String groupId = payload.get("groupId").toString();
            String groupName = payload.get("groupName").toString();
            String creatorName = payload.get("creatorName").toString();
            String expenseID, expenseName, message, paymentDescription;
            long price;
            switch (notificationModel.getType()) {
                case PAYMENT_REMINDER:
                    expenseID = payload.getString("expenseId");
                    expenseName = payload.getString("expenseName");
                    paymentDescription = payload.getString("paymentDescription");
                    price = payload.getLong("price");
                    sendNotification_PAYMENTREMINDER(notificationID, groupId, groupName, creatorName, expenseID, expenseName, paymentDescription, price);
                    break;
                case NEW_PAYMENT:
                    expenseID = payload.getString("expenseId");
                    expenseName = payload.getString("expenseName");
                    paymentDescription = payload.getString("paymentDescription");
                    price = payload.getLong("paymentPrice");
                    sendNotification_NEWPAYMENT(notificationID, groupId, groupName, creatorName, expenseID, expenseName, paymentDescription, price);
                    break;
                case NEW_MESSAGE:
                    message = payload.getString("message");
                    sendNotification_NEWMESSAGE(notificationID, groupId, groupName, creatorName, message);
                    break;
                case NEW_EXPENSE:
                    expenseID = payload.getString("expenseId");
                    expenseName = payload.getString("expenseName");
                    sendNotification_NEWEXPENSE(notificationID, groupId, groupName, creatorName, expenseID, expenseName);
                    break;
                case NEW_GROUP:
                    sendNotification_NEWGROUP(notificationID, groupId, groupName, creatorName);
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification_PAYMENTREMINDER(String notificationID, String groupId, String groupName, String creator, String expenseID, String expenseName, String paymentDescription, long price) {
        NotificationCompat.Builder notification = new android.support.v7.app.NotificationCompat.Builder(this)
                .setGroupSummary(false)
                .setGroup(KEY_NOTIFICATION);

        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this.getApplicationContext(), uniqueID, enableSwipe(notificationID), PendingIntent.FLAG_ONE_SHOT);
        notification.setDeleteIntent(pendingIntent1);

        Intent intent = new Intent(this, ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.USER_MODEL, currentUserModel);
        intent.putExtra(ExpenseActivity.EXPENSE_ID, expenseID);
        intent.putExtra(ExpenseActivity.FRAGMENT, "REFUND");
        intent.putExtra(ExpenseActivity.NOTIFICATION_ID, notificationID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        notification.setContentIntent(pendingIntent);

        notification.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.ic_payment_black_24dp);
        notification.setTicker(getString(R.string.notification_ticker_payment_reminder));
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle(String.format(getString(R.string.notification_title_payment_reminder), expenseName, groupName));
        notification.setContentText(String.format(Locale.getDefault(), getString(R.string.notification_text_payment_reminder), price, creator));


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueID, notification.build());
        uniqueID++;

    }

    private void sendNotification_NEWPAYMENT(String notificationID, String groupId, String groupName, String creator, String expenseID, String expenseName, String paymentDescription, long price) {
        NotificationCompat.Builder notification = new android.support.v7.app.NotificationCompat.Builder(this)
                .setGroupSummary(false)
                .setGroup(KEY_NOTIFICATION);

        PendingIntent deleteIntent = PendingIntent.getBroadcast(this.getApplicationContext(), uniqueID, enableSwipe(notificationID), PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setDeleteIntent(deleteIntent);

        Intent intent = new Intent(this, ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.USER_MODEL, currentUserModel);
        intent.putExtra(ExpenseActivity.EXPENSE_ID, expenseID);
        intent.putExtra(ExpenseActivity.FRAGMENT, "REFUND");
        intent.putExtra(ExpenseActivity.NOTIFICATION_ID, notificationID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        notification.setContentIntent(pendingIntent);

        notification.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.ic_monetization_on_black_24dp);
        notification.setTicker(getString(R.string.notification_ticker_new_payment));
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle(String.format(getString(R.string.notification_title_new_payment), groupName, creator));
        notification.setContentText(String.format(Locale.getDefault(), getString(R.string.notification_text_new_payment), paymentDescription, price));


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueID, notification.build());
        uniqueID++;

    }

    private void sendNotification_NEWMESSAGE(String notificationID, String groupId, String groupName, String creator, String message) {
        NotificationCompat.Builder notification = new android.support.v7.app.NotificationCompat.Builder(this)
                .setGroupSummary(false)
                .setGroup(KEY_NOTIFICATION);


        //sendBroadcast(swipe);

        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this.getApplicationContext(), uniqueID, enableSwipe(notificationID), PendingIntent.FLAG_ONE_SHOT);
        notification.setDeleteIntent(pendingIntent1);

        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra(GroupActivity.USER_MODEL, currentUserModel);
        intent.putExtra(GroupActivity.GROUP_NAME, groupName);
        intent.putExtra(GroupActivity.GROUP_ID, groupId);
        intent.putExtra(GroupActivity.NOTIFICATION_ID, notificationID);
        intent.putExtra(GroupActivity.FRAGMENT, "CHAT");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        notification.setContentIntent(pendingIntent);

        notification.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.ic_mail_white_24dp);
        notification.setTicker(getString(R.string.notification_ticker_new_message));
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle(String.format(getString(R.string.notification_title_new_message), groupName, creator));
        notification.setContentText(message);


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueID, notification.build());
        uniqueID++;

    }

    private Intent enableSwipe(String notificationID) {
        Intent swipe = new Intent(getApplicationContext(), NotificationSwipeBroadcastReceiver.class);
        swipe.putExtra(NotificationSwipeBroadcastReceiver.NOTIFICATION_ID, notificationID);
        swipe.setAction(NotificationSwipeBroadcastReceiver.CANCEL_NOTIFICATION);

        return swipe;
    }

    private void sendNotification_NEWEXPENSE(String notificationID, String groupId, String groupName, String creator, String expenseID, String expenseName) {
        NotificationCompat.Builder notification = new android.support.v7.app.NotificationCompat.Builder(this)
                .setGroupSummary(false)
                .setGroup(KEY_NOTIFICATION);

        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this.getApplicationContext(), uniqueID, enableSwipe(notificationID), PendingIntent.FLAG_ONE_SHOT);
        notification.setDeleteIntent(pendingIntent1);

        Intent intent = new Intent(this, ExpenseActivity.class);
        intent.putExtra(ExpenseActivity.USER_MODEL, currentUserModel);
        intent.putExtra(ExpenseActivity.EXPENSE_ID, expenseID);
        intent.putExtra(ExpenseActivity.FRAGMENT, "WAITING");
        intent.putExtra(ExpenseActivity.NOTIFICATION_ID, notificationID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        notification.setContentIntent(pendingIntent);
        notification.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.ic_card_giftcard_black_24dp);
        notification.setTicker(getString(R.string.notification_ticker_new_expense));
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle(String.format(getString(R.string.notification_title_new_expense), groupName));
        notification.setContentText(String.format(getString(R.string.notification_text_new_expense), expenseName, creator));


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueID, notification.build());
        uniqueID++;
    }

    private void sendNotification_NEWGROUP(String notificationID, String groupId, String groupName, String creator) {

        NotificationCompat.Builder notification = new android.support.v7.app.NotificationCompat.Builder(this)
                .setGroupSummary(false)
                .setGroup(KEY_NOTIFICATION);

        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this.getApplicationContext(), uniqueID, enableSwipe(notificationID), PendingIntent.FLAG_ONE_SHOT);
        notification.setDeleteIntent(pendingIntent1);

        Intent intent = new Intent(this, GroupActivity.class);
        intent.putExtra(GroupActivity.USER_MODEL, currentUserModel);
        intent.putExtra(GroupActivity.GROUP_NAME, groupName);
        intent.putExtra(GroupActivity.GROUP_ID, groupId);
        intent.putExtra(GroupActivity.NOTIFICATION_ID, notificationID);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        notification.setColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.ic_supervisor_account_black_24dp);
        notification.setTicker(getString(R.string.notification_ticker_new_group));
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle(String.format(getString(R.string.notification_title_new_group), groupName));
        notification.setContentText(String.format(getString(R.string.notification_text_new_group), creator));
        notification.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(uniqueID, notification.build());
        uniqueID++;

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
