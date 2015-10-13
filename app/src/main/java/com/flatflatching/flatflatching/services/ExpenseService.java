package com.flatflatching.flatflatching.services;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.ServerConnector;
import com.flatflatching.flatflatching.models.Flat;
import com.flatflatching.flatflatching.models.StaticUserExpense;

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
public class ExpenseService {
    private static RequestBuilder requestBuilder = new RequestBuilder();
    private static String createStaticExpenseUrl = "";
    private static String createVariableExpenseUrl = "";

    public static void createStaticExpense(BaseActivity activity, String flatId, String expenseName,
                                           double expenseAmount, Date expenseEnd,
                                  int expenseInterval, List<StaticUserExpense> userExpenses) {
        new CreateStaticExpenseTask(activity, createStaticExpenseUrl, flatId, expenseName,
                expenseAmount, expenseEnd, expenseInterval, userExpenses).execute();
    }

    public static void createVariableExpense(BaseActivity activity, String flatId, String expenseName,
                                             double expenseAmount, Date expenseEnd, List<String> userEmails) {
        new CreateVariableExpenseTask(activity, createVariableExpenseUrl, flatId, expenseName,
                expenseAmount, expenseEnd, userEmails).execute();
    }
    private static class CreateStaticExpenseTask extends AbstractGetAuthTokenTask {
        private String flatId;
        private String expenseName;
        private double expenseAmount;
        private Date expenseEnd;
        private int expenseInterval;
        private List<StaticUserExpense> userExpenses;

        public CreateStaticExpenseTask(BaseActivity activity, String url, String flatId, String expenseName, double expenseAmount,
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
}
