package it.mad8.expenseshare.viewholder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import it.mad8.expenseshare.R;
import it.mad8.expenseshare.activity.ExpenseActivity;
import it.mad8.expenseshare.model.UserGroupExpenseModel;

/**
 * Created by Rosario on 30/04/2017.
 */

public class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mExpenseStatus;
    private TextView mExpenseName;
    private TextView mExpenseStatusExpanded;
    private TextView mExpensePrice;
    private TextView mExpenseNotification;
    private Context mContext;
    private UserGroupExpenseModel expense;
    private OnClickListener mListener;
    //private int expenseStatus;


    public ExpenseViewHolder(View itemView) {
        super(itemView);
        mExpenseStatus = (TextView) itemView.findViewById(R.id.txt_expense_status);
        mExpenseName = (TextView) itemView.findViewById(R.id.txt_expense_title);
        mExpenseStatusExpanded = (TextView) itemView.findViewById(R.id.txt_expense_extended_status);
        mExpensePrice = (TextView) itemView.findViewById(R.id.txt_expense_price);
        mExpenseNotification = (TextView) itemView.findViewById(R.id.txt_expense_notification);

        itemView.setOnClickListener(this);
        mContext = itemView.getContext();
    }

    public void setData(UserGroupExpenseModel model) {
        expense = model;
        mExpenseNotification.setVisibility(View.GONE);
        mExpenseName.setText(model.getName());
        switch (model.getStatus()) {
            case PROPOSAL:
                mExpenseStatus.setBackgroundColor(mContext.getResources().getColor(R.color.expense_proposal));
                mExpenseStatus.setText("P");
                mExpenseStatusExpanded.setText(R.string.list_text_proposal);
                mExpensePrice.setText("");
                //expenseStatus = 0;
                break;
            case WAITING:
                mExpenseStatus.setBackgroundColor(mContext.getResources().getColor(R.color.expense_waiting));
                mExpenseStatus.setText("W");
                mExpenseStatusExpanded.setText(R.string.list_text_waiting);
                mExpensePrice.setTextColor(mContext.getResources().getColor(R.color.colorSecondaryText));
                mExpensePrice.setText(String.format(Locale.getDefault(), "%1$,.2f €", model.getCost()));

                //expenseStatus = 1;
                break;
            case REFUND:
                mExpenseStatus.setBackgroundColor(mContext.getResources().getColor(R.color.expense_refund));
                mExpenseStatus.setText("R");
                if (model.getCost() < 0) {
                    mExpensePrice.setTextColor(mContext.getResources().getColor(R.color.negative_balance));
                    mExpenseStatusExpanded.setText(R.string.list_text_to_pay);
                } else if (model.getCost() > 0) {
                    mExpensePrice.setTextColor(mContext.getResources().getColor(R.color.positive_balance));
                    mExpenseStatusExpanded.setText(R.string.list_text_other_pay);
                } else {
                    mExpensePrice.setTextColor(mContext.getResources().getColor(R.color.neutral_balance));
                    mExpenseStatusExpanded.setText(R.string.list_text_no_pay);
                }
                mExpensePrice.setText(String.format(Locale.getDefault(), "%1$,.2f €", model.getCost()));

                //expenseStatus = 2;
                break;
            case CLOSED:
                mExpenseStatus.setBackgroundColor(mContext.getResources().getColor(R.color.expense_closed));
                mExpenseStatus.setText("C");
                mExpenseStatusExpanded.setText(R.string.list_text_closed);
                mExpensePrice.setText("");
                //expenseStatus = 3;
                break;
        }
    }


    @Override
    public void onClick(View v) {
        if (this.mListener != null)
            mListener.onClickListener(v, expense);
    }

    public OnClickListener getOnClickListener() {
        return mListener;
    }

    public void setOnClickListener(OnClickListener mListener) {
        this.mListener = mListener;
    }

    public interface OnClickListener {
        void onClickListener(View v, UserGroupExpenseModel expense);
    }
}
