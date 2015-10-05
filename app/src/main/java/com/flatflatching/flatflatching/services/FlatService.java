package com.flatflatching.flatflatching.services;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private static Flat currentFlat;
    private static String ownUrl;
    public static void createFlat(Activity activity, TextView messageShower, ViewGroup viewContainer, String chosenEmail, Flat flat) {
        currentFlat = flat;
        getAuth(activity, messageShower, viewContainer, chosenEmail);

    }

    public static void getAuth(Activity activity, TextView messageShower, ViewGroup viewContainer, String chosenEmail) {
        new GetAuthForFlatTask(activity, messageShower, viewContainer, chosenEmail).execute();
    }

    private static class CreateFlatTask extends AbstractAsyncTask {

        private Flat flat;
        private String token;
        public CreateFlatTask(Context context, TextView textView, ViewGroup viewGroup, String url, String token, Flat flat) {
            super(context, textView, viewGroup, url);
            this.flat = flat;
            this.token = token;
        }

        @Override
        protected String doInBackground(final JSONObject... jsonObject) {
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
                        url, "UTF-8");
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
        protected void onPostExecute(String result)  {
            super.onPostExecute(result);

        }
    }

    private static class GetAuthForFlatTask extends AbstractGetAuthTokenTask {

        GetAuthForFlatTask(Activity activity, TextView messageShower, ViewGroup viewContainer, String url){
            super(activity, messageShower, viewContainer, url);
        }
        @Override
        protected void onPostExecute(Void result)  {
            super.onPostExecute(result);
            if(status == Status.tokenAcquired) {
                createFlat();
            } else {
                reactToError();
            }
        }

        protected void createFlat() {
            if(status == Status.tokenAcquired) {
                try{
                    new CreateFlatTask(activity, getMessageShower(), getViewContainer(), ownUrl, token, currentFlat).execute();

                } catch (IOException e) {
                    reactToError();
                }
            } else {
                reactToError();
            }
        }
    }
}
