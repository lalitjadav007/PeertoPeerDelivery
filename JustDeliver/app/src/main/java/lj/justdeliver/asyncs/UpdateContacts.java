package lj.justdeliver.asyncs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneNumberUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import lj.justdeliver.model.User;
import lj.justdeliver.tables.UserTable;

/**
 * Created by lj on 3/6/2017.
 */

public class UpdateContacts extends AsyncTask<Void, Void, Void> {
    private Context context;
    private ArrayList<User> contactListFromFB = new ArrayList<>();

    public UpdateContacts(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
        dbRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> iterable = dataSnapshot.getChildren();
                for (DataSnapshot ds : iterable) {
                    contactListFromFB.add(ds.getValue(User.class));
                }
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                getAllContacts();

                dbRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ContactSyncs"));
    }

    private void getAllContacts() {
        Cursor phones = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (phones != null) {
            UserTable userTable = new UserTable();
            while (phones.moveToNext()) {
                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                if (phones.getInt(phones.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) == 1) {
                    Cursor pCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    // continue till this cursor reaches to all phone numbers which are associated with a contact in the contact list
                    if (pCursor != null) {
                        while (pCursor.moveToNext()) {
                            int phoneType = pCursor.getInt(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            //String isStarred 		= pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.STARRED));
                            String phoneNo = pCursor.getString(pCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).trim();
                            //you will get all phone numbers according to it's type as below switch case.
                            //Logs.e will print the phone number along with the name in DDMS. you can use these details where ever you want.
                            for (User user : contactListFromFB) {
                                if (PhoneNumberUtils.compare(phoneNo, user.phone)) {
                                    userTable.addUser(user);
                                }
                            }
                        }
                        pCursor.close();
                    }
                }
            }
            phones.close();
        }
    }
}
