package com.flatflatching.flatflatching.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by rafael on 28.09.2015.
 */
public class VariableExpense extends Expense {

    public VariableExpense(final JSONObject jsonExpense) throws JSONException {
        super(jsonExpense);
    }
    public VariableExpense(final String description, final double amount, final Date dueDate) {
        super(description, amount, dueDate);
    }
    public VariableExpense(final String name, final double amount) {
        super(name, amount);
    }

    public VariableExpense(final String name, final double amount, final List<FlatMate> contributors) {
        super(name, amount, contributors);
    }

    public VariableExpense(final String name, final Date dueDate, final double amount, final double toPay, final List<FlatMate> contributors) {
        super(name, dueDate, amount, toPay, contributors);
    }

    @Override
    public final ExpenseType getExpenseType() {
        return ExpenseType.Variable;
    }
}
