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
public class exitFlatTask extends AbstractGetAuthTokenTask {
    private String flatId;
    private String userEmailToDelete;
    private String deleteFlatUrl;

    public exitFlatTask(BaseActivity activity, String flatId, String deleteFlatUrl, String userEmailToDelete) {
        super(activity, activity.getUserEmail());
        this.flatId = flatId;
        this.deleteFlatUrl = deleteFlatUrl;
        this.userEmailToDelete = userEmailToDelete;
    }

    @Override
    protected final void onPostExecute(String result) {
        super.onPostExecute(result);
        if (status == Status.requestFailed || result.isEmpty()) {
            reactToError();
        } else {
            activity.checkPreConditions();
        }
    }

    @Override
    protected final void handleToken(String token) {
        String response = deleteFlat(token);
        handleFlatResponse(response);

    }

    private void handleFlatResponse(String response) {
        JSONObject res = null;
        try {
            res = new JSONObject(response);
            String responseMessage = res.getString("response");
            if (responseMessage.equals("good bye :(")) {
                status = Status.okay;
            } else {
                status = Status.requestFailed;
            }
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

    @Override
    protected void postToken() {

    }

    private String deleteFlat(final String token) {
        JSONObject params;
        String result = "";
        RequestBuilder requestBuilder = new RequestBuilder();
        try {
            params = requestBuilder.getDeleteFlatRequest(token, flatId, userEmailToDelete);

        } catch (JSONException e) {
            status = Status.requestFailed;
            return result;
        }
        try {
            result = RequestService.sendRequestWithData(ServerConnector.Method.POST, deleteFlatUrl, params);
        } catch (IOException|JSONException e) {
            status = Status.requestFailed;
        }
        return result;
    }

}
