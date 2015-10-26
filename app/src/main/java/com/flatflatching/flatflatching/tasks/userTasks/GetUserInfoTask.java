package com.flatflatching.flatflatching.tasks.userTasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;
import com.flatflatching.flatflatching.helpers.SerialBitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

public class GetUserInfoTask extends AbstractAsyncTask {
    public GetUserInfoTask(BaseActivity activity, String url) {
        super(activity, url);
    }

    @Override
    protected final void onPostExecute(final String result) {
        if (result.isEmpty()) {
            reactToError();
        }
    }
    @Override
    protected void handleResponse(String response) {
        try {
            saveProfileInfo(new JSONObject(response));

        } catch(JSONException e) {
            status = Status.requestFailed;
        }
    }
    private void saveProfileInfo(JSONObject userProfile) throws JSONException {
        final String familyName = userProfile.getString("family_name");
        final String givenName = userProfile.getString("given_name");
        final String name = userProfile.getString("name");
        final String profileLink = userProfile.getString("link");
        final String profileImageUrl = userProfile.getString("picture");
        try {
            InputStream in = new java.net.URL(profileImageUrl).openStream();
            SerialBitmap profileImage = new SerialBitmap(in);
            activity.persistObject(BaseActivity.PROFILE_BITMAP, profileImage);
        } catch (Exception e) {
            Log.d("parse_error", "unable to parse Stream");
        }
        activity.persistToPreferences(BaseActivity.USER_NAME, name);
        activity.persistToPreferences(BaseActivity.USER_NAME_FAMILY, familyName);
        activity.persistToPreferences(BaseActivity.USER_NAME_GIVEN, givenName);
        activity.persistToPreferences(BaseActivity.USER_PROFILE_LINK, profileLink);
        activity.persistToPreferences(BaseActivity.USER_IMAGE_URL, profileImageUrl);
    }
}
