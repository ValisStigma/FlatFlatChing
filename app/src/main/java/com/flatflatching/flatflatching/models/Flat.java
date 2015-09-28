package com.flatflatching.flatflatching.models;

import java.util.ArrayList;

/**
 * Created by rafael on 28.09.2015.
 */
public class Flat {
    private ArrayList<FlatMate> inhabitants = new ArrayList<>();

    public void addFlatMate(final FlatMate flatMate) {
        this.inhabitants.add(flatMate);
    }

    public void removeFlatMate(final FlatMate flatMate) {
        this.inhabitants.remove(flatMate);
    }
}
