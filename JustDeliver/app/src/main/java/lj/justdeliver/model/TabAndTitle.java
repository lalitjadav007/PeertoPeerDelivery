package lj.justdeliver.model;

import android.app.Fragment;

/**
 * Created by lj on 2/20/2017.
 */

public class TabAndTitle {
    public Fragment fragment;
    public String title;

    public TabAndTitle(Fragment fragment, String title) {
        this.fragment = fragment;
        this.title = title;
    }
}
