package com.flatflatching.flatflatching.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.adapter.ContributorsAdapter;
import com.flatflatching.flatflatching.helpers.SnackBarStyler;
import com.flatflatching.flatflatching.models.Expense;
import com.flatflatching.flatflatching.models.FlatMate;
import com.flatflatching.flatflatching.models.StaticExpense;
import com.flatflatching.flatflatching.models.StaticUserExpense;
import com.flatflatching.flatflatching.models.VariableExpense;
import com.flatflatching.flatflatching.services.ExpenseService;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NewExpenseActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{

    private CheckBox staticExpenseCheckBox;
    private LinearLayout staticExpenseArea;
    private EditText descriptionEditText;
    private EditText amountEditText;
    private EditText dateEditText;
    private DatePickerDialog dueDateDialog;
    private Button createExpenseButton;
    private RecyclerView contributorsGui;
    private ProgressBar progressBar;
    private TextView contributorsTextView;
    private RelativeLayout noFlatMatesView;
    private BaseActivity self;
    private int currentInterval = StaticExpense.SECONDS_IN_MONTH;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_new_expense);
        registerGuiElements();
        setupSpinner();
        setupContributors();
        createExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processExpense();
            }
        });
        staticExpenseCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox) v).isChecked()) {
                    staticExpenseArea.setVisibility(View.VISIBLE);
                } else {
                    staticExpenseArea.setVisibility(View.INVISIBLE);
                }
            }
        });
        setupDatePicker();
        checkPreConditions();
    }



    private void setupDatePicker() {
        Calendar newCalendar = Calendar.getInstance();
        dueDateDialog = new DatePickerDialog(self, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateEditText.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate();
            }
        });
    }

    private void processExpense() {
        try {
            Expense expense = parseForm();
            setWaitingLayout();
            List<FlatMate> contributors = parseContributors();
            registerExpense(expense, contributors);

        } catch (IOException e) {
            makeSnackbar("Alle Angaben sind zwingend");
        }
    }

    private void setupContributors() {
        List<FlatMate> flatMates = null;
        try {
            flatMates = getFlatMates();
        } catch (IOException|ClassNotFoundException e) {
            noFlatMatesView.setVisibility(View.VISIBLE);
            contributorsGui.setVisibility(View.GONE);
        }
        if(flatMates != null ) {
            if(flatMates.size() > 0) {
                noFlatMatesView.setVisibility(View.GONE);
                contributorsGui.setVisibility(View.VISIBLE);
                contributorsGui.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                contributorsGui.setLayoutManager(llm);
                ContributorsAdapter ca = new ContributorsAdapter(flatMates);
                contributorsGui.setAdapter(ca);
            } else if(flatMates.size() > 2) {
                noFlatMatesView.setVisibility(View.GONE);
                contributorsGui.setVisibility(View.VISIBLE);
                contributorsGui.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(this);
                llm.setOrientation(LinearLayoutManager.VERTICAL);
                contributorsGui.setLayoutManager(llm);
                ContributorsAdapter ca = new ContributorsAdapter(flatMates);
                contributorsGui.setAdapter(ca);
            }
        } else {
            noFlatMatesView.setVisibility(View.VISIBLE);
            contributorsGui.setVisibility(View.GONE);
        }


    }

    private void registerGuiElements() {
        messageShower = (TextView) findViewById(R.id.messageShower);
        layoutContainer = (LinearLayout) findViewById(R.id.newExpenseBackgroundContainer);
        descriptionEditText = (EditText) findViewById(R.id.editTextDescription);
        amountEditText = (EditText) findViewById(R.id.editTextAmount);
        dateEditText = (EditText) findViewById(R.id.editTextDueDate);
        dateEditText.setInputType(InputType.TYPE_NULL);
        staticExpenseCheckBox = (CheckBox) findViewById(R.id.checkBoxStaticExpense);
        staticExpenseArea = (LinearLayout) findViewById(R.id.flatAdminExpenseArea);
        createExpenseButton = (Button) findViewById(R.id.buttonCreateExpense);
        contributorsGui = (RecyclerView) findViewById(R.id.cardListContributors);
        progressBar = (ProgressBar) findViewById(R.id.progressBarCreateExpense);
        contributorsTextView = (TextView) findViewById(R.id.textViewContributors);
        noFlatMatesView = (RelativeLayout) findViewById(R.id.no_flat_mates_view);
        self = this;
    }

    private List<FlatMate> parseContributors() {
        ArrayList<FlatMate> flatMates = new ArrayList<>();
        for(int i = 0; i < contributorsGui.getChildCount(); i++) {
            View contributorsItem = contributorsGui.getChildAt(i);
            Switch switcher = (Switch) contributorsItem.findViewById(R.id.switchContributes);
            if(switcher.isChecked()) {
                final String flatMateName = ((TextView)contributorsItem.findViewById(R.id.contributorNameTextView)).getText().toString();
                final String flatMateEmail = ((TextView) contributorsItem.findViewById(R.id.flatMateEmailTextView)).getText().toString();
                FlatMate flatMate = new FlatMate(flatMateName, flatMateEmail, false);
                flatMates.add(flatMate);
            }
        }
        flatMates.add(new FlatMate(getUserEmail(), getUserEmail(), false));
        return flatMates;
    }

    private void setupSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerPeriod);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.interval_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void registerExpense(Expense expense, List<FlatMate> contributors) {
        final String flatId = getFlatId();
        if(!flatId.isEmpty()) {
            if (hasConnection()) {
                switch (expense.getExpenseType()) {
                    case Variable:
                        registerVariableExpense((VariableExpense) expense, contributors);
                        break;
                    case Static:
                        registerStaticExpense((StaticExpense) expense, contributors);
                        break;
                }
            } else {
                notifyError(getResources().getString(R.string.connection_error));
            }
        } else {
            //TODO: what if no flat registered
        }
    }

    private void registerStaticExpense(StaticExpense expense, List<FlatMate> contributors) {
        expense.addInterval(currentInterval);
        ArrayList<StaticUserExpense> staticUserExpenses = new ArrayList<>();
        double contributorShare = 100.0 / contributors.size();
        if(contributorShare > 0) {
            for(FlatMate flatMate:contributors) {
                staticUserExpenses.add(new StaticUserExpense(flatMate.getEmail(), contributorShare));
            }
        }
        ExpenseService.createStaticExpense(self, getFlatId(), expense, staticUserExpenses);
    }

    private void registerVariableExpense(VariableExpense expense, List<FlatMate> contributors) {
        for(FlatMate contributor: contributors) {
            expense.addContributor(contributor);
        }
        String flatId = getFlatId();
        ExpenseService.createVariableExpense(self, flatId, expense);
    }

    private void makeSnackbar(final String message) {

        Snackbar snackbar = Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG);
        SnackBarStyler.alert(snackbar, this).show();
    }

    private Expense parseForm() throws IOException {
        final String description = descriptionEditText.getText().toString();
        double amount;
        final Date dueDate;
        boolean isStatic = staticExpenseCheckBox.isChecked();
        if(description.isEmpty()) {
            throw new IOException("Description is missing");
        }
        try {
            amount = Double.parseDouble(amountEditText.getText().toString());
        } catch(NumberFormatException | NullPointerException e) {
            throw new IOException("Amount is missing", e);
        }
        try {
            dueDate  = dateFormatter.parse(dateEditText.getText().toString());
        } catch (ParseException e) {
            throw new IOException("Date is missing", e);
        }
        if(isStatic) {
            return new StaticExpense(description, amount, dueDate);
        } else {
            return new VariableExpense(description, amount, dueDate);
        }
    }

    private void selectDate() {
        dueDateDialog.show();
    }

    @Override
    public void setWaitingLayout() {
        contributorsGui.setVisibility(View.GONE);
        staticExpenseArea.setVisibility(View.GONE);
        contributorsTextView.setVisibility(View.GONE);
        staticExpenseCheckBox.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void reactToSuccess() {
        Intent intent = new Intent(self, ExpensesActivity.class);
        intent.putExtra(BaseActivity.INTENT_EXTRAS, BaseActivity.EXPENSE_WAS_CREATED);
        startActivity(intent);
    }

    @Override
    public void checkPreConditions() {
        if(isAdmin()) {
            staticExpenseCheckBox.setVisibility(View.VISIBLE);
        } else {
            staticExpenseCheckBox.setVisibility(View.GONE);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                currentInterval = StaticExpense.SECONDS_IN_DAY;
                break;
            case 1:
                currentInterval = StaticExpense.SECONDS_IN_WEEK;
                break;
            case 2:
                currentInterval = StaticExpense.SECONDS_IN_TWO_WEEKS;
                break;
            case 3:
                currentInterval = StaticExpense.SECONDS_IN_MONTH;
                break;
            case 4:
                currentInterval = StaticExpense.SECONDS_IN_YEAR;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
