package com.flatflatching.flatflatching.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.services.FlatService;

public final class ManageFlatMatesActivity extends BaseActivity {

    private EditText newFlatMateEmail;
    private BaseActivity self;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_flat_mates);
        self = this;
        final Button inviteFlatMate = (Button) findViewById(R.id.buttonInviteFlatMate);
        newFlatMateEmail = (EditText) findViewById(R.id.editTextFlatMateEmail);
        layoutContainer = (LinearLayout) findViewById(R.id.layoutContainerFlatMate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_own);
        setSupportActionBar(toolbar);
        messageShower = (TextView) findViewById(R.id.textViewNewFlatMate);
        inviteFlatMate.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  InputMethodManager imm = (InputMethodManager)getSystemService(
                          Context.INPUT_METHOD_SERVICE);
                  imm.hideSoftInputFromWindow(newFlatMateEmail.getWindowToken(), 0);
                  final String emailAddress = newFlatMateEmail.getText().toString();
                  final String flatId = settings.getString(BaseActivity.FLAT_ID, "");

                  if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches() && !flatId.isEmpty()) {
                      FlatService.inviteFlatMate(self, flatId , emailAddress );
                  } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                      Snackbar.make(findViewById(android.R.id.content), "Ungültige Emailadresse", Snackbar.LENGTH_LONG)
                              .show();
                  } else {
                      register(emailAddress);
                  }
              }
          }

        );

    }

    @Override
    public void setWaitingLayout() {

    }

    @Override
    public void reactToSuccess() {

    }

    @Override
    public void checkPreConditions() {

    }

}
