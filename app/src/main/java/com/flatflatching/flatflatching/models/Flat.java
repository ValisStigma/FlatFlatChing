package com.flatflatching.flatflatching.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafael on 28.09.2015.
 */
public class Flat {
    private ArrayList<FlatMate> inhabitants = new ArrayList<>();
    private String name;
    private Address address;

    public Flat(final String name) {
        this.name = name;
    }

    public Flat(final String name, final Address address) {
        this(name);
        this.address = address;
    }

    public ArrayList<FlatMate> getInhabitants() {
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
