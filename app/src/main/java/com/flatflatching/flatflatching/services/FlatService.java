package com.flatflatching.flatflatching.services;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flatflatching.flatflatching.helpers.AbstractGetAuthTokenTask;
import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.ServerConnector;
import com.flatflatching.flatflatching.models.Flat;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by rafael on 05.10.2015.
 */

public class FlatService {
    private static Flat currentFlat;
    private static String ownUrl = "";

    public static void createFlat(Activity activity, TextView messageShower, ViewGroup viewContainer, String chosenEmail, Flat flat) {
        currentFlat = flat;
        new CreateFlatTask(activity, messageShower, viewContainer, chosenEmail, flat);
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
