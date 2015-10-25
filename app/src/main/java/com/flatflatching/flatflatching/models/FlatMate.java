package com.flatflatching.flatflatching.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public final class FlatMate implements Serializable{
    private final String name;
    private final String email;
    private boolean isAdmin;

    public FlatMate(final JSONObject jsonFlatMate) throws JSONException {
        isAdmin = jsonFlatMate.getBoolean("_is_admin");
        name = jsonFlatMate.getString("user_email").split("@")[0];
        email = jsonFlatMate.getString("user_email");
    }
    public FlatMate(final String name, final String email, final boolean isAdmin) {
        this.email = email;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    public boolean isAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}
