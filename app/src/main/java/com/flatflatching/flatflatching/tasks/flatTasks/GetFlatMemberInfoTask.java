package com.flatflatching.flatflatching.tasks.flatTasks;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.flatflatching.flatflatching.helpers.AbstractAsyncTask;
import com.flatflatching.flatflatching.models.FlatMate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by rafael on 13.10.2015.
 */
public class GetFlatMemberInfoTask extends AbstractAsyncTask {

    public GetFlatMemberInfoTask(BaseActivity activity, String url) {
        super(activity, url);
    }

    @Override
    protected final void onPostExecute(String result) {
        super.onPostExecute(result);
        if (status == Status.requestFailed || result.isEmpty()) {
            reactToError();
        } else {
            try {
                persistFlatMemberInfo(result);
            } catch (JSONException | IOException e) {
                exceptionMessage = "Infos über WG-Bewohner nicht einholbar";
                reactToError();
            }
        }
    }

    private void persistFlatMemberInfo(String res) throws JSONException, IOException {
        JSONArray response = new JSONArray(res);
        ArrayList<String> flatMateNames = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject flatMember = response.getJSONObject(i);
            final FlatMate flatMate = new FlatMate(flatMember);
            activity.persistObject(flatMate.getName(), flatMate);
            flatMateNames.add(flatMate.getName());
        }
        activity.persistObject(BaseActivity.FLAT_MATE_NAMES, flatMateNames);
    }
}
