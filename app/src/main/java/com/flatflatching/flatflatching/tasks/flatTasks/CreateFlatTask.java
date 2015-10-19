package com.flatflatching.flatflatching.tasks.flatTasks;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.ExceptionParser;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.ServerConnector;
import com.flatflatching.flatflatching.models.Flat;
import com.flatflatching.flatflatching.services.RequestService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by rafael on 13.10.2015.
 */
public class CreateFlatTask extends AbstractGetAuthTokenTask {
    private Flat flat;
    private String createFlatUrl;
    public CreateFlatTask(BaseActivity activity, String ownEmail, String url, Flat flat) {
        super(activity, ownEmail);
        this.flat = flat;
        this.createFlatUrl = url;
    }

    @Override
    protected final void handleToken(final String token) {
        String response = registerFlat(token);
        handleFlatResponse(response);
    }

    @Override
    protected void postToken() {

    }
    @Override
    protected final void onPostExecute(String result)  {
        super.onPostExecute(result);
        if(status == Status.okay) {
            activity.reactToSuccess();
        } else {
            activity.notifyError(exceptionMessage);
        }
    }

    private void handleFlatResponse(String response) {
        JSONObject res = null;
        try {
            res = new JSONObject(response);
            String flatId = res.getString("flat_uuid");
            persistToPreferences(BaseActivity.FLAT_ID, flatId);
            status = Status.okay;
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

    private String registerFlat(final String token) {
        String params;
        String result = "";
        RequestBuilder requestBuilder = new RequestBuilder();
        try {
            params = requestBuilder.getCreateFlatRequest(token, flat).toString();

        } catch (JSONException e) {
            status = Status.requestFailed;
            return result;
        }
        try {
            result = RequestService.sendRequestWithData(ServerConnector.Method.POST, createFlatUrl, params);
        } catch (IOException e) {
            status = Status.requestFailed;
            return result;
        }
        return result;
    }

}
