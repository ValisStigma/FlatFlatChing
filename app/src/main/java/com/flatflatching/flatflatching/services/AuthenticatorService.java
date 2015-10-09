package com.flatflatching.flatflatching.services;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.ServerConnector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by rafael on 02.10.2015.
 */
public  class AuthenticatorService {
    public static void getAuth(BaseActivity activity, String chosenEmail) {
        new GetUserData(activity, chosenEmail).execute();
    }

    public static void register(BaseActivity activity, String chosenEmail) {
        new RegisterUser(activity, chosenEmail);
    }

    private static class RegisterUser extends AbstractAsyncTask {
        private final String registerUrl = "";
        private final String email;
        public RegisterUser(BaseActivity activity, String chosenEmail) {
            super(activity, chosenEmail);
            email = chosenEmail;

        }

        @Override
        protected String doInBackground(final JSONObject... jsonObject) {
            String result = "";
            final StringBuilder stringBuilder = new StringBuilder();
            RequestBuilder requestBuilder = new RequestBuilder();
            String params;
            try {
                params = requestBuilder.getRegisterRequest(email).toString();
            } catch (JSONException e) {
                return result;
            }
            try {
                final ServerConnector serverConnector = new ServerConnector(
                        registerUrl, "UTF-8");
                serverConnector.addFormField("data", params);
                final List<String> response = serverConnector.finish();
                stringBuilder.setLength(0);
                for (final String line : response) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();
            } catch (IOException e) {
                result = "";
            }
            return result;
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
        protected void onPostExecute(Void result)  {
            super.onPostExecute(result);
            if(status == Status.tokenAcquired) {
                saveToken();
            } else {
                reactToError();
            }
        }

        @Override
        protected void handleToken(String token) {

        }

        @Override
        protected void postToken() {

        }

        protected void saveToken() {
            persistToPreferences(BaseActivity.ACCOUNT_TOKEN, token);
            UserProfileService.fillUserProfile(activity, token);
        }
    }
}
