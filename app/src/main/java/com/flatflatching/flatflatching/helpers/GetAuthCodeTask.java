package com.flatflatching.flatflatching.helpers;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class GetAuthCodeTask extends AbstractAsyncTask {
    private static final String GET_AUTH_CODE_URL = "http://152.96.56.39/authenticate";
    private final transient WeakReference<TextView> authCodeReference;
    private final transient WeakReference<RelativeLayout> waitReference;

    
    /** This is the standard constructor that will give feedback to the user.
     * @param context The context from which it is invoked
     * @param textView Here the auth-code will be typed to
     * @param spinnerLayout The waiting spinner
     * @param messageView The title
     */
    public GetAuthCodeTask(final Context context, final TextView textView,
            final RelativeLayout spinnerLayout, final TextView messageView, final ViewGroup viewGroup) {
        super(context, messageView, viewGroup, GET_AUTH_CODE_URL);
        authCodeReference = new WeakReference<TextView>(textView);
        setMessageShowerReference( new WeakReference<TextView>(
                messageView));
        waitReference = new WeakReference<RelativeLayout>(
                spinnerLayout);
    }

    /**
     * This constructor is used by the setName method, the call then gets unnoticed by the user.
     */
    public GetAuthCodeTask() {
        super(null, null, null, GET_AUTH_CODE_URL);
        authCodeReference = null;
        waitReference = null;
    }

    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);
        if (authCodeReference == null) {
            //Ignore, we only needed to change the name in the db with this request
        } else {
            final TextView authCodeTextView = authCodeReference.get();
            final RelativeLayout waitingLayout = waitReference.get();
            if (result.isEmpty() || authCodeTextView == null || waitingLayout == null) {
                reactToError();
            } else {
                try {  
                    final JSONObject jsonObject = new JSONObject(result);
                    authCodeTextView.setText(jsonObject.getString("auth_string"));
                    authCodeTextView.setVisibility(View.VISIBLE);
                    waitingLayout.setVisibility(View.GONE);
                    final TextView welcomeTextView = getMessageShower();
                   /* welcomeTextView.setText(getContext().getResources()
                            .getString(R.string.authenticate_username_set));*/
                } catch (JSONException | IOException e) {
                    reactToError();
                } 
            } 
        }
    }
}