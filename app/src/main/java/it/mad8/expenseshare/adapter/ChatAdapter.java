package it.mad8.expenseshare.adapter;

import android.content.Context;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import it.mad8.expenseshare.R;
import it.mad8.expenseshare.model.MessageChatModel;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;

/**
 * Created by Aniello Malinconico on 08/05/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyHolderView> {

    private List<MessageChatModel> list_mess;
    private LayoutInflater inflater;
    private Context context;
    private TextView tv_date;
    private String group_id;

    public ChatAdapter(Context applicationContext, List<MessageChatModel> list_message, TextView tv,String id) {
        this.context = applicationContext;
        list_mess = list_message;
        this.inflater = LayoutInflater.from(applicationContext);
        this.tv_date = tv;
        this.group_id = id;

    }


    @Override
    public MyHolderView onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        ChatAdapter.MyHolderView vh;
        switch (viewType) {
            case 0:
                v = (View) inflater.inflate(R.layout.list_item_message_send, parent, false);
                vh = new ChatAdapter.MyHolderView(v);
                return vh;
            case 1:
                v = (View) inflater.inflate(R.layout.list_item_message_rcv, parent, false);
                vh = new ChatAdapter.MyHolderView(v);
                return vh;
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        MessageChatModel c = list_mess.get(position);

        if (c.getCreatorId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
            return 0;
        else
            return 1;

    }

    @Override
    public void onBindViewHolder(MyHolderView holder, int position) {
        MessageChatModel c = list_mess.get(position);
        holder.setData(c, position);
    }

    @Override
    public int getItemCount() {
        return list_mess.size();
    }

    public class MyHolderView extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView unameTv;
        private TextView messageTv;
        private TextView time;
        private CircularImageView circularImageView;
        int mPosition;


        public MyHolderView(View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.card_view_contact_chat);
            unameTv = (TextView) itemView.findViewById(R.id.tv_userneme_message);
            messageTv = (TextView) itemView.findViewById(R.id.tv_message_chat);
            time = (TextView) itemView.findViewById(R.id.tv_time);
            circularImageView = (CircularImageView) itemView.findViewById(R.id.image_view_selected);

        }

        public void setData(MessageChatModel c, int pos) {
            this.unameTv.setText(c.getCreator().getUsername());
            this.messageTv.setText(c.getMessage());

            SimpleDateFormat parseFormat = new SimpleDateFormat("E MMMM dd,yyyy");

            String s = parseFormat.format(c.getTimestamp().getTime());
            //String t = DateFormat.getTimeInstance(DateFormat.DAY_OF_WEEK_IN_MONTH_FIELD).format(calendar.getTime());
            tv_date.setText(s);



            this.time.setText(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(c.getTimestamp().getTime()));
            //    cardView.setCardElevation(10);



            if (c.getCreator().isHasImage())
                getImage(c);


            this.mPosition = pos;


        }


        private void getImage(final MessageChatModel c) {


            StorageReference mProfileImageRef = FirebaseStorage.getInstance().getReference(UserDataMapper.USER_IMAGE_PATH + c.getCreatorId());
            Glide.with(context)
                    .using(new FirebaseImageLoader())
                    .load(mProfileImageRef)
                    .into(circularImageView);


        }
    }
}

