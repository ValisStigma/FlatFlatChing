package com.flatflatching.flatflatching.helpers;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ch.hsr.requesthandlers.GetAuthCodeTask;
import ch.hsr.requesthandlers.GetSurveysTask;
import ch.hsr.requesthandlers.StartSendAnswerTask;
import ch.hsr.requesthandlers.StartSurveyActivityTask;

import org.json.JSONObject;

import java.lang.ref.WeakReference;



public class ServeyQuerier {

    public ServeyQuerier() {
        //Maybe here's potential for small method params
    }

    /** Returns a JSONObject with the initial surveys to show.
     * @param theListView The listview in which the survey will be shown
     * @param relativeLayout  The layout in which the surveys will be shown
     * @param welcomeTextView The textview in which success or error-messages are shown
     * @param theContext The context of the caller-activity
     * @param theActivity The name of the Activity which is called
     * @param requestParams The parameters for the post-request
     * @param identifier The client-id
     */
    public void getDefaultSurveys(final ListView theListView,
            final RelativeLayout relativeLayout, final TextView welcomeTextView,
            final Context theContext, final Activity theActivity, final JSONObject requestParams,
            final String identifier, final ViewGroup viewGroup) {
        new GetSurveysTask(theListView, theContext, theActivity, identifier,
                relativeLayout, welcomeTextView, viewGroup).execute(requestParams);
    }

    public void getAuthCode(final Context context, final TextView textView,
            final JSONObject requestParams, final RelativeLayout spinnerLayout,
            final TextView welcomeView, final ViewGroup viewGroup) {
        new GetAuthCodeTask(context, textView, spinnerLayout,
                welcomeView, viewGroup).execute(requestParams);
    }

    public void getSurvey(final Context context, final Activity activity,
            final WeakReference<TextView> titleReference,
            final JSONObject requestParams, final ViewGroup viewGroup) {
        new StartSurveyActivityTask(context, activity, titleReference, viewGroup)
                .execute(requestParams);
    }

    public void sendAnswer(final JSONObject requestParams, final ViewGroup container,
            final TextView textView,
            final Context context) {
        new StartSendAnswerTask(container, textView, context).execute(requestParams);
    }

    public void setUserName(final JSONObject requestParams) {
        new GetAuthCodeTask().execute(requestParams);
    }
}
