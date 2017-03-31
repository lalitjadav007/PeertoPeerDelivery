package lj.justdeliver.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lj.justdeliver.tables.AddressTable;
import lj.justdeliver.tables.DriverTable;
import lj.justdeliver.tables.UserTable;

/**
 * Created by lj on 3/6/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "DeliverDatabase";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        AddressTable addressTable = new AddressTable();
        addressTable.createTable(db);
        DriverTable driverTable = new DriverTable();
        driverTable.createTable(db);
        UserTable userTable = new UserTable();
        userTable.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        AddressTable addressTable = new AddressTable();
        addressTable.upGradeTable(db);
        DriverTable driverTable = new DriverTable();
        driverTable.upGradeTable(db);
        UserTable userTable = new UserTable();
        userTable.upGradeTable(db);
    }
}
