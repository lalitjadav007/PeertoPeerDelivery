package lj.justdeliver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import lj.justdeliver.asyncs.GetContacts;
import lj.justdeliver.asyncs.UpdateContacts;
import lj.justdeliver.holders.UserHolder;
import lj.justdeliver.model.User;

public class ContactsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<User>> {

    private DatabaseReference dbRef;
    private RecyclerView rvContacts;
    private ArrayList<User> contactListFromFB, finalContactList;
    private UserAdapter userAdapter;
    private BroadcastReceiver contactsUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getSupportLoaderManager().initLoader(1000, null, ContactsActivity.this).forceLoad();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        init();
        LocalBroadcastManager.getInstance(this).registerReceiver(contactsUpdateReceiver, new IntentFilter("ContactSyncs"));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(contactsUpdateReceiver);
        super.onDestroy();
    }

    private void init() {
        finalContactList = new ArrayList<>();
        contactListFromFB = new ArrayList<>();
        userAdapter = new UserAdapter(this, finalContactList);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.label_my_contacts));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        rvContacts.setItemAnimator(new DefaultItemAnimator());
        rvContacts.setAdapter(userAdapter);

        getSupportLoaderManager().initLoader(1000, null, this).forceLoad();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, 11);
            } else {
                new UpdateContacts(this).execute();
            }
        } else {
            new UpdateContacts(this).execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 11 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            new UpdateContacts(this).execute();
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
    public Loader<ArrayList<User>> onCreateLoader(int id, Bundle args) {
        return new GetContacts(this);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<User>> loader, ArrayList<User> data) {
        if (data != null) {
            userAdapter.refresh(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<User>> loader) {

    }

    public class UserAdapter extends RecyclerView.Adapter<UserHolder> implements UserHolder.UserSelect {
        private ArrayList<User> finalContactList;
        private LayoutInflater inflater;

        UserAdapter(Context context, ArrayList<User> finalContactList) {
            this.finalContactList = finalContactList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new UserHolder(inflater.inflate(R.layout.item_users_list, parent, false), this);
        }

        @Override
        public void onBindViewHolder(UserHolder holder, int position) {
            holder.bind(finalContactList.get(position));
        }

        @Override
        public int getItemCount() {
            return finalContactList.size();
        }

        void refresh(ArrayList<User> finalContactList) {
            this.finalContactList = finalContactList;
            notifyDataSetChanged();
        }

        @Override
        public void userSelected(User selectedUser) {
            Intent data = new Intent();
            data.putExtra("user", selectedUser);
            setResult(111, data);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}
