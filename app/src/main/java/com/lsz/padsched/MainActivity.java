package com.lsz.padsched;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.ViewGroup;

public class MainActivity extends FragmentActivity {
    MyPagerAdapter myViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        myViewPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(myViewPagerAdapter);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;
        private static final int SCHEDULE = 0;
        private static final int CALCULATOR = 1;
        private static final int MISC = 2;
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        private FragmentActivity myActivity;

        public MyPagerAdapter(FragmentManager fm, FragmentActivity myActivity) {
            super(fm);
            this.myActivity = myActivity;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;

        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case SCHEDULE:
                    ScheduleFragment newSchedule = new ScheduleFragment();
                    return newSchedule;
                case CALCULATOR:
                    return new AlertsFragment();
                case MISC:
                    return new MiscFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int pos) {
            switch (pos) {
                case SCHEDULE:
                    return myActivity.getString(R.string.ScheduleTitle);
                case 1:
                    return myActivity.getString(R.string.AlertsTitle);
                case MISC:
                    return myActivity.getString(R.string.MiscTitle);
                default:
                    return "";
            }
        }

    }

}
