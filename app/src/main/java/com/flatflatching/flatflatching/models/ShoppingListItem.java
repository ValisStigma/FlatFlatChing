package com.flatflatching.flatflatching.models;

import java.util.Objects;

/**
 * Created by rafael on 28.09.2015.
 */
public final class ShoppingListItem {
    private double price;
    private int amount;
    private final String name;

    ShoppingListItem(final String name, final double price, final int amount) {
        Objects.requireNonNull(name);
        if(price <= 0 || amount <= 0) {
            throw new IllegalArgumentException("Doesn't make sense to have negative or zero amounts or prices.");
        }
        this.name = name;
        this.price = price;
        this.amount = amount;
    }

    public double getTotal() {
        return this.price * this.amount;
    }

    public String getName() {
        return this.name;
    }
}
