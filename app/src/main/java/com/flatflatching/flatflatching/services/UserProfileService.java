/**
 * Created by rafael on 04.10.2015.
 */
package com.flatflatching.flatflatching.services;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.BaseRequest;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class UserProfileService {
    public static boolean userDataAcquired = false;
    private static final String USER_INFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=%s";
    private static Activity callingActivity;

    public static void fillUserProfile(Activity activity, TextView messageShower, ViewGroup viewContainer, final String authToken) {
        callingActivity = activity;
        final String requestUrl = String.format(USER_INFO_REQUEST_URL, authToken);

        new GetUserInfoTask(activity, messageShower, viewContainer, requestUrl).execute();
    }
    private static class GetUserInfoTask extends AbstractAsyncTask {
        GetUserInfoTask(Context context, TextView textView, ViewGroup viewGroup, String url) {
            super(context, textView, viewGroup, url);
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            String result;
            try {
                result = new BaseRequest(url).openRequest().finish();
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
                    saveProfileInfo(new JSONObject(result));
                    userDataAcquired = true;
                } catch (JSONException e) {
                    reactToError();
                }
            }
        }
    }

    private static void saveProfileInfo(JSONObject userProfile) throws JSONException {
        final String familyName = userProfile.getString("family_name");
        final String givenName = userProfile.getString("given_name");
        final String name = userProfile.getString("name");
        final String profileLink = userProfile.getString("link");
        final String profileImageUrl = userProfile.getString("picture");
        SharedPreferences preferences = callingActivity.getSharedPreferences(BaseActivity.PREFERENCES, 0);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putString(BaseActivity.USER_NAME, name);
        editor.putString(BaseActivity.USER_NAME_FAMILY, familyName);
        editor.putString(BaseActivity.USER_NAME_GIVEN, givenName);
        editor.putString(BaseActivity.USER_PROFILE_LINK, profileLink);
        editor.putString(BaseActivity.USER_IMAGE_URL, profileImageUrl);
        editor.apply();
    }
}
