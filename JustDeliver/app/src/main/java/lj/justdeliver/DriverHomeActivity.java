package lj.justdeliver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import lj.justdeliver.adapters.SenderGigsTabAdapter;
import lj.justdeliver.fragments.DisplayAllGig;
import lj.justdeliver.fragments.DisplayMyGig;
import lj.justdeliver.fragments.DisplaySenderInProgressGig;
import lj.justdeliver.helper.CommonConstants;

public class DriverHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        init();
    }

    private void init() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        FloatingActionButton fabDisplayContacts = (FloatingActionButton) findViewById(R.id.fabDisplayContacts);
//        if (CommonConstants.isDriver)
            fabDisplayContacts.hide();
        fabDisplayContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(DriverHomeActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 11);
                        return;
                    }
                }
                startActivity(new Intent(DriverHomeActivity.this, ContactsActivity.class));
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SenderGigsTabAdapter gigsAdapter = new SenderGigsTabAdapter(getFragmentManager());
        gigsAdapter.addFragment(DisplayMyGig.newInstance(), getString(R.string.my_gigs));
        if (CommonConstants.isDriver)
            gigsAdapter.addFragment(DisplayAllGig.newInstance(), getString(R.string.all_gigs));
        else
            gigsAdapter.addFragment(DisplaySenderInProgressGig.newInstance(), getString(R.string.in_progress_gig));
        viewPager.setAdapter(gigsAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(DriverHomeActivity.this, ContactsActivity.class));
        }
    }
}
