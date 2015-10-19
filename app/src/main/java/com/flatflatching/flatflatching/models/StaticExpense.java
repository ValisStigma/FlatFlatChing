package com.flatflatching.flatflatching.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by rafael on 28.09.2015.
 */
public class StaticExpense extends Expense {
    public StaticExpense(final JSONObject jsonExpense) throws JSONException {
        super(jsonExpense);
    }

    public StaticExpense(String name, double amount) {
        super(name, amount);
    }

    public StaticExpense(String name, double amount, List<FlatMate> contributors) {
        super(name, amount, contributors);
    }

    public StaticExpense(final String name, final Date dueDate, final double amount, final double toPay, final List<FlatMate> contributors) {
        super(name, dueDate, amount, toPay, contributors);
    }

    @Override
    public final ExpenseType getExpenseType() {
        return ExpenseType.Static;
    }
}
