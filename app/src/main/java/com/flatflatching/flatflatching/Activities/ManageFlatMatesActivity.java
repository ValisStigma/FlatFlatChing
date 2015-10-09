package com.flatflatching.flatflatching.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.services.FlatService;

public class ManageFlatMatesActivity extends BaseActivity {

    private EditText newFlatMateEmail;
    private BaseActivity self;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_flat_mates);
        self = this;
        final Button inviteFlatMate = (Button) findViewById(R.id.buttonInviteFlatMate);
        newFlatMateEmail = (EditText) findViewById(R.id.editTextFlatMateEmail);
        layoutContainer = (RelativeLayout) findViewById(R.id.layoutContainerFlatMate);
        messageShower = (TextView) findViewById(R.id.textViewNewFlatMate);
        inviteFlatMate.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  final String emailAddress = newFlatMateEmail.toString();
                  final String flatId = settings.getString(BaseActivity.FLAT_ID, "");

                  if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches() && !flatId.isEmpty()) {
                        FlatService.inviteFlatMate(self, flatId , emailAddress );
                  } else if(!flatId.isEmpty()) {
                      Snackbar.make(findViewById(android.R.id.content), "Ungültige Emailadresse", Snackbar.LENGTH_LONG)
                              .show();
                  } else {
                      register(emailAddress);
                  }
              }
          }

        );

    }

}
