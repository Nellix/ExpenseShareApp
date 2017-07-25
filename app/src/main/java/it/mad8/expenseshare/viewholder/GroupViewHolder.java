package it.mad8.expenseshare.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.Calendar;
import java.util.Locale;

import it.mad8.expenseshare.R;
import it.mad8.expenseshare.model.UserGroupModel;
import it.mad8.expenseshare.utils.CalendarUtils;
import it.mad8.expenseshare.model.datamapper.GroupDataMapper;

/**
 * Created by Rosario on 29/04/2017.
 */

public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private TextView mGroupName;
    private TextView mNotification;
    private TextView mGroupMessage;
    private TextView mLastModificationDate;
    private CircularImageView mGroupImage;
    private Context mContext;
    private OnClickListener mListener;
    private UserGroupModel model;

    private String groupId;

    public GroupViewHolder(View itemView) {
        super(itemView);
        mGroupName = (TextView) itemView.findViewById(R.id.txt_group_name);
        mGroupMessage = (TextView) itemView.findViewById(R.id.txt_group_message);
        mLastModificationDate = (TextView) itemView.findViewById(R.id.txt_time);
        mNotification = (TextView) itemView.findViewById(R.id.txt_notification);
        mGroupImage = (CircularImageView) itemView.findViewById(R.id.img_group);
        itemView.setOnClickListener(this);
        mContext = itemView.getContext();
    }

    public void setData(UserGroupModel model) {
        this.model = model;

        mGroupName.setText(model.getName());
        if (model.getNotifications() > 0) {
            mNotification.setVisibility(View.VISIBLE);
            mNotification.setText(String.format(Locale.getDefault(), "%1$d", model.getNotifications()));
        } else {
            mNotification.setVisibility(View.GONE);
        }
        if (CalendarUtils.onSameDay(Calendar.getInstance(), model.getLastModificationDate()))
            mLastModificationDate.setText(DateFormat.format("hh:mm", model.getLastModificationDate()));
        else
            mLastModificationDate.setText(DateFormat.format("dd/MM/yyyy", model.getLastModificationDate()));
        mGroupMessage.setText(model.getLastModification());
        if (model.getHasImage()) {
            StorageReference mProfileImageRef = FirebaseStorage.getInstance().getReference(GroupDataMapper.GROUP_IMAGE_PATH + model.getGroupId());
            Glide.with(mContext)
                    .using(new FirebaseImageLoader())
                    .load(mProfileImageRef)
                    .into(mGroupImage);
        } else {
            mGroupImage.setImageResource(R.drawable.ic_group);
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (this.mListener != null)
            this.mListener.onClick(v, model);
    }

    public interface OnClickListener {
        void onClick(View v, UserGroupModel model);
    }
}
