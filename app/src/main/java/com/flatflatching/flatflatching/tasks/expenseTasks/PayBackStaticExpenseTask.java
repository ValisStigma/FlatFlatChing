package com.flatflatching.flatflatching.tasks.expenseTasks;

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
 * Created by rafael on 14.10.2015.
 */
public class PayBackStaticExpenseTask extends AbstractGetAuthTokenTask {
    private String flatId;
    private String expenseId;
    private String userEmail;

    public PayBackStaticExpenseTask(BaseActivity activity, String payBackStaticExpenseUrl, String flatId, String expenseId, String userEmail) {
        super(activity, payBackStaticExpenseUrl);
        this.flatId = flatId;
        this.expenseId = expenseId;
        this.userEmail = userEmail;
    }

    @Override
    protected void handleToken(String token) {
        String response = payBackExpense(token);
        handleExpenseResponse(response);
    }

    @Override
    protected void postToken() {

    }

    private String payBackExpense(final String token) {
        String params;
        String result = "";
        RequestBuilder requestBuilder = new RequestBuilder();
        try {
            params = requestBuilder.getPayBackStaticExpenseRequest(token, flatId, expenseId, userEmail).toString();

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

    private void handleExpenseResponse(String response) {
        JSONObject res = null;
        try {
            res = new JSONObject(response);
            String done = res.getString("response");
            if (done.equals("Done!")) {
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
}
