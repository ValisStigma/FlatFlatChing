package com.flatflatching.flatflatching.tasks.expenseTasks;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.ExceptionParser;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.ServerConnector;
import com.flatflatching.flatflatching.models.StaticExpense;
import com.flatflatching.flatflatching.models.StaticUserExpense;
import com.flatflatching.flatflatching.services.ExpenseService;
import com.flatflatching.flatflatching.services.RequestService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateStaticExpenseTask extends AbstractGetAuthTokenTask {
    private String flatId;
    private StaticExpense staticExpense;
    private List<StaticUserExpense> userExpenses;

    public CreateStaticExpenseTask(BaseActivity activity, String flatId, StaticExpense staticExpense, List<StaticUserExpense> userExpenses) {
        super(activity, activity.getUserEmail());
        this.flatId = flatId;
        this.staticExpense = staticExpense;
        this.userExpenses = new ArrayList<>(userExpenses);
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

    @Override
    protected final void handleToken(final String token) {
        String response = registerExpense(token);
        handleExpenseResponse(response);
    }

    @Override
    protected void postToken() {

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

    private String registerExpense(final String token) {
        JSONObject params;
        String result = "";
        RequestBuilder requestBuilder = new RequestBuilder();
        try {
            JSONArray userExpenses = new JSONArray();
            for (StaticUserExpense expense : this.userExpenses) {
                userExpenses.put(requestBuilder.getStaticUserObject(expense.getEmail(), expense.getDivisionKey()));
            }
            params = requestBuilder.getCreateStaticExpenseRequest(token, flatId, activity.getUserEmail(),
                    staticExpense, userExpenses);

        } catch (JSONException e) {
            status = Status.requestFailed;
            return result;
        }
        try {
            result = RequestService.sendRequestWithData(ServerConnector.Method.POST, ExpenseService.CREATE_STATIC_EXPENSE_URL, params);
        } catch (IOException| JSONException e) {
            status = Status.requestFailed;
        }
        return result;
    }

}
