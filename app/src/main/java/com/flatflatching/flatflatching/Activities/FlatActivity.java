package com.flatflatching.flatflatching.activities;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.services.AuthenticatorService;

public class FlatActivity extends BaseActivity {

    private Activity self = this;
    private TextView titleTextView;
    private ViewGroup viewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat);
        Button shoppingListButton = (Button) findViewById(R.id.shoppingListButton);
        Button expensesButton = (Button) findViewById(R.id.expensesButton);
        Button flateMateButtion = (Button) findViewById(R.id.flatMatesButton);
        titleTextView = (TextView) findViewById(R.id.messageShower);
        viewContainer = (ViewGroup) findViewById(R.id.baseContainer);

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
        flateMateButtion.setOnClickListener(new View.OnClickListener() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flat, menu);
        return true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BaseActivity.REQUEST_CODE_PICK_ACCOUNT || requestCode == BaseActivity.REQUEST_PERMISSION){
            if(resultCode == RESULT_OK){
                String selectedAccountEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                settings.edit().putString(BaseActivity.CHOSEN_USER_EMAIL, selectedAccountEmail);
                settings.edit().apply();
                if(hasConnection()){
                    AuthenticatorService.getAuth(self, titleTextView, viewContainer, selectedAccountEmail);
                }
                else{
                    notifyError(titleTextView, viewContainer, R.string.connection_error);
                }
            }
            else if(resultCode == RESULT_CANCELED){
                notifyError(titleTextView, viewContainer, R.string.internal_error);
            }
        }
    }
}
