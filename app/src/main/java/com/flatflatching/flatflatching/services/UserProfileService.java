
/**
 * Created by rafael on 04.10.2015.
 */
package com.flatflatching.flatflatching.services;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.BaseRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;

public class UserProfileService {
    private static JSONObject userProfile;
    private static Bitmap userProfileBitmap;
    public static boolean userDataAcquired = false;
    private static final String USER_INFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=%s";
    private static Activity callingActivity;

    public static void fillUserProfile(Activity activity, final String authToken) {
        String[] params =  {authToken};
        callingActivity = activity;
        new GetUserInfoTask().execute(params);
    }
    private static class GetUserInfoTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result;
            final String requestUrl = String.format(USER_INFO_REQUEST_URL, params[0]);
            try {
                result = new BaseRequest(requestUrl).openRequest().finish();
            } catch (IOException e) {
                result = "";
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            if (result.isEmpty()) {
                //TODO: React to error
            } else {
                try {
                    saveProfileInfo(new JSONObject(result));
                    userDataAcquired = true;
                } catch (JSONException e) {
                    //TODO: React to error
                }
            }
        }
    }

    private static void saveProfileInfo(JSONObject userProfile) throws JSONException {
        final String familyName = userProfile.getString("family_name");
        final String givenName = userProfile.getString("given_name");
        final String name = userProfile.getString("name");
        final String profileLink = userProfile.getString("link");
        userProfileBitmap = getBitMap(userProfile.getString("picture"));

        SharedPreferences preferences = callingActivity.getSharedPreferences(BaseActivity.PREFERENCES, 0);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BaseActivity.USER_NAME, name);
        editor.putString(BaseActivity.USER_NAME_FAMILY, familyName);
        editor.putString(BaseActivity.USER_NAME_GIVEN, givenName);
        editor.putString(BaseActivity.USER_PROFILE_LINK, profileLink);
        editor.apply();

    }
    public static Bitmap getBitMap(String url){
        Bitmap bitmapImage = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            bitmapImage = BitmapFactory.decodeStream(in);
            return bitmapImage;
        } catch (Exception e) {
            //TODO: React to error
        }
        return bitmapImage;
    }
}
