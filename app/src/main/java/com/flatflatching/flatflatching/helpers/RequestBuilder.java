package com.flatflatching.flatflatching.helpers;

import com.flatflatching.flatflatching.models.Address;
import com.flatflatching.flatflatching.models.Flat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class RequestBuilder {

    private static final String CLIENT_ID = "client_id";
    private String userName;

    public RequestBuilder(){
    }








    public JSONObject getRegisterRequest(final String accountName) throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put("account_name", accountName);
        return requestParams;
    }

    public JSONObject getCreateFlatRequest(final String accountToken, Flat flat) throws JSONException {
        if(flat.hasAddress()) {
            Address address = flat.getAddress();
            return getCreateFlatRequestWithAddress(accountToken, flat.getName(), address.getStreetName(), address.getStreetNumber()
            ,address.getCity(), address.getZipCode(), "Switzerland");
        } else {
            return getCreateFlatRequest(accountToken, flat.getName());
        }
    }
    private JSONObject getCreateFlatRequest(final String accountToken, final String flatName) throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put("account_token", accountToken);
        requestParams.put("flat_name", flatName);
        return requestParams;
    }

    private JSONObject getCreateFlatRequestWithAddress(final String accountToken, final String flatName,
                                                     final String streetName, final String streetNumber,
                                                     final String plz, final String city, final String country) throws JSONException {
        JSONObject requestParams = getCreateFlatRequest(accountToken, flatName);
        JSONObject address = new JSONObject();
        address.put("flat_address_street", streetName);
        address.put("flat_address_number", streetNumber);
        address.put("flat_address_plz", plz);
        address.put("flat_address_place", city);
        address.put("flat_address_land", country);
        requestParams.put("flat_address", address);
        return requestParams;
    }

    private String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
}
