package it.mad8.expenseshare.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.github.aakira.expandablelayout.ExpandableLayoutListenerAdapter;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;
import com.github.aakira.expandablelayout.Utils;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Locale;

import it.mad8.expenseshare.R;
import it.mad8.expenseshare.model.ProposalModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.model.datamapper.ExpenseDataMapper;

/**
 * Created by giaco on 14/04/2017.
 */

public class ProposalsAdapter extends RecyclerView.Adapter<ProposalsAdapter.ProposalViewHolder> {

    private boolean mReadOnly;
    private UserModel mUser;
    private Context mContext;
    private List<ProposalModel> mItems;
    private SparseBooleanArray expandState = new SparseBooleanArray();
    private String mExpenseid;
    private OnVoteClickListener mVoteListener;
    private OnSelectionListener mSelectionListener;


    public ProposalsAdapter(String expenseId, List<ProposalModel> items, UserModel currentUser, boolean reaOnly) {
        mItems = items;
        mExpenseid = expenseId;
        for (int i = 0; i < items.size(); i++) {
            expandState.append(i, false);
        }
        mUser = currentUser;
        mReadOnly = reaOnly;
    }

    @Override
    public ProposalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        return new ProposalViewHolder(mInflater.inflate(R.layout.list_item_proposal_recycler, parent, false));
    }

    @Override
    public void onBindViewHolder(ProposalViewHolder holder, int position) {
        holder.setData(mItems.get(position), position, expandState.get(position));

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public OnVoteClickListener getVoteClickListener() {
        return mVoteListener;
    }

    public void setVoteClickListener(OnVoteClickListener mListener) {
        this.mVoteListener = mListener;
    }

    public OnSelectionListener getSelectionListener() {
        return mSelectionListener;
    }

    public void setSelectionListener(OnSelectionListener mSelectionListener) {
        this.mSelectionListener = mSelectionListener;
    }

    public boolean isReadOnly() {
        return mReadOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.mReadOnly = readOnly;
    }


    class ProposalViewHolder extends RecyclerView.ViewHolder {

        Button mChoose;
        TextView mTitle;
        TextView mInfo;
        TextView mVotes;
        ImageView mImage;
        TextView mDescription;
        TextView mPrice;
        Button mLike;
        Button mDislike;
        RelativeLayout mSwitch;
        LinearLayout mItemCompact;
        ExpandableLinearLayout mExpandableLayout;

        ProposalModel mItem;
        int mPosition;


        ProposalViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.txt_proposal_title);
            mInfo = (TextView) itemView.findViewById(R.id.txt_proposal_info);
            mVotes = (TextView) itemView.findViewById(R.id.txt_proposal_vote);
            mDescription = (TextView) itemView.findViewById(R.id.txt_proposal_description);
            mPrice = (TextView) itemView.findViewById(R.id.txt_proposal_price);
            mImage = (ImageView) itemView.findViewById(R.id.img_proposal);
            mLike = (Button) itemView.findViewById(R.id.btn_like);
            mLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mVoteListener != null)
                        mVoteListener.onLikeClick(mItem, mPosition);
                }
            });
            mDislike = (Button) itemView.findViewById(R.id.btn_dislike);
            mDislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mVoteListener != null)
                        mVoteListener.onDislikeClick(mItem, mPosition);
                }
            });
            mChoose = (Button) itemView.findViewById(R.id.btn_select_proposal);
            mChoose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectionListener != null)
                        mSelectionListener.onSelect(mItem, mPosition, mImage.getDrawable());
                }
            });

            mSwitch = (RelativeLayout) itemView.findViewById(R.id.btn_switch);
            mItemCompact = (LinearLayout) itemView.findViewById(R.id.ll_item_compact);
            mExpandableLayout = (ExpandableLinearLayout) itemView.findViewById(R.id.proposal_expandable);


        }

        void setData(ProposalModel item, int position, boolean expanded) {
            mItem = item;
            mPosition = position;

            mTitle.setText(item.getName());
            mInfo.setText(String.format(Locale.getDefault(), mContext.getResources().getString(R.string.added_on), item.getCreator().getUsername(), item.getCreationDate()));

            int voto = 0;
            for (String uid : item.getUsers().keySet()) {
                voto += item.getUsers().get(uid);
            }

            if (item.getCreator().equals(mUser) && !isReadOnly()) {
                mChoose.setVisibility(View.VISIBLE);
            } else {
                mChoose.setVisibility(View.GONE);
            }

            if (item.getUsers().get(mUser.getUid()) > 0) {
                mLike.setEnabled(false);
                mLike.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorPrimary)));
                mDislike.setEnabled(true);
                mDislike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
            } else if (item.getUsers().get(mUser.getUid()) < 0) {
                mLike.setEnabled(true);
                mLike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                mDislike.setEnabled(false);
                mDislike.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorPrimary)));
            } else {
                mLike.setEnabled(true);
                mLike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                mDislike.setEnabled(true);
                mDislike.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
            }

            mVotes.setText(String.format(Locale.getDefault(), "%1$d", voto));

            if (item.getDescription().trim().length() > 0)
                mDescription.setText(item.getDescription().trim());
            else
                mDescription.setText(R.string.no_description);
            mPrice.setText(String.format(Locale.getDefault(), "%1$,.2f", item.getPrice()));

            //this.setIsRecyclable(false);
            mExpandableLayout.setInRecyclerView(true);
            mExpandableLayout.setExpanded(expanded);
            mExpandableLayout.setInterpolator(Utils.createInterpolator(Utils.ACCELERATE_DECELERATE_INTERPOLATOR));

            if (mItem.getHasImage()) {
                StorageReference mItemImageRef = FirebaseStorage.getInstance().getReference(ExpenseDataMapper.PROPOSAL_PATH.replace("{eid}", mExpenseid).replace("{pid}", mItem.getId()));
                Glide.with(mContext)
                        .using(new FirebaseImageLoader())
                        .load(mItemImageRef)
                        .into(mImage);
            }

            mExpandableLayout.setListener(new ExpandableLayoutListenerAdapter() {
                @Override
                public void onPreOpen() {
                    createRotateAnimator(mSwitch, 0f, 180f).start();
                    expandState.put(mPosition, true);
                }

                @Override
                public void onPreClose() {
                    createRotateAnimator(mSwitch, 180f, 0f).start();
                    expandState.put(mPosition, false);
                }
            });

            mSwitch.setRotation(expandState.get(position) ? 180f : 0f);
            mSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mExpandableLayout.toggle();
                }
            });
            mItemCompact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    mExpandableLayout.toggle();
                }
            });
        }

        private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
            ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
            animator.setDuration(300);
            animator.setInterpolator(Utils.createInterpolator(Utils.LINEAR_INTERPOLATOR));
            return animator;
        }
    }

    public interface OnVoteClickListener {
        void onLikeClick(ProposalModel proposal, int position);

        void onDislikeClick(ProposalModel proposal, int position);
    }

    public interface OnSelectionListener {
        void onSelect(ProposalModel proposal, int position, Drawable image);
    }
}
