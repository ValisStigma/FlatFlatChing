package com.flatflatching.flatflatching.services;

import android.util.Log;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;
import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.ServerConnector;
import com.flatflatching.flatflatching.models.Address;
import com.flatflatching.flatflatching.models.Flat;
import com.flatflatching.flatflatching.models.FlatMate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by rafael on 05.10.2015.
 */

public class FlatService {
    private static String ownUrl = "";
    private static String inviteUrl = "";
    private static String getFlatInfoUrl = "";
    private static String getFlatMemberInfoUrl = "";
    private static String setFlatAdminUrl = "";
    private static String deleteFlatUrl = "";
    private static String answerInvitationUrl = "";

    private static RequestBuilder requestBuilder = new RequestBuilder();

    public static void createFlat(BaseActivity activity, String chosenEmail, Flat flat) {
        new CreateFlatTask(activity, chosenEmail, flat).execute();
    }

    public static void getFlatInfo(BaseActivity activity, String flatId) {
        try {
            JSONObject params = requestBuilder.getFlatInfoRequest(flatId);
            new GetFlatInfoTask(activity, getFlatInfoUrl).execute(params);
        } catch (JSONException e) {
            activity.notifyError(R.string.server_error);
        }
    }

    public static void getFlatMemberInfo(BaseActivity activity, String flatId) {
        try{
            JSONObject params = requestBuilder.getFlatInfoRequest(flatId);
            new GetFlatMemberInfoTask(activity, getFlatMemberInfoUrl).execute(params);
        } catch (JSONException e) {
            activity.notifyError(R.string.server_error);
        }
    }
    public static void inviteFlatMate(BaseActivity activity, String flatId, String email) {
        new InviteFlatMateTask(activity, flatId, email).execute();
    }

    public static void setAdmin(BaseActivity activity, String userEmail) {
        new SetFlatAdminTask(activity, userEmail).execute();
    }

    public static void deleteFlat(BaseActivity activity, String flatId) {
        new DeleteFlatTask(activity, flatId, deleteFlatUrl).execute();
    }

    public static void answerInvitation(BaseActivity activity, String userEmail, String flatAdminEmail, String flatId, boolean accept)  {
        new AnswerInvitationTask(activity, flatId, userEmail, flatAdminEmail,accept).execute();
    }

    private static class GetFlatMemberInfoTask extends AbstractAsyncTask {

        public GetFlatMemberInfoTask(BaseActivity activity, String url) {
            super(activity, url);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(status == Status.requestFailed || result.isEmpty()) {
                reactToError();
            } else {
                try {
                    persistFlatMemberInfo(result);
                } catch (JSONException | IOException e) {
                    exceptionMessage = "Infos über WG-Bewohner nicht einholbbar";
                    reactToError();
                }
            }
        }

        private void persistFlatMemberInfo(String res) throws JSONException, IOException {
            JSONArray response = new JSONArray(res);
            for(int i = 0; i < response.length(); i++) {
                JSONObject flatMember = response.getJSONObject(i);
                final boolean isAdmin = flatMember.getBoolean("isAdmin");
                final String email = flatMember.getString("email");
                final FlatMate flatMate = new FlatMate(email, isAdmin);
                activity.persistObject(email, flatMate);

            }
        }
    }
    private static class GetFlatInfoTask extends AbstractAsyncTask {

        public GetFlatInfoTask(BaseActivity activity, String url) {
            super(activity, url);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(status == Status.requestFailed || result.isEmpty()) {
                reactToError();
            } else {
                try {
                    persistFlatInfo(result);
                } catch (JSONException e) {
                    exceptionMessage = "Infos über WG nicht einholbbar";
                    reactToError();
                }
            }
        }

        private void persistFlatInfo(String res) throws JSONException {
            JSONObject response = new JSONObject(res);
            final String name = response.getString("flat_name");
            activity.persistToPreferences(BaseActivity.FLAT_NAME, name);
            try {
                final JSONObject jsonAddress = response.getJSONObject("flat_address");
                final String streetName = jsonAddress.getString("flat_address_street");
                final String houseNumber = jsonAddress.getString("flat_address_number");
                final String plz = jsonAddress.getString("flat_address_plz");
                final String city = jsonAddress.getString("flat_address_place");
                if(!streetName.isEmpty() && !houseNumber.isEmpty() && !plz.isEmpty() && !city.isEmpty()) {
                    final Address address = new Address(streetName, houseNumber, plz, city);
                    persistObject(BaseActivity.FLAT, address);

                }
            } catch (JSONException e) {
                //Swallow JSONException because address not mandatory
                Log.d("address_error", "Incomplete address for " + name);
            } catch (IOException e) {
                Log.d("address_error", "Unable to store address for " + name);
            }

        }

    }
    private  static class InviteFlatMateTask extends AbstractGetAuthTokenTask {
        private final String flatId;
        private final String email;

        public InviteFlatMateTask(BaseActivity activity, String flatId, String email) {
            super(activity, inviteUrl);
            this.flatId = flatId;
            this.email = email;
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
                        status = Status.requestFailed;
                        int errorCode = result.getInt("error_code");
                        switch (errorCode) {
                            case 1:
                                exceptionMessage = "Du bist noch nicht registriert";
                                break;
                            case 2:
                                exceptionMessage = "Du bist kein WG Admin";
                                break;
                            case 11:
                                exceptionMessage = "Diese Email-addresse ist ungültig";
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
                status = Status.requestFailed;
                return result;
            }
            try {
                result = RequestService.sendRequestWithData(ServerConnector.Method.POST, inviteUrl, params);
            } catch (IOException e) {
                status = Status.requestFailed;
            }
            return result;
        }

        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            if(status == Status.requestFailed) {
                reactToError();
            }
        }
    }

    private static class SetFlatAdminTask extends AbstractGetAuthTokenTask {
        private final String userEmail;
        public SetFlatAdminTask(BaseActivity activity, final String userEmail) {
            super(activity, setFlatAdminUrl);
            this.userEmail = userEmail;
        }

        @Override
        protected void handleToken(String token) {
            String response = setAdminRequest(token);
            handleSetAdminResponse(response);
        }

        private void handleSetAdminResponse(String response) {
            JSONObject res = null;
            try{
                res = new JSONObject(response);
                String state = res.getString("response");
                if(state.equals("Done")) {
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
                            case 2:
                                exceptionMessage = "Du bist kein Admin";
                                break;
                            case 11:
                                exceptionMessage = "User wurde nicht gefunden";
                                break;
                        }
                    } catch (JSONException i) {
                        status = Status.requestFailed;
                    }
                }
            }
        }

        private String setAdminRequest(String token) {
            String params;
            String result = "";
            RequestBuilder requestBuilder = new RequestBuilder();
            try {
                params = requestBuilder.getSetFlatAdminRequest(token, userEmail).toString();

            } catch (JSONException e) {
                status = Status.requestFailed;
                return result;
            }
            try {
                result = RequestService.sendRequestWithData(ServerConnector.Method.POST, setFlatAdminUrl, params);
            } catch (IOException e) {
                status = Status.requestFailed;
            }
            return result;
        }

        @Override
        protected void postToken() {

        }
    }

    private static class DeleteFlatTask extends AbstractGetAuthTokenTask {
        private String flatId;

        public DeleteFlatTask(BaseActivity activity, String flatId, String deleteFlatUrl) {
            super(activity, deleteFlatUrl);
        }

        @Override
        protected void handleToken(String token) {
            String response = deleteFlat(token);
            handleFlatResponse(response);

        }

        private void handleFlatResponse(String response) {
            JSONObject res = null;
            try{
                res = new JSONObject(response);
                String responseMessage = res.getString("response");
                if(responseMessage.equals("good bye :(")) {
                    status = Status.okay;
                } else {
                    status = Status.requestFailed;
                }
                status =  Status.okay;
            } catch (JSONException e) {
                if(res == null) {
                    status = Status.requestFailed;
                } else {
                    try {
                        int errCode = res.getInt("error_code");
                        switch (errCode) {
                            case 1:
                                exceptionMessage = "Du bist kein Admin";
                                break;
                            case 202:
                                exceptionMessage = "Diese WG wurde nicht gefunden";
                                break;
                            case 301:
                                exceptionMessage = "Es gibt noch offene Finanzposten";
                                break;
                        }
                    } catch (JSONException i) {
                        status = Status.requestFailed;
                    }
                }
            }
        }

        @Override
        protected void postToken() {

        }

        private String deleteFlat(final String token) {
            String params;
            String result = "";
            RequestBuilder requestBuilder = new RequestBuilder();
            try {
                params = requestBuilder.getDeleteFlatRequest(token, flatId).toString();

            } catch (JSONException e) {
                status = Status.requestFailed;
                return result;
            }
            try {
                result = RequestService.sendRequestWithData(ServerConnector.Method.POST, deleteFlatUrl, params);
            } catch (IOException e) {
                status = Status.requestFailed;
            }
            return result;
        }

    }

    private static class AnswerInvitationTask extends AbstractGetAuthTokenTask {
        private String flatId;
        private String userEmail;
        private String adminEmail;
        private boolean accept;
        public AnswerInvitationTask(BaseActivity activity, String flatId, String userEmail, String adminEmail, boolean accept) {
            super(activity, answerInvitationUrl);
            this.flatId = flatId;
            this.userEmail = userEmail;
            this.adminEmail = adminEmail;
            this.accept = accept;
        }

        @Override
        protected void handleToken(String token) {
            String response = answerInvitation(token);
            handleInvitationResponse(response);
        }

        private void handleInvitationResponse(String response) {
            JSONObject res = null;
            try{
                res = new JSONObject(response);
                String flatId = res.getString("flat_uuid");
                if(!flatId.isEmpty()) {
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
                                exceptionMessage = "Login ist voll gefailt";
                                break;
                            case 201:
                                exceptionMessage = "Keine Einladung gefunden";
                                break;
                        }
                    } catch (JSONException i) {
                        status = Status.requestFailed;
                    }
                }
            }
        }

        private String answerInvitation(String token) {
            String params;
            String result = "";
            RequestBuilder requestBuilder = new RequestBuilder();
            try {
                params = requestBuilder.getAnswerInvitationRequest(token, flatId, userEmail, adminEmail, accept).toString();

            } catch (JSONException e) {
                status = Status.requestFailed;
                return result;
            }
            try {
                result = RequestService.sendRequestWithData(ServerConnector.Method.POST, answerInvitationUrl, params);
            } catch (IOException e) {
                status = Status.requestFailed;
            }
            return result;
        }

        @Override
        protected void postToken() {

        }
    }
    private static class CreateFlatTask extends AbstractGetAuthTokenTask {
        private Flat flat;

        public CreateFlatTask(BaseActivity activity, String url, Flat flat) {
            super(activity, url);
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
                                exceptionMessage = "Du bist kein Admin";
                                break;
                            case 111:
                                exceptionMessage = "Du hast keine Strasse angegeben";
                                break;
                            case 112:
                                exceptionMessage = "Du hast keine Hausnummer angegeben";
                                break;
                            case 113:
                                exceptionMessage = "Du hast keine Ortschaft angegeben";
                                break;
                            case 114:
                                exceptionMessage = "Du hast keine Postleitzahl angegeben";
                                break;
                            case 115:
                                exceptionMessage = "Du hast kein Land angegeben";
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
                status = Status.requestFailed;
                return result;
            }
            try {
                result = RequestService.sendRequestWithData(ServerConnector.Method.POST, ownUrl, params);
            } catch (IOException e) {
                status = Status.requestFailed;
            }
            return result;
        }

    }
}
