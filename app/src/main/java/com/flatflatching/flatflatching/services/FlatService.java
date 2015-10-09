package com.flatflatching.flatflatching.services;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.ServerConnector;
import com.flatflatching.flatflatching.models.Flat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by rafael on 05.10.2015.
 */

public class FlatService {
    private static String ownUrl = "";
    private static String inviteUrl = "";

    public static void createFlat(BaseActivity activity, String chosenEmail, Flat flat) {
        new CreateFlatTask(activity, chosenEmail, flat).execute();
    }

    public static void inviteFlatMate(BaseActivity activity, String flatId, String email) {
        new InviteFlatMateTask(activity, flatId, email).execute();
    }

    private static class GetFlatInfoTask extends AbstractAsyncTask {
        public GetFlatInfoTask(BaseActivity activity, String url) {
            super(activity, url);
        }

        // flat_uuid=
        //RESPONSE JSON Object

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

/*        {
            "flat_name": "<name>",
                "flat_address": {
            "flat_address_street": "<strasse>",
                    "flat_address_number": <number>,
                    "flat_address_plz": <plz>,
                    "flat_address_place": "<ortsname>",
                    "flat_address_land": "<landname>"
        }
        }*/


    }
    private  static class InviteFlatMateTask extends AbstractGetAuthTokenTask {
        private final String flatId;
        private final String email;
        private enum Status {
            authFailed,
            noAdmin,
            userNotFound,
            requestFailed,
            notCompleted,
            okay
        }
        private Status status;

        public InviteFlatMateTask(BaseActivity activity, String flatId, String email) {
            super(activity, inviteUrl);
            this.flatId = flatId;
            this.email = email;
            status = Status.notCompleted;
        }

        @Override
        protected void handleToken(String token) {
            final String response = inviteFlatMate(token);
            handleInviteResponse(response);
        }

        @Override
        protected void postToken() {

        }

        private void handleInviteResponse(final String response) {
            JSONObject result = null;
            try {
                result = new JSONObject(response);
                String invite = result.getString("response");
                status = Status.okay;
            } catch (JSONException e) {
                try {
                    if(result == null) {
                        status = Status.requestFailed;
                    } else {
                        int errorCode = result.getInt("error_code");
                        switch (errorCode) {
                            case 1:
                                status = Status.authFailed;
                                break;
                            case 2:
                                status = Status.noAdmin;
                                break;
                            case 11:
                                status = Status.userNotFound;
                                break;
                        }
                    }
                } catch (JSONException | NullPointerException i) {
                    status = Status.requestFailed;
                }
            }

        }

        private String inviteFlatMate(final String token) {
            String params;
            String result = "";
            RequestBuilder requestBuilder = new RequestBuilder();
            try {
                params = requestBuilder.getInvitationRequest(token, flatId, email).toString();
            } catch (JSONException e) {
                return result;
            }
            final StringBuilder stringBuilder = new StringBuilder();
            try {
                final ServerConnector serverConnector = new ServerConnector(
                        inviteUrl, "UTF-8");
                serverConnector.addFormField("data", params);
                final List<String> response = serverConnector.finish();
                for (final String line : response) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();
            } catch (IOException i) {
                result = "";
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            switch (status) {
                case authFailed:
                    reactToError();
                    break;
                case noAdmin:
                    reactToError();
                    break;
                case userNotFound:
                    reactToError();
                    break;
                case notCompleted:
                    reactToError();
                    break;
            }
        }
    }


    private static class CreateFlatTask extends AbstractGetAuthTokenTask {
        private Flat flat;

        private enum Status {
            authFailed,
            incompleteAdress,
            requestFailed,
            notCompleted,
            okay
        }
        private Status status;

        public CreateFlatTask(BaseActivity activity, String url, Flat flat) {
            super(activity, url);
            this.flat = flat;
            status = Status.notCompleted;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @Override
        protected void handleToken(final String token) {
            String response = registerFlat(token);
            handleFlatResponse(response);
        }

        @Override
        protected void postToken() {

        }

        private void handleFlatResponse(String response) {
            JSONObject res = null;
            try{
                res = new JSONObject(response);
                String flatId = res.getString("flat_uuid");
                persistToPreferences(BaseActivity.FLAT_ID, flatId);
                status =  Status.okay;
            } catch (JSONException e) {
                if(res == null) {
                    status = Status.requestFailed;
                } else {
                    try {
                        int errCode = res.getInt("error_code");
                        switch (errCode) {
                            case 1:
                                status = Status.authFailed;
                                break;
                            case 111:
                                status = Status.incompleteAdress;
                                break;
                            case 112:
                                status = Status.incompleteAdress;
                                break;
                            case 113:
                                status = Status.incompleteAdress;
                                break;
                            case 114:
                                status = Status.incompleteAdress;
                                break;
                            case 115:
                                status = Status.incompleteAdress;
                                break;
                        }
                    } catch (JSONException i) {
                        status = Status.requestFailed;
                    }
                }
            }
        }

        private String registerFlat(final String token) {
            String params;
            String result = "";
            RequestBuilder requestBuilder = new RequestBuilder();
            try {
                params = requestBuilder.getCreateFlatRequest(token, flat).toString();

            } catch (JSONException e) {
                return result;
            }
            final StringBuilder stringBuilder = new StringBuilder();
            try {
                final ServerConnector serverConnector = new ServerConnector(
                        ownUrl, "UTF-8");
                serverConnector.addFormField("data", params);
                final List<String> response = serverConnector.finish();
                for (final String line : response) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();
            } catch (IOException i) {
                result = "";
            }
            return result;
        }

    }
}
