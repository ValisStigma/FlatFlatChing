package com.flatflatching.flatflatching.models;

/**
 * Created by rafael on 13.10.2015.
 */
public class StaticUserExpense {
    final private String email;
    final private double divisionKey;

    StaticUserExpense(final String email, final double divisionKey) {
        if(divisionKey < 0 || divisionKey > 100) {
            throw new IllegalArgumentException("Divisionkey must be between 0 and 100");
        }
        this.email = email;
        this.divisionKey = divisionKey;
    }

    public double getDivisionKey() {
        return divisionKey;
    }

    public String getEmail() {
        return email;
    }

}
