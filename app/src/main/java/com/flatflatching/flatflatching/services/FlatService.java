package com.flatflatching.flatflatching.services;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.models.Flat;
import com.flatflatching.flatflatching.tasks.flatTasks.AnswerInvitationTask;
import com.flatflatching.flatflatching.tasks.flatTasks.CreateFlatTask;
import com.flatflatching.flatflatching.tasks.flatTasks.DeleteFlatTask;
import com.flatflatching.flatflatching.tasks.flatTasks.GetFlatMemberInfoTask;
import com.flatflatching.flatflatching.tasks.flatTasks.GetFlatInfoTask;
import com.flatflatching.flatflatching.tasks.flatTasks.InviteFlatMateTask;
import com.flatflatching.flatflatching.tasks.flatTasks.SetFlatAdminTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rafael on 05.10.2015.
 */

public final class FlatService {
    public static final String CREATE_FLAT_URL = String.format(BaseActivity.BASE_URL, "api/create");
    public static final String INVITE_URL = String.format(BaseActivity.BASE_URL, "api/invite");
    public static final String GET_FLAT_INFO_URL = String.format(BaseActivity.BASE_URL, "api/get/flat");
    public static final String GET_FLAT_MEMBER_INFO_URL = String.format(BaseActivity.BASE_URL, "api/get/flat/members");
    public static final String SET_FLAT_ADMIN_URL = String.format(BaseActivity.BASE_URL, "api/set/admin");
    public static final String DELETE_FLAT_URL = String.format(BaseActivity.BASE_URL, "api/exit");
    public static final String ANSWER_INVITATION_URL = String.format(BaseActivity.BASE_URL, "api/accept/invite");

    private FlatService() {

    }
    private static RequestBuilder requestBuilder = new RequestBuilder();

    public static void createFlat(final BaseActivity activity, final String ownEmail, final Flat flat) {
        new CreateFlatTask(activity, ownEmail, CREATE_FLAT_URL, flat).execute();
    }

    public static void getFlatInfo(BaseActivity activity, String flatId) {
        try {
            JSONObject params = requestBuilder.getFlatInfoRequest(flatId, activity.getUserEmail());
            new GetFlatInfoTask(activity, GET_FLAT_INFO_URL).execute(params);
        } catch (JSONException e) {
            activity.notifyError(activity.getResources().getString(R.string.server_error));
        }
    }

    public static void getFlatMemberInfo(BaseActivity activity, String flatId) {
        try{
            JSONObject params = requestBuilder.getFlatInfoRequest(flatId, activity.getUserEmail());
            new GetFlatMemberInfoTask(activity, GET_FLAT_MEMBER_INFO_URL).execute(params);
        } catch (JSONException e) {
            activity.notifyError(activity.getResources().getString(R.string.server_error));
        }
    }
    public static void inviteFlatMate(BaseActivity activity, String flatId, String email) {
        new InviteFlatMateTask(activity, INVITE_URL, flatId, email).execute();
    }

    public static void setAdmin(BaseActivity activity, String userEmail) {
        new SetFlatAdminTask(activity, SET_FLAT_ADMIN_URL, userEmail).execute();
    }

    public static void deleteFlat(BaseActivity activity, String flatId) {
        new DeleteFlatTask(activity, flatId, DELETE_FLAT_URL).execute();
    }

    public static void answerInvitation(BaseActivity activity, String userEmail, String flatAdminEmail, String flatId, boolean accept)  {
        new AnswerInvitationTask(activity, ANSWER_INVITATION_URL, flatId, userEmail, flatAdminEmail,accept).execute();
    }

}
