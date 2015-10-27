package com.flatflatching.flatflatching.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public abstract class Expense {

    public enum ExpenseType {
        Variable,
        Static
    }

    private static final int MILLISECONDS_IN_SECONDS = 1000;
    private double amount;
    private String name;
    private List<FlatMate> contributors = new ArrayList<>();
    private Date dueDate;
    private String id;

    public Expense(final String name, final double amount, final Date dueDate) {
        this(name, amount);
        Objects.requireNonNull(dueDate);
        this.dueDate = new Date(dueDate.getTime());
    }

    public Expense(final JSONObject jsonExpense) throws JSONException {
        Objects.requireNonNull(jsonExpense);
        name = jsonExpense.getString("expense_name");
        dueDate = new Date((long) MILLISECONDS_IN_SECONDS * jsonExpense.getInt("expense_end"));
        amount = jsonExpense.getDouble("expense_amount");
        id = jsonExpense.getString("expense_uuid");
        final JSONArray expenseContributors = jsonExpense.getJSONArray("expense_users");
        for (int j = 0; j < expenseContributors.length(); j++) {
            final JSONObject contributor = expenseContributors.getJSONObject(j);
            final String userEmail = contributor.getString("user_email");
            contributors.add(new FlatMate(userEmail, userEmail, false));
        }
    }
    public Expense(final String name, final Date dueDate, final double amount, final List<FlatMate> contributors) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(dueDate);
        Objects.requireNonNull(contributors);
        this.name = name;
        this.dueDate = new Date(dueDate.getTime());
        this.amount = amount;
        this.contributors.addAll(contributors);
    }

    public Expense(final String name, final double amount) {
        Objects.requireNonNull(name);
        this.name = name;
        this.amount = amount;
    }

    public Expense(final String name, final double amount, final List<FlatMate> contributors) {
        this(name, amount);
        Objects.requireNonNull(contributors);
        this.contributors.addAll(contributors);
    }

    public final List<FlatMate> getContributors() {
        return new ArrayList<>(this.contributors);
    }
    public final void addContributor(final FlatMate flatMate) {
        this.contributors.add(flatMate);
    }

    public abstract ExpenseType getExpenseType();

    public final double getAmount() {
        return amount;
    }

    public final String getName() {
        return name;
    }

    public final long getDueDateTimeStamp() {
        return dueDate.getTime() / MILLISECONDS_IN_SECONDS;
    }

    public final String getId() {
        return id;
    }
}
