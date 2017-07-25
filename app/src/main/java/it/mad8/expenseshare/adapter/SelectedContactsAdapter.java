package it.mad8.expenseshare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import it.mad8.expenseshare.R;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;

/**
 * Created by giaco on 26/05/2017.
 */

public class SelectedContactsAdapter extends RecyclerView.Adapter<SelectedContactsAdapter.SelectedContactViewHolder> {

    private boolean removable;
    private Context context;
    private List<UserModel> selectedContacts;
    private OnItemRemovedListener itemRemovedListener;

    public SelectedContactsAdapter(List<UserModel> selectedUsers) {
        this(selectedUsers, true);
    }

    public SelectedContactsAdapter(List<UserModel> selectedUsers, boolean removable) {
        this.selectedContacts = selectedUsers;
        this.removable = removable;
    }

    @Override
    public SelectedContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_item_selected_contact, parent, false);
        return new SelectedContactViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SelectedContactViewHolder holder, int position) {
        UserModel c = selectedContacts.get(position);
        holder.setData(c, position);
    }

    @Override
    public int getItemCount() {
        return selectedContacts.size();
    }

    public OnItemRemovedListener getItemRemovedListener() {
        return itemRemovedListener;
    }

    public void setItemRemovedListener(OnItemRemovedListener itemRemovedListener) {
        this.itemRemovedListener = itemRemovedListener;
    }

    class SelectedContactViewHolder extends RecyclerView.ViewHolder {


        private ImageView contactImage;
        private ImageView removeBox;
        private TextView txtName;

        private UserModel currentUser;
        private int currentPosition;

        public SelectedContactViewHolder(View itemView) {
            super(itemView);
            contactImage = (ImageView) itemView.findViewById(R.id.iv_selected_contact_image);
            removeBox = (ImageView) itemView.findViewById(R.id.iv_selected_remove);
            txtName = (TextView) itemView.findViewById(R.id.txt_selected_contact_name);

            if (removable) {
                contactImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(currentUser, currentPosition);
                    }
                });
                removeBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(currentUser, currentPosition);
                    }
                });
            } else {
                removeBox.setVisibility(View.GONE);
            }
        }

        void setData(UserModel user, int position) {
            currentUser = user;
            currentPosition = position;

            this.txtName.setText(currentUser.getUsername());

            //decode image
            if (currentUser.isHasImage()) {
                try {
                    StorageReference mProfileImageRef = FirebaseStorage.getInstance().getReference(UserDataMapper.USER_IMAGE_PATH + currentUser.getUid());
                    Glide.with(context)
                            .using(new FirebaseImageLoader())
                            .load(mProfileImageRef)
                            .into(this.contactImage);
                } catch (Exception e) {
                    e.printStackTrace();
                    this.contactImage.setImageResource(R.drawable.ic_person_white_24dp);
                }
            } else {
                contactImage.setImageResource(R.drawable.ic_person_white_24dp);
            }
        }

        public void removeItem(UserModel user, int pos) {
            selectedContacts.remove(pos);
            notifyItemRemoved(pos);
            notifyItemRangeChanged(pos, selectedContacts.size());
            if (getItemRemovedListener() != null) {
                getItemRemovedListener().onItemRemove(user, pos);
            }
        }
    }

    public interface OnItemRemovedListener {
        void onItemRemove(UserModel removedUser, int position);
    }
}
