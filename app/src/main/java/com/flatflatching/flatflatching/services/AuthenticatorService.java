package com.flatflatching.flatflatching.services;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    public static void getAuth(Activity activity, TextView messageShower, ViewGroup viewContainer, String chosenEmail) {
        new GetUserData(activity, messageShower, viewContainer, chosenEmail).execute();
    }

    public static void register(Activity activity, TextView messageShower, ViewGroup viewContainer, String chosenEmail) {
        new RegisterUser(activity, messageShower, viewContainer, chosenEmail);
    }

    private static class RegisterUser extends AbstractAsyncTask {
        private final String registerUrl = "";
        private final String email;
        public RegisterUser(Activity activity, TextView messageShower, ViewGroup viewContainer, String chosenEmail) {
            super(activity, messageShower, viewContainer, chosenEmail);
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
                        SharedPreferences preferences = getContext().getSharedPreferences(BaseActivity.PREFERENCES, 0);
                        final SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(BaseActivity.FLAT_ID, flatId);
                        editor.apply();
                    }
                } catch (JSONException e) {
                    reactToError();
                }
            }
        }
    }

    private static class GetUserData extends AbstractGetAuthTokenTask {

        GetUserData(Activity activity, TextView messageShower, ViewGroup viewContainer, String url){
            super(activity, messageShower, viewContainer, url);
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
            SharedPreferences preferences = activity.getSharedPreferences(BaseActivity.PREFERENCES, 0);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putString(BaseActivity.ACCOUNT_TOKEN, token);
            editor.apply();
            try {
                UserProfileService.fillUserProfile(activity, getMessageShower(), getViewContainer(), token);
            } catch(IOException e) {
                reactToError();
            }
        }
    }
}
