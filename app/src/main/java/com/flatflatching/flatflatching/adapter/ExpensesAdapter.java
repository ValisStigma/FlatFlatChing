package com.flatflatching.flatflatching.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.models.Expense;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by rafael on 23.10.2015.
 */
public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>{
    private List<Expense> expenseList;
    private View.OnClickListener payExpense;
    private View.OnClickListener showExpense;

    public ExpensesAdapter(List<Expense> expenseList, View.OnClickListener payExpense, View.OnClickListener showExpense) {
        this.expenseList = expenseList;
        this.payExpense = payExpense;
        this.showExpense = showExpense;
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    @Override
    public void onBindViewHolder(ExpenseViewHolder expenseViewHolder, int i) {
        Expense expense = expenseList.get(i);
        expenseViewHolder.expenseIdTextView.setText(expense.getId());
        expenseViewHolder.expenseNameTextView.setText(expense.getName());
        expenseViewHolder.expenseType.setText(expense.getExpenseType().toString());
        expenseViewHolder.expenseAmountTextView.setText(Double.toString(expense.getAmount()));
        expenseViewHolder.switchPaid.setOnClickListener(this.payExpense);
        expenseViewHolder.expenseAmountTextView.setOnClickListener(this.showExpense);
        expenseViewHolder.expenseNameTextView.setOnClickListener(this.showExpense);
    }

    @Override
    public ExpenseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.expense_card, viewGroup, false);

        return new ExpenseViewHolder(itemView);
    }

    public class ExpenseViewHolder extends RecyclerView.ViewHolder{
        protected TextView expenseNameTextView;
        protected TextView expenseAmountTextView;
        protected TextView expenseIdTextView;
        protected TextView expenseType;
        protected Switch switchPaid;
        protected View rootView;
        public ExpenseViewHolder(View v) {
            super(v);
            expenseNameTextView =  (TextView) v.findViewById(R.id.expenseNameTextView);
            expenseAmountTextView = (TextView) v.findViewById(R.id.expenseAmountTextView);
            expenseIdTextView = (TextView) v.findViewById(R.id.expense_id);
            switchPaid = (Switch) v.findViewById(R.id.switchPaid);
            expenseType = (TextView) v.findViewById(R.id.expense_type);
            rootView = v;

        }

    }
}
