package com.flatflatching.flatflatching.helpers;

import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.services.RequestService;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public abstract class AbstractAsyncTask extends AsyncTask<JSONObject, Void, String> {

    @Override
    protected void onPostExecute(String result) {

    }
    protected BaseActivity activity;
    protected final  String url;
    protected enum Status  {
        running,        notReady,

        requestFailed,
        userRecoverableError,
        okay
    }
    protected ServerConnector.Method method = ServerConnector.Method.GET;
    protected String exceptionMessage;
    protected Status status;
    public AbstractAsyncTask(final BaseActivity activity, final String url) {
        super();
        status = Status.notReady;
        exceptionMessage = activity.getString(R.string.server_error);
        this.url = url;
        this.activity = activity;
    }

    public final AbstractAsyncTask doesPost() {
        this.method = ServerConnector.Method.POST;
        return this;
    }

    @Override
    protected String doInBackground(final JSONObject... jsonObject) {
        status = Status.running;
        String result = "";
        try {
            if(jsonObject == null || jsonObject.length == 0) {
                result = RequestService.sendRequest(this.method, url);
            } else {
                result = RequestService.sendRequestWithData(this.method, url, jsonObject[0]);
            }
        } catch(IOException | JSONException e) {
            status = Status.requestFailed;
        }
        handleResponse(result);
        return result;
    }

    protected void handleResponse(String response) {

    }
    protected final void reactToError() {
        final ViewGroup layoutContainer = activity.getLayoutContainer();
        for (int i = 0; i < layoutContainer.getChildCount(); i++) {
            layoutContainer.getChildAt(i).setVisibility(View.GONE);
        }
        final TextView titleText = activity.getMessageShower();
        titleText.setVisibility(View.VISIBLE);
        titleText.setText(exceptionMessage);
    }

    protected final void persistToPreferences(String key, String value) {
        activity.persistToPreferences(key, value);
    }

    protected final void persistObject(String key, Object obj) throws IOException {
        activity.persistObject(key, obj);
    }
}
