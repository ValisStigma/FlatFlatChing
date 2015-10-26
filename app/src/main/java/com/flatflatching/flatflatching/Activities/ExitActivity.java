package com.flatflatching.flatflatching.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.services.FlatService;

import java.io.IOException;

public class ExitActivity extends BaseActivity {

    private ProgressBar progressBar;
    private Button exitButton;
    private BaseActivity self;
    private RelativeLayout exitFlatContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        exitButton = (Button) findViewById(R.id.buttonExitFlat);
        progressBar = (ProgressBar) findViewById(R.id.progressBarExit);
        layoutContainer = (LinearLayout) findViewById(R.id.layoutContainerExit);
        messageShower = (TextView) findViewById(R.id.messageShower);
        exitFlatContainer = (RelativeLayout) findViewById(R.id.exitFlatMessageContainer);
        exitButton.setOnClickListener(checkForDeletion());
        setupNavigation();
        customizeNavigation();
        self = this;
    }

    @Override
    public void setWaitingLayout() {
        progressBar.setVisibility(View.VISIBLE);
        exitFlatContainer.setVisibility(View.INVISIBLE);
    }

    @Override
    public void reactToSuccess() {

        persistToPreferences(BaseActivity.FLAT_ID, "");
        try {
            persistObject(BaseActivity.FLAT, null);
        } catch (IOException e) {
            Log.d("didnt work", "didnt work");
        }
        Intent intent = new Intent(self, FlatActivity.class);
        intent.putExtra(BaseActivity.INTENT_EXTRAS, BaseActivity.USER_EXITED);
        startActivity(intent);
    }

    @Override
    public void checkPreConditions() {
        persistToPreferences(BaseActivity.FLAT_ID, "");
        try {
            persistObject(BaseActivity.FLAT, null);
        } catch (IOException e) {
            Log.d("didnt work", "didnt work");
        }
        Intent intent = new Intent(self, FlatActivity.class);
        intent.putExtra(BaseActivity.INTENT_EXTRAS, BaseActivity.USER_EXITED);
        startActivity(intent);
    }

    public View.OnClickListener checkForDeletion() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(self);
                builder.setMessage(R.string.confirmExitString);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteUser(getUserEmail());
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        };
    }

    private void deleteUser(String userEmail) {
        setWaitingLayout();
        FlatService.exitFlat(self, getFlatId(), userEmail);
    }
}
