package com.flatflatching.flatflatching.models;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafael on 28.09.2015.
 */
public class Flat {
    private List<FlatMate> inhabitants = new ArrayList<>();
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

    public List<FlatMate> getInhabitants() {
        return new ArrayList<>(inhabitants);
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public void addFlatMate(final FlatMate flatMate) {
        this.inhabitants.add(flatMate);
    }

    public void removeFlatMate(final FlatMate flatMate) {
        this.inhabitants.remove(flatMate);
    }

    public boolean hasAddress() {
        return address != null;
    }
}
