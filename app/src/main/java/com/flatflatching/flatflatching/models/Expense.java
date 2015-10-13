package com.flatflatching.flatflatching.models;

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

    public Expense(final String name, final Date dueDate, final double amount, final double toPay, final List<FlatMate> contributors) {
        this.name = name;
        this.dueDate = dueDate;
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
        this.dueDate = dueDate;
    }

    public boolean isOutstanding() {
        return this.outstanding;
    }

    public void process() {
        this.outstanding = false;
    }

    abstract public ExpenseType getExpenseType();


}
