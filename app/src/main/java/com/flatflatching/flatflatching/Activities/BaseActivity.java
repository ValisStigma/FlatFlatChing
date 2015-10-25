package com.flatflatching.flatflatching.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.helpers.InternalStorage;
import com.flatflatching.flatflatching.models.Flat;
import com.flatflatching.flatflatching.models.FlatMate;
import com.flatflatching.flatflatching.services.AuthenticatorService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String PREFERENCES = "local_preferences";
    public static final String FLAT_ID = "FLAT_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final int REQUEST_PERMISSION = 1111;
    public static final String USER_NAME_GIVEN = "USER_NAME_GIVEN";
    public static final String USER_NAME_FAMILY = "USER_NAME_FAMILY";
    public static final String USER_PROFILE_LINK = "USER_PROFILE_LINK";
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;
    public static final String USER_IMAGE_URL = "USER_IMAGE_URL";
    public static final String CHOSEN_USER_EMAIL = "CHOSEN_USER_EMAIL";
    public static final String FLAT_NAME = "FLAT_NAME";
    public static final String FLAT = "FLAT";
    public static final String FLAT_MATE_NAMES = "FLAT_MATE_NAMES";
    public static final int FLAT_WAS_CREATED = 8888;
    public static final int EXPENSE_WAS_CREATED = 9999;
    public static final String INTENT_EXTRAS = "INTENT_EXTRAS";
    public static final String BASE_URL = "http://192.168.0.136:3000/%s";
    protected SharedPreferences settings;
    private String userName;
    protected ViewGroup layoutContainer;
    protected TextView messageShower;

    public final ViewGroup getLayoutContainer() {
        return layoutContainer;
    }

    public final TextView getMessageShower() {
        return messageShower;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public final void persistToPreferences(String key, String value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();

    }

    protected final void register(final String userEmail) {
        AuthenticatorService.register(this, userEmail);
    }

    public final String getUserEmail() {
        return settings.getString(CHOSEN_USER_EMAIL, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_own);
        setSupportActionBar(toolbar);
        try {
            toolbar.setTitle(getFlatName());
        } catch (IOException|ClassNotFoundException e) {
            toolbar.setTitle(R.string.flatName);
        }
    }
    @Override
    protected void onCreate(final Bundle savedState) {
        super.onCreate(savedState);



        settings = getSharedPreferences(PREFERENCES, 0);
    }

    /**Tests if the activity gets internet connection.
     * @return Boolean if connection is available
     */
    public final boolean hasConnection() {
        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    public final void notifyError(String message) {
        for (int i = 0; i < layoutContainer.getChildCount(); i++) {
            layoutContainer.getChildAt(i).setVisibility(View.GONE);
        }
        messageShower.setVisibility(View.VISIBLE);
        messageShower.setText(message);
    }

    protected final boolean isGoogleServicesAvailable(){
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return result == ConnectionResult.SUCCESS;
    }

    public final void persistObject(String key, Object obj) throws IOException {
        InternalStorage.writeObject(this, key, obj);
    }

    public final Object getObject(String key) throws IOException, ClassNotFoundException {
        return InternalStorage.readObject(this, key);
    }

    protected final boolean isAuthenticated() {
        userName = settings.getString(USER_NAME_GIVEN, "");
        return !userName.isEmpty();
    }

    protected final String getFlatName() throws IOException, ClassNotFoundException {
        Flat myFlat =(Flat)getObject(BaseActivity.FLAT);
        return myFlat.getName();
    }

    protected final List<FlatMate> getFlatMates() throws IOException, ClassNotFoundException {
        return (List<FlatMate>) getObject(BaseActivity.FLAT_MATE_NAMES);
    }
    protected final boolean isFlatMember() {
        String flatId = getFlatId();
        return !flatId.isEmpty() && !flatId.equals("null");
    }

    protected final String getFlatId() {
        final String flatId = settings.getString(FLAT_ID, "");
        return flatId;
    }
    protected final boolean isAdmin() {
        //TODO:correct implementation
        return true;
    }

    public abstract void setWaitingLayout();
    public abstract void reactToSuccess();
    public abstract void checkPreConditions();

    public void reactToGet(Object response) {

    }
}