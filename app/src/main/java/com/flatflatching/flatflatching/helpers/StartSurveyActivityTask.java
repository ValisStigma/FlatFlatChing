package com.flatflatching.flatflatching.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class StartSurveyActivityTask extends
        AbstractAsyncTask {

    private static final String GET_SURVEY_URL = "http://152.96.56.39/getsurvey";
    private Activity activity;
    
    /** This Tasks starts a new activity which will show the form for a new survey.
     * @param context The context of the activity that will start the new activity
     * @param activity The class representing the activity which will show the new survey
     * @param titleReference The textview in which possible error-messages are written
     */
    public StartSurveyActivityTask(final Context context, final Activity activity,
            final WeakReference<TextView> titleReference,
            final ViewGroup viewGroup) {
        super(context, titleReference.get(), viewGroup, GET_SURVEY_URL);
        setActivity(activity);
    }

    private Activity getActivity() {
        return activity;
    }

    private void setActivity(final Activity activity) {
        this.activity = activity;
    }

    @Override
    protected void onPostExecute(final String result) {
        super.onPostExecute(result);

        if (result.isEmpty()) {
            reactToError();
        } else {
/*            final Intent surveyIntent = new Intent(getContext(), SurveyActivity.class);
            surveyIntent.putExtra(SurveyActivity.SURVEY_DATA, result);
            getActivity().startActivity(surveyIntent);*/
        }
    }
}
