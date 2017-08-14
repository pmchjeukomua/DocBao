package com.example.nguyentrung.docbao.view.Fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.nguyentrung.docbao.R;
import com.example.nguyentrung.docbao.control.Constant;
import com.example.nguyentrung.docbao.control.SqliteM;
import com.example.nguyentrung.docbao.model.News;
import com.example.nguyentrung.docbao.model.NewsSave;
import com.example.nguyentrung.docbao.view.activity.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nguyentrung on 5/23/2017.
 */

public class FragmentReadingNewsSave extends Fragment {
    private NewsSave news;
    private int from;
    private LinearLayout linearLayoutDisPlayNews;
    private FloatingActionButton floatingActionButtonSave;
    private SqliteM sqLiteManager;
    private boolean isRunning = true;
    private SqliteM sqliteM;
    private ProgressDialog progressDialog;
    private String link;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constant.LINK_SAVED, link);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            link = savedInstanceState.getString(Constant.LINK_SAVED);
        }
        Bundle bundle = getArguments();
        news = (NewsSave) bundle.getSerializable(Constant.KEY_ITEM_NEWS);
        from = bundle.getInt(Constant.KEY_FROM);
        sqliteM = new SqliteM(getContext());
        initDialog();
    }

    private void initDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_readingnews, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        linearLayoutDisPlayNews = new LinearLayout(getContext());
        linearLayoutDisPlayNews = (LinearLayout) view.findViewById(R.id.linenearDisplayNews);
        floatingActionButtonSave = (FloatingActionButton) getActivity().findViewById(R.id.fabSave);
        floatingActionButtonSave.setVisibility(View.VISIBLE);
        floatingActionButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNews();
                floatingActionButtonSave.setVisibility(View.GONE);
            }
        });
        addTextView(news.getTitle(), getResources().getInteger(R.integer.size_title_textview_readingnews), getResources().getColor(R.color.color_text_textview_title_readingnews), true, false);
        addTextView(news.getPubdate(), getResources().getInteger(R.integer.size_pubdate_textview_readingnews), getResources().getColor(R.color.color_text_textview_pubdate_readingnews), false, true);
        String [] arr=news.getLink().split("[_]");
        for (String s : arr) {
            Log.e("chuoi",s);
            if (!TextUtils.isEmpty(s) ){
                if (s.contains("Image:")) {
                    s = s.substring(s.indexOf("http://"));
                    addImage(s);
                    Log.e("image",s);
                } else if (s.contains("Title:")) {
                    s = s.substring(6);
//                                addTextView(s, getResources().getInteger(R.integer.size_title_textview_readingnews), getResources().getColor(R.color.color_text_textview_title_readingnews), true, false);
                } else if (s.contains("PubDate:")) {
                    s = s.substring(8);
//                                addTextView(s, getResources().getInteger(R.integer.size_pubdate_textview_readingnews), getResources().getColor(R.color.color_text_textview_pubdate_readingnews), false, true);
                } else if (s.contains("First:")) {
                    s = s.substring(6);
                    addTextView(s, getResources().getInteger(R.integer.size_first_paragraph_textview_readingnews), getResources().getColor(R.color.color_text_textview_first_readingnews), false, false);
                } else if (s.contains("Journalist:")) {
                    Log.e("doc bao journalist", s);
                    s = s.substring(11);
                    addTextViewJournalist(s, getResources().getInteger(R.integer.size_first_paragraph_textview_readingnews), getResources().getColor(R.color.color_text_textview_first_readingnews));
                } else {
                    addTextView(s, getResources().getInteger(R.integer.size_paragraph_textview_readingnews), getResources().getColor(R.color.color_text_textview_first_readingnews), false, false);
                }
            }
            progressDialog.dismiss();
//                    loadAds();
        }
        Log.e("bai viet ", news.getLink());

    }

    private void addImage(String s) {
        sqliteM = new SqliteM(getContext());
        byte[] outImage = sqliteM.getImage(s);
        ByteArrayInputStream imageStream = new ByteArrayInputStream(outImage);
        Bitmap theImage = BitmapFactory.decodeStream(imageStream);
//        holder.img.setImageBitmap(theImage);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 160);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.margin_bottom_textview_readingnews));
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(layoutParams);
        linearLayoutDisPlayNews.addView(imageView);
        imageView.setImageBitmap(theImage);
    }

    private void deleteNews() {
//        sqLiteManager.deleteNews(news.getLink());
        sqLiteManager.deleteNews(news.getTitle());
        ArrayList<NewsSave> arrayList = new ArrayList<>();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportFragmentManager().popBackStack();
        mainActivity.setToolbarAfterBackPress();
        arrayList.addAll(sqLiteManager.getAllNews());
    }

    private void addTextView(String s, int size, int color, boolean bold, boolean italic) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMarginStart(5);
        layoutParams.setMargins((int) getResources().getDimension(R.dimen.margin_left_textview_readingnews), 0, 0, (int) getResources().getDimension(R.dimen.margin_bottom_textview_readingnews));
        textView.setLayoutParams(layoutParams);
        textView.setText(s);
        textView.setTextColor(color);
        textView.setTextSize(size);
        if (bold) {
            textView.setTypeface(null, Typeface.BOLD);
        }
        if (italic) {
            textView.setTypeface(null, Typeface.ITALIC);
        }
        linearLayoutDisPlayNews.addView(textView);

    }

    private void addTextViewJournalist(String s, int size, int color) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMarginStart(5);
        layoutParams.setMargins((int) getResources().getDimension(R.dimen.margin_left_textview_readingnews), 0, 0, (int) getResources().getDimension(R.dimen.margin_bottom_textview_readingnews));
        textView.setLayoutParams(layoutParams);
        textView.setText(s);
        textView.setTextColor(color);
        textView.setTextSize(size);
        textView.setTypeface(null, Typeface.BOLD);
        linearLayoutDisPlayNews.setGravity(Gravity.RIGHT);
        linearLayoutDisPlayNews.addView(textView);
    }
}
