package lj.justdeliver;

import android.app.Application;

import lj.justdeliver.database.DatabaseHelper;
import lj.justdeliver.database.DatabaseManager;

/**
 * Created by lj on 3/6/2017.
 */

public class JustDeliverApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try{
            DatabaseManager.initializeInstance(new DatabaseHelper(this));
            DatabaseManager.getInstance().openDatabase();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onTerminate() {
        DatabaseManager.getInstance().closeDatabase();
        super.onTerminate();
    }
}
