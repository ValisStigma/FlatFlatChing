package com.flatflatching.flatflatching.activities;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.services.AuthenticatorService;

public class FlatActivity extends BaseActivity {

    private BaseActivity self = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat);
        Button shoppingListButton = (Button) findViewById(R.id.shoppingListButton);
        Button expensesButton = (Button) findViewById(R.id.expensesButton);
        Button flatMateButton = (Button) findViewById(R.id.flatMatesButton);
        messageShower = (TextView) findViewById(R.id.messageShower);
        layoutContainer = (ViewGroup) findViewById(R.id.baseContainer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_own);
        setSupportActionBar(toolbar);
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
        checkForAuthentication();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BaseActivity.REQUEST_CODE_PICK_ACCOUNT || requestCode == BaseActivity.REQUEST_PERMISSION){
            if(resultCode == RESULT_OK){
                String selectedAccountEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                settings.edit().putString(BaseActivity.CHOSEN_USER_EMAIL, selectedAccountEmail);
                settings.edit().apply();
                if(hasConnection()){
                    AuthenticatorService.getAuth(self, selectedAccountEmail);
                }
                else{
                    notifyError(R.string.connection_error);
                }
            }
            else if(resultCode == RESULT_CANCELED){
                notifyError(R.string.internal_error);
            }
        }
    }
}
