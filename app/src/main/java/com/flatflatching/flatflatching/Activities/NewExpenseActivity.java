package com.flatflatching.flatflatching.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.flatflatching.flatflatching.R;
import com.flatflatching.flatflatching.models.Expense;
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
import java.util.Locale;

public class NewExpenseActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{

    private CheckBox staticExpenseCheckBox;
    private LinearLayout staticExpenseArea;
    private EditText descriptionEditText;
    private EditText amountEditText;
    private EditText dateEditText;
    private DatePickerDialog dueDateDialog;
    private Button createExpenseButton;
    private BaseActivity self;
    private int currentInterval = StaticExpense.SECONDS_IN_MONTH;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_own);
        setSupportActionBar(toolbar);
        messageShower = (TextView) findViewById(R.id.messageShower);
        layoutContainer = (LinearLayout) findViewById(R.id.newExpenseBackgroundContainer);
        descriptionEditText = (EditText) findViewById(R.id.editTextDescription);
        amountEditText = (EditText) findViewById(R.id.editTextAmount);
        dateEditText = (EditText) findViewById(R.id.editTextDueDate);
        dateEditText.setInputType(InputType.TYPE_NULL);
        staticExpenseCheckBox = (CheckBox) findViewById(R.id.checkBoxStaticExpense);
        staticExpenseArea = (LinearLayout) findViewById(R.id.flatAdminExpenseArea);
        createExpenseButton = (Button) findViewById(R.id.buttonCreateExpense);
        setupSpinner();
        createExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Expense expense = parseForm();
                    registerExpense(expense);

                } catch (IOException e) {
                    makeToast("Alle Angaben sind zwingend");
                }
            }
        });
        staticExpenseCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(((CheckBox) v).isChecked()) {
                    staticExpenseArea.setVisibility(View.VISIBLE);
                } else {
                    staticExpenseArea.setVisibility(View.GONE);
                }
            }
        });
        self = this;
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
        checkPreConditions();
    }

    private void setupSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerPeriod);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.interval_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void registerExpense(Expense expense) {
        final String flatId = getFlatId();
        if(!flatId.isEmpty()) {
            if (hasConnection()) {
                switch (expense.getExpenseType()) {
                    case Variable:
                        ExpenseService.createVariableExpense(self, flatId, (VariableExpense)expense);
                        break;
                    case Static:
                        StaticExpense staticExpense = (StaticExpense) expense;
                        staticExpense.addInterval(currentInterval);
                        ExpenseService.createStaticExpense(self, flatId, (StaticExpense)expense, new ArrayList<StaticUserExpense>());
                        break;
                }
            } else {
                notifyError(getResources().getString(R.string.connection_error));
            }
        } else {
            //TODO: what if no flat registered
        }
    }

    private void makeToast(final String message) {
        Snackbar.make(
                findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG)
                .show();
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

    }

    @Override
    public void reactToSuccess() {

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
