package com.flatflatching.flatflatching.models;

import java.util.Objects;

/**
 * Created by rafael on 13.10.2015.
 */
public final class StaticUserExpense {
    private final String email;
    private final double divisionKey;
    private static final double LOWER_BOUND = 0;
    private static final double UPPER_BOUND = 100;
    StaticUserExpense(final String email, final double divisionKey) {
        Objects.requireNonNull(email);
        if(divisionKey < LOWER_BOUND || divisionKey > UPPER_BOUND) {
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
