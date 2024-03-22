package com.example.walletmanager.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

public class LendActivity extends AppCompatActivity {

    RelativeLayout layout;
    Button partyButton;

    ArrayAdapter<String> adapter2;
    SQLiteDatabase mydb;
    DBManager db = new DBManager();

    EditText textPartySearch;
    TextView  dateView;
    TextInputLayout amountInputLayout, narrationInputLayout;
    String amountString, narrationString, dateFinal;
    String day, month, year;
    private Calendar calendar;
    Date c = new Date();
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
    String currTime = time.format(c);
    StringBuilder date;
    private MyDatabaseHelper databaseHelper;
    private boolean isFirebaseMode;

    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lend);
        getSupportActionBar().hide();
        layout = findViewById(R.id.lendlayout);

        // Getting the value of firebase_mode from sharedPreference
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        isFirebaseMode = sharedPreferences.getBoolean("firebase_mode", false);
        databaseHelper = new MyDatabaseHelper(this);
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

        partyButton = findViewById(R.id.selectPartyButton);
        amountInputLayout = findViewById(R.id.amountTextView);
        narrationInputLayout = findViewById(R.id.narrationTextView);
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

    public void selectParty(View v) {
        PartySelectionUtil.showPartySelectionDialog(this, partyName -> {
            partyButton.setText(partyName);
        });
    }

    public void saveButton(View v) {
        amountString = amountInputLayout.getEditText().getText().toString();
        narrationString = narrationInputLayout.getEditText().getText().toString();
        String date = dateFinal;
        String party = partyButton.getText().toString();
        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
        String datetime = ft.format(new Date());
        if (party.equals("PARTY")) {
            Snackbar.make(layout, "Please select a party", Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (amountString.isEmpty()) {
            amountInputLayout.setError("Field cannot be empty");
            return;
        }
        try {
            SQLiteDatabase mydb = databaseHelper.getWritableDatabase();
            databaseHelper.saveELB(datetime, isFirebaseMode, "LEND", mydb, date, currTime, party, Double.parseDouble(amountString), narrationString);
            finish();
//                Snackbar.make(relativeLayout, "Borrow recorded", Snackbar.LENGTH_SHORT).show();
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