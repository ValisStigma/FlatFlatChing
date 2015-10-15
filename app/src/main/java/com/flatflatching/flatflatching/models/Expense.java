package com.flatflatching.flatflatching.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rafael on 28.09.2015.
 */

public abstract class Expense {

    public enum ExpenseType {
        Variable,
        Static
    }

    private double amount;
    private String name;
    private List<FlatMate> contributors = new ArrayList<>();
    private Date dueDate;
    private double toPay;
    private boolean outstanding = true;



    public Expense(final JSONObject jsonExpense) throws JSONException {
        name = jsonExpense.getString("expense_name");
        dueDate = new Date((long) 1000 * jsonExpense.getInt("expense_end"));
        amount = jsonExpense.getDouble("expense_amount");
        toPay = jsonExpense.getDouble("expense_user_amount");
        final JSONArray expenseContributors = jsonExpense.getJSONArray("expense_users");
        for (int j = 0; j < expenseContributors.length(); j++) {
            final JSONObject contributor = expenseContributors.getJSONObject(j);
            final String userEmail = contributor.getString("user_email");
            contributors.add(new FlatMate(userEmail, false));
        }
    }
    public Expense(final String name, final Date dueDate, final double amount, final double toPay, final List<FlatMate> contributors) {
        this.name = name;
        this.dueDate = new Date(dueDate.getTime());
        this.amount = amount;
        this.toPay = toPay;
        this.contributors.addAll(contributors);
    }

    public Expense(final String name, final double amount) {
        this.name = name;
        this.amount = amount;
    }

    public Expense(final String name, final double amount, final List<FlatMate> contributors) {
        this(name, amount);
        this.contributors.addAll(contributors);
    }

    public void setDueDate(final Date dueDate) {
        this.dueDate = new Date(dueDate.getTime());
    }

    public boolean isOutstanding() {
        return this.outstanding;
    }

    public void process() {
        this.outstanding = false;
    }

    public abstract ExpenseType getExpenseType();

    public double getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public Date getDueDate() {
        return new Date(dueDate.getTime());
    }

    public double getToPay() {
        return toPay;
    }
}
