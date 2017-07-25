package it.mad8.expenseshare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import it.mad8.expenseshare.R;
import it.mad8.expenseshare.model.RefunderModel;
import it.mad8.expenseshare.model.PaymentModel;

/**
 * Created by giaco on 17/04/2017.
 */

public class RefundPersonAdapter extends RecyclerView.Adapter<RefundPersonAdapter.RefundPersonViewHolder> {
    private PaymentModel mPayment;
    private LayoutInflater mInflater;
    private RefundListener mListener;
    private Context mContext;
    private String mExpenseId;

    public RefundPersonAdapter(String expenseId, PaymentModel payment, RefundListener listener) {
        mPayment = payment;
        mListener = listener;
        mExpenseId = expenseId;
    }

    @Override
    public RefundPersonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mInflater = LayoutInflater.from(mContext);
        return new RefundPersonViewHolder(mInflater.inflate(R.layout.list_item_refunder, parent, false));
    }

    @Override
    public void onBindViewHolder(RefundPersonViewHolder holder, int position) {
        holder.setData(mPayment.getRefunders().get(position),position);
    }

    @Override
    public int getItemCount() {
        if(mPayment.getRefunders() != null)
            return mPayment.getRefunders().size();
        return 0;
    }

    public void swap(final List<RefunderModel> datas){
        /*mList.clear();
        mList.addAll(datas);*/
        notifyDataSetChanged();
    }

    class RefundPersonViewHolder extends RecyclerView.ViewHolder {

        ImageView mRefunderImage;
        TextView mRefunderUsername;
        TextView mRefunderState;
        TextView mRefunderVisualState;
        Button mConfirm;
        Button mRemind;

        RefunderModel mRefunder;

        int mPosition;

        RefundPersonViewHolder(View itemView) {
            super(itemView);

            mRefunderImage = (ImageView) itemView.findViewById(R.id.img_refund_user);
            mRefunderUsername = (TextView) itemView.findViewById(R.id.txt_refund_username);
            mRefunderState = (TextView) itemView.findViewById(R.id.txt_refund_state);
            mRefunderVisualState = (TextView) itemView.findViewById(R.id.txt_refund_visual_state);
            mConfirm = (Button) itemView.findViewById(R.id.btn_confirm_payment);
            mConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onConfirmRefund(mRefunder);
                }
            });
            mRemind = (Button) itemView.findViewById(R.id.btn_remind_payment);
            mRemind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onRemindRefund(mRefunder);
                }
            });
        }

        void setData(RefunderModel refunder, Integer position){
            mRefunder = refunder;
            mPosition = position;

            if (refunder.getUser().isHasImage()) {
                {
                    StorageReference mItemImageRef = FirebaseStorage.getInstance().getReference("users/"+refunder.getUserId());
                    Glide.with(mContext)
                            .using(new FirebaseImageLoader())
                            .load(mItemImageRef)
                            .into(mRefunderImage);
                }
            } else {
         //       mRefunderImage.setImageBitmap(refunder.getUser().getImage());
                //TODO image viene presa da una Url
                mRefunderImage.setImageResource(R.drawable.ic_person_white_24dp);
            }

            mRefunderUsername.setText(refunder.getUser().getUsername());
            switch (refunder.getStatus()){
                case PAID:
                    mRefunderVisualState.setBackgroundColor(mContext.getResources().getColor(R.color.positive_balance));
                    mRefunderState.setText(mContext.getString(R.string.refund_state_refunded));
                    mConfirm.setVisibility(View.GONE);
                    mRemind.setVisibility(View.GONE);
                    break;
                case TO_PAY:
                    mRefunderVisualState.setBackgroundColor(mContext.getResources().getColor(R.color.negative_balance));
                    mRefunderState.setText(mContext.getString(R.string.refund_state_to_refund));
                    mConfirm.setVisibility(View.GONE);
                    mRemind.setVisibility(View.VISIBLE);
                    break;
                case CONFIRMED:
                    mRefunderVisualState.setBackgroundColor(mContext.getResources().getColor(R.color.neutral_balance));
                    mRefunderState.setText(mContext.getString(R.string.refund_state_waiting_approval));
                    mConfirm.setVisibility(View.VISIBLE);
                    mRemind.setVisibility(View.GONE);
                    break;
            }
        }
    }

    public interface RefundListener{
        public void onConfirmRefund(final RefunderModel refunder);
        public void onRemindRefund(final RefunderModel refunder);
    }
}
