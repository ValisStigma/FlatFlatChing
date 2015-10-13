package com.flatflatching.flatflatching.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.helpers.InternalStorage;
import com.flatflatching.flatflatching.services.AuthenticatorService;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String PREFERENCES = "local_preferences";
    public static final String UUID = "UUID";
    public static final String USERNAME = "USERNAME";
    public static final String ACCOUNT_TOKEN = "ACCOUNT_TOKEN";
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
    public static final String FLAT_ADDRESS = "FLAT_ADDRESS";
    public static final String STREET_NAME = "STREET_NAME";
    public static final String HOUSE_NUMBER = "HOUSE_NUMBER";
    public static final String CITY = "CITY";
    public static final String PLZ = "PLZ";


    protected SharedPreferences settings;
    protected String uuid;
    protected String accountToken;
    protected String userName;
    protected String flatId;
    protected ViewGroup layoutContainer;
    protected TextView messageShower;
    public ViewGroup getLayoutContainer() {
        return layoutContainer;
    }

    public TextView getMessageShower() {
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

    public void persistToPreferences(String key, String value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();

    }

    protected void checkForUserEmail() {
        String userEmail = settings.getString(CHOSEN_USER_EMAIL, "");
        if(userEmail.isEmpty()) {
            tryAuthenticate();
        } else {
            register(userEmail);
        }
    }

    protected void register(final String userEmail) {
        AuthenticatorService.register(this, userEmail);
    }

    protected String getUserEmail() {
        return settings.getString(CHOSEN_USER_EMAIL, "");
    }

    protected void checkForUuid() {
        uuid = settings.getString(UUID, "");
        if (uuid.isEmpty()) {
            generateUuid();
        }
    }

    protected void checkForAuthentication() {
        accountToken = settings.getString(ACCOUNT_TOKEN, "");
        userName = settings.getString(USER_NAME, "");
        if(userName.isEmpty()){
            tryAuthenticate();
        }
    }

    protected void checkForFlatID() {
        flatId = settings.getString(FLAT_ID, "");
        if(flatId.isEmpty()) {

        }
    }

    private void generateUuid() {
        final TelephonyManager telephoneManager = (TelephonyManager) getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String tmDevice;
        String tmSerial;
        String androidId;
        tmDevice = telephoneManager.getDeviceId();
        tmSerial = telephoneManager.getSimSerialNumber();
        androidId = android.provider.Settings.Secure.getString(
                getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        final java.util.UUID deviceUuid = new UUID(androidId.hashCode(),
                ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        final String newUuid = deviceUuid.toString();
        persistToPreferences(UUID, newUuid);
    }

    @Override
    protected void onCreate(final Bundle savedState) {
        super.onCreate(savedState);

        settings = getSharedPreferences(PREFERENCES, 0);
    }

    /**Tests if the activity gets internet connection.
     * @return Boolean if connection is available
     */
    public boolean hasConnection() {
        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /** Used to notify Users of an error.
     * For this app implemented as a method which hides all GUI-elements except a textview and shows an error
     * message in it
     * @param stringId the id of the error-message
     */
    public void notifyError(final int stringId) {
        for (int i = 0; i < layoutContainer.getChildCount(); i++) {
            layoutContainer.getChildAt(i).setVisibility(View.GONE);
        }
        messageShower.setVisibility(View.VISIBLE);
        messageShower.setText(stringId);
    }

    protected void tryAuthenticate() {
      if(hasConnection()) {
          if(isGoogleServicesAvailable()) {
              pickUserAccount();
          } else {
              //TODO:Google services is not available
          }
      } else {
          //TODO: There is no internet connection
      }
    }

    protected void pickUserAccount() {
        Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null);
        startActivityForResult(googlePicker, BaseActivity.REQUEST_CODE_PICK_ACCOUNT);
    }

    public boolean isGoogleServicesAvailable(){
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return result == ConnectionResult.SUCCESS;
    }

    protected String getUserName() {
        return settings.getString(USERNAME, "");
    }

    public Bitmap getBitMap(){
        Bitmap bitmapImage = null;
        try {
            InputStream in = new java.net.URL(settings.getString(USER_IMAGE_URL, "")).openStream();
            bitmapImage = BitmapFactory.decodeStream(in);
            return bitmapImage;
        } catch (Exception e) {
            //TODO: React to error
        }
        return bitmapImage;
    }
    public void persistObject(String key, Object obj) throws IOException {
        InternalStorage.writeObject(this, key, obj);
    }

    public Object getObject(String key) throws IOException, ClassNotFoundException {
        return InternalStorage.readObject(this, key);
    }
}