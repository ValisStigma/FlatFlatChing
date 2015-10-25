package com.flatflatching.flatflatching.tasks.expenseTasks;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.ExceptionParser;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.ServerConnector;
import com.flatflatching.flatflatching.models.Expense;
import com.flatflatching.flatflatching.services.ExpenseService;
import com.flatflatching.flatflatching.services.RequestService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class PayBackStaticExpenseTask extends AbstractGetAuthTokenTask {
    private String flatId;
    private String expenseId;
    private String userEmail;
    private Expense.ExpenseType expenseType;
    public PayBackStaticExpenseTask(BaseActivity activity, Expense.ExpenseType expenseType, String flatId, String expenseId, String userEmail) {
        super(activity, activity.getUserEmail());
        this.flatId = flatId;
        this.expenseId = expenseId;
        this.userEmail = userEmail;
        this.expenseType = expenseType;
    }

    @Override
    protected final void handleToken(String token) {
        String response = payBackExpense(token);
        handleExpenseResponse(response);
    }

    @Override
    protected void postToken() {

    }

    private String payBackExpense(final String token) {
        JSONObject params;
        String result = "";
        RequestBuilder requestBuilder = new RequestBuilder();
        try {
            params = requestBuilder.getPayBackStaticExpenseRequest(token, flatId, expenseId, userEmail);

        } catch (JSONException e) {
            status = Status.requestFailed;
            return result;
        }
        String serverUrl = "";
        switch (this.expenseType) {
            case Static:
                serverUrl = ExpenseService.PAY_BACK_STATIC_EXPENSE_URL;
                break;
            case Variable:
                serverUrl = ExpenseService.PAY_BACK_VARIABLE_EXPENSE_URL;
                break;
        }
        try {
            result = RequestService.sendRequestWithData(ServerConnector.Method.POST, serverUrl, params);
        } catch (IOException|JSONException e) {
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
