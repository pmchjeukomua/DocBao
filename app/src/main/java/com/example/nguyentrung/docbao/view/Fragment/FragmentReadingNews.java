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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.example.nguyentrung.docbao.control.Constant;
import com.example.nguyentrung.docbao.control.SqliteM;
import com.example.nguyentrung.docbao.model.NewsSave;
import com.example.nguyentrung.docbao.view.activity.MainActivity;
import com.example.nguyentrung.docbao.model.News;
import com.example.nguyentrung.docbao.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.nguyentrung.docbao.R.id.imageView;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class FragmentReadingNews extends Fragment implements View.OnClickListener {
    public static final String TAG = "FragmentReadingNews";

    private String link;
    private ArrayList<String> arrRows = new ArrayList<>();
    private String paragraph = "";
    private LinearLayout linearLayoutDisPlayNews;
    private ProgressDialog progressDialog;
    private Button btnShareFb;
    private String title;
    private String description;
    private String imageURL;
    private boolean isRunning = false;
    private int typeNewspapger;
    private int from;
    private FloatingActionButton floatingActionButtonSave;
    //    private SQLiteManager sqLiteManager;
    private News news;
    private NewsSave newsSave;
    private SqliteM sqLiteManager;
    private String linkNews = "";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(Constant.LINK_SAVED, link);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRunning = true;
        if (savedInstanceState != null) {
            link = savedInstanceState.getString(Constant.LINK_SAVED);
        }
        Bundle bundle = getArguments();
        typeNewspapger = bundle.getInt(Constant.KEY_TYPE_NEWSPAPER);
        news = (News) bundle.getSerializable(Constant.KEY_ITEM_NEWS);
        from = bundle.getInt(Constant.KEY_FROM);
        link = news.getLink();
        imageURL = news.getUrlImage();
        newsSave = new NewsSave();


        newsSave.setTitle(news.getTitle());
        newsSave.setPubdate(news.getDate());
        newsSave.setDescription(news.getDescription());

        sqLiteManager = new SqliteM(getContext());

        if (!TextUtils.isEmpty(link)) {
            if (link.contains("24h.com.vn")) {
                link = link.replace("24h.com.vn", "24h.vn");
            }
        }
//        if (!TextUtils.isEmpty(imageURL)) {
//            imageURL = imageURL.replace("24h.com.vn", "24h.vn");
//        }
        newsSave.setImg(imageURL);
        initDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (from == Constant.NEW_NEWS) {
            floatingActionButtonSave.setImageResource(R.drawable.ic_save);
        } else if (from == Constant.OLD_NEWS) {
            floatingActionButtonSave.setImageResource(R.drawable.ic_delete);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        linearLayoutDisPlayNews = new LinearLayout(getContext());
        linearLayoutDisPlayNews = (LinearLayout) view.findViewById(R.id.linenearDisplayNews);
        btnShareFb = (Button) getActivity().findViewById(R.id.btnShareFb);
        floatingActionButtonSave = (FloatingActionButton) getActivity().findViewById(R.id.fabSave);
        floatingActionButtonSave.setVisibility(View.VISIBLE);
        floatingActionButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sqLiteManager = new SQLiteManager(getContext());
                if (from == Constant.NEW_NEWS) {
                    insertNews();
                } else {
                    deleteNews();
                }
                floatingActionButtonSave.setVisibility(View.GONE);
            }
        });
//        btnShareFb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity mainActivity = (MainActivity) getActivity();
//                mainActivity.showShareDialog(title, description, link, imageURL);
//            }
//        });

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
                if (progressDialog.isShowing()) {
                    progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    ((MainActivity) getActivity()).setToolbarAfterBackPress();
                                    progressDialog.dismiss();
                                }
                                return true;
                            }
                            return false;
                        }
                    });
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (isRunning) {
                    if (typeNewspapger == Constant.TYPE_24H) {
                        try {
                            arrRows.clear();
                            Document document = Jsoup.connect(link).get();
                            Elements elements = document.select("div.container");
                            Elements eTitle = elements.select("div.baivietTitle h1");
                            Elements ePubdate = elements.select("div.baivietDate");
                            Elements eFirst = elements.select("div.text-conent h2");
                            title = eTitle.text();
                            description = eFirst.text();
                            arrRows.add("Title:" + eTitle.text());
                            arrRows.add("PubDate:" + ePubdate.text());
                            arrRows.add("First:" + "\t" + eFirst.text());
                            Elements elements3 = elements.select("div.text-conent p");
                            for (Element element : elements3) {
                                if (!element.select("a").hasAttr("onclick")) {
                                    if (element.select("img.news-image").hasAttr("src")) {
                                        if (!paragraph.isEmpty()) {
                                            arrRows.add(paragraph);
                                            paragraph = "";
                                        }
                                        String img = element.select("img.news-image").attr("src");
                                        arrRows.add("Image:" + img);
                                    } else {
                                        if (!element.text().isEmpty()) {
                                            if (!paragraph.isEmpty()) {
                                                paragraph += "\n" + "\t" + element.text();
                                            } else {
                                                paragraph += "\t" + element.text();
                                            }
                                        }
                                    }
                                }
                            }
                            if (!paragraph.isEmpty()) {
                                arrRows.add(paragraph);
                            }
                            Element eJournalist = elements.select("div.nguontin").first();
                            if (eJournalist.hasText()) {
                                arrRows.add("Journalist:Theo: " + eJournalist.text());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (typeNewspapger == Constant.TYPE_VNEXPRESS) {
                        arrRows.clear();
                        try {
                            Document document = Jsoup.connect(link).get();
                            Elements eTitle = document.select("div#container div.title_news h1");
                            arrRows.add(eTitle.text().trim());
                            Elements ePubdate = document.select("div#container div.block_timer.left.txt_666");
                            arrRows.add(ePubdate.text().trim());
                            Elements eFirst = document.select("div#container h3.short_intro.txt_666");
                            Elements eFirst2 = document.select("div#container div.short_intro.txt_666");
                            arrRows.add("First:" + "\t" + eFirst.text().trim());
                            arrRows.add("First:" + "\t" + eFirst2.text().trim());

                            Elements eSlideShows = document.select("div#article_content.block_ads_connect div.item_slide_show");
                            for (Element element : eSlideShows) {
                                String URLImage = element.select("div.block_thumb_slide_show img").attr("src");
                                arrRows.add("Image:" + URLImage);
                                String caption = element.select("div.desc_cation p").text().trim();
                                arrRows.add("\t" + caption);
                            }

                            Elements eContent = document.select("div#container div.fck_detail.width_common.block_ads_connect");
                            if (!eContent.isEmpty()) {
                                for (Element element : eContent.first().children()) {
                                    if (!element.select("table.tplCaption").isEmpty()) {
                                        String URLImage = element.select("table.tplCaption tbody tr td img").attr("src");
                                        arrRows.add("Image:" + URLImage);
                                        String caption = element.select("table.tplCaption tbody tr td p.Image").text().trim();
                                        arrRows.add("\t" + caption);
                                    } else if (!element.select("table.tbl_insert").isEmpty()) {
                                        String mailTo = element.select("table.tbl_insert tbody tr td").text().trim();
                                        arrRows.add("\t" + mailTo);
                                    } else if (!element.select("p").isEmpty() && element.select("p em").isEmpty() && element.select("p strong a").isEmpty()) {
                                        paragraph = "";

                                        if (!element.select("p strong").isEmpty()) {
                                            paragraph += "Journalist:Theo: " + element.text().trim();
                                        } else {
                                            paragraph += "\t" + element.text().trim();
                                        }
                                        arrRows.add(paragraph);
                                    }
                                }
                            }

                            if (!document.select("div#container div.author_mail.width_common").isEmpty()) {
                                paragraph = "Journalist:Theo: " + document.select("div#container div.author_mail.width_common p strong").text().trim();
                                arrRows.add(paragraph);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
//                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                addTextView(news.getTitle(), getResources().getInteger(R.integer.size_title_textview_readingnews), getResources().getColor(R.color.color_text_textview_title_readingnews), true, false);
                addTextView(news.getDate(), getResources().getInteger(R.integer.size_pubdate_textview_readingnews), getResources().getColor(R.color.color_text_textview_pubdate_readingnews), false, true);
                for (String s : arrRows) {
                    linkNews += "_" + s;
                    if (!TextUtils.isEmpty(s)) {
                        if (isRunning) {
                            if (s.contains("Image:")) {
                                s = s.substring(s.indexOf("http://"));
                                addImage(s);
                            } else if (s.contains("Title:")) {
                                s = s.substring(6);
//                                addTextView(s, getResources().getInteger(R.integer.size_title_textview_readingnews), getResources().getColor(R.color.color_text_textview_title_readingnews), true, false);
                            } else if (s.contains("PubDate:")) {
                                s = s.substring(8);
//                                addTextView(s, getResources().getInteger(R.integer.size_pubdate_textview_readingnews), getResources().getColor(R.color.color_text_textview_pubdate_readingnews), false, true);
                            } else if (s.contains("First:")) {
                                s = s.substring(6);
                                Log.e("doc bao journalist", s);
                                addTextView(s, getResources().getInteger(R.integer.size_first_paragraph_textview_readingnews), getResources().getColor(R.color.color_text_textview_first_readingnews), false, false);
                            } else if (s.contains("Journalist:")) {
                                Log.e("doc bao journalist", s);
                                s = s.substring(11);
                                addTextViewJournalist(s, getResources().getInteger(R.integer.size_first_paragraph_textview_readingnews), getResources().getColor(R.color.color_text_textview_first_readingnews));
                            } else {
                                addTextView(s, getResources().getInteger(R.integer.size_paragraph_textview_readingnews), getResources().getColor(R.color.color_text_textview_first_readingnews), false, false);
                            }
                        }
                    }
                    progressDialog.dismiss();
//                    loadAds();
                }
                newsSave.setLink(linkNews);
            }
        }.execute();
    }

    private void deleteNews() {
//        sqLiteManager.deleteNews(news.getLink());
        sqLiteManager.deleteNews(newsSave.getTitle());
        ArrayList<NewsSave> arrayList = new ArrayList<>();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportFragmentManager().popBackStack();
        mainActivity.setToolbarAfterBackPress();
        arrayList.addAll(sqLiteManager.getAllNews());
    }

    private void insertNews() {
        for (String s : arrRows) {
            linkNews += "^_^" + s;
            if (!TextUtils.isEmpty(s)) {
                if (isRunning) {
                    if (s.contains("Image:")) {
                        s = s.substring(s.indexOf("http://"));
                        saveImg(s);
                    }
                }
            }
        }
        saveImg(imageURL);
        sqLiteManager.addNews(newsSave);

        Log.e("image cua link save", newsSave.getImg());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isRunning = false;
    }

//    public void loadAds(){
//        adView = (AdView) getActivity().findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
////                .addTestDevice("94B99A903C1706B2740808BECA1185EE")
//                .build();
//        adView.loadAd(adRequest);
//    }

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

    private void addImage(String urlImage) {
        int w = (int) getResources().getDimension(R.dimen.w_image_readingnews);
        int h = (int) getResources().getDimension(R.dimen.h_image_readingnews);
        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, h);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.margin_bottom_textview_readingnews));
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(layoutParams);
        linearLayoutDisPlayNews.addView(imageView);
        Glide.with(getContext())
                .load(urlImage)
                .asBitmap()
                .fitCenter()
                .into(imageView);
    }

    private void saveImg(final String urlImg) {
//        Log.e("SAVE ANHsdfskdfhsdkj","sadjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjjj");
//        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 160);
//        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
//        layoutParams.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen.margin_bottom_textview_readingnews));
//        ImageView imageView = new ImageView(getContext());
//        imageView.setLayoutParams(layoutParams);
//        linearLayoutDisPlayNews.addView(imageView);
//        imageView = new ImageView(getContext());
//        Glide.
//                with(this).
//                load(urlImg).
//                asBitmap().
//                into(new BitmapImageViewTarget(imageView) {
//                    @Override
//                    protected void setResource(Bitmap resource) {
//                        super.setResource(resource);
//                        ByteArrayOutputStream blob = new ByteArrayOutputStream();
//                        resource.compress(Bitmap.CompressFormat.PNG, 100, blob);
//                        sqLiteManager.addImage(urlImg, blob.toByteArray());
//                        Log.e("byte image", blob.toByteArray().toString());
//                    }
//                });
        new DownloadImage().execute(urlImg);
    }

    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {

            String urlImg = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(urlImg).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
                ByteArrayOutputStream blob = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, blob);
                sqLiteManager.addImage(urlImg, blob.toByteArray());
                Log.e("byte image", blob.toByteArray().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onClick(View v) {
        if ((v.getId()) == Constant.TAG_SHARE_BUTTON) {

            Toast.makeText(getContext(), "shared", Toast.LENGTH_SHORT).show();
        }
    }
}

