package com.flatflatching.flatflatching.activities;

import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.models.Address;
import com.flatflatching.flatflatching.models.Flat;
import com.flatflatching.flatflatching.services.AuthenticatorService;
import com.flatflatching.flatflatching.services.FlatService;

import java.io.IOException;

public class CreateFlatActivity extends BaseActivity {

    private EditText flatTitleEditText;
    private EditText streetNameEditText;
    private EditText streetNumberEditText;
    private EditText cityEditText;
    private EditText postCodeEditText;
    private BaseActivity self;
    private String chosenEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flat);
        self = this;
        layoutContainer = (RelativeLayout) findViewById(R.id.createFlatLayoutContainer);
        flatTitleEditText = (EditText) findViewById(R.id.editTextFlatTitle);
        streetNameEditText = (EditText) findViewById(R.id.editTextStreetName);
        streetNumberEditText = (EditText) findViewById(R.id.editTextStreetNumber);
        cityEditText = (EditText) findViewById(R.id.editTextCityName);
        postCodeEditText = (EditText) findViewById(R.id.editTextPostCode);
        messageShower = (TextView) findViewById(R.id.textViewNewFlatTitle);
        final Button createFlatButton = (Button) findViewById(R.id.buttonCreateFlat);
        createFlatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createFlat();


                }
            }
        );
    }

    private void createFlat() {
        chosenEmail = getUserEmail();
        if(chosenEmail.isEmpty()) {
            pickUserAccount();
        } else {
            Flat flat;
            try {
                flat = parseFlat();
            } catch(IOException e) {
                Snackbar.make(findViewById(android.R.id.content), "Gib der WG einen Namen", Snackbar.LENGTH_LONG)
                        .show();
                return;
            }
            registerFlat(flat);
        }
    }
    private void registerFlat(Flat flat) {
        if(hasConnection()){
            FlatService.createFlat(self, chosenEmail, flat);
            AuthenticatorService.getAuth(self, chosenEmail);
        }
        else{
            notifyError(R.string.connection_error);
        }
    }

    private Flat parseFlat() throws IOException {
        final String flatName = flatTitleEditText.getText().toString();
        final String streetName = streetNameEditText.getText().toString();
        final String streetNumber = streetNumberEditText.getText().toString();
        final String city = cityEditText.getText().toString();
        final String postCode = postCodeEditText.getText().toString();
        if(flatName.isEmpty()) {
            throw new IOException("No Flatname given");
        }
        else if(!streetName.isEmpty() && !streetNumber.isEmpty() && !city.isEmpty() && !postCode.isEmpty()) {
            final Address address = new Address(streetName, streetNumber, city, postCode);
            return new Flat(flatName, address);
        } else {
            return new Flat(flatName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BaseActivity.REQUEST_CODE_PICK_ACCOUNT || requestCode == BaseActivity.REQUEST_PERMISSION){
            if(resultCode == RESULT_OK){
                String selectedAccountEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                settings.edit().putString(BaseActivity.CHOSEN_USER_EMAIL, selectedAccountEmail);
                settings.edit().apply();
                createFlat();
            }
            else if(resultCode == RESULT_CANCELED){
                notifyError(R.string.internal_error);
            }
        }
    }

}
