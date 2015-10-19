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
public class SetFlatAdminTask extends AbstractGetAuthTokenTask {
    private final String userEmail;

    public SetFlatAdminTask(BaseActivity activity, final String url, final String userEmail) {
        super(activity, url);
        this.userEmail = userEmail;
    }

    @Override
    protected final void handleToken(String token) {
        String response = setAdminRequest(token);
        handleSetAdminResponse(response);
    }

    private void handleSetAdminResponse(String response) {
        JSONObject res = null;
        try {
            res = new JSONObject(response);
            String state = res.getString("response");
            if (state.equals("Done")) {
                status = Status.okay;
            } else {
                status = Status.requestFailed;
            }
        } catch (JSONException e) {
            if (res == null) {
                status = Status.requestFailed;
            } else {
                try {
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

    private String setAdminRequest(String token) {
        String params;
        String result = "";
        RequestBuilder requestBuilder = new RequestBuilder();
        try {
            params = requestBuilder.getSetFlatAdminRequest(token, userEmail).toString();

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
