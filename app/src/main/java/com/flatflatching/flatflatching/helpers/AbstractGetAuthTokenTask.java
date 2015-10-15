package com.flatflatching.flatflatching.helpers;

import android.app.Activity;

import com.flatflatching.flatflatching.activities.BaseActivity;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by rafael on 05.10.2015.
 */
public abstract class AbstractGetAuthTokenTask extends AbstractAsyncTask{
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";

    protected String token;
    String chosenEmail;
    UserRecoverableAuthException userException;

    public AbstractGetAuthTokenTask(BaseActivity activity, String chosenEmail){
        super(activity, SCOPE);
        this.status = Status.notReady;
        this.chosenEmail = chosenEmail;

    }

    @Override
    protected String doInBackground(JSONObject... params){
        try{
            status = Status.running;
            token = getAccessToken(activity, chosenEmail, SCOPE);
            if(token != null){
                //Access Token sent
                status = Status.okay;
                handleToken(token);
                postToken();
            } else {
                status = Status.requestFailed;
            }

        }
        catch (Exception ex){
            token = null;
            status = Status.requestFailed;
        }
        return token;

    }

    @Override
    protected void onPostExecute(String result)  {
        switch (status) {
            case userRecoverableError:
                activity.startActivityForResult(userException.getIntent(), BaseActivity.REQUEST_PERMISSION);
                break;
            case requestFailed:
                reactToError();
                break;
            case okay:
                //is okay
                break;
            default:
                throw new UnsupportedOperationException("Not implemented this case");
        }
    }

    protected final String getAccessToken(Activity activity,String accountName, String scope){
        try{
            return GoogleAuthUtil.getToken(activity, accountName, scope);
        }
        catch(UserRecoverableAuthException userRecoverableError){
            status = Status.userRecoverableError;
            userException = userRecoverableError;
        }
        catch(GoogleAuthException | IOException googleAuthException){
            status = Status.requestFailed;
        }
        catch(Exception e) {
            status = Status.requestFailed;
        }
        return null;
    }

    protected abstract void handleToken(final String token);

    protected abstract void postToken();
}
