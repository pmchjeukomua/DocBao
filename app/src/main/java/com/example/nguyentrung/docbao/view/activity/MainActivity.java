package com.example.nguyentrung.docbao.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nguyentrung.docbao.control.Constant;
import com.example.nguyentrung.docbao.model.News;
import com.example.nguyentrung.docbao.R;
import com.example.nguyentrung.docbao.view.Fragment.FragmentAboutApp;
import com.example.nguyentrung.docbao.view.Fragment.FragmentController;
import com.example.nguyentrung.docbao.view.Fragment.FragmentLinkSaved;
import com.example.nguyentrung.docbao.view.Fragment.FragmentReadingNews;
import com.example.nguyentrung.docbao.view.Fragment.FragmentSearch;
import com.example.nguyentrung.docbao.view.Fragment.FragmentSetting;
import com.example.nguyentrung.docbao.view.Fragment.FragmentViewpagerNews;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";


    private FragmentViewpagerNews fragmentViewpagerNews;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private boolean doubleBackToExitPressedOnce = false;
    private FragmentAboutApp fragmentAboutApp;
    private boolean isPressAbout = false;
    private boolean isPressedSavedLink = false;
    private Stack<String> stackTitleToolbar;



    private boolean isPressedSetting = false;
    private ArrayList<News> allArrNews = new ArrayList<>();
    private FragmentSearch fragmentSearch;
    private EditText editSearch;
    private boolean isSearch =false;
    private FragmentViewpagerNews fragmentViewpagerNews24h;
    private FragmentViewpagerNews fragmentViewpagerNewsExp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_24h);

        stackTitleToolbar = new Stack<>();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        initFragment(R.array.catalog24h, R.array.arrrsscatalog24h, Constant.TYPE_24H);
        setToolbarAfterReplaceFragment(Constant.PAGE_24H);

//        loadAds();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey
                    (Constant.KEY_ITEM_NEWS_NOTIFICATION)) {
                // extract the extra-data in the Notification
                News xxx = (News) bundle.getSerializable(Constant.KEY_ITEM_NEWS_NOTIFICATION);
                Log.e(TAG, "infooooooo: " + xxx.toString());
                FragmentReadingNews fragmentReadingNews = new FragmentReadingNews();
                Bundle newBundle = new Bundle();
                newBundle.putSerializable(Constant.KEY_ITEM_NEWS, xxx);
                newBundle.putInt(Constant.KEY_TYPE_NEWSPAPER, Constant.TYPE_24H);
                newBundle.putInt(Constant.KEY_FROM, Constant.NEW_NEWS);
                fragmentReadingNews.setArguments(newBundle);
                FragmentController.addFragmentAnimation(MainActivity.this, fragmentReadingNews);
                setToolbarAfterAddFragment(getResources().getString(R.string.tintuc));
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void pushTitleToolbar(String title) {
        stackTitleToolbar.push(title);
    }

//    public void showShareDialog(String contentTitle, String contentDescription, String contentURL, String imageURL) {
//        shareDialog = new ShareDialog(this);
//        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
//            @Override
//            public void onSuccess(Sharer.Result result) {
//                Log.e(Constant.SHAREED, "Success");
//            }
//
//            @Override
//            public void onCancel() {
//                Log.e(Constant.SHAREED, "Canceled");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                Log.e(Constant.SHAREED, "Error");
//            }
//        });
//
//        ShareLinkContent linkContent = new ShareLinkContent.Builder()
//                .setContentTitle(contentTitle)
//                .setContentDescription(contentDescription)
//                .setContentUrl(Uri.parse(contentURL))
//                .setImageUrl(Uri.parse(imageURL))
//                .build();
//        shareDialog.show(linkContent);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initFragment(int idArrCatalog, int idArrRSSCatalog, int type) {

        fragmentViewpagerNews = new FragmentViewpagerNews(allArrNews);
        Bundle bundle = new Bundle();
        bundle.putInt(Constant.KEY_TYPE, type);
        bundle.putInt(Constant.KEY_CATALOG, idArrCatalog);
        bundle.putInt(Constant.KEY_RSS_CATALOG, idArrRSSCatalog);
        fragmentViewpagerNews.setArguments(bundle);
        FragmentController.replaceWithPopAllBackStack(this, fragmentViewpagerNews);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        setToolbarAfterBackPress();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Nhấn thêm lần nữa để thoát.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 1500);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        return super.onCreateOptionsMenu(menu);
//        return true; // giu nguyen nhu cua m day nha
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setToolbarAfterPressHomeNavi();
        }
        if (item.getItemId() == R.id.nav_search && !isSearch) {
            fragmentSearch = new FragmentSearch(allArrNews);
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
            FragmentController.addWithAddToBackStackAnimation(this, fragmentSearch,fragment);
            setToolbarAfterAddFragment("Tìm kiếm");
            isSearch = true;
            isPressAbout = false;
            isPressedSetting = false;
            isPressedSavedLink = false;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.e("Item",item.isChecked() +"");
        if (!item.isChecked()) {
            int id = item.getItemId();
            if (id == R.id.nav_24h) {
                setToolbarAfterReplaceFragment(Constant.PAGE_24H);
                initFragment(R.array.catalog24h, R.array.arrrsscatalog24h, Constant.TYPE_24H);
                setDefaultMenuCheck();

            } else if (id == R.id.nav_vnexpress) {
                setToolbarAfterReplaceFragment(Constant.PAGE_VNEXPRESS);
                initFragment(R.array.catalogvnexpress, R.array.arrrrscatalogvnexpress, Constant.TYPE_VNEXPRESS);
                setDefaultMenuCheck();
            } else if (id == R.id.nav_saved_link && !isPressedSavedLink) {
                setToolbarAfterAddFragment(getResources().getString(R.string.savedlink));
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
                if (fragment instanceof FragmentReadingNews) {
                    getSupportFragmentManager().popBackStack();
                    stackTitleToolbar.pop();
                }
                FragmentLinkSaved fragmentSavedLink = new FragmentLinkSaved();
                fragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
                FragmentController.addWithAddToBackStackAnimation(this, fragmentSavedLink, fragment);
                isSearch = false;
                isPressAbout = false;
                isPressedSetting = false;
                isPressedSavedLink = true;
            } else if (id == R.id.nav_setting && !isPressedSetting) {
                setToolbarAfterAddFragment(getResources().getString(R.string.setting));
//                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
//                if (fragment instanceof FragmentReadingNews) {
//                    getSupportFragmentManager().popBackStack();
//                    stackTitleToolbar.pop();
//                }
                FragmentSetting fragmentSetting = new FragmentSetting();
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
                FragmentController.addWithAddToBackStackAnimation(this, fragmentSetting, fragment);
                isPressAbout = false;
                isSearch = false;
                isPressedSavedLink = false;
                isPressedSetting = true;
            } else if (id == R.id.nav_share) {

            } else if (id == R.id.nav_about && !isPressAbout) {
                fragmentAboutApp = new FragmentAboutApp();
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
                FragmentController.addWithAddToBackStackAnimation(this, fragmentAboutApp,fragment);
                setToolbarAfterAddFragment(getResources().getString(R.string.about));
                isSearch = false;
                isPressedSavedLink = false;
                isPressedSetting = false;
                isPressAbout = true;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setDefaultMenuCheck() {
        isSearch = false;
        isPressAbout = false;
        isPressedSavedLink = false;
        isPressedSetting = false;
    }


    public void setToolbarAfterAddFragment(String fragment) {
        setSupportActionBar(toolbar);
        pushTitleToolbar(fragment);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setTitle(fragment);
        }
    }

    public void setToolbarAfterReplaceFragment(String fragment) {
        setSupportActionBar(toolbar);
        stackTitleToolbar.removeAllElements();
        stackTitleToolbar.push(fragment);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_menu);
            toolbar.setTitle(fragment);
        }
    }

    public void setToolbarAfterPressHomeNavi() {
        setSupportActionBar(toolbar);
        String fragmentName;
        if (stackTitleToolbar.size() > 1) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }
            stackTitleToolbar.pop();
            fragmentName = stackTitleToolbar.peek();
            if (toolbar != null) {
                if (fragmentName.equals(Constant.PAGE_24H) || fragmentName.equals(Constant.PAGE_VNEXPRESS)) {
                    toolbar.setNavigationIcon(R.drawable.ic_menu);
                } else {
                    toolbar.setNavigationIcon(R.drawable.ic_back);
                }
                toolbar.setTitle(fragmentName);
                setDefaultMenuCheck();
            }
        } else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
        }
    }

    public void setToolbarAfterBackPress() {
        setSupportActionBar(toolbar);
        String fragmentName;
        if (stackTitleToolbar.size() > 1) {
            stackTitleToolbar.pop();
            fragmentName = stackTitleToolbar.peek();
        } else {
            fragmentName = stackTitleToolbar.peek();
        }
        if (toolbar != null) {
            if (fragmentName.equals(Constant.PAGE_24H) || fragmentName.equals(Constant.PAGE_VNEXPRESS)) {
                toolbar.setNavigationIcon(R.drawable.ic_menu);
            } else {
                toolbar.setNavigationIcon(R.drawable.ic_back);
            }
            setDefaultMenuCheck();
            toolbar.setTitle(fragmentName);
        }
    }


    //    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        if (getSupportFragmentManager().findFragmentById(R.id.content_main) instanceof FragmentViewpagerNews) {
//            fragmentViewpagerNews = (FragmentViewpagerNews) getSupportFragmentManager().findFragmentById(R.id.content_main);
//            fragmentManager.putFragment(outState, CURRENT_FRAGMENT, fragmentViewpagerNews);
//        }
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        if (savedInstanceState != null) {
//            if (fragmentManager.getFragment(savedInstanceState, CURRENT_FRAGMENT) instanceof FragmentViewpagerNews) {
//                fragmentViewpagerNews = (FragmentViewpagerNews) fragmentManager.getFragment(savedInstanceState, CURRENT_FRAGMENT);
//                transaction.replace(R.id.content_main, fragmentViewpagerNews, fragmentViewpagerNews.getClass().getName()).commit();
//            }
//        }
//    }
}