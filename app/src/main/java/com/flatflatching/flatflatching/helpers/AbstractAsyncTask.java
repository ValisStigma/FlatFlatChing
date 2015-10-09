package com.flatflatching.flatflatching.helpers;

import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.services.RequestService;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public abstract class AbstractAsyncTask extends AsyncTask<JSONObject, Void, String> {
    
    protected BaseActivity activity;
    protected final  String url;
    protected enum Status  {
        notReady,
        running,
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
    public AbstractAsyncTask doesPost() {
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
                result = RequestService.sendRequestWithData(this.method, url, jsonObject[0].toString());
            }
        } catch(IOException e) {
            status = Status.requestFailed;
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
        titleText.setText(exceptionMessage);
    }

    protected void persistToPreferences(String key, String value) {
        activity.persistToPreferences(key, value);
    }

    protected void persistObject(String key, Object obj) throws IOException {
        activity.persistObject(key, obj);
    }
}
