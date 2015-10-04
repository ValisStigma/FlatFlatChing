package com.flatflatching.flatflatching.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flatflatching.flatflatching.helpers.RequestBuilder;
import com.flatflatching.flatflatching.helpers.TouchDetector;
import com.flatflatching.flatflatching.services.UserProfileService;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.UUID;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String PREFERENCES = "local_preferences";
    public static final String UUID = "UUID";
    public static final String USERNAME = "USERNAME";
    public static final String ACCOUNT_TOKEN = "ACCOUNT_TOKEN";
    public static final String FLAT_ID = "FLAT_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final int REQUEST_PERMISSION = 1111;
    public static final String CHANGE_NAME = "ch.hsr.surveyapp.CHANGE_NAME";
    public static final String RESET_USERNAME = "userNameWasSetButNotSent";
    public static final String CHANGE = "change";
    public static final int[] RADIUS_DIST = {5, 10, 30, 100, 0};
    public static final String SURVEY_DATA = "SurveyData";
    public static final String USER_NAME_GIVEN = "USER_NAME_GIVEN";
    public static final String USER_NAME_FAMILY = "USER_NAME_FAMILY";
    public static final String USER_PROFILE_LINK = "USER_PROFILE_LINK";
    static final int REQUEST_CODE_PICK_ACCOUNT = 1000;

    protected SharedPreferences settings;
    protected String uuid;
    protected String accountToken;
    protected String userName;
    protected String flatId;
    protected RequestBuilder requestBuilder;
    protected TouchDetector touchDetector;
    protected TextView titleTextView;
    protected ViewGroup container;

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
        final SharedPreferences.Editor editor = settings.edit();
        uuid = newUuid;
        editor.putString(UUID, uuid);
        editor.apply();
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
     * @param textView the textView in which the errormessage shall be shown
     * @param container the basic viewgroup which holds all other gui-elements
     * @param stringId the id of the error-message
     */
    public void notifyError(final TextView textView, final ViewGroup container, final int stringId) {
        for (int i = 0; i < container.getChildCount(); i++) {
            container.getChildAt(i).setVisibility(View.GONE);
        }
        textView.setVisibility(View.VISIBLE);
        textView.setText(stringId);
    }

//    protected void registerUserName() {
//        final Intent surveyIntent = new Intent(this,
//                EnterNameActivity.class);
//        surveyIntent.putExtra(CHANGE_NAME, "");
//        startActivity(surveyIntent);
//    }
//
//
//
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
//
//    protected void changeUserName() {
//        final Intent surveyIntent = new Intent(this,
//                EnterNameActivity.class);
//        surveyIntent.putExtra(CHANGE_NAME, "change");
//        startActivity(surveyIntent);
//    }



    private void pickUserAccount() {
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
}
