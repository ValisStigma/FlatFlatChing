package com.flatflatching.flatflatching.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.helpers.InternalStorage;
import com.flatflatching.flatflatching.helpers.SerialBitmap;
import com.flatflatching.flatflatching.helpers.SnackBarStyler;
import com.flatflatching.flatflatching.models.Flat;
import com.flatflatching.flatflatching.models.FlatMate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BaseActivity self;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Menu navigationDrawerMenu;
    public static final int EXPENSES_SCREEN_INDEX = 1;
    public static final int FLATMATES_SCREEN_INDEX = 2;
    public static final int EXIT_SCREEN_INDEX = 3;

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
    public static final int USER_EXITED = 7777;
    public static final String FLAT_USER = "FLAT_USER";
    public static final String INTENT_EXTRAS = "INTENT_EXTRAS";
    public static final String BASE_URL = "http://152.96.239.56:3000/%s";
    public static final String PROFILE_BITMAP = "PROFILE_BITMAP";
    protected SharedPreferences settings;
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
        int id = item.getItemId();
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

    public final String getUserEmail() {
        return settings.getString(CHOSEN_USER_EMAIL, "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if(toolbar == null) {
                toolbar = (Toolbar) findViewById(R.id.tool_bar_own);

            }
            toolbar.setTitle(getFlatName());
        } catch (IOException|ClassNotFoundException e) {
            toolbar.setTitle(R.string.flatName);
        }

    }
    @Override
    protected void onCreate(final Bundle savedState) {
        super.onCreate(savedState);
        self = this;
        settings = getSharedPreferences(PREFERENCES, 0);

    }

    void customizeNavigation() {
        TextView userName = (TextView) findViewById(R.id.usernameHeader);
        if(userName != null) {
            userName.setText(getUserName());
        }
        CircleImageView circleImageView = (CircleImageView) findViewById(R.id.profile_image);
        try {
            if(circleImageView != null){
                circleImageView.setImageBitmap(getProfileImage());
            }
        } catch (IOException|ClassNotFoundException e) {
            Log.d("parse_error", "unable to find image");
        }
    }

    void setupNavigation() {

        toolbar = (Toolbar) findViewById(R.id.tool_bar_own);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if(menuItem.isChecked()){
                    menuItem.setChecked(false);
                }
                else {
                    menuItem.setChecked(true);
                }
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.homeScreenDrawerItem:
                        Intent intent4 = new Intent(self, FlatActivity.class);
                        startActivity(intent4);
                        self.finish();
                        return true;
                    case R.id.expensesDrawerItem:
                        Intent intent = new Intent(self, ExpensesActivity.class);
                        startActivity(intent);
                        self.finish();
                        return true;
                    case R.id.flatmatesDrawerItem:
                        Intent intent2 = new Intent(self, ManageFlatMatesActivity.class);
                        startActivity(intent2);
                        self.finish();
                        return true;
                    case R.id.leaveFlatDrawerItem:
                        Intent intent3 = new Intent(self, ExitActivity.class);
                        startActivity(intent3);
                        return true;

                    default:
                        SnackBarStyler.makeConfirmSnackBar(self, R.string.internal_error);
                        messageShower.setVisibility(View.GONE);
                        return true;
                }
            }
        });
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!isAdmin()) {
                    hideMenuItem(FLATMATES_SCREEN_INDEX);
                } else {
                    showMenuItem(FLATMATES_SCREEN_INDEX);
                }
                if(!isFlatMember()) {
                    hideMenuItem(EXIT_SCREEN_INDEX);
                    hideMenuItem(EXPENSES_SCREEN_INDEX);
                } else {
                    showMenuItem(EXIT_SCREEN_INDEX);
                    showMenuItem(EXPENSES_SCREEN_INDEX);
                }
                customizeNavigation();
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        navigationDrawerMenu = navigationView.getMenu();
        actionBarDrawerToggle.syncState();
    }

    /**Tests if the activity gets internet connection.
     * @return Boolean if connection is available
     */
    public final boolean hasConnection() {
        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }


    public void hideMenuItem(int index) {
        MenuItem item = navigationDrawerMenu.getItem(index);
        item.setVisible(false);
    }

    public void showMenuItem(int index) {
        MenuItem item = navigationDrawerMenu.getItem(index);
        item.setVisible(true);
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
        String userName = settings.getString(USER_NAME_GIVEN, "");
        return !userName.isEmpty();
    }

    protected final String getFlatName() throws IOException, ClassNotFoundException {
        Flat myFlat =(Flat)getObject(BaseActivity.FLAT);
        if(myFlat == null)  {
            throw new IOException("Flat is empty");
        }
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
        return settings.getString(FLAT_ID, "");
    }
    protected final boolean isAdmin() {
        FlatMate user;
        try {
            user = (FlatMate)getObject(FLAT_USER);
        } catch (IOException|ClassNotFoundException e) {
            return false;
        }
        return user.isAdmin();
    }

    protected final String getUserName() {
        return settings.getString(USER_NAME_GIVEN, "Superman");
    }

    protected final Bitmap getProfileImage() throws IOException, ClassNotFoundException {
        return ((SerialBitmap) getObject(BaseActivity.PROFILE_BITMAP)).getImage();
    }
    public abstract void setWaitingLayout();
    public abstract void reactToSuccess();
    public abstract void checkPreConditions();

    public void reactToGet(Object response) {

    }
    public void persistFlatUser(FlatMate flatMate) throws IOException {
        persistObject(FLAT_USER, flatMate);
    }

    public void setCurrentUserAdmin() throws IOException, ClassNotFoundException {
        FlatMate user = (FlatMate) getObject(FLAT_USER);
        user.setIsAdmin(true);
        persistObject(FLAT_USER, user);
    }
}