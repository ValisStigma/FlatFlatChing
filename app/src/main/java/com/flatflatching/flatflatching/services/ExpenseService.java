package com.flatflatching.flatflatching.services;
import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.models.Expense;
import com.flatflatching.flatflatching.models.StaticExpense;
import com.flatflatching.flatflatching.models.StaticUserExpense;
import com.flatflatching.flatflatching.models.VariableExpense;
import com.flatflatching.flatflatching.tasks.expenseTasks.CreateStaticExpenseTask;
import com.flatflatching.flatflatching.tasks.expenseTasks.CreateVariableExpenseTask;
import com.flatflatching.flatflatching.tasks.expenseTasks.GetExpensesTask;
import com.flatflatching.flatflatching.tasks.expenseTasks.PayBackStaticExpenseTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

//TODO: Change Url of api calls to payback
public final class ExpenseService {
    private static RequestBuilder requestBuilder = new RequestBuilder();
    public static final String CREATE_STATIC_EXPENSE_URL = String.format(BaseActivity.BASE_URL, "api/expense/create/static");
    public static final String CREATE_VARIABLE_EXPENSE_URL = String.format(BaseActivity.BASE_URL, "api/expense/create/variable");
    public static final String PAY_BACK_STATIC_EXPENSE_URL = String.format(BaseActivity.BASE_URL, "api/expense/payback/static");
    public static final String PAY_BACK_VARIABLE_EXPENSE_URL = String.format(BaseActivity.BASE_URL, "api/expense/payback/variable");
    public static final String GET_EXPENSES_URL = String.format(BaseActivity.BASE_URL, "api/get/expenses");

    private ExpenseService() {

    }
    public static void payBackStaticExpense(BaseActivity activity, String flatId, String expenseId, String userEmail, Expense.ExpenseType expenseType) {
        new PayBackStaticExpenseTask(activity, expenseType, flatId, expenseId, userEmail).execute();
    }
    public static void createStaticExpense(BaseActivity activity, String flatId, StaticExpense staticExpense, List<StaticUserExpense> userExpenses) {
        new CreateStaticExpenseTask(activity, flatId, staticExpense, userExpenses).execute();
    }

    public static void createVariableExpense(BaseActivity activity, String flatId, VariableExpense variableExpense) {
        new CreateVariableExpenseTask(activity, flatId, variableExpense).execute();
    }

    public static void getExpenses(BaseActivity activity, String flatId, String userEmail) {
        try {
            JSONObject params = requestBuilder.getExpensesRequest(flatId, userEmail);
            new GetExpensesTask(activity, GET_EXPENSES_URL).execute(params);
        } catch (JSONException e) {
            activity.notifyError(activity.getResources().getString(R.string.server_error));
        }
    }


}
