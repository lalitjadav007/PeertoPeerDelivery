package lj.justdeliver.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import lj.justdeliver.database.DatabaseManager;
import lj.justdeliver.model.Driver;

/**
 * Created by lj on 3/6/2017.
 */

public class DriverTable {

    private final String TABLE_NAME = "DriverTable";
    private final String KEY_ID = "UserID";
    private final String KEY_LECENCE_NUMBER = "LicenceNumber";
    private final String KEY_VALID_DATE = "ValidDate";
    private final String KEY_VEHICAL = "Vehical";
    private final String KEY_VERHICAL_NUMBER = "VehicalNumber";
    private final String KEY_STATUS = "Status";


    public void upGradeTable(SQLiteDatabase db) {
        String DROP_TABLE = "DROP TABLE " + TABLE_NAME;
        db.execSQL(DROP_TABLE);
        createTable(db);
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + "("
                + KEY_ID + " PRIMARY KEY,"
                + KEY_LECENCE_NUMBER + " TEXT,"
                + KEY_VALID_DATE + " TEXT,"
                + KEY_VEHICAL + " TEXT,"
                + KEY_VERHICAL_NUMBER + " TEXT,"
                + KEY_STATUS + " TEXT"
                + ")";
        ;
        db.execSQL(CREATE_TABLE);
    }

    public void addDriver(String uid, Driver driver) {
        if (getDriver(uid) != null) {
            SQLiteDatabase sqLiteDatabase = DatabaseManager.getInstance().openDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ID, uid);
            values.put(KEY_LECENCE_NUMBER, driver.licenceNumber);
            values.put(KEY_VALID_DATE, driver.validDate);
            values.put(KEY_VEHICAL, driver.vehicalType);
            values.put(KEY_VERHICAL_NUMBER, driver.vehicalNumber);
            values.put(KEY_STATUS, driver.status);
            sqLiteDatabase.update(TABLE_NAME, values, null, null);
        } else {
            SQLiteDatabase sqLiteDatabase = DatabaseManager.getInstance().openDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_ID, uid);
            values.put(KEY_LECENCE_NUMBER, driver.licenceNumber);
            values.put(KEY_VALID_DATE, driver.validDate);
            values.put(KEY_VEHICAL, driver.vehicalType);
            values.put(KEY_VERHICAL_NUMBER, driver.vehicalNumber);
            values.put(KEY_STATUS, driver.status);
            sqLiteDatabase.insert(TABLE_NAME, null, values);
        }
    }

    public Driver getDriver(String uid) {
        try {
            SQLiteDatabase sqLiteDatabase = DatabaseManager.getInstance().openDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID + "='" + uid + "'", null);
            if (cursor != null && cursor.moveToFirst()) {
                Driver driver;
                driver = new Driver();
                driver.licenceNumber = cursor.getString(cursor.getColumnIndex(KEY_LECENCE_NUMBER));
                driver.validDate = cursor.getString(cursor.getColumnIndex(KEY_VALID_DATE));
                driver.vehicalNumber = cursor.getString(cursor.getColumnIndex(KEY_VERHICAL_NUMBER));
                driver.vehicalType = cursor.getString(cursor.getColumnIndex(KEY_VEHICAL));
                driver.status = cursor.getString(cursor.getColumnIndex(KEY_STATUS));
                cursor.close();
                return driver;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
