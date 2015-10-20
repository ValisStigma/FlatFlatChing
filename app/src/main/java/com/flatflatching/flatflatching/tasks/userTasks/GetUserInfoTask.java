package com.flatflatching.flatflatching.tasks.userTasks;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

public class GetUserInfoTask extends AbstractAsyncTask {
    public GetUserInfoTask(BaseActivity activity, String url) {
        super(activity, url);
    }

    @Override
    protected final void onPostExecute(final String result) {
        if (result.isEmpty()) {
            reactToError();
        } else {
            try {
                saveProfileInfo(new JSONObject(result));
                activity.checkPreConditions();
            } catch (JSONException e) {
                reactToError();
            }
        }
    }

    private void saveProfileInfo(JSONObject userProfile) throws JSONException {
        final String familyName = userProfile.getString("family_name");
        final String givenName = userProfile.getString("given_name");
        final String name = userProfile.getString("name");
        final String profileLink = userProfile.getString("link");
        final String profileImageUrl = userProfile.getString("picture");
        activity.persistToPreferences(BaseActivity.USER_NAME, name);
        activity.persistToPreferences(BaseActivity.USER_NAME_FAMILY, familyName);
        activity.persistToPreferences(BaseActivity.USER_NAME_GIVEN, givenName);
        activity.persistToPreferences(BaseActivity.USER_PROFILE_LINK, profileLink);
        activity.persistToPreferences(BaseActivity.USER_IMAGE_URL, profileImageUrl);
    }
}
