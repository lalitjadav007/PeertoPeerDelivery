package lj.justdeliver.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import lj.justdeliver.database.DatabaseManager;
import lj.justdeliver.model.User;

/**
 * Created by lj on 3/6/2017.
 */

public class UserTable {

    private final String TABLE_NAME = "UserTable";
    private final String KEY_ID = "UserID";
    private final String KEY_PROFILE_PIC = "ProfilePic";
    private final String KEY_FULL_NAME = "FullName";
    private final String KEY_EMAIL = "Email";
    private final String KEY_PHONE = "Phone";
    private final String KEY_AS_DRIVER = "ASDriver";

    public void upGradeTable(SQLiteDatabase db) {
        String DROP_TABLE = "DROP TABLE " + TABLE_NAME;
        db.execSQL(DROP_TABLE);
        createTable(db);
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NAME + "("
                + KEY_ID + " PRIMARY KEY,"
                + KEY_PROFILE_PIC + " TEXT,"
                + KEY_FULL_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT,"
                + KEY_PHONE + " TEXT,"
                + KEY_AS_DRIVER + " TEXT"
                + ")";
        ;
        db.execSQL(CREATE_TABLE);
    }

    public boolean addUser(User user) {
        if (getUser(user.uid) != null) {
            try {
                if (user.asDriver.equals("true")) {
                    DriverTable driverTable = new DriverTable();
                    driverTable.addDriver(user.uid, user.driver);
                }
                SQLiteDatabase sqLiteDatabase = DatabaseManager.getInstance().openDatabase();
                ContentValues values = new ContentValues();
                values.put(KEY_ID, user.uid);
                values.put(KEY_PROFILE_PIC, user.profilePic);
                values.put(KEY_FULL_NAME, user.fullName);
                values.put(KEY_EMAIL, user.email);
                values.put(KEY_PHONE, user.phone);
                values.put(KEY_AS_DRIVER, user.asDriver);
                long id = sqLiteDatabase.update(TABLE_NAME, values, null, null);
                return id != -1;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            try {
                if (user.asDriver.equals("true")) {
                    DriverTable driverTable = new DriverTable();
                    driverTable.addDriver(user.uid, user.driver);
                }
                SQLiteDatabase sqLiteDatabase = DatabaseManager.getInstance().openDatabase();
                ContentValues values = new ContentValues();
                values.put(KEY_ID, user.uid);
                values.put(KEY_PROFILE_PIC, user.profilePic);
                values.put(KEY_FULL_NAME, user.fullName);
                values.put(KEY_EMAIL, user.email);
                values.put(KEY_PHONE, user.phone);
                values.put(KEY_AS_DRIVER, user.asDriver);
                long id = sqLiteDatabase.insert(TABLE_NAME, null, values);
                return id != -1;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private User getUser(String uid) {
        SQLiteDatabase sqLiteDatabase = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + KEY_ID + "='" + uid + "'", null);
        if (cursor != null && cursor.moveToFirst()) {
            User user;
            user = new User();
            user.uid = cursor.getString(cursor.getColumnIndex(KEY_ID));
            user.profilePic = cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC));
            user.fullName = cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME));
            user.email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
            user.phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE));
            user.asDriver = cursor.getString(cursor.getColumnIndex(KEY_AS_DRIVER));
            if (user.asDriver.equals("true")) {
                DriverTable driverTable = new DriverTable();
                user.driver = driverTable.getDriver(user.uid);
            }
            cursor.close();
            return user;
        }
        return null;
    }

    public ArrayList<User> getAllUsersList() {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = DatabaseManager.getInstance().openDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor != null && cursor.moveToFirst()) {
            User user;
            do {
                user = new User();
                user.uid = cursor.getString(cursor.getColumnIndex(KEY_ID));
                user.profilePic = cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC));
                user.fullName = cursor.getString(cursor.getColumnIndex(KEY_FULL_NAME));
                user.email = cursor.getString(cursor.getColumnIndex(KEY_EMAIL));
                user.phone = cursor.getString(cursor.getColumnIndex(KEY_PHONE));
                user.asDriver = cursor.getString(cursor.getColumnIndex(KEY_AS_DRIVER));
                if (user.asDriver.equals("true")) {
                    DriverTable driverTable = new DriverTable();
                    user.driver = driverTable.getDriver(user.uid);
                }
                users.add(user);

            } while (cursor.moveToNext());
            cursor.close();
        }
        return users;
    }
}
