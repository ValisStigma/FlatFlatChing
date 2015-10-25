package com.flatflatching.flatflatching.activities;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.helpers.SnackBarStyler;
import com.flatflatching.flatflatching.models.FlatMate;
import com.flatflatching.flatflatching.services.AuthenticatorService;
import com.flatflatching.flatflatching.services.FlatService;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import java.io.IOException;
import java.util.List;

public final class FlatActivity extends BaseActivity {

    private BaseActivity self = this;
    private View expenseButtonLayout;
    private View shoppingListButtonLayout;
    private View flatMateButtonLayout;
    private  View createFlatButtonLayout;
    private  ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat);
        Button shoppingListButton = (Button) findViewById(R.id.shoppingListButton);
        Button expensesButton = (Button) findViewById(R.id.expensesButton);
        Button flatMateButton = (Button) findViewById(R.id.flatMatesButton);
        Button createFlatButton = (Button) findViewById(R.id.createFlatButton);
        messageShower = (TextView) findViewById(R.id.messageShower);
        layoutContainer = (ViewGroup) findViewById(R.id.baseContainer);
        shoppingListButtonLayout = findViewById(R.id.layoutShoppingListButton);
        expenseButtonLayout = findViewById(R.id.layoutExpenseButton);
        flatMateButtonLayout = findViewById(R.id.layoutFlatMatesButton);
        createFlatButtonLayout = findViewById(R.id.layoutCreateFlatButton);
        progressBar = (ProgressBar) findViewById(R.id.progressBarFlatActivity);
        shoppingListButtonLayout.setVisibility(View.GONE);
        expenseButtonLayout.setVisibility(View.GONE);
        flatMateButtonLayout.setVisibility(View.GONE);
        createFlatButtonLayout.setVisibility(View.GONE);

        shoppingListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(self, ShoppingListActivity.class);
                startActivity(intent);
            }
        });

        expensesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(self, ExpensesActivity.class);
                startActivity(intent);
            }
        });
        flatMateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(self, ManageFlatMatesActivity.class);
                startActivity(intent);
            }
        });
        createFlatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(self, CreateFlatActivity.class);
                startActivity(intent);
            }
        });
        saveFlatMates();
        checkPreConditions();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int code = extras.getInt(BaseActivity.INTENT_EXTRAS);
            switch (code) {
                case BaseActivity.FLAT_WAS_CREATED:
                    notifyFlatCreation();
                    FlatService.setAdmin(this, getUserEmail());
                break;
            }
        }
    }

    @Override
    public void checkPreConditions() {
        setWaitingLayout();
        if(isAuthenticated()) {
            AuthenticatorService.register(self, getUserEmail());
            checkForFlat();
        } else {
            tryAuthenticate();
        }
    }

    private void saveFlatMates() {
        FlatService.getFlatMemberInfo(this, getFlatId());
    }

    @Override
    public void setWaitingLayout() {
        createFlatButtonLayout.setVisibility(View.GONE);
        flatMateButtonLayout.setVisibility(View.GONE);
        expenseButtonLayout.setVisibility(View.GONE);
        shoppingListButtonLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void reactToSuccess() {
        progressBar.setVisibility(View.GONE);
    }

    protected boolean checkForFlat() {
        if(isFlatMember()) {
            reactToSuccess();
            FlatService.getFlatInfo(this, getFlatId());
            expenseButtonLayout.setVisibility(View.VISIBLE);
            //shoppingListButtonLayout.setVisibility(View.VISIBLE);
            if(isAdmin()) {
                flatMateButtonLayout.setVisibility(View.VISIBLE);
            }
            return true;
        } else {
            reactToSuccess();
            createFlatButtonLayout.setVisibility(View.VISIBLE);
            //TODO: Check for invites
            messageShower.setText(R.string.without_flat_action_description);
            messageShower.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BaseActivity.REQUEST_CODE_PICK_ACCOUNT || requestCode == BaseActivity.REQUEST_PERMISSION){
            if(resultCode == RESULT_OK){
                String selectedAccountEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                persistToPreferences(BaseActivity.CHOSEN_USER_EMAIL, selectedAccountEmail);
                if(hasConnection()){
                    AuthenticatorService.getAuth(self, selectedAccountEmail);
                }
                else{
                    notifyError(getResources().getString(R.string.connection_error));
                }
            }
            else if(resultCode == RESULT_CANCELED){
                notifyError(getResources().getString(R.string.internal_error));
            }
        } else if(requestCode == BaseActivity.FLAT_WAS_CREATED) {
            notifyFlatCreation();
        }
    }

    protected void tryAuthenticate() {
        if(hasConnection()) {
            if(isGoogleServicesAvailable()) {
                startAccountPickingNotification();
            } else {
                notifyError(getResources().getString(R.string.server_error));
            }
        } else {
            notifyError(getResources().getString(R.string.connection_error));
        }
    }

    protected void pickUserAccount() {
        Intent googlePicker = AccountPicker.newChooseAccountIntent(null, null,
                new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, true, null, null, null, null);
        startActivityForResult(googlePicker, BaseActivity.REQUEST_CODE_PICK_ACCOUNT);
    }

    protected void startAccountPickingNotification() {
        AlertDialog.Builder builder = new AlertDialog.Builder(self);
        builder.setMessage(R.string.login_text);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                pickUserAccount();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected void notifyFlatCreation() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.alert_flat_created, Snackbar.LENGTH_LONG);
        SnackBarStyler.confirm(snackbar, this).show();
        messageShower.setVisibility(View.GONE);
        expenseButtonLayout.setVisibility(View.VISIBLE);
        //shoppingListButtonLayout.setVisibility(View.VISIBLE);
        if(isAdmin()) {
            flatMateButtonLayout.setVisibility(View.VISIBLE);
        }
        createFlatButtonLayout.setVisibility(View.GONE);
    }
}
