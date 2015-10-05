package com.flatflatching.flatflatching.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.flatflatching.flatflatching.R;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

public abstract class AbstractAsyncTask extends AsyncTask<JSONObject, Void, String> {
    
    private Context context;
    private transient WeakReference<TextView> textReference;
    private transient WeakReference<ViewGroup> viewContainer;
    protected final  String url;
    
    public AbstractAsyncTask(final Context context, final TextView textView, final ViewGroup viewGroup, final String url) {
        super();
        this.url = url;
        setContext(context);
        setViewContainerReference(new WeakReference<>(viewGroup));
        setMessageShowerReference(new WeakReference<>(textView));
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
    
    protected void setMessageShowerReference(final WeakReference<TextView> textView) {
        this.textReference = textView;
    }
    
    protected WeakReference<TextView> getMessageShowReference() {
        return this.textReference;
    }
    
    protected TextView getMessageShower() throws IOException {
        final TextView textView = getMessageShowReference().get();
        if (textView == null) {
            throw new IOException();
        }
        return textView;
    }
    
    private void setViewContainerReference(final WeakReference<ViewGroup> viewGroup) {
        this.viewContainer = viewGroup;
    }
    
    private WeakReference<ViewGroup> getViewContainerReference() {
        return this.viewContainer;
    }
    
    protected ViewGroup getViewContainer() throws IOException {
        final ViewGroup viewGroup = getViewContainerReference().get();
        if (viewGroup == null) {
            throw new IOException();
        }
        return viewGroup;
    }
    
    protected Context getContext() {
        return context;
    }

    private void setContext(final Context context) {
        this.context = context;
    }
    
    protected boolean hasConnection() {
        final ConnectivityManager connMgr = (ConnectivityManager) getContext().getSystemService(
            Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    
    protected void reactToError() {
        try {
            final ViewGroup surveyContainer = getViewContainer();
            for (int i = 0; i < surveyContainer.getChildCount(); i++) {
                surveyContainer.getChildAt(i).setVisibility(View.GONE);
            }
            final TextView titleText = getMessageShower();
            titleText.setVisibility(View.VISIBLE);
            titleText.setText(R.string.server_error);
        } catch (IOException e) {
            Log.d("this", e.getMessage());
        }  
    }
}
