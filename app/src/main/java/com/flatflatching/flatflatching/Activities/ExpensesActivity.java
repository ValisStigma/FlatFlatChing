package com.flatflatching.flatflatching.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.adapter.ContributorsAdapter;
import com.flatflatching.flatflatching.adapter.ExpensesAdapter;
import com.flatflatching.flatflatching.helpers.SnackBarStyler;
import com.flatflatching.flatflatching.models.Expense;
import com.flatflatching.flatflatching.models.FlatMate;
import com.flatflatching.flatflatching.services.ExpenseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ExpensesActivity extends BaseActivity {

    private boolean mTwoPane;
    private ProgressBar progressBar;
    private RecyclerView cardListExpenses;
    private RelativeLayout noExpenseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_own);
        setSupportActionBar(toolbar);
        messageShower = (TextView) findViewById(R.id.messageShower);
        layoutContainer = (LinearLayout) findViewById(R.id.expensesBackgroundLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBarExpenses);
        cardListExpenses = (RecyclerView) findViewById(R.id.cardListExpenses);
        noExpenseView = (RelativeLayout) findViewById(R.id.no_expenses_view);
        Button goToNewExpenseButton = (Button) findViewById(R.id.buttonGoToExpenses);
        final Activity self = this;
        goToNewExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(self, NewExpenseActivity.class);
                startActivity(intent);

            }
        });
        setWaitingLayout();
        checkPreConditions();
/*        if (findViewById(R.id.expense_detail_container) != null) {

            mTwoPane = true;

            ((ExpenseListFragment) getSupportFragmentManager().findFragmentById(
                    R.id.expense_list)).setActivateOnItemClick(true);
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int code = extras.getInt(BaseActivity.INTENT_EXTRAS);
            switch (code) {
                case BaseActivity.EXPENSE_WAS_CREATED:
                    notifyExpenseCreation();
                    break;
            }
        }
    }

    @Override
    public void setWaitingLayout() {
        //progressBar.setVisibility(View.VISIBLE);
        //cardListExpenses.setVisibility(View.GONE);
    }

    @Override
    public void reactToSuccess() {
        progressBar.setVisibility(View.GONE);
        cardListExpenses.setVisibility(View.VISIBLE);
    }

    @Override
    public void checkPreConditions() {
        loadExpenses();

    }

    private void loadExpenses() {
        ExpenseService.getExpenses(this, getFlatId(), getUserEmail());
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

    protected void notifyExpenseCreation() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.expense_created_message, Snackbar.LENGTH_LONG);
        SnackBarStyler.confirm(snackbar, this).show();
        messageShower.setVisibility(View.GONE);
    }

    @Override
    public void reactToGet(Object response) {
        List<Expense> expenses = (List<Expense>) response;
        if(expenses == null || expenses.size() < 1) {
            noExpenseView.setVisibility(View.VISIBLE);
        } else {
            noExpenseView.setVisibility(View.GONE);
            cardListExpenses.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            cardListExpenses.setLayoutManager(llm);
            ExpensesAdapter ea = new ExpensesAdapter(expenses);
            cardListExpenses.setAdapter(ea);
            progressBar.setVisibility(View.GONE);
            cardListExpenses.setVisibility(View.VISIBLE);
        }

    }
}
