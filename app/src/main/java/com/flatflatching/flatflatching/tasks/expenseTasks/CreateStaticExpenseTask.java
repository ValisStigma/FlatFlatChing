package com.flatflatching.flatflatching.tasks.expenseTasks;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.ExceptionParser;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.ServerConnector;
import com.flatflatching.flatflatching.models.StaticUserExpense;
import com.flatflatching.flatflatching.services.RequestService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rafael on 14.10.2015.
 */
public class CreateStaticExpenseTask extends AbstractGetAuthTokenTask {
    private String flatId;
    private String expenseName;
    private double expenseAmount;
    private Date expenseEnd;
    private int expenseInterval;
    private List<StaticUserExpense> userExpenses;

    public CreateStaticExpenseTask(BaseActivity activity, String createExpenseUrl, String flatId, String expenseName, double expenseAmount,
                                   Date expenseEnd, int expenseInterval, List<StaticUserExpense> userExpenses) {
        super(activity, createExpenseUrl);
        this.flatId = flatId;
        this.expenseName = expenseName;
        this.expenseAmount = expenseAmount;
        this.expenseEnd = expenseEnd;
        this.expenseInterval = expenseInterval;
        this.userExpenses = new ArrayList<>(userExpenses);
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
        String params;
        String result = "";
        RequestBuilder requestBuilder = new RequestBuilder();
        try {
            JSONArray userExpenses = new JSONArray();
            for (StaticUserExpense expense : this.userExpenses) {
                userExpenses.put(requestBuilder.getStaticUserObject(expense.getEmail(), expense.getDivisionKey()));
            }
            params = requestBuilder.getCreateStaticExpenseRequest(token, flatId, expenseName,
                    expenseAmount, expenseEnd, expenseInterval, userExpenses).toString();

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

}
