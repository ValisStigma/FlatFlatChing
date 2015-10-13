package com.flatflatching.flatflatching.services;
import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.ServerConnector;
import com.flatflatching.flatflatching.models.Expense;
import com.flatflatching.flatflatching.models.FlatMate;
import com.flatflatching.flatflatching.models.StaticExpense;
import com.flatflatching.flatflatching.models.StaticUserExpense;
import com.flatflatching.flatflatching.models.VariableExpense;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rafael on 13.10.2015.
 */
//TODO: Change Url of api calls to payback
public class ExpenseService {
    private static RequestBuilder requestBuilder = new RequestBuilder();
    private static String createStaticExpenseUrl = "";
    private static String createVariableExpenseUrl = "";
    private static String payBackStaticExpenseUrl = "";
    private static String getExpensesUrl = "";

    public static void payBackStaticExpense(BaseActivity activity, String flatId, String expenseId, String userEmail) {
        new PayBackStaticExpenseTask(activity, flatId, expenseId, userEmail).execute();
    }
    public static void createStaticExpense(BaseActivity activity, String flatId, String expenseName,
                                           double expenseAmount, Date expenseEnd,
                                  int expenseInterval, List<StaticUserExpense> userExpenses) {
        new CreateStaticExpenseTask(activity, flatId, expenseName,
                expenseAmount, expenseEnd, expenseInterval, userExpenses).execute();
    }

    public static void createVariableExpense(BaseActivity activity, String flatId, String expenseName,
                                             double expenseAmount, Date expenseEnd, List<String> userEmails) {
        new CreateVariableExpenseTask(activity, createVariableExpenseUrl, flatId, expenseName,
                expenseAmount, expenseEnd, userEmails).execute();
    }

    public static void getFlatInfo(BaseActivity activity, String flatId, String userEmail) {
        try {
            JSONObject params = requestBuilder.getExpensesRequest(flatId, userEmail);
            new GetExpensesTask(activity, getExpensesUrl).execute(params);
        } catch (JSONException e) {
            activity.notifyError(R.string.server_error);
        }
    }

    private static class CreateStaticExpenseTask extends AbstractGetAuthTokenTask {
        private String flatId;
        private String expenseName;
        private double expenseAmount;
        private Date expenseEnd;
        private int expenseInterval;
        private List<StaticUserExpense> userExpenses;

        public CreateStaticExpenseTask(BaseActivity activity, String flatId, String expenseName, double expenseAmount,
                                       Date expenseEnd, int expenseInterval, List<StaticUserExpense> userExpenses) {
            super(activity, createStaticExpenseUrl);
            this.flatId = flatId;
            this.expenseName = expenseName;
            this.expenseAmount = expenseAmount;
            this.expenseEnd = expenseEnd;
            this.expenseInterval = expenseInterval;
            this.userExpenses = new ArrayList<>(userExpenses);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @Override
        protected void handleToken(final String token) {
            String response = registerExpense(token);
            handleExpenseResponse(response);
        }

        @Override
        protected void postToken() {

        }

        private void handleExpenseResponse(String response) {
            JSONObject res = null;
            try{
                res = new JSONObject(response);
                String done = res.getString("response");
                if(done.equals("Done!")) {
                    status = Status.okay;
                } else {
                    status = Status.requestFailed;

                }
            } catch (JSONException e) {
                if(res == null) {
                    status = Status.requestFailed;
                } else {
                    try {
                        int errCode = res.getInt("error_code");
                        switch (errCode) {
                            case 1:
                                exceptionMessage = "Authentifizierung fehlgeschlagen";
                                break;
                            case 11:
                                exceptionMessage = "Ungültige Useremail";
                                break;
                            case 17:
                                exceptionMessage = "Verteilschlüssel sind ungültig";
                                break;
                            case 202:
                                exceptionMessage = "WG existiert nicht";
                                break;
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
            try {
                JSONArray userExpenses = new JSONArray();
                for(StaticUserExpense expense: this.userExpenses) {
                    userExpenses.put(requestBuilder.getStaticUserObject(expense.getEmail(), expense.getDivisionKey()));
                }
                params = requestBuilder.getCreateStaticExpenseRequest(token, flatId, expenseName,
                        expenseAmount, expenseEnd, expenseInterval, userExpenses).toString();

            } catch (JSONException e) {
                status = Status.requestFailed;
                return result;
            }
            try {
                result = RequestService.sendRequestWithData(ServerConnector.Method.POST, createStaticExpenseUrl, params);
            } catch (IOException e) {
                status = Status.requestFailed;
            }
            return result;
        }

    }

    private static class CreateVariableExpenseTask extends AbstractGetAuthTokenTask{
        private String flatId;
        private String expenseName;
        private double expenseAmount;
        private Date expenseEnd;
        private List<String> userEmails;

        public CreateVariableExpenseTask(BaseActivity activity, String createStaticExpenseUrl, String flatId, String expenseName, double expenseAmount, Date expenseEnd, List<String> userEmails) {
            super(activity, createStaticExpenseUrl);
            this.flatId = flatId;
            this.expenseName = expenseName;
            this.expenseAmount = expenseAmount;
            this.expenseEnd = expenseEnd;
            this.userEmails = new ArrayList<>(userEmails);
        }

        @Override
        protected void handleToken(String token) {
            String response = registerExpense(token);
            handleExpenseResponse(response);
        }

        @Override
        protected void postToken() {

        }

        private String registerExpense(final String token) {
            String params;
            String result = "";
            try {
                JSONArray userExpenses = new JSONArray();
                for(String email: this.userEmails) {
                    userExpenses.put(requestBuilder.getUserEmail(email));
                }
                params = requestBuilder.getCreateVariableExpenseRequest(token, flatId, expenseName,
                        expenseAmount, expenseEnd, userExpenses).toString();

            } catch (JSONException e) {
                status = Status.requestFailed;
                return result;
            }
            try {
                result = RequestService.sendRequestWithData(ServerConnector.Method.POST, createVariableExpenseUrl, params);
            } catch (IOException e) {
                status = Status.requestFailed;
            }
            return result;
        }

        private void handleExpenseResponse(String response) {
            JSONObject res = null;
            try{
                res = new JSONObject(response);
                String done = res.getString("response");
                if(done.equals("Done!")) {
                    status = Status.okay;
                } else {
                    status = Status.requestFailed;

                }
            } catch (JSONException e) {
                if(res == null) {
                    status = Status.requestFailed;
                } else {
                    try {
                        int errCode = res.getInt("error_code");
                        switch (errCode) {
                            case 1:
                                exceptionMessage = "Authentifizierung fehlgeschlagen";
                                break;
                            case 11:
                                exceptionMessage = "Ungültige Useremail";
                                break;
                            case 202:
                                exceptionMessage = "WG existiert nicht";
                                break;
                        }
                    } catch (JSONException i) {
                        status = Status.requestFailed;
                    }
                }
            }
        }

    }


    private static class GetExpensesTask extends AbstractAsyncTask {

        public GetExpensesTask(BaseActivity activity, String url) {
            super(activity, url);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(status == Status.requestFailed || result.isEmpty()) {
                reactToError();
            } else {
                try {
                    persistExpenses(result);
                } catch (JSONException e) {
                    exceptionMessage = "Infos über Ausgaben nicht einholbbar";
                    reactToError();
                }
            }
        }

        private void persistExpenses(String res) throws JSONException {
            ArrayList<Expense> expenseList = new ArrayList<>();
            JSONArray expenses = new JSONArray(res);
            for(int i = 0; i < expenses.length(); i++) {
                JSONObject jsonExpense = expenses.getJSONObject(i);
                final String expenseName = jsonExpense.getString("expense_name");
                final Date expenseEnd = new Date((long)1000*jsonExpense.getInt("expense_end"));
                final double expenseAmount = jsonExpense.getDouble("expense_amount");
                final double amountToPay = jsonExpense.getDouble("expense_user_amount");
                final String expenseTypeString = jsonExpense.getString("expense_type");
                final Expense.ExpenseType expenseType;
                switch (expenseTypeString) {
                    case "variable":
                        expenseType = Expense.ExpenseType.Variable;
                        break;
                    case "static":
                        expenseType = Expense.ExpenseType.Static;
                        break;
                    default:
                        throw new UnsupportedOperationException("not implemented");
                }
                final JSONArray expenseContributors = jsonExpense.getJSONArray("expense_users");
                final ArrayList<FlatMate> contributors = new ArrayList<>();
                for(int j = 0; j < expenseContributors.length(); j++) {
                    final JSONObject contributor = expenseContributors.getJSONObject(j);
                    final String userEmail = contributor.getString("user_email");
                    contributors.add(new FlatMate(userEmail, false));
                }
                Expense expense;
                switch (expenseType) {
                    case Variable:
                        expense = new VariableExpense(expenseName, expenseEnd, expenseAmount, amountToPay, contributors);
                        break;
                    case Static:
                        expense = new StaticExpense(expenseName, expenseEnd, expenseAmount, amountToPay, contributors);
                        break;
                    default:
                        throw new UnsupportedOperationException("Not implemented");
                }
                expenseList.add(expense);

            }
        }

    }

    private static class PayBackStaticExpenseTask extends AbstractGetAuthTokenTask{
        private String flatId;
        private String expenseId;
        private String userEmail;
        public PayBackStaticExpenseTask(BaseActivity activity, String flatId, String expenseId, String userEmail) {
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
            try {
                params = requestBuilder.getPayBackStaticExpenseRequest(token, flatId, expenseId, userEmail).toString();

            } catch (JSONException e) {
                status = Status.requestFailed;
                return result;
            }
            try {
                result = RequestService.sendRequestWithData(ServerConnector.Method.POST, payBackStaticExpenseUrl, params);
            } catch (IOException e) {
                status = Status.requestFailed;
            }
            return result;
        }

        private void handleExpenseResponse(String response) {
            JSONObject res = null;
            try{
                res = new JSONObject(response);
                String done = res.getString("response");
                if(done.equals("Done!")) {
                    status = Status.okay;
                } else {
                    status = Status.requestFailed;

                }
            } catch (JSONException e) {
                if(res == null) {
                    status = Status.requestFailed;
                } else {
                    try {
                        int errCode = res.getInt("error_code");
                        switch (errCode) {
                            case 1:
                                exceptionMessage = "Authentifizierung fehlgeschlagen";
                                break;
                            case 11:
                                exceptionMessage = "Ungültige Useremail";
                                break;
                            case 202:
                                exceptionMessage = "WG existiert nicht";
                                break;
                            case 701:
                                exceptionMessage = "Ausgabe nicht gefunden";
                        }
                    } catch (JSONException i) {
                        status = Status.requestFailed;
                    }
                }
            }
        }
    }
}
