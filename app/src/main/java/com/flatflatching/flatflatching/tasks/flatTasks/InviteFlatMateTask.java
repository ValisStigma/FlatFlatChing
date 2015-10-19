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
public class InviteFlatMateTask extends AbstractGetAuthTokenTask {
    private final String flatId;
    private final String email;

    public InviteFlatMateTask(BaseActivity activity, String url, String flatId, String email) {
        super(activity, url);
        this.flatId = flatId;
        this.email = email;
    }

    @Override
    protected final void handleToken(String token) {
        final String response = inviteFlatMate(token);
        handleInviteResponse(response);
    }

    @Override
    protected void postToken() {

    }

    private void handleInviteResponse(final String response) {
        JSONObject result = null;
        try {
            result = new JSONObject(response);
            String invite = result.getString("response");
            status = Status.okay;
        } catch (JSONException e) {
            try {
                if (result == null) {
                    status = Status.requestFailed;
                } else {
                    status = Status.requestFailed;
                    int errCode = result.getInt("error_code");
                    String exMes = ExceptionParser.EXCEPTION_MAP.get(errCode);
                    if(exMes != null) {
                        exceptionMessage = exMes;
                    }
                }
            } catch (JSONException i) {
                status = Status.requestFailed;
            }
        }

    }

    private String inviteFlatMate(final String token) {
        String params;
        String result = "";
        RequestBuilder requestBuilder = new RequestBuilder();
        try {
            params = requestBuilder.getInvitationRequest(token, flatId, email).toString();
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
    protected final void onPostExecute(final String result) {
        super.onPostExecute(result);
        if (status == Status.requestFailed) {
            reactToError();
        }
    }
}
