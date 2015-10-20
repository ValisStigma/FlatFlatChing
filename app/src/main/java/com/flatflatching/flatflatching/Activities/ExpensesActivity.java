package com.flatflatching.flatflatching.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;

public final class ExpensesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_own);
        setSupportActionBar(toolbar);
        messageShower = (TextView) findViewById(R.id.messageShower);
        layoutContainer = (LinearLayout) findViewById(R.id.expensesBackgroundLayout);
        Button goToNewExpenseButton = (Button) findViewById(R.id.buttonGoToExpenses);
        final Activity self = this;
        goToNewExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(self, NewExpenseActivity.class);
                startActivity(intent);

            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_expenses, menu);
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
}
