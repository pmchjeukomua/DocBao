package com.example.nguyentrung.docbao.model;

import android.support.v4.app.Fragment;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class TabPagerItem {
    private final CharSequence title;
    private final Fragment fragment;

    public TabPagerItem(CharSequence title, Fragment fragment) {
        this.title = title;
        this.fragment = fragment;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public CharSequence getTitle() {
        return title;
    }
}

