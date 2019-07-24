package com.unclezs.novel.Adapter;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.unclezs.novel.Activity.AnalysisFragment;
import com.unclezs.novel.Activity.DownloadFragment;
import com.unclezs.novel.Activity.MainActivity;
import com.unclezs.novel.Activity.SearchFragment;

/**
 * Created by Uncle
 * 2019.07.20.
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 3;
    public static SearchFragment searchFragment;
    public static DownloadFragment downloadFragment;
    public static AnalysisFragment analysisFragment;

    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
        searchFragment = new SearchFragment();
        downloadFragment = new DownloadFragment();
        analysisFragment = new AnalysisFragment();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case MainActivity.SEARCH_PAGE:
                return searchFragment;
            case MainActivity.ANALYSIS_PAGE:
                return analysisFragment;
            default:
                return downloadFragment;
        }
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    }
}
