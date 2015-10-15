/**
 * Created by rafael on 04.10.2015.
 */
package com.flatflatching.flatflatching.services;
import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.tasks.userTasks.GetUserInfoTask;

public final class UserProfileService {
    public static boolean userDataAcquired = false;
    private static final String USER_INFO_REQUEST_URL = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json&access_token=%s";

    private UserProfileService() {

    }
    public static void fillUserProfile(BaseActivity activity, final String authToken) {
        final String requestUrl = String.format(USER_INFO_REQUEST_URL, authToken);
        new GetUserInfoTask(activity, requestUrl).execute();
    }
}
