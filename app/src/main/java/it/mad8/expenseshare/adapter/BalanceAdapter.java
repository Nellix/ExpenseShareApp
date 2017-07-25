package it.mad8.expenseshare.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.mad8.expenseshare.ExpenseShareApplication;
import it.mad8.expenseshare.R;
import it.mad8.expenseshare.model.BalanceModel;
import it.mad8.expenseshare.model.PaymentModel;
import it.mad8.expenseshare.model.RefunderModel;
import it.mad8.expenseshare.model.UserModel;
import it.mad8.expenseshare.utils.CalendarUtils;

/**
 * Created by Rosario on 11/06/2017.
 */

public class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.BalanceViewHolder>{

    private List<BalanceModel> mItemsList;
    private Context mContext;
    private LayoutInflater mInflater;


    public BalanceAdapter(List<BalanceModel> mItemsList) {
        this.mItemsList = mItemsList;
    }

    @Override
    public BalanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mInflater = LayoutInflater.from(mContext);

        return new BalanceViewHolder(mInflater.inflate(R.layout.list_item_past_expenses, parent, false));
    }

    @Override
    public void onBindViewHolder(BalanceViewHolder holder, int position) {
        holder.setData(mItemsList.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }



    class BalanceViewHolder extends RecyclerView.ViewHolder{

        private TextView mExpenseName;
        private TextView mDate;
        private TextView mAmount;


        public BalanceViewHolder(View itemView) {
            super(itemView);

            mExpenseName = (TextView) itemView.findViewById(R.id.tv_expense_name);
            mDate = (TextView) itemView.findViewById(R.id.tv_date);
            mAmount = (TextView) itemView.findViewById(R.id.tv_amount);
        }

        public void setData (BalanceModel model){
            mExpenseName.setText(model.getName());
            mDate.setText(CalendarUtils.mapCalendarToISOString(model.getDate()));
            if(model.getAmount() < 0)
                mAmount.setTextColor(mContext.getResources().getColor(R.color.negative_balance));
            else
                mAmount.setTextColor(mContext.getResources().getColor(R.color.positive_balance));
            mAmount.setText(String.format(Locale.getDefault(), "%1$,.2f €", model.getAmount()));
        }
    }

}
