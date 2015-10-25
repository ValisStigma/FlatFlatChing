package com.flatflatching.flatflatching.helpers;

import com.flatflatching.flatflatching.models.Address;
import com.flatflatching.flatflatching.models.Expense;
import com.flatflatching.flatflatching.models.Flat;
import com.flatflatching.flatflatching.models.StaticExpense;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class RequestBuilder {

    private static final String ACCOUNT_NAME = "account_name";
    private static final String ACCOUNT_TOKEN = "account_token";
    private static final String FLAT_ID = "flat_uuid";
    private static final String USER_EMAIL = "user_email";

    public RequestBuilder(){
    }

    private JSONObject getBaseRequest (final String userEmail) throws JSONException {
        JSONObject requestParams = new JSONObject();
        requestParams.put(USER_EMAIL, userEmail);
        return requestParams;
    }
    public JSONObject getRegisterRequest(final String accountName) throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(ACCOUNT_NAME, accountName);
        return requestParams;
    }

    public JSONObject getCreateFlatRequest(final String accountToken, final String userEmail, Flat flat) throws JSONException {
        if(flat.hasAddress()) {
            Address address = flat.getAddress();
            return getCreateFlatRequestWithAddress(accountToken, userEmail, flat.getName(), address.getStreetName(), address.getStreetNumber()
            ,address.getCity(), address.getZipCode(), "Switzerland");
        } else {
            return getCreateFlatRequest(accountToken, userEmail, flat.getName());
        }
    }
    private JSONObject getCreateFlatRequest(final String accountToken, final String userEmail, final String flatName) throws JSONException {
        final JSONObject requestParams = getBaseRequest(userEmail);
        requestParams.put(ACCOUNT_TOKEN, accountToken);
        requestParams.put("flat_name", flatName);
        return requestParams;
    }

    private JSONObject getCreateFlatRequestWithAddress(final String accountToken, final String userEmail, final String flatName,
                                                     final String streetName, final String streetNumber,
                                                     final String plz, final String city, final String country) throws JSONException {
        JSONObject requestParams = getCreateFlatRequest(accountToken, userEmail, flatName);
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
        requestParams.put("email", userEmail);
        return requestParams;
    }

    public JSONObject getFlatInfoRequest(final String flatId, final String userEmail) throws JSONException {
        final JSONObject requestParams = getBaseRequest(userEmail);
        requestParams.put(FLAT_ID, flatId);
        return requestParams;
    }

    public JSONObject getSetFlatAdminRequest(final String token, final String userEmail) throws JSONException {
        final JSONObject requestParams = new JSONObject();
        requestParams.put(ACCOUNT_TOKEN, token);
        requestParams.put(USER_EMAIL, userEmail);
        return requestParams;
    }

    public JSONObject getDeleteFlatRequest(final String token, final String flatId, final String userEmail) throws JSONException {
        final JSONObject requestParams = getBaseRequest(userEmail);
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

    public JSONObject getCreateVariableExpenseRequest(final String token, final String userEmail, final String flatId, final Expense variableExpense, final JSONArray expenseUsers) throws JSONException {
        JSONObject requestParams = getBaseRequest(userEmail);
        requestParams.put(ACCOUNT_TOKEN, token);
        requestParams.put(FLAT_ID, flatId);
        requestParams.put("expense_name", variableExpense.getName());
        requestParams.put("expense_amount", variableExpense.getAmount());
        requestParams.put("expense_end", variableExpense.getDueDateTimeStamp());
        if(expenseUsers.length() > 0) {
            requestParams.put("expense_users", expenseUsers);
        }
        return requestParams;
    }
    public JSONObject getCreateStaticExpenseRequest(final String token, final String flatId,
                                                    final String userEmail,
                                                    final StaticExpense staticExpense,
                                                    final JSONArray expenseUsers) throws JSONException {
        JSONObject requestParams = getCreateVariableExpenseRequest(token, userEmail, flatId, staticExpense, expenseUsers);
        requestParams.put("expense_interval", staticExpense.getInterval());
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
