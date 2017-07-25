package it.mad8.expenseshare.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import it.mad8.expenseshare.R;
import it.mad8.expenseshare.model.ContactModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.UserDataMapper;

/**
 * Created by giaco on 26/05/2017.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    private List<ContactModel> contacts;
    private Context context;
    private OnItemDeselectedListener deselectedListener;
    private OnItemSelectedListener selectedListener;

    public ContactsAdapter(List<ContactModel> contacts) {
        this.contacts = contacts;
    }

    @Override
    public ContactsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_item_contact, parent, false);
        return new ContactsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ContactsViewHolder holder, int position) {
        ContactModel c = contacts.get(position);
        holder.setData(c, position);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public OnItemDeselectedListener getDeselectedListener() {
        return deselectedListener;
    }

    public void setDeselectedListener(OnItemDeselectedListener deselectedListener) {
        this.deselectedListener = deselectedListener;
    }

    public OnItemSelectedListener getSelectedListener() {
        return selectedListener;
    }

    public void setSelectedListener(OnItemSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView unameTv;
        private TextView emailTv;
        private ImageView mImageView;
        private CheckBox checkBox;

        private ContactModel currentContact;
        private int currentPosition;

        public ContactsViewHolder(View v) {
            super(v);
            cardView = (CardView) v.findViewById(R.id.card_view_contact);
            unameTv = (TextView) v.findViewById(R.id.username_tv);
            emailTv = (TextView) v.findViewById(R.id.email_tv);

            mImageView = (ImageView) v.findViewById(R.id.image_view);

            checkBox = (CheckBox) v.findViewById(R.id.checkBox);
            //checkBox.setEnabled(false);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    UserModel c = currentContact.getUser();
                    if(isChecked){
                        currentContact.setSelected(true);
                        //selectedListener.onItemSelected(c,currentPosition);
                    } else {
                        currentContact.setSelected(false);
                        //deselectedListener.onItemDeselected(c,currentPosition);
                    }
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserModel c = currentContact.getUser();

                    if(currentContact.isSelected()){
                        currentContact.setSelected(false);
                        checkBox.setChecked(false);
                        deselectedListener.onItemDeselected(c,currentPosition);
                    } else {
                        currentContact.setSelected(true);
                        checkBox.setChecked(true);
                        selectedListener.onItemSelected(c,currentPosition);
                    }
                }
            });
        }

        public void setData(ContactModel contact, int position){
            this.unameTv.setText(contact.getUser().getUsername());
            this.emailTv.setText(contact.getUser().getEmail());
            this.currentContact = contact;
            this.currentPosition = position;
            this.checkBox.setChecked(contact.isSelected());

            if (contact.getUser().isHasImage()) {
                try {
                    StorageReference mProfileImageRef = FirebaseStorage.getInstance().getReference(UserDataMapper.USER_IMAGE_PATH + contact.getUser().getUid());
                    Glide.with(context)
                            .using(new FirebaseImageLoader())
                            .load(mProfileImageRef)
                            .into(this.mImageView);
                } catch (Exception e) {
                    e.printStackTrace();
                    this.mImageView.setImageResource(R.drawable.ic_person_white_24dp);
                }
            } else {
                this.mImageView.setImageResource(R.drawable.ic_person_white_24dp);
            }
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(UserModel user,int position);
    }

    public interface OnItemDeselectedListener {
        void onItemDeselected(UserModel user,int position);
    }
}
