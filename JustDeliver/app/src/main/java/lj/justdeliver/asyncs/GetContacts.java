package lj.justdeliver.asyncs;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;

import lj.justdeliver.model.User;
import lj.justdeliver.tables.UserTable;

/**
 * Created by lj on 3/6/2017.
 */

public class GetContacts extends AsyncTaskLoader<ArrayList<User>> {

    public GetContacts(Context context) {
        super(context);
    }

    @Override
    public ArrayList<User> loadInBackground() {
        UserTable userTable = new UserTable();
        return userTable.getAllUsersList();
    }
}
