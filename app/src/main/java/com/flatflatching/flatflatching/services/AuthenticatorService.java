package com.flatflatching.flatflatching.services;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by rafael on 02.10.2015.
 */
public  class AuthenticatorService {
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    private static  String accountName;
    public static void getAuth(Activity activity, TextView messageShower, ViewGroup viewContainer, String aName) {
        accountName = aName;
        new GetUserData(activity, messageShower, viewContainer, SCOPE).execute();
    }
    private static class GetUserData extends AbstractAsyncTask {
        Activity activity;
        String status;
        String token;

        GetUserData(Activity activity, TextView messageShower, ViewGroup viewContainer, String url){
            super(activity, messageShower, viewContainer, url);
            this.activity = activity;
        }

        @Override
        protected String doInBackground(JSONObject... params){
            try{
                token = getAccessToken(this.activity, accountName, SCOPE);
                if(token != null){
                    //Access Token sent
                    status = "Access Token Acquired";
                } else {
                    reactToError();
                }
            }
            catch (Exception ex){
                reactToError();
            }
            return token;
        }

        public String getAccessToken(Activity activity,String accountName, String scope){
            try{
                return GoogleAuthUtil.getToken(activity, accountName, scope);
            }
            catch(UserRecoverableAuthException userRecoverableError){
                status = "User Recoverable Error";
                activity.startActivityForResult(userRecoverableError.getIntent(), BaseActivity.REQUEST_PERMISSION);
            }
            catch(GoogleAuthException googleAuthException){
                status = "Google Auth Exception";
                reactToError();
            }
            catch(IOException ioException) {
                status = "IO Exception";
                reactToError();
            }
            return null;
        }


        protected void onPostExecute(Void result)  {
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
