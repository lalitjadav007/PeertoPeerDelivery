package lj.justdeliver;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import lj.justdeliver.helper.CommonConstants;
import lj.justdeliver.model.Driver;
import lj.justdeliver.model.User;

public class DriverDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etLicenceNumber, etValidDate, etVehicalType, etVehicalNumber;
    private Button btnCompleteRegister;
    private User user;
    private ProgressDialog progressDialog;
    private DatabaseReference dbRef;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_details);
        init();
    }

    private void init() {
        dbRef = FirebaseDatabase.getInstance().getReference("users");

        etLicenceNumber = (EditText) findViewById(R.id.etLicenceNumber);
        etValidDate = (EditText) findViewById(R.id.etValidDate);
        etVehicalType = (EditText) findViewById(R.id.etVehicalType);
        etVehicalNumber = (EditText) findViewById(R.id.etVehicalNumber);
        btnCompleteRegister = (Button) findViewById(R.id.btnCompleteRegister);
        btnCompleteRegister.setOnClickListener(this);
        user = (User) getIntent().getSerializableExtra("user");
        etValidDate.setFocusable(false);
        etValidDate.setOnClickListener(this);

        calendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                etValidDate.setText(sdf.format(calendar.getTime()));
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.etValidDate:
                DatePickerDialog dialog = new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
                break;
            case R.id.btnCompleteRegister:
                String licenceNumver, vehicalType, vehicalNumber, validDate, completeRegister = "false";
                licenceNumver = etLicenceNumber.getText().toString().trim();
                vehicalType = etVehicalType.getText().toString().trim();
                validDate = etValidDate.getText().toString().trim();
                vehicalNumber = etVehicalNumber.getText().toString().trim();

                if (TextUtils.isEmpty(licenceNumver)) {
                    displaySnackbar(getString(R.string.error_invalid_licence_number));
                    return;
                }
                if (TextUtils.isEmpty(vehicalType)) {
                    displaySnackbar(getString(R.string.error_invalid_vehicalTpy));
                    return;
                }
                if (TextUtils.isEmpty(validDate)) {
                    displaySnackbar(getString(R.string.error_empty_valid_date));
                    return;
                }
                if (TextUtils.isEmpty(vehicalNumber)) {
                    displaySnackbar(getString(R.string.error_invalid_vehical_number));
                    return;
                }

                Driver driver = new Driver();
                driver.licenceNumber = licenceNumver;
                driver.validDate = validDate;
                driver.vehicalNumber = vehicalNumber;
                driver.vehicalType = vehicalType;
                driver.status = completeRegister;

                user.driver = driver;

                showProgressDialog();
                dbRef.child(user.uid).setValue(user).addOnCompleteListener(DriverDetailsActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        hideProgressDialog();

                        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("licenceNumber", user.driver.licenceNumber);
                        editor.putString("validDate", user.driver.validDate);
                        editor.putString("vehicalNumber", user.driver.vehicalNumber);
                        editor.putString("vehicalType", user.driver.vehicalType);
                        editor.putString("status", user.driver.status);
                        editor.apply();
                        CommonConstants.isDriver = true;
                        Intent intent1 = new Intent(DriverDetailsActivity.this, DriverHomeActivity.class);
                        startActivity(intent1);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                });

                break;
        }
    }

    private void displaySnackbar(String message) {
        Snackbar.make(etLicenceNumber, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.msg_please_wait));
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
