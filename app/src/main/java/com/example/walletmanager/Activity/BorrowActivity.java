package com.example.walletmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.walletmanager.Database.DBManager;
import com.example.walletmanager.Database.MyDatabaseHelper;
import com.example.walletmanager.R;
import com.example.walletmanager.Utils.PartySelectionUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BorrowActivity extends AppCompatActivity {

    RelativeLayout relativeLayout,toolbar;
    Button partyButton,saveButton;

    ArrayAdapter<String> adapter2;
    SQLiteDatabase mydb;
    DBManager db = new DBManager();

    EditText textPartySearch;
    TextView dateView;
    TextInputLayout amountInputLayout, narrationInputLayout;
    String amountString, narrationString, dateFinal;
    String day, month, year;
    Calendar calendar;
    Date c = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
    String currTime = time.format(c);
    StringBuilder date;
    private MyDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow);
        getSupportActionBar().hide();

        databaseHelper = new MyDatabaseHelper(this);
        relativeLayout = findViewById(R.id.borrowlayout);
        toolbar = findViewById(R.id.toolbar);
        partyButton = findViewById(R.id.selectPartyButton);
        saveButton = findViewById(R.id.save_btn);
        amountInputLayout = findViewById(R.id.amountTextView);
        narrationInputLayout = findViewById(R.id.narrationTextView);
        // DATE PICKER
        try {
            calendar = Calendar.getInstance();
            year = String.valueOf(calendar.get(Calendar.YEAR));
            dateView = findViewById(R.id.dateTextView);
            int newMonth = calendar.get(Calendar.MONTH) + 1;
            int newDay = calendar.get(Calendar.DAY_OF_MONTH);
            month = db.dateOrTimeConverter(newMonth);
            day = db.dateOrTimeConverter(newDay);
            showDate(year, month, day);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectParty(View v) {
        PartySelectionUtil.showPartySelectionDialog(this, partyName -> {
            partyButton.setText(partyName);
        });
    }

    private void showDate(String year, String month, String day) {
        try {
            date = new StringBuilder().append(year).append("-")
                    .append(month).append("-").append(day);
            dateFinal = df.format(df.parse(date.toString()));
            dateView.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void saveButton(View v) {
        try {
            amountString = amountInputLayout.getEditText().getText().toString();
            narrationString = narrationInputLayout.getEditText().getText().toString();
            String date = dateFinal;
            String party = partyButton.getText().toString();
            if (party.equals("PARTY")) {
                Snackbar.make(relativeLayout, "Please select a party", Snackbar.LENGTH_SHORT).show();
                return;
            }
            if (amountString.isEmpty()) {
                amountInputLayout.setError("Field cannot be empty");
                return;
            }

            SQLiteDatabase mydb = databaseHelper.getWritableDatabase();
            databaseHelper.saveBorrow(mydb, date, currTime, party, Double.parseDouble(amountString), narrationString);
            mydb.close();

            Snackbar.make(relativeLayout, "Borrow recorded", Snackbar.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    arg2 = arg2 + 1;
                    // arg3 = day
                    showDate(String.valueOf(arg1), String.valueOf(arg2), String.valueOf(arg3));
                }
            };

    public void selectDate(View v) {

        try {

            showDialog(999);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}