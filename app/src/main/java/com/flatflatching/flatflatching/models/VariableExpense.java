package com.flatflatching.flatflatching.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class VariableExpense extends Expense {

    public VariableExpense(final JSONObject jsonExpense) throws JSONException {
        super(jsonExpense);
    }
    public VariableExpense(final String description, final double amount, final Date dueDate) {
        super(description, amount, dueDate);
    }

    @Override
    public final ExpenseType getExpenseType() {
        return ExpenseType.Variable;
    }
}
