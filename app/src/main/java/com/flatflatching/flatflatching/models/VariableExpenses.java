package com.flatflatching.flatflatching.models;

import java.util.List;

/**
 * Created by rafael on 28.09.2015.
 */
public class VariableExpenses extends Expenses {
    public VariableExpenses(String name, double amount) {
        super(name, amount);
    }

    public VariableExpenses(String name, double amount, List<FlatMate> contributors) {
        super(name, amount, contributors);
    }
}
