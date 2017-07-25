package it.mad8.expenseshare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.shawnlin.numberpicker.NumberPicker;

import java.util.List;

import it.mad8.expenseshare.R;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;

/**
 * Created by Aniello Malinconico on 07/05/2017.
 */

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
    private List<UserModel> contacts;
    private LayoutInflater inflater;
    private Context context;

    public UsersAdapter(Context c, List<UserModel> d) {
        this.contacts = d;
        this.context = c;
        this.inflater = LayoutInflater.from(c);
    }


    @Override
    public UsersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = (View) inflater.inflate(R.layout.item_user, parent, false);
        UsersAdapter.MyViewHolder vh = new UsersAdapter.MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(UsersAdapter.MyViewHolder holder, int position) {
        UserModel c = contacts.get(position);
        holder.setData(c, position);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView image;
        private TextView username;

        public MyViewHolder(View itemView) {
            super(itemView);

            image = (CircularImageView) itemView.findViewById(R.id.image_view_user);
            username = (TextView) itemView.findViewById(R.id.username_user);
        }


        public void setData(UserModel c, int position) {

            username.setText(c.getUsername());

            //TODO: load image from storage
            if (c.isHasImage()) {
                getImage(c);
            } else {

                image.setImageResource(R.drawable.ic_person_white_24dp);
            }
        }


        private void getImage(final UserModel c) {
            try {
                StorageReference mProfileImageRef = FirebaseStorage.getInstance().getReference(UserDataMapper.USER_IMAGE_PATH + c.getUid());
                Glide.with(context)
                        .using(new FirebaseImageLoader())
                        .load(mProfileImageRef)
                        .into(image);
            } catch (Exception e) {
                e.printStackTrace();
                this.image.setImageResource(R.drawable.ic_person_white_24dp);
            }

        }

    }
}