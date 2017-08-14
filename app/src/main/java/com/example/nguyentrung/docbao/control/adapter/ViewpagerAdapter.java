package com.example.nguyentrung.docbao.control.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.nguyentrung.docbao.R;
import com.example.nguyentrung.docbao.model.TabPagerItem;

import java.util.ArrayList;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class ViewpagerAdapter extends FragmentPagerAdapter {

    private ArrayList<TabPagerItem> arrTabs;

    public ViewpagerAdapter(FragmentManager fm, ArrayList<TabPagerItem> arrTabs) {
        super(fm);
        this.arrTabs = arrTabs;
    }

    @Override
    public Fragment getItem(int position) {
        return arrTabs.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return arrTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return arrTabs.get(position).getTitle();
    }

    public View getTabView(LayoutInflater inflater, int position){
        View view = inflater.inflate(R.layout.custom_tab, null);
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitleCustomTab);
        tvTitle.setText(arrTabs.get(position).getTitle());
        return view;
    }
}
