package com.flatflatching.flatflatching.helpers;

import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.activities.BaseActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public abstract class AbstractAsyncTask extends AsyncTask<JSONObject, Void, String> {
    
    protected BaseActivity activity;
    protected final  String url;
    public AbstractAsyncTask(final BaseActivity activity, final String url) {
        super();
        this.url = url;
        this.activity = activity;
    }
    
    @Override
    protected String doInBackground(final JSONObject... jsonObject) {
        String result = "";
        final StringBuilder stringBuilder = new StringBuilder();
        for (JSONObject aJsonObject : jsonObject) {
            try {
                final ServerConnector serverConnector = new ServerConnector(
                        url, "UTF-8");
                serverConnector.addFormField("data", aJsonObject.toString());
                final List<String> response = serverConnector.finish();
                stringBuilder.setLength(0);
                for (final String line : response) {
                    stringBuilder.append(line);
                }
                result = stringBuilder.toString();
            } catch (IOException e) {
                result = "";
            }
        }
        return result;
    }

    protected boolean hasConnection() {
        return activity.hasConnection();
    }
    
    protected void reactToError() {
        final ViewGroup layoutContainer = activity.getLayoutContainer();
        for (int i = 0; i < layoutContainer.getChildCount(); i++) {
            layoutContainer.getChildAt(i).setVisibility(View.GONE);
        }
        final TextView titleText = activity.getMessageShower();
        titleText.setVisibility(View.VISIBLE);
        titleText.setText(activity.getString(R.string.server_error));
    }

    protected void persistToPreferences(String key, String value) {
        activity.persistToPreferences(key, value);
    }
}
