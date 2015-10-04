package com.flatflatching.flatflatching.helpers;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class StartSendAnswerTask extends AbstractAsyncTask {

    private static final String GET_SURVEY_URL = "http://152.96.56.39/sendanswer";
    private static final int WORKED = 200;

    /** Sends the data consisting of the users survey-answers back to the server.
     * @param viewContainer  The layout that will be cleaned to show error-messages
     * @param context The context of the activity calling this method
     */
    public StartSendAnswerTask(final ViewGroup viewContainer, final TextView textView, final Context context) {
        super(context, textView, viewContainer, GET_SURVEY_URL);
    }

    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);
        if (result.isEmpty()) {
            reactToError();
        } else {
            int statusCode = 0;
            try {
                final JSONObject resultJson = new JSONObject(result);
                statusCode = resultJson.getInt("status_code");
            } catch (JSONException e) {
                reactToError();
            }
            if (statusCode != WORKED) {
                reactToError();
            }
        }
    }
}
