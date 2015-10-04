package com.flatflatching.flatflatching.services;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;

/**
 * Created by rafael on 02.10.2015.
 */
public  class AuthenticatorService {
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    public static void getAuth(Activity activity, String accountName) {
        new GetUserData(activity, accountName).execute();
    }
    private static class GetUserData extends AsyncTask<Void,Void,Void> {
        Activity activity;
        String accountName;
        String status;
        String token;

        GetUserData(Activity activity, String accountName){
            this.activity = activity;
            this.accountName = accountName; //Account Name is email.
        }


        @Override
        protected Void doInBackground(Void... params){
            try{
                token = getAccessToken(this.activity,this.accountName, SCOPE);
                if(token != null){
                    //Access Token sent
                    status = "Access Token Acquired";
                }
            }
            catch (Exception ex){
                status = ex.getMessage();
            }
            return null;
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
            }
            catch(IOException ioException) {
                status = "IO Exception";
            }
            return null;
        }


        protected void onPostExecute(Void result) {
            SharedPreferences preferences = activity.getSharedPreferences(BaseActivity.PREFERENCES, 0);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putString(BaseActivity.ACCOUNT_TOKEN, token);
            editor.apply();
            UserProfileService.fillUserProfile(activity, token);
        }

    }
}
