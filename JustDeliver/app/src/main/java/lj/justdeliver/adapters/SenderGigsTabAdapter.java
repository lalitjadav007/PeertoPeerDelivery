package lj.justdeliver.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.ArrayList;

import lj.justdeliver.model.TabAndTitle;

/**
 * Created by lj on 2/20/2017.
 */

public class SenderGigsTabAdapter extends FragmentPagerAdapter {
    ArrayList<TabAndTitle> tabList = new ArrayList<>();

    public SenderGigsTabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return tabList.get(position).fragment;
    }

    @Override
    public int getCount() {
        return tabList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabList.get(position).title;
    }

    public void addFragment(android.app.Fragment fragment, String title) {
        tabList.add(new TabAndTitle(fragment, title));
    }
}
