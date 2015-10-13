package com.flatflatching.flatflatching.models;

import java.util.Date;
import java.util.List;

/**
 * Created by rafael on 28.09.2015.
 */
public class VariableExpense extends Expense {
    public VariableExpense(String name, double amount) {
        super(name, amount);
    }

    public VariableExpense(String name, double amount, List<FlatMate> contributors) {
        super(name, amount, contributors);
    }

    public VariableExpense(final String name, final Date dueDate, final double amount, final double toPay, final List<FlatMate> contributors) {
        super(name, dueDate, amount, toPay, contributors);
    }
        @Override
    public ExpenseType getExpenseType() {
        return ExpenseType.Variable;
    }
}
