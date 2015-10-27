package com.flatflatching.flatflatching.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.adapter.FlatMateAdapter;
import com.flatflatching.flatflatching.helpers.SnackBarStyler;
import com.flatflatching.flatflatching.models.FlatMate;
import com.flatflatching.flatflatching.services.FlatService;

import java.io.IOException;
import java.util.List;

public final class ManageFlatMatesActivity extends BaseActivity {

    private EditText newFlatMateEmail;
    private BaseActivity self;
    private RelativeLayout noFlatMatesView;
    private RecyclerView flatMatesList;
    private ProgressBar progressBar;
    private LinearLayout invitationGui;
    private FlatMateAdapter flatMateAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_flat_mates);
        self = this;
        final Button inviteFlatMate = (Button) findViewById(R.id.buttonInviteFlatMate);
        newFlatMateEmail = (EditText) findViewById(R.id.editTextFlatMateEmail);
        layoutContainer = (LinearLayout) findViewById(R.id.layoutContainerFlatMate);
        noFlatMatesView = (RelativeLayout) findViewById(R.id.no_flat_mates_view);
        flatMatesList = (RecyclerView) findViewById(R.id.cardListContributors);
        invitationGui = (LinearLayout) findViewById(R.id.linearLayoutInvitation);
        progressBar = (ProgressBar) findViewById(R.id.progressBarInvitation);
        setupContributors();
        messageShower = (TextView) findViewById(R.id.messageShower);
        setupNavigation();
        customizeNavigation();
        inviteFlatMate.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  InputMethodManager imm = (InputMethodManager)getSystemService(
                          Context.INPUT_METHOD_SERVICE);
                  imm.hideSoftInputFromWindow(newFlatMateEmail.getWindowToken(), 0);
                  final String emailAddress = newFlatMateEmail.getText().toString();
                  final String flatId = settings.getString(BaseActivity.FLAT_ID, "");

                  if(android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches() && !flatId.isEmpty()) {
                      setWaitingLayout();
                      FlatService.inviteFlatMate(self, flatId , emailAddress );
                  } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                      SnackBarStyler.makeAlertSnackBar(self, R.string.error_invalid_email);
                  }
              }
          }

        );

    }

    private void setupContributors() {
        List<FlatMate> flatMates = null;
        try {
            flatMates = getFlatMates();
        } catch (IOException |ClassNotFoundException e) {
            noFlatMatesView.setVisibility(View.VISIBLE);
            flatMatesList.setVisibility(View.GONE);
        }
        if(flatMates != null ) {
            if(flatMates.size() > 0) {
                noFlatMatesView.setVisibility(View.GONE);
                flatMatesList.setVisibility(View.VISIBLE);
                flatMatesList.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                flatMatesList.setLayoutManager(llm);
                flatMateAdapter = new FlatMateAdapter(flatMates, checkForDeletion());
                flatMatesList.setAdapter(flatMateAdapter);
            } else if(flatMates.size() > 2) {
                noFlatMatesView.setVisibility(View.GONE);
                flatMatesList.setVisibility(View.VISIBLE);
                flatMatesList.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                flatMatesList.setLayoutManager(llm);
                flatMateAdapter = new FlatMateAdapter(flatMates, checkForDeletion());
                flatMatesList.setAdapter(flatMateAdapter);
            }
        } else {
            noFlatMatesView.setVisibility(View.VISIBLE);
            flatMatesList.setVisibility(View.GONE);
        }


    }

    @Override
    public void setWaitingLayout() {
        progressBar.setVisibility(View.VISIBLE);
        invitationGui.setVisibility(View.INVISIBLE);
    }

    @Override
    public void reactToSuccess() {
        invitationGui.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        newFlatMateEmail.setText("");
        SnackBarStyler.makeConfirmSnackBar(self, R.string.invitation_sent);
        messageShower.setVisibility(View.GONE);
    }

    public View.OnClickListener checkForDeletion() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View flatMateCard = (View) v.getParent();
                TextView userEmailView = (TextView)flatMateCard.findViewById(R.id.flatMateEmailTextView);
                TextView positionView = (TextView)flatMateCard.findViewById(R.id.flatMatePosition);
                final int position = Integer.parseInt(positionView.getText().toString());
                final String userEmail = userEmailView.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(self);
                builder.setMessage(R.string.delete_user_confirmation);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteUser(userEmail, position);
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
    }

    private void deleteUser(String userEmail, int position) {
        setWaitingLayout();
        FlatService.exitFlat(this, getFlatId(), userEmail);
        flatMateAdapter.removeItem(position);
    }

    @Override
    public void checkPreConditions() {
        invitationGui.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        SnackBarStyler.makeConfirmSnackBar(self, R.string.deletet_flatmate);
        messageShower.setVisibility(View.GONE);
    }

}
