package com.example.nguyentrung.docbao.view.Fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.nguyentrung.docbao.R;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class FragmentController {
    public static void replaceDontAddToBackStack(Context mContext, Fragment mFragment) {
        FragmentManager mFragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.content_main, mFragment).commit();
    }


    public static void replaceWithAddToBackStack(Context mContext, Fragment mFragment, String nameClass) {
        FragmentManager mFragmentManager = ((FragmentActivity) mContext).getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.container, mFragment, nameClass).addToBackStack(nameClass)
                .commit();
    }


    public static void replaceWithAddToBackStackAnimation(Context mContext, Fragment mFragment, String nameClass) {
        FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit);
        ft.replace(R.id.content_main, mFragment, nameClass);
        ft.addToBackStack(nameClass);
        ft.commit();
    }

    public static void addWithAddToBackStackAnimation(Context mContext, Fragment mFragmentShow, Fragment mFragmentHide) {
        FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
        ft.hide(mFragmentHide);
        ft.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit);

        ft.add(R.id.content_main, mFragmentShow);

//		ft.replace(R.id.content_main, mFragment, nameClass);
        ft.addToBackStack(mFragmentHide.getClass().getName());
        ft.commit();
    }

    public static void addFragmentAnimation(Context mContext,Fragment mFragmentShow) {
        FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_left_enter, R.anim.fragment_slide_left_exit,
                R.anim.fragment_slide_right_enter, R.anim.fragment_slide_right_exit);
        ft.add(R.id.content_main, mFragmentShow);

//		ft.replace(R.id.content_main, mFragment, nameClass);
        ft.addToBackStack(mFragmentShow.getClass().getName());
        ft.commit();
    }


    public static void replaceWithPopAllBackStack(Context mContext, Fragment mFragment) {
        ((FragmentActivity) mContext).getSupportFragmentManager().popBackStack();
        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mFragment)
                .commit();
    }
//    public static void replaceWithPopAllBackStack(Context mContext, Fragment fragmentNew) {
//        ((FragmentActivity) mContext).getSupportFragmentManager().popBackStack(null,
//                FragmentManager.POP_BACK_STACK_INCLUSIVE);
//        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().show(fragmentNew)
//                .commit();
//    }

}

