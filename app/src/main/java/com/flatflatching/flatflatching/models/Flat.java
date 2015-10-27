package com.flatflatching.flatflatching.models;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public final class Flat implements Serializable{
    private String name;
    private Address address;

    public Flat(final JSONObject jsonFlat) throws JSONException {
        name = jsonFlat.getString("flat_name");
        try{
            final JSONObject jsonAddress = jsonFlat.getJSONObject("flat_address");
            final String streetName = jsonAddress.getString("flat_address_street");
            final String houseNumber = jsonAddress.getString("flat_address_number");
            final String plz = jsonAddress.getString("flat_address_plz");
            final String city = jsonAddress.getString("flat_address_place");
            if (!streetName.isEmpty() && !houseNumber.isEmpty() && !plz.isEmpty() && !city.isEmpty()) {
                address = new Address(streetName, houseNumber, plz, city);
            }
        } catch(JSONException e) {
            //Do nothing, adress is not mandatory for flats
            Log.d("address_error", "Incomplete address for " + name);
        }

    }
    public Flat(final String name) {
        this.name = name;
    }

    public Flat(final String name, final Address address) {
        this(name);
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public boolean hasAddress() {
        return address != null;
    }
}
