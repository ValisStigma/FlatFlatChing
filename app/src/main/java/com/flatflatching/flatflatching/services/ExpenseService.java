package com.flatflatching.flatflatching.services;
import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.models.StaticUserExpense;
import com.flatflatching.flatflatching.tasks.expenseTasks.CreateStaticExpenseTask;
import com.flatflatching.flatflatching.tasks.expenseTasks.CreateVariableExpenseTask;
import com.flatflatching.flatflatching.tasks.expenseTasks.GetExpensesTask;
import com.flatflatching.flatflatching.tasks.expenseTasks.PayBackStaticExpenseTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by rafael on 13.10.2015.
 */
//TODO: Change Url of api calls to payback
public final class ExpenseService {
    private static RequestBuilder requestBuilder = new RequestBuilder();
    private static final String CREATE_STATIC_EXPENSE_URL = "";
    private static final String CREATE_VARIABLE_EXPENSE_URL = "";
    private static final String PAY_BACK_STATIC_EXPENSE_URL = "";
    private static final String GET_EXPENSES_URL = "";

    private ExpenseService() {

    }
    public static void payBackStaticExpense(BaseActivity activity, String flatId, String expenseId, String userEmail) {
        new PayBackStaticExpenseTask(activity, PAY_BACK_STATIC_EXPENSE_URL, flatId, expenseId, userEmail).execute();
    }
    public static void createStaticExpense(BaseActivity activity, String flatId, String expenseName,
                                           double expenseAmount, Date expenseEnd,
                                  int expenseInterval, List<StaticUserExpense> userExpenses) {
        new CreateStaticExpenseTask(activity, CREATE_STATIC_EXPENSE_URL, flatId, expenseName,
                expenseAmount, expenseEnd, expenseInterval, userExpenses).execute();
    }

    public static void createVariableExpense(BaseActivity activity, String flatId, String expenseName,
                                             double expenseAmount, Date expenseEnd, List<String> userEmails) {
        new CreateVariableExpenseTask(activity, CREATE_VARIABLE_EXPENSE_URL, flatId, expenseName,
                expenseAmount, expenseEnd, userEmails).execute();
    }

    public static void getFlatInfo(BaseActivity activity, String flatId, String userEmail) {
        try {
            JSONObject params = requestBuilder.getExpensesRequest(flatId, userEmail);
            new GetExpensesTask(activity, GET_EXPENSES_URL).execute(params);
        } catch (JSONException e) {
            activity.notifyError(activity.getResources().getString(R.string.server_error));
        }
    }


}
