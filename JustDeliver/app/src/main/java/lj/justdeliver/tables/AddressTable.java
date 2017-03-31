package lj.justdeliver.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import lj.justdeliver.database.DatabaseManager;
import lj.justdeliver.model.AddressModel;

/**
 * Created by lj on 3/6/2017.
 */

public class AddressTable {

    private final String TABLE_NAME = "AddressTable";
    private final String KEY_ID = "UserID";
    private final String KEY_NAME = "AddressName";
    private final String KEY_ADDRESS = "FullAddress";
    private final String KEY_LAT = "Lat";
    private final String KEY_LNG = "Lng";

    public void upGradeTable(SQLiteDatabase db) {
        String DROP_TABLE = "DROP TABLE " + TABLE_NAME;
        db.execSQL(DROP_TABLE);
        createTable(db);
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + "("
                + KEY_ID + " PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_LAT + " TEXT,"
                + KEY_LNG + " TEXT"
                + ")";
        ;
        db.execSQL(CREATE_TABLE);
    }

    public void addAddress(String uid, AddressModel address) {
        if (getAdddress(uid) != null) {
            SQLiteDatabase sqLiteDatabase = DatabaseManager.getInstance().openDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ID, uid);
            values.put(KEY_NAME, address.addressName);
            values.put(KEY_ADDRESS, address.address);
            values.put(KEY_LAT, address.lat);
            values.put(KEY_LNG, address.lng);
            sqLiteDatabase.update(TABLE_NAME, values, null, null);
        } else {
            SQLiteDatabase sqLiteDatabase = DatabaseManager.getInstance().openDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ID, uid);
            values.put(KEY_NAME, address.addressName);
            values.put(KEY_ADDRESS, address.address);
            values.put(KEY_LAT, address.lat);
            values.put(KEY_LNG, address.lng);
            sqLiteDatabase.insert(TABLE_NAME, null, values);
        }
    }

    AddressModel getAdddress(String uid) {
        SQLiteDatabase sqLiteDatabase = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID + "='" + uid + "'", null);
        if (cursor != null && cursor.moveToFirst()) {
            AddressModel address;
            address = new AddressModel();
            address.addressName = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            address.address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
            address.lat = cursor.getString(cursor.getColumnIndex(KEY_LAT));
            address.lng = cursor.getString(cursor.getColumnIndex(KEY_LNG));
            cursor.close();
            return address;
        }
        return null;
    }
}
