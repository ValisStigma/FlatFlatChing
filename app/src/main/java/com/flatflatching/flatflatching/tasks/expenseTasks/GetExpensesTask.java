package com.flatflatching.flatflatching.tasks.expenseTasks;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;
import com.flatflatching.flatflatching.models.Expense;
import com.flatflatching.flatflatching.models.StaticExpense;
import com.flatflatching.flatflatching.models.VariableExpense;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rafael on 14.10.2015.
 */
public class GetExpensesTask extends AbstractAsyncTask {

    public GetExpensesTask(BaseActivity activity, String url) {
        super(activity, url);
    }

    @Override
    protected final void onPostExecute(String result) {
        super.onPostExecute(result);
        if (status == Status.requestFailed || result.isEmpty()) {
            reactToError();
        } else {
            try {
                persistExpenses(result);
            } catch (JSONException e) {
                exceptionMessage = "Infos über Ausgaben nicht einholbar";
                reactToError();
            }
        }
    }

    private void persistExpenses(String res) throws JSONException {
        ArrayList<Expense> expenseList = new ArrayList<>();
        JSONArray expenses = new JSONArray(res);
        for (int i = 0; i < expenses.length(); i++) {
            JSONObject jsonExpense = expenses.getJSONObject(i);
            final String expenseTypeString = jsonExpense.getString("expense_type");
            Expense expense;
            switch (expenseTypeString) {
                case "variable":
                    expense = new VariableExpense(jsonExpense);
                    break;
                case "static":
                    expense = new StaticExpense(jsonExpense);
                    break;
                default:
                    throw new UnsupportedOperationException("not implemented");
            }
            expenseList.add(expense);

        }
    }

}
