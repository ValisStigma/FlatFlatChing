package com.flatflatching.flatflatching.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by rafael on 28.09.2015.
 */
public class StaticExpense extends Expense {
    private int interval;
    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final int SECONDS_IN_WEEK = SECONDS_IN_DAY * 7;
    public static final int SECONDS_IN_TWO_WEEKS = SECONDS_IN_WEEK * 2;
    public static final int SECONDS_IN_MONTH = SECONDS_IN_TWO_WEEKS * 2;
    public static final int SECONDS_IN_YEAR = 365 * SECONDS_IN_DAY;
    public StaticExpense(final JSONObject jsonExpense) throws JSONException {
        super(jsonExpense);
    }

    public StaticExpense(final String description, final double amount, final Date dueDate) {
        super(description, amount, dueDate);
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

    public final void addInterval(final int interval) {
        this.interval = interval;
    }
    public final int getInterval() {
        return this.interval;
    }
}
