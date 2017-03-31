package lj.justdeliver;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import lj.justdeliver.asyncs.GetDistanceAsyncs;
import lj.justdeliver.helper.CommonConstants;
import lj.justdeliver.model.AddressModel;
import lj.justdeliver.model.OneGig;
import lj.justdeliver.model.User;

public class CreateGigActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Float> {

    private EditText etDeliveryDate, etGigName, etGigDesc, etSenderName, etSenderLocation, etDeliverPerson, etDeliverLocation;
    private RadioButton rbSizeSmall, rbPaymentAfterDelivery;
    private Calendar calendar;
    private DatePickerDialog.OnDateSetListener date;
    private DatabaseReference dbRef;
    private ProgressBar pbCreateGig;
    private Button btnCreateGigNow;
    private boolean isSenderLocation = false;
    private AddressModel senderLocation;
    private AddressModel deliverLocation;
    private boolean isDeliverLocation = false;
    private TextView tvAmount;
    private float charge;
    private ImageView ivGigImageUpload;
    private StorageReference imageRef;
    private Bitmap imageBitmap;
    private User sender;
    private User receiver;
    private boolean senderClicked = false;
    private boolean receiverClick = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gig);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.create_a_gig));
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        init();
    }

    private void init() {
        dbRef = FirebaseDatabase.getInstance().getReference("Gigs");
        imageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://just-deliver.appspot.com");

        etDeliveryDate = (EditText) findViewById(R.id.etDeliveryDate);
        etDeliveryDate.setOnClickListener(this);
        etGigName = (EditText) findViewById(R.id.etGigName);
        etGigDesc = (EditText) findViewById(R.id.etGigDesc);
        etSenderName = (EditText) findViewById(R.id.etSenderName);
        etSenderName.setOnClickListener(this);
        etSenderLocation = (EditText) findViewById(R.id.etSenderLocation);
        etDeliverPerson = (EditText) findViewById(R.id.etDeliverPerson);
        etDeliverPerson.setOnClickListener(this);
        etDeliverLocation = (EditText) findViewById(R.id.etDeliverLocation);
        rbSizeSmall = (RadioButton) findViewById(R.id.rbSizeSmall);
        rbPaymentAfterDelivery = (RadioButton) findViewById(R.id.rbPaymentAfterDelivery);
        pbCreateGig = (ProgressBar) findViewById(R.id.pbCreateGig);
        tvAmount = (TextView) findViewById(R.id.tvAmount);

        calendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                String myFormat = "MM/dd/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                etDeliveryDate.setText(sdf.format(calendar.getTime()));
            }
        };

        btnCreateGigNow = (Button) findViewById(R.id.btnCreateGigNow);
        btnCreateGigNow.setOnClickListener(this);
        findViewById(R.id.ivSelectSenderLocation).setOnClickListener(this);
        findViewById(R.id.ivSelectDeliverLocation).setOnClickListener(this);
        ivGigImageUpload = (ImageView) findViewById(R.id.ivGigImageUpload);
        ivGigImageUpload.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (isSenderLocation) {
                    isSenderLocation = false;
                    senderLocation = (AddressModel) data.getSerializableExtra("address");
                    etSenderLocation.setText(senderLocation.address);
                    getDistance();
                    return;
                }

                if (isDeliverLocation) {
                    isDeliverLocation = false;
                    deliverLocation = (AddressModel) data.getSerializableExtra("address");
                    etDeliverLocation.setText(deliverLocation.address);
                    getDistance();
                }
            }
        } else if (requestCode == 101 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            ivGigImageUpload.setImageBitmap(imageBitmap);
        } else if (requestCode == 111 && data != null) {
            if (senderClicked) {
                sender = (User) data.getSerializableExtra("user");
                etSenderName.setText(sender.fullName);
                senderClicked = false;
            }

            if (receiverClick) {
                receiver = (User) data.getSerializableExtra("user");
                etDeliverPerson.setText(receiver.fullName);
                receiverClick = false;
            }
        }
    }

    private void getDistance() {
        if (senderLocation != null && deliverLocation != null && senderLocation.lat != null && senderLocation.lng != null && deliverLocation.lat != null && deliverLocation.lng != null) {
            getSupportLoaderManager().restartLoader(10, null, this).forceLoad();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivGigImageUpload: {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 101);
                }
            }
            break;

            case R.id.etDeliveryDate: {
                DatePickerDialog dialog = new DatePickerDialog(this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                dialog.show();
            }
            break;

            case R.id.btnCreateGigNow: {
                String gigName = etGigName.getText().toString().trim();
                String gigDesc = etGigDesc.getText().toString().trim();
                String senderName = etSenderName.getText().toString().trim();
                String deliverPerson = etDeliverPerson.getText().toString().trim();
                String size = "small";
                String paymentType = "COD";
                String deliveryDate = etDeliveryDate.getText().toString().trim();

                if (TextUtils.isEmpty(gigName)) {
                    displaySnackbar(getString(R.string.error_give_a_name_to_gig));
                    return;
                }
                if (TextUtils.isEmpty(gigDesc)) {
                    displaySnackbar(getString(R.string.error_provide_desc_for_gig));
                    return;
                }
                if (TextUtils.isEmpty(senderName)) {
                    displaySnackbar(getString(R.string.error_sender_name_empty));
                    return;
                }
                if (TextUtils.isEmpty(senderLocation.lat) || TextUtils.isEmpty(senderLocation.lng)) {
                    displaySnackbar(getString(R.string.error_sender_location_empty));
                    return;
                }
                if (TextUtils.isEmpty(deliverPerson)) {
                    displaySnackbar(getString(R.string.error_deliver_name_empty));
                    return;
                }
                if (TextUtils.isEmpty(deliverLocation.lat) || TextUtils.isEmpty(deliverLocation.lng)) {
                    displaySnackbar(getString(R.string.error_deliver_location_empty));
                    return;
                }
                if (TextUtils.isEmpty(deliveryDate)) {
                    displaySnackbar(getString(R.string.error_deliver_date_empty));
                    return;
                }

                if (pbCreateGig != null)
                    pbCreateGig.setVisibility(View.VISIBLE);
                if (btnCreateGigNow != null)
                    btnCreateGigNow.setVisibility(View.INVISIBLE);

                SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
                String creator = preferences.getString("uid", "");

                final OneGig gig = new OneGig(gigName, gigDesc, sender, senderLocation, receiver, deliverLocation, size, paymentType, deliveryDate, charge, creator);
                final DatabaseReference ref = dbRef.push();
                gig.id = ref.getKey();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] data = baos.toByteArray();
                UploadTask uploadTask = imageRef.child(ref.getKey()).putBytes(data);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().getDownloadUrl() != null) {
                            gig.gigImage = task.getResult().getDownloadUrl().toString();
                            ref.setValue(gig).addOnCompleteListener(CreateGigActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (pbCreateGig != null)
                                        pbCreateGig.setVisibility(View.GONE);
                                    if (btnCreateGigNow != null)
                                        btnCreateGigNow.setVisibility(View.VISIBLE);

                                    if (task.isSuccessful()) {
                                        displaySnackbar("done");
                                        finish();
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    } else {
                                        displaySnackbar(getString(R.string.something_went_wrong));
                                    }
                                }
                            });
                        } else {
                            displaySnackbar(getString(R.string.something_went_wrong));
                        }

                    }
                });
            }
            break;

            case R.id.ivSelectSenderLocation: {
                isSenderLocation = true;
                Intent intent = new Intent(this, PickLocationActivity.class);
                startActivityForResult(intent, 100);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            break;

            case R.id.ivSelectDeliverLocation: {
                isDeliverLocation = true;
                Intent intent = new Intent(this, PickLocationActivity.class);
                startActivityForResult(intent, 100);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            break;

            case R.id.etSenderName:
                senderClicked = true;
                startActivityForResult(new Intent(this, ContactsActivity.class), 111);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;

            case R.id.etDeliverPerson:
                receiverClick = true;
                startActivityForResult(new Intent(this, ContactsActivity.class), 111);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }
    }

    private void displaySnackbar(String message) {
        Snackbar.make(etGigName, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Float> onCreateLoader(int id, Bundle args) {
        String sendrLocationLatLng = senderLocation.lat + "," + senderLocation.lng;
        String deliverLocationLatLng = deliverLocation.lat + "," + deliverLocation.lng;
        return new GetDistanceAsyncs(this, sendrLocationLatLng, deliverLocationLatLng);
    }

    @Override
    public void onLoadFinished(Loader<Float> loader, Float data) {
        float meters = data;
        charge = meters * CommonConstants.BASE_FARE;
        if (data != 0 && charge < CommonConstants.MIN_FARE) {
            charge = CommonConstants.MIN_FARE;
        }
        tvAmount.setText(String.valueOf(charge));
    }

    @Override
    public void onLoaderReset(Loader<Float> loader) {
        charge = 0;
        tvAmount.setText("0");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }
}
