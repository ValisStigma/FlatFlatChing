package com.flatflatching.flatflatching.models;

/**
 * Created by rafael on 05.10.2015.
 */
public class Address {
    private String streetName;
    private String streetNumber;
    private String city;
    private String zipCode;

    public Address(final String streetName, final String streetNumber, final String city, final String zipCode) {
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.city = city;
        this.zipCode = zipCode;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }
}
