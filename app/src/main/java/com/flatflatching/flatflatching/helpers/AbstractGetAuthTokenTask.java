package com.flatflatching.flatflatching.helpers;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.TextView;

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
    protected enum Status {
        notReady,
        userRecoverableError,
        IOException,
        googleAuthException,
        tokenAcquired
    }

    protected  Activity activity;
    protected Status status;
    protected String token;
    String chosenEmail;
    UserRecoverableAuthException userException;

    public AbstractGetAuthTokenTask(Activity activity, TextView messageShower, ViewGroup viewContainer, String chosenEmail){
        super(activity, messageShower, viewContainer, SCOPE);
        this.activity = activity;
        this.status = Status.notReady;
        this.chosenEmail = chosenEmail;
    }

    @Override
    protected String doInBackground(JSONObject... params){
        try{
            token = getAccessToken(this.activity, chosenEmail, SCOPE);
            if(token != null){
                //Access Token sent
                status = Status.tokenAcquired;
                handleToken(token);
                postToken();
            } else {
                status = Status.IOException;
            }

        }
        catch (Exception ex){
            token = null;
            status = Status.IOException;
        }
        return token;

    }

    protected void onPostExecute(Void result)  {
        switch (status) {
            case notReady:
                reactToError();
                break;
            case userRecoverableError:
                activity.startActivityForResult(userException.getIntent(), BaseActivity.REQUEST_PERMISSION);
                break;
            case googleAuthException:
                reactToError();
                break;
            case IOException:
                reactToError();
                break;
            default:
                throw new UnsupportedOperationException("Not implemented this case");
        }
    }

    protected String getAccessToken(Activity activity,String accountName, String scope){
        try{
            return GoogleAuthUtil.getToken(activity, accountName, scope);
        }
        catch(UserRecoverableAuthException userRecoverableError){
            status = Status.userRecoverableError;
            userException = userRecoverableError;
        }
        catch(GoogleAuthException googleAuthException){
            status = Status.googleAuthException;
        }
        catch(IOException ioException) {
            status = Status.IOException;
        }
        return null;
    }

    protected abstract void handleToken(final String token);

    protected abstract void postToken();
}
