package com.flatflatching.flatflatching.helpers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class GetSurveysTask extends AbstractAsyncTask {
    private static final String GET_SURVEYS_URL = "http://152.96.56.39/getsurveys";

    private WeakReference<ListView> listViewReference;
    private Activity activity;
    private WeakReference<RelativeLayout> waitReference;
    private RequestBuilder requestBuilder;
    private String uuid;
    


    /** Gets all Surveys from the server and displays them in a listview.
     * @param listView The listview in which the surveys shall be displayed
     * @param context The context of the calling activity
     * @param activity The activity calling
     * @param uuid The client-id
     * @param relativeLayout The layout which inhibits the listview
     * @param welcomeTextView The textfield in which messages are written
     */
    public GetSurveysTask(final ListView listView, final Context context,
            final Activity activity, final String uuid, final RelativeLayout relativeLayout,
            final TextView welcomeTextView, final ViewGroup viewGroup) {
        super(context, welcomeTextView, viewGroup, GET_SURVEYS_URL);
        setListViewReference(new WeakReference<ListView>(listView));
        setWaitReference(new WeakReference<RelativeLayout>(
                relativeLayout));
        setActivity(activity);
        setUuid(uuid);
        setRequestBuilder();
    }

    private String getUuid() {
        return this.uuid;
    }
    
    private void setUuid(final String uuid) {
        this.uuid = uuid;
    }
    
    private ListView getListView() throws IOException {
        final ListView listView = getListViewReference().get();
        if (listView == null) {
            throw new IOException();
        }
        return listView;
    }


    private RelativeLayout getWaitLayout() throws IOException {
        final RelativeLayout relativeLayout = getWaitReference().get();
        if (relativeLayout == null) {
            throw new IOException();
        }
        return relativeLayout;
    }
    
    private void setListViewReference(final WeakReference<ListView> listReference) {
        this.listViewReference = listReference;
    }
    
    private WeakReference<ListView> getListViewReference() {
        return listViewReference;
    }

    private void setWaitReference(final WeakReference<RelativeLayout> waitReference) {
        this.waitReference = waitReference;
    }
    
    private WeakReference<RelativeLayout> getWaitReference() {
        return waitReference;
    }


    private Activity getActivity() {
        return activity;
    }
    
    private void setActivity(final Activity activity) {
        this.activity = activity;
    }

    private RequestBuilder getRequestBuilder() {
        return requestBuilder;
    }
    
    private void setRequestBuilder() {
        this.requestBuilder = new RequestBuilder();
    }

    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);
        
        if (result.isEmpty()) {
            reactToError();
        } else {
            try {
                final JSONArray jArray = new JSONArray(result);
                setupListView(jArray);
                final RelativeLayout waitLayout = getWaitLayout();
                final TextView welcomeTextView = getMessageShower();
                final ListView listView = getListView();
                waitLayout.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                //welcomeTextView.setText(getContext().getResources().getString(R.string.welcome_message));
            } catch (JSONException | IOException e) {
                reactToError();
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void setupListView(final JSONArray jsonData) throws JSONException {
        /*SurveyListItem[] listData = new SurveyListItem[jsonData
                .length()];

        for (int i = 0; i < jsonData.length(); i++) {
            final Survey survey = new Survey(jsonData.getJSONObject(i), getUuid());
            final SurveyListItem surveyListItem = new SurveyListItem(survey.getTitle(), survey.getCreator(),
                    survey.getExpire(), survey.getIdentifier());
            listData[i] = surveyListItem;
        }
        final SurveyAdapter adapter = new SurveyAdapter(getActivity(),
                R.layout.listview_survey_item, listData);

        try {
            final ListView listView = getListView();
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(final AdapterView<?> parent, final View view,
                        final int position, final long identifier) {
                    final SurveyListItem thisSurveyObject = (SurveyListItem) (parent
                            .getAdapter().getItem(position));
                    final LinearLayout surveyContainer = (LinearLayout) view;
                    final int height = surveyContainer.getHeight();
                    final int width = surveyContainer.getWidth();
                    for (int i = 0; i < surveyContainer.getChildCount(); i++) {
                        final View nextChild = surveyContainer.getChildAt(i);
                        nextChild.setVisibility(View.GONE);
                    }
                    final View nextChild = surveyContainer.getChildAt(3);
                    nextChild.setVisibility(View.VISIBLE);
                    surveyContainer.setLayoutParams(new ListView.LayoutParams(
                            width, height));
                    if (hasConnection()) {
                        try {
                            final ServeyQuerier serverQuerier = new ServeyQuerier();
                            serverQuerier.getSurvey(getContext(), getActivity(),
                                    getMessageShowReference(),
                                    getRequestBuilder().getSurveyRequest(thisSurveyObject
                                            .getIdentifier()), getViewContainer());
                        } catch (JSONException | IOException e) {
                            listView.setVisibility(View.GONE);
                            reactToError();
                        }
                    } else {
                        listView.setVisibility(View.GONE);
                        reactToError();
                    }
                }
            });
        } catch (IOException e1) {
            reactToError();
        }*/
    }
}