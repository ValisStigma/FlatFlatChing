package com.flatflatching.flatflatching.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.adapter.ExpensesAdapter;
import com.flatflatching.flatflatching.helpers.SnackBarStyler;
import com.flatflatching.flatflatching.models.Expense;
import com.flatflatching.flatflatching.services.ExpenseService;
import java.util.List;

public final class ExpensesActivity extends BaseActivity {

    private ProgressBar progressBar;
    private RecyclerView cardListExpenses;
    private RelativeLayout noExpenseView;
    private BaseActivity self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        messageShower = (TextView) findViewById(R.id.messageShower);
        layoutContainer = (LinearLayout) findViewById(R.id.expensesBackgroundLayout);
        progressBar = (ProgressBar) findViewById(R.id.progressBarExpenses);
        cardListExpenses = (RecyclerView) findViewById(R.id.cardListExpenses);
        noExpenseView = (RelativeLayout) findViewById(R.id.no_expenses_view);
        Button goToNewExpenseButton = (Button) findViewById(R.id.buttonGoToExpenses);
        self = this;
        final Activity self = this;
        goToNewExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(self, NewExpenseActivity.class);
                startActivity(intent);
                self.finish();

            }
        });
        setWaitingLayout();
        checkPreConditions();
        setupNavigation();
        customizeNavigation();
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
        progressBar.setVisibility(View.VISIBLE);
        cardListExpenses.setVisibility(View.GONE);
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
        SnackBarStyler.makeConfirmSnackBar(this, R.string.expense_created_message);
        messageShower.setVisibility(View.GONE);
    }

    @Override
    public void reactToGet(Object response) {
        List<Expense> expenses = (List<Expense>) response;
        if(expenses == null || expenses.size() < 1) {
            noExpenseView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            noExpenseView.setVisibility(View.GONE);
            cardListExpenses.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            cardListExpenses.setLayoutManager(llm);
            ExpensesAdapter ea = new ExpensesAdapter(expenses, payExpense(), showExpense());
            cardListExpenses.setAdapter(ea);
            progressBar.setVisibility(View.GONE);
            cardListExpenses.setVisibility(View.VISIBLE);
        }

    }

    public View.OnClickListener payExpense() {
        return  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View expenseCard = (View)v.getParent();
                TextView idText = (TextView) expenseCard.findViewById(R.id.expense_id);
                String id = idText.getText().toString();
                TextView typeText = (TextView) expenseCard.findViewById(R.id.expense_type);
                switch (typeText.getText().toString()) {
                    case "Variable":
                        setWaitingLayout();
                        ExpenseService.payBackStaticExpense(self, getFlatId(), id, getUserEmail(), Expense.ExpenseType.Variable);
                        break;
                    case "Static":
                        setWaitingLayout();
                        ExpenseService.payBackStaticExpense(self, getFlatId(), id, getUserEmail(), Expense.ExpenseType.Static);
                        break;
                }

            }
        };
    }

    public View.OnClickListener showExpense() {
        return  new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };
    }
}
