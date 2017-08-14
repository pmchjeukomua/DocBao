package com.example.nguyentrung.docbao.view.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nguyentrung.docbao.control.Constant;
import com.example.nguyentrung.docbao.view.activity.MainActivity;
import com.example.nguyentrung.docbao.control.NetworkUtil;
import com.example.nguyentrung.docbao.model.News;
import com.example.nguyentrung.docbao.R;
import com.example.nguyentrung.docbao.control.Support;
import com.example.nguyentrung.docbao.model.TabPagerItem;
import com.example.nguyentrung.docbao.control.adapter.ViewpagerAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class FragmentViewpagerNews extends Fragment implements View.OnClickListener {
    private static final String TAG = "FragmentViewpagerNews";
    private final ArrayList<News> allArrNews;
    private Context context;
    private ViewPager viewPagerNews;
    private TabLayout tabLayout;
    private LayoutInflater inflater;
    private ViewpagerAdapter viewpagerAdapter;
    private ArrayList<TabPagerItem> arrTabs = new ArrayList<>();
    private ArrayList<String> arrCatalogs = new ArrayList<>();
    private ArrayList<String> arrURLCatalogs = new ArrayList<>();
    private Dialog dialogSettingInternet;
    private Button btnExit;
    private Button btnSettingInternet;
    private int typeNewspaper;
    private int catalog;
    private int rssCatalog;
    private boolean isConnected = false;

    public FragmentViewpagerNews(ArrayList<News> allArrNews) {
        this.allArrNews = allArrNews;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        arrCatalogs.clear();
        arrURLCatalogs.clear();
        Bundle bundle = getArguments();
        catalog = bundle.getInt(Constant.KEY_CATALOG);
        rssCatalog = bundle.getInt(Constant.KEY_RSS_CATALOG);
        typeNewspaper = bundle.getInt(Constant.KEY_TYPE);
        Collections.addAll(arrCatalogs, getResources().getStringArray(catalog));
        Collections.addAll(arrURLCatalogs, getResources().getStringArray(rssCatalog));
        context = getActivity();
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //initDialog();
        isConnected = true;
        createTabPagerItem();
    }


    private void createTabPagerItem() {
        for (int i = 0; i < arrCatalogs.size(); i++) {
            FragmentNews fragmentNews = new FragmentNews(allArrNews);
            Bundle bundle = new Bundle();
            bundle.putInt(Constant.KEY_TYPE, typeNewspaper);
            bundle.putString(Constant.KEY_LINK, arrURLCatalogs.get(i));
            bundle.putString(Constant.KEY_NAME_CATALOG, arrCatalogs.get(i));
            fragmentNews.setArguments(bundle);
            arrTabs.add(new TabPagerItem(arrCatalogs.get(i), fragmentNews));
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

//    public void loadAds(){
//        adView = (AdView) getActivity().findViewById(R.id.adViewListNews);
//        AdRequest adRequest = new AdRequest.Builder()
////                .addTestDevice("94B99A903C1706B2740808BECA1185EE")
//                .build();
//        adView.loadAd(adRequest);
//    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPagerNews = (ViewPager) getActivity().findViewById(R.id.viewpagerNews);
        viewPagerNews.setOffscreenPageLimit(arrTabs.size());

        viewpagerAdapter = new ViewpagerAdapter(getChildFragmentManager(), arrTabs);
        viewPagerNews.setAdapter(viewpagerAdapter);
        viewpagerAdapter.notifyDataSetChanged();
        tabLayout = (TabLayout) getActivity().findViewById(R.id.tablayoutCatalog);
        tabLayout.setupWithViewPager(viewPagerNews);
        setupTab();
//        loadAds();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        Log.e(TAG, "isConnected: " + isConnected);
        Log.e(TAG, "hasInternet: " + Support.hasInternet(getContext()));
        if (!isConnected) {
            if (NetworkUtil.hasInternet(getContext())) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.initFragment(catalog, rssCatalog, typeNewspaper);
                viewpagerAdapter.notifyDataSetChanged();
                setupTab();
                dialogSettingInternet.dismiss();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    public void initDialog() {
        dialogSettingInternet = new Dialog(getContext(), android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_nointernet, null);
        dialogSettingInternet.setContentView(view);
        dialogSettingInternet.setCancelable(false);
        btnExit = (Button) view.findViewById(R.id.btnExit);
        btnSettingInternet = (Button) view.findViewById(R.id.btnSettingInternet);
        btnSettingInternet.setOnClickListener(this);
        btnExit.setOnClickListener(this);

    }

    private void setupTab() {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(viewpagerAdapter.getTabView(inflater, i));
            }
            tabLayout.getTabAt(i).getCustomView().setSelected(true);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnExit:
                getActivity().finish();
                break;
            case R.id.btnSettingInternet:
                settingInternet();
                break;
        }
    }

    private void settingInternet() {
        startActivity(new Intent(Settings.ACTION_SETTINGS));
    }

    @Override
    public void onStart() {
        Log.e(TAG, "onStart");
        super.onStart();
    }
}
