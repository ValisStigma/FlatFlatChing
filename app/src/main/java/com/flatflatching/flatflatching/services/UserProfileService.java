/**
 * Created by rafael on 04.10.2015.
 */
package com.flatflatching.flatflatching.services;
import android.content.SharedPreferences;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.BaseRequest;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class UserProfileService {
    public static boolean userDataAcquired = false;
    private static final String USER_INFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=%s";
    private static BaseActivity callingActivity;

    public static void fillUserProfile(BaseActivity activity, final String authToken) {
        callingActivity = activity;
        final String requestUrl = String.format(USER_INFO_REQUEST_URL, authToken);
        new GetUserInfoTask(activity, requestUrl).execute();
    }
    private static class GetUserInfoTask extends AbstractAsyncTask {
        GetUserInfoTask(BaseActivity context, String url) {
            super(context, url);
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
        callingActivity.persistToPreferences(BaseActivity.USER_NAME, name);
        callingActivity.persistToPreferences(BaseActivity.USER_NAME_FAMILY, familyName);
        callingActivity.persistToPreferences(BaseActivity.USER_NAME_GIVEN, givenName);
        callingActivity.persistToPreferences(BaseActivity.USER_PROFILE_LINK, profileLink);
        callingActivity.persistToPreferences(BaseActivity.USER_IMAGE_URL, profileImageUrl);
    }
}
