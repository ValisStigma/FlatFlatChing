package com.flatflatching.flatflatching.tasks.flatTasks;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.ExceptionParser;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.ServerConnector;
import com.flatflatching.flatflatching.services.RequestService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by rafael on 13.10.2015.
 */
public class AnswerInvitationTask extends AbstractGetAuthTokenTask {
    private String flatId;
    private String userEmail;
    private String adminEmail;
    private boolean accept;

    public AnswerInvitationTask(BaseActivity activity, String url, String flatId, String userEmail, String adminEmail, boolean accept) {
        super(activity, url);
        this.flatId = flatId;
        this.userEmail = userEmail;
        this.adminEmail = adminEmail;
        this.accept = accept;
    }

    @Override
    protected final void handleToken(String token) {
        String response = answerInvitation(token);
        handleInvitationResponse(response);
    }

    private void handleInvitationResponse(String response) {
        JSONObject res = null;
        try {
            res = new JSONObject(response);
            String flatId = res.getString("flat_uuid");
            if (!flatId.isEmpty()) {
                status = Status.okay;
            } else {
                status = Status.requestFailed;
            }
        } catch (JSONException e) {
            if (res == null) {
                status = Status.requestFailed;
            } else {
                try {
                    status = Status.requestFailed;
                    int errCode = res.getInt("error_code");
                    String exMes = ExceptionParser.EXCEPTION_MAP.get(errCode);
                    if(exMes != null) {
                        exceptionMessage = exMes;
                    }
                } catch (JSONException i) {
                    status = Status.requestFailed;
                }
            }
        }
    }

    private String answerInvitation(String token) {
        String params;
        String result = "";
        RequestBuilder requestBuilder = new RequestBuilder();
        try {
            params = requestBuilder.getAnswerInvitationRequest(token, flatId, userEmail, adminEmail, accept).toString();

        } catch (JSONException e) {
            status = Status.requestFailed;
            return result;
        }
        try {
            result = RequestService.sendRequestWithData(ServerConnector.Method.POST, url, params);
        } catch (IOException e) {
            status = Status.requestFailed;
        }
        return result;
    }

    @Override
    protected void postToken() {

    }
}
