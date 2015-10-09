package com.flatflatching.flatflatching.services;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.RequestBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rafael on 02.10.2015.
 */
public  class AuthenticatorService {
    private static final String registerUrl = "";
    public static void getAuth(BaseActivity activity, String chosenEmail) {
        new GetUserData(activity, chosenEmail).execute();
    }

    public static void register(BaseActivity activity, String chosenEmail) {
        JSONObject params;
        try {
            params = new RequestBuilder().getRegisterRequest(chosenEmail);
            new RegisterUser(activity).execute(params);
        } catch (JSONException e) {
            activity.notifyError(R.string.server_error);
        }
        new RegisterUser(activity);
    }

    private static class RegisterUser extends AbstractAsyncTask {
        public RegisterUser(BaseActivity activity) {
            super(activity, registerUrl);
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            if (result.isEmpty()) {
                reactToError();
            } else {
                try {
                    JSONObject response = new JSONObject(result);
                    JSONArray flats = response.getJSONArray("flat_uuids");
                    if(flats == null ||flats.length() == 0) {
                        reactToError();
                    } else {
                        final String flatId = flats.getString(flats.length() - 1);
                        activity.persistToPreferences(BaseActivity.FLAT_ID, flatId);
                    }
                } catch (JSONException e) {
                    reactToError();
                }
            }
        }
    }

    private static class GetUserData extends AbstractGetAuthTokenTask {
        GetUserData(BaseActivity activity, String url){
            super(activity, url);
        }
        @Override
        protected void handleToken(String token) {
            UserProfileService.fillUserProfile(activity, token);
        }
        @Override
        protected void postToken() {

        }
    }
}
