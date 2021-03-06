package com.flatflatching.flatflatching.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.helpers.SnackBarStyler;
import com.flatflatching.flatflatching.models.Address;
import com.flatflatching.flatflatching.models.Flat;
import com.flatflatching.flatflatching.services.FlatService;

import java.io.IOException;

public final class CreateFlatActivity extends BaseActivity {

    private EditText flatTitleEditText;
    private EditText streetNameEditText;
    private EditText streetNumberEditText;
    private EditText cityEditText;
    private EditText postCodeEditText;
    private BaseActivity self;
    private String chosenEmail;
    private Button createFlatButton;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flat);
        self = this;
        chosenEmail = getUserEmail();
        layoutContainer = (LinearLayout) findViewById(R.id.createFlatLayoutContainer);
        flatTitleEditText = (EditText) findViewById(R.id.editTextFlatTitle);
        streetNameEditText = (EditText) findViewById(R.id.editTextStreetName);
        streetNumberEditText = (EditText) findViewById(R.id.editTextStreetNumber);
        cityEditText = (EditText) findViewById(R.id.editTextCityName);
        postCodeEditText = (EditText) findViewById(R.id.editTextPostCode);
        messageShower = (TextView) findViewById(R.id.textViewMessageHolder);
        progressBar = (ProgressBar) findViewById(R.id.progressBarCreateFlat);
        createFlatButton = (Button) findViewById(R.id.buttonCreateFlat);
        createFlatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createFlat();

                }
            }
        );
        setupNavigation();

        customizeNavigation();
    }

    @Override
    public void setWaitingLayout() {
        createFlatButton.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void reactToSuccess() {
        Intent intent = new Intent(self, FlatActivity.class);
        intent.putExtra(BaseActivity.INTENT_EXTRAS, BaseActivity.FLAT_WAS_CREATED);
        startActivity(intent);
    }

    @Override
    public void checkPreConditions() {

    }

    private void createFlat() {
        Flat flat;
        try {
            flat = parseFlat();
        } catch(IOException e) {
            SnackBarStyler.makeAlertSnackBar(self, R.string.error_no_name);
            return;
        }
        registerFlat(flat);
    }
    private void registerFlat(Flat flat) {
        if(hasConnection()){
            setWaitingLayout();
            FlatService.createFlat(self, chosenEmail, flat);

        }
        else{
            notifyError(getResources().getString(R.string.connection_error));
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
}
