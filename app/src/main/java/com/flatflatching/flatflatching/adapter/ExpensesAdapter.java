package com.flatflatching.flatflatching.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.models.Expense;

import java.util.List;

/**
 * Created by rafael on 23.10.2015.
 */
public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>{
    private List<Expense> expenseList;

    public ExpensesAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder expenseViewHolder, int i) {
        Expense expense = expenseList.get(i);
        expenseViewHolder.expenseNameTextView.setText(expense.getName());
        expenseViewHolder.expenseAmountTextView.setText(Double.toString(expense.getAmount()));
        expenseViewHolder.rootView.setOnClickListener(expenseViewHolder);
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.expense_card, viewGroup, false);

        return new ExpenseViewHolder(itemView);
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        protected TextView expenseNameTextView;
        protected TextView expenseAmountTextView;
        protected View rootView;
        public ExpenseViewHolder(View v) {
            super(v);
            expenseNameTextView =  (TextView) v.findViewById(R.id.expenseNameTextView);
            expenseAmountTextView = (TextView) v.findViewById(R.id.expenseAmountTextView);
            rootView = v;
        }

        @Override
        public void onClick(View v) {
            Switch switcher = (Switch)v.findViewById(R.id.switchPaid);
            switcher.setChecked(!switcher.isChecked());
        }
    }
}
