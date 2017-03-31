package lj.justdeliver;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import lj.justdeliver.helper.CommonConstants;
import lj.justdeliver.model.OneGig;

public class SelectedGigActivity extends AppCompatActivity implements View.OnClickListener {
    private OneGig gig;
    private LinearLayout llDynamicStatusAdd;
    private Button btnUpdateStatus;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_gig);

        getData();
        init();
    }

    private void getData() {
        Intent intent = getIntent();
        gig = (OneGig) intent.getSerializableExtra("gig");
        if (gig == null) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void init() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        TextView tvSenderName = (TextView) findViewById(R.id.tvSenderName);
        TextView tvReceiverName = (TextView) findViewById(R.id.tvReceiverName);

        tvSenderName.setText(gig.sender.fullName);
        tvReceiverName.setText(gig.receiver.fullName);

        findViewById(R.id.clSenderLayout).setOnClickListener(this);
        findViewById(R.id.clReceiverLayout).setOnClickListener(this);

        btnUpdateStatus = (Button) findViewById(R.id.btnUpdateStatus);
        btnUpdateStatus.setOnClickListener(this);

        llDynamicStatusAdd = (LinearLayout) findViewById(R.id.llDynamicStatusAdd);
        addViews();

    }

    private void addViews() {


        if (!gig.driverID.equalsIgnoreCase("no")) {
            boolean matchFound = true;
            for (String s : CommonConstants.status) {
                ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.item_delivery_status, llDynamicStatusAdd, false);
                ImageView tvProgressImage = (ImageView) layout.findViewById(R.id.ivProgressImage);
                TextView tvProgressStatus = (TextView) layout.findViewById(R.id.tvProgressStatus);
                if (matchFound) {
                    Glide.with(this).load(R.drawable.ic_yes).dontAnimate().into(tvProgressImage);
                } else {
                    Glide.with(this).load(R.drawable.ic_no).dontAnimate().into(tvProgressImage);
                }

                if (gig.deliveryStatus != null && gig.deliveryStatus.equals(s)) {
                    if (s.equals("Payed")) {
                        btnUpdateStatus.setVisibility(View.INVISIBLE);
                    }
                    matchFound = false;
                }

                tvProgressStatus.setText(s);
                llDynamicStatusAdd.addView(layout);
            }
        }
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
            case R.id.clReceiverLayout:
                if (CommonConstants.isDriver) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setSingleChoiceItems(new String[]{getString(R.string.action_call), getString(R.string.action_show_in_map)}, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + gig.receiver.phone)));
                                    break;
                                case 1:
                                    showReceiverLocation();
                                    break;
                            }
                        }
                    });
                    builder.show();
                } else {
                    showReceiverLocation();
                }
                break;

            case R.id.clSenderLayout:
                if (CommonConstants.isDriver) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setSingleChoiceItems(new String[]{getString(R.string.action_call), getString(R.string.action_show_in_map)}, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + gig.sender.phone)));
                                    break;
                                case 1:
                                    showReceiverLocation();
                                    break;
                            }
                        }
                    });
                    builder.show();
                } else {
                    showSenderLocation();
                }
                break;

            case R.id.btnUpdateStatus:
                if (gig.driverID.equals("no")) {
                    displaySnackbar(getString(R.string.error_no_one_has_selected));
                } else if (CommonConstants.isDriver) {
                    if (gig.deliveryStatus != null && !gig.deliveryStatus.equals("Picked up") && !gig.deliveryStatus.equals("Deliverd")) {
                        String currentStatus = "";
                        int currentIndex = -1;
                        for (int i = 0; i < CommonConstants.status.size(); i++) {
                            if (gig.deliveryStatus.equals(CommonConstants.status.get(i))) {
                                currentStatus = CommonConstants.status.get(i);
                                currentIndex = i;
                                break;
                            }
                        }

                        if (currentIndex != -1 && currentIndex < CommonConstants.status.size() - 1) {
                            gig.deliveryStatus = CommonConstants.status.get(currentIndex + 1);
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Is it " + gig.deliveryStatus + "?");
                            builder.setPositiveButton(getString(R.string.label_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgress();
                                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Gigs");
                                    dbRef.child(gig.id).setValue(gig).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            llDynamicStatusAdd.removeAllViews();
                                            addViews();
                                            hideProgress();
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton(getString(R.string.label_no), null);
                            builder.show();
                        }
                    } else {
                        displaySnackbar(getString(R.string.error_you_can_not_update_now));
                    }
                } else {
                    if (gig.deliveryStatus != null && (gig.deliveryStatus.equals("Picked up") || gig.deliveryStatus.equals("Deliverd"))) {
                        String currentStatus = "";
                        int currentIndex = -1;
                        for (int i = 0; i < CommonConstants.status.size(); i++) {
                            if (gig.deliveryStatus.equals(CommonConstants.status.get(i))) {
                                currentStatus = CommonConstants.status.get(i);
                                currentIndex = i;
                                break;
                            }
                        }

                        if (currentIndex != -1 && currentIndex < CommonConstants.status.size() - 1) {
                            gig.deliveryStatus = CommonConstants.status.get(currentIndex + 1);
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Is it " + gig.deliveryStatus + "?");
                            builder.setPositiveButton(getString(R.string.label_yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showProgress();
                                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Gigs");
                                    dbRef.child(gig.id).setValue(gig).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            llDynamicStatusAdd.removeAllViews();
                                            addViews();
                                            hideProgress();
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton(getString(R.string.label_no), null);
                            builder.show();
                        }
                    } else {
                        displaySnackbar(getString(R.string.error_you_can_not_update_now_sender));
                    }
                }
                break;
        }
    }

    private void showReceiverLocation() {
        String uriBegin = "geo:" + gig.deliverLocation.lat + "," + gig.deliverLocation.lng;
        String query = gig.deliverLocation.lat + "," + gig.deliverLocation.lng + "(" + gig.receiver.fullName + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void showSenderLocation() {
        String uriBegin1 = "geo:" + gig.senderLocation.lat + "," + gig.senderLocation.lng;
        String query1 = gig.senderLocation.lat + "," + gig.senderLocation.lng + "(" + gig.sender.fullName + ")";
        String encodedQuery1 = Uri.encode(query1);
        String uriString1 = uriBegin1 + "?q=" + encodedQuery1 + "&z=16";
        Uri uri1 = Uri.parse(uriString1);
        Intent intent1 = new Intent(Intent.ACTION_VIEW, uri1);
        startActivity(intent1);
    }

    private void displaySnackbar(String message) {
        Snackbar.make(llDynamicStatusAdd, message, Snackbar.LENGTH_SHORT).show();
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
