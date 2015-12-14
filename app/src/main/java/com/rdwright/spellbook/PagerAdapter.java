package com.rdwright.spellbook;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Ryan on 12/14/2015.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    Context context;

    public PagerAdapter(FragmentManager fm, int NumOfTabs, Context c) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.context = c;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                PreparedSpellTabFragment tab1 = new PreparedSpellTabFragment();
                return tab1;
            case 1:
                KnownSpellTabFragment tab2 = new KnownSpellTabFragment();
                return tab2;
            case 2:
                AllSpellTabFragment tab3 = new AllSpellTabFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}