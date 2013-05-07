package com.gzoneandroid.ringtonetool.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.gzoneandroid.ringtonetool.R;
import com.viewpagerindicator.TabPageIndicator;

public class SampleTabsDefault extends FragmentActivity {
//    private static final String[] CONTENT = new String[] { "Recent", "Artists", "Albums", "Songs", "Playlists", "Genres" };
    private static final int[] TITLES_IDS = new int[] {R.string.hot_songs, R.string.my_songs};
    private static String[] CONTENT = new String[TITLES_IDS.length];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_tabs);

        for (int i = 0; i < TITLES_IDS.length; i++) {
            CONTENT[i] = getResources().getString(TITLES_IDS[i]);
        }

        FragmentPagerAdapter adapter = new GoogleMusicAdapter(getSupportFragmentManager());

        ViewPager pager = (ViewPager)findViewById(R.id.pager);
        pager.setAdapter(adapter);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(pager);
    }

    class GoogleMusicAdapter extends FragmentPagerAdapter {
        public GoogleMusicAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //TODO
            switch (position) {
                case 0:
                    return HotSongsFragment.newInstance();
                case 1:
                    return MySongsFragment.newInstance();
                default:
                    return HotSongsFragment.newInstance();
            }
//            return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
//            return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

        @Override
        public int getCount() {
          return CONTENT.length;
        }
    }
}
