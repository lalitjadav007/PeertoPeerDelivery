package lj.justdeliver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import lj.justdeliver.helper.CommonConstants;
import lj.justdeliver.model.OneGig;

public class GigNormalActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvGigDesc;
    private TextView ivGigSize;
    private TextView tvGigAmount;
    private TextView tvSenderName;
    private TextView tvSenderAddress;
    private TextView tvReceiverName;
    private TextView tvReceiverAddress;
    private TextView tvPaymentOption;
    private TextView tvDateToDeliver;
    private String message;
    private OneGig gig;
    private ProgressDialog progressDialog;
    private Button btnCancelGig, btnSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gig_normal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        getData();
    }

    private void init() {
        tvGigDesc = (TextView) findViewById(R.id.tvGigDesc);
        ivGigSize = (TextView) findViewById(R.id.ivGigSize);
        tvGigAmount = (TextView) findViewById(R.id.tvGigAmount);
        tvSenderName = (TextView) findViewById(R.id.tvSenderName);
        tvSenderAddress = (TextView) findViewById(R.id.tvSenderAddress);
        tvReceiverName = (TextView) findViewById(R.id.tvReceiverName);
        tvReceiverAddress = (TextView) findViewById(R.id.tvReceiverAddress);
        tvPaymentOption = (TextView) findViewById(R.id.tvPaymentOption);
        tvDateToDeliver = (TextView) findViewById(R.id.tvDateToDeliver);

        btnCancelGig = (Button) findViewById(R.id.btnCancelGig);
        btnCancelGig.setOnClickListener(this);
        btnSelect = (Button) findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(this);

        if (!CommonConstants.isDriver) {
            btnCancelGig.setVisibility(View.INVISIBLE);
            btnSelect.setVisibility(View.INVISIBLE);
        }
    }

    private void getData() {
        Intent intent = getIntent();
        gig = (OneGig) intent.getSerializableExtra("gig");
        if (gig == null) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return;
        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(gig.gigName);
        toolbar.setSubtitle(gig.gigDesc);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        ImageView ivGigPicture = (ImageView) findViewById(R.id.ivGigPicture);

        if (gig.gigImage != null)
            Glide.with(this).load(gig.gigImage).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).centerCrop().into(ivGigPicture);
        else
            Glide.with(this).load(R.drawable.placeholder).centerCrop().into(ivGigPicture);

        tvGigDesc.setText(gig.gigDesc);
        ivGigSize.setText(gig.size);
        tvGigAmount.setText(String.valueOf(gig.charge));
        tvSenderName.setText(gig.sender.fullName);
        tvSenderAddress.setText(gig.senderLocation.address);
        tvReceiverName.setText(gig.receiver.fullName);
        tvReceiverAddress.setText(gig.deliverLocation.address);
        tvPaymentOption.setText(gig.paymentType);
        tvDateToDeliver.setText(gig.deliveryDate);
        message = gig.gigName + " at " + gig.deliverLocation.addressName;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSelect:
                SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
                String status = preferences.getString("status", "");
                if (status.equals("false")) {
                    displaySnackbar(getString(R.string.error_confirm_your_docs));
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.label_deliver));
                builder.setMessage(message);
                builder.setPositiveButton(getString(R.string.label_i_will), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        showProgress();
                        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
                        String driverID = preferences.getString("uid", "");

                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Gigs");
                        gig.driverID = driverID;
                        gig.deliveryStatus = CommonConstants.status.get(0);
                        dbRef.child(gig.id).setValue(gig).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hideProgress();
                                finish();
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        });
                    }
                });
                builder.setNegativeButton(getString(R.string.label_let_me_think), null);
                builder.show();
                break;
            case R.id.btnCancelGig:
                onBackPressed();
                break;
        }
    }

    private void displaySnackbar(String message) {
        Snackbar.make(tvGigDesc, message, Snackbar.LENGTH_SHORT).show();
    }

    private void showProgress() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.msg_please_wait));
        progressDialog.show();
    }

    private void hideProgress() {
        progressDialog.hide();
    }
}
