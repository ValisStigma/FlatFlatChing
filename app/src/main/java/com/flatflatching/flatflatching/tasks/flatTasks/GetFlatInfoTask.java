package com.flatflatching.flatflatching.tasks.flatTasks;

import android.util.Log;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;
import com.flatflatching.flatflatching.models.Flat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by rafael on 13.10.2015.
 */
public class GetFlatInfoTask extends AbstractAsyncTask {

    public GetFlatInfoTask(BaseActivity activity, String url) {
        super(activity, url);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (status == Status.requestFailed || result.isEmpty()) {
            reactToError();
        } else {
            try {
                persistFlatInfo(result);
            } catch (JSONException e) {
                exceptionMessage = "Infos über WG nicht einholbbar";
                reactToError();
            }
        }
    }

    private void persistFlatInfo(String res) throws JSONException {
        JSONObject response = new JSONObject(res);
        Flat flat = new Flat(response);
        try{
            persistObject(BaseActivity.FLAT, flat);
        } catch (IOException e ) {
            //Thus is entered a fatal part of code
            //We try to at least store the name und loggen
            activity.persistToPreferences(BaseActivity.FLAT_NAME, flat.getName());
            Log.d("store_error", "unable to store: " + flat.getName());
        }
    }
}
