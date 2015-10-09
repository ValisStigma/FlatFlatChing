package com.flatflatching.flatflatching.services;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public static void createFlat(Activity activity, TextView messageShower, ViewGroup viewContainer, String chosenEmail, Flat flat) {
        new CreateFlatTask(activity, messageShower, viewContainer, chosenEmail, flat).execute();
    }

    public static void inviteFlatMate(Activity activity, TextView messageShower, ViewGroup viewContainer, String flatId, String email) {
        new InviteFlatMateTask(activity, messageShower, viewContainer, flatId, email).execute();
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

        public InviteFlatMateTask(Activity activity, TextView messageShower, ViewGroup viewContainer, String flatId, String email) {
            super(activity, messageShower, viewContainer, inviteUrl);
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

        public CreateFlatTask(Activity activity, TextView textView, ViewGroup viewGroup, String url, Flat flat) {
            super(activity, textView, viewGroup, url);
            this.flat = flat;
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
