package com.flatflatching.flatflatching.models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Created by rafael on 05.10.2015.
 */
public class Address implements Serializable {
    private String streetName;
    private String streetNumber;
    private String city;
    private String zipCode;

    public Address(final String streetName, final String streetNumber, final String city, final String zipCode)  {
        Objects.requireNonNull(streetName);
        Objects.requireNonNull(streetNumber);
        Objects.requireNonNull(city);
        Objects.requireNonNull(zipCode);
        this.streetName = streetName;
        this.streetNumber = streetNumber;
        this.city = city;
        this.zipCode = zipCode;
    }

    public final String getStreetName() {
        return streetName;
    }

    public final String getStreetNumber() {
        return streetNumber;
    }

    public final String getCity() {
        return city;
    }

    public final String getZipCode() {
        return zipCode;
    }
}
