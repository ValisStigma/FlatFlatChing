package com.flatflatching.flatflatching.models;

/**
 * Created by rafael on 13.10.2015.
 */
public class StaticUserExpense {
    private final String email;
    private final double divisionKey;
    private static final double lowerBound = 0;
    private static final double upperBound = 100;
    StaticUserExpense(final String email, final double divisionKey) {
        if(divisionKey < lowerBound || divisionKey > upperBound) {
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
