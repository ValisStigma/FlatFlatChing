package com.flatflatching.flatflatching.tasks.expenseTasks;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.ExceptionParser;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.ServerConnector;
import com.flatflatching.flatflatching.models.FlatMate;
import com.flatflatching.flatflatching.models.VariableExpense;
import com.flatflatching.flatflatching.services.ExpenseService;
import com.flatflatching.flatflatching.services.RequestService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


/**
 * Created by rafael on 14.10.2015.
 */
public class CreateVariableExpenseTask extends AbstractGetAuthTokenTask {
    private String flatId;
    private VariableExpense variableExpense;

    public CreateVariableExpenseTask(BaseActivity activity, String flatId,
                                     VariableExpense variableExpense) {
        super(activity, activity.getUserEmail());
        this.flatId = flatId;
        this.variableExpense = variableExpense;
    }

    @Override
    protected final void handleToken(String token) {
        String response = registerExpense(token);
        handleExpenseResponse(response);
    }

    @Override
    protected void postToken() {

    }

    private String registerExpense(final String token) {
        String params;
        String result = "";
        RequestBuilder requestBuilder = new RequestBuilder();
        try {
            JSONArray userExpenses = new JSONArray();
            for (FlatMate contributor : this.variableExpense.getContributors()) {
                userExpenses.put(requestBuilder.getUserEmail(contributor.getName()));
            }
            params = requestBuilder.getCreateVariableExpenseRequest(token,
                    activity.getUserEmail(), flatId, variableExpense, userExpenses).toString();

        } catch (JSONException e) {
            status = Status.requestFailed;
            return result;
        }
        try {
            result = RequestService.sendRequestWithData(ServerConnector.Method.POST, ExpenseService.CREATE_VARIABLE_EXPENSE_URL, params);
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
