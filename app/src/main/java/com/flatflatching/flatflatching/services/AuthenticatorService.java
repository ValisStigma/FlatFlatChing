package com.flatflatching.flatflatching.services;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;

import java.io.IOException;

/**
 * Created by rafael on 02.10.2015.
 */
public  class AuthenticatorService {
    public static void getAuth(Activity activity, TextView messageShower, ViewGroup viewContainer, String chosenEmail) {
        new GetUserData(activity, messageShower, viewContainer, chosenEmail).execute();
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
