package com.flatflatching.flatflatching.models;

/**
 * Created by rafael on 28.09.2015.
 */
public class FlatMate {
    private final String name;
    private boolean isAdmin;

    public FlatMate(final String name, final boolean isAdmin) {
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
