package com.flatflatching.flatflatching.helpers;

import com.flatflatching.flatflatching.models.Address;
import com.flatflatching.flatflatching.models.Flat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class RequestBuilder {

    private static final String ACCOUNT_NAME = "account_name";
    private static final String ACCOUNT_TOKEN = "account_token";
    private static final String FLAT_ID = "flat_uuid";
    private static final String USER_EMAIL = "user_email";

    public RequestBuilder(){
    }


    public JSONObject getRegisterRequest(final String accountName) throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(ACCOUNT_NAME, accountName);
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
        requestParams.put(ACCOUNT_TOKEN, accountToken);
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

    public JSONObject getInvitationRequest(final String accountToken,
                                           final String flatId, final String userEmail) throws JSONException {

        final JSONObject requestParams = new JSONObject();
        requestParams.put(ACCOUNT_TOKEN, accountToken);
        requestParams.put(FLAT_ID, flatId);
        requestParams.put(USER_EMAIL, userEmail);
        return requestParams;
    }

    public JSONObject getFlatInfoRequest(final String flatId) throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(FLAT_ID, flatId);
        return requestParams;
    }

    public JSONObject getSetFlatAdminRequest(final String token, final String userEmail) throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(ACCOUNT_TOKEN, token);
        requestParams.put(USER_EMAIL, userEmail);
        return requestParams;
    }

    public JSONObject getDeleteFlatRequest(final String token, final String flatId) throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(ACCOUNT_TOKEN, token);
        requestParams.put(FLAT_ID, flatId);
        return requestParams;
    }

    public JSONObject getAnswerInvitationRequest(final String token, final String flatId,
                                                 final String userEmail, final String adminEmail,
                                                 final boolean accept) throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(ACCOUNT_TOKEN, token);
        requestParams.put(FLAT_ID, flatId);
        requestParams.put("flat_admin_email", adminEmail);
        requestParams.put(USER_EMAIL, userEmail);
        requestParams.put("invite_accept", accept);
        return requestParams;
    }

    public JSONObject getCreateVariableExpenseRequest(final String token, final String flatId, final String expenseName, final double expenseAmount,
                                                      final Date expenseEnd, final JSONArray expenseUsers) throws JSONException {
        JSONObject requestParams = new JSONObject();
        requestParams.put(ACCOUNT_TOKEN, token);
        requestParams.put(FLAT_ID, flatId);
        requestParams.put("expense_name", expenseName);
        requestParams.put("expense_amount", expenseAmount);
        requestParams.put("expense_end", expenseEnd);
        requestParams.put("expense_users", expenseUsers);
        return requestParams;
    }
    public JSONObject getCreateStaticExpenseRequest(final String token, final String flatId,
                                                    final String expenseName, final double expenseAmount,
                                                    final Date expenseEnd, final int expenseInterval,
                                                    final JSONArray expenseUsers) throws JSONException {
        JSONObject requestParams = getCreateVariableExpenseRequest(token, flatId, expenseName, expenseAmount, expenseEnd, expenseUsers);
        requestParams.put("expense_interval", expenseInterval);
        return requestParams;

    }

    public JSONObject getUserEmail(final String userEmail) throws JSONException {
        JSONObject variableUser = new JSONObject();
        variableUser.put(USER_EMAIL, userEmail);
        return variableUser;
    }
    public JSONObject getStaticUserObject(final String userEmail, final double divisionKey) throws JSONException {
        JSONObject staticUser = new JSONObject();
        staticUser.put(USER_EMAIL, userEmail);
        staticUser.put("division_key", divisionKey);
        return staticUser;
    }

    public JSONObject getPayBackStaticExpenseRequest(final String token, final String flatId, final String expenseId, final String userEmail) throws JSONException {
        JSONObject requestParams = new JSONObject();
        requestParams.put(ACCOUNT_TOKEN, token);
        requestParams.put(FLAT_ID, flatId);
        requestParams.put("expense_id", expenseId);
        requestParams.put(USER_EMAIL, userEmail);
        return requestParams;
    }

    public JSONObject getExpensesRequest(final String flatId, final String userEmail) throws JSONException {
        JSONObject requestParams = new JSONObject();
        requestParams.put(USER_EMAIL, userEmail);
        requestParams.put(FLAT_ID, flatId);
        return requestParams;
    }
}
