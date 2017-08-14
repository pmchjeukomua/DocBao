package com.example.nguyentrung.docbao.control;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.nguyentrung.docbao.model.News;
import com.example.nguyentrung.docbao.model.NewsSave;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.nguyentrung.docbao.R.id.imageView;
import static java.security.AccessController.getContext;

/**
 * Created by nguyentrung on 5/20/2017.
 */

public class SqliteM extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;
    // Database Name
    private static final String DATABASE_NAME = "newsdatabase";
    private static final String TABLE_NEWS = "news";
    private static final String KEY_ID = "id";
    private static final String KEY_LINK = "content";
    private static final String KEY_IMAGE = "img";
    private SQLiteDatabase sqLiteDatabase;
    private static final String TABLE_IMAGE = "image";
    private static final String KEY_LINK_IMAGE = "linkimage";
    private static final String KEY_TITLE = "title";
    private static final String KEY_PUBDATE = "pubdate";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE_NEWS = "image";
    private Context context;

    public SqliteM(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NEWS_TABLE = "CREATE TABLE " + TABLE_NEWS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + KEY_IMAGE_NEWS + " TEXT, "
                + KEY_TITLE + " TEXT, "
                + KEY_PUBDATE + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_LINK + " TEXT " + ")";
        db.execSQL(CREATE_NEWS_TABLE);

        String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + KEY_LINK_IMAGE + " TEXT,"
                + KEY_IMAGE + " BLOB " + ")";
        db.execSQL(CREATE_IMAGE_TABLE);
        Log.e("tao bang", "asdsfsssffffffffffffffffffffdasda");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);
// Create tables again
        onCreate(db);
    }

    public ArrayList<NewsSave> getAllNews() {
        ArrayList<NewsSave> arrNews = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NEWS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String image = cursor.getString(1);
            Log.e("IMAGE",image);
            String title = cursor.getString(2);
            String pubdate = cursor.getString(3);
            String description = cursor.getString(4);
            String link = cursor.getString(5);
            arrNews.add(new NewsSave(image, title, pubdate, description, link));
            cursor.moveToNext();
        }
        db.close();
        return arrNews;
    }

    public void addNews(NewsSave news) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE_NEWS, news.getImg());
        values.put(KEY_TITLE, news.getTitle());
        values.put(KEY_PUBDATE, news.getPubdate());
        values.put(KEY_DESCRIPTION,news.getDescription());
        values.put(KEY_LINK, news.getLink());
        db.insert(TABLE_NEWS, null, values);
        db.close(); // Closing database connection
    }

    public void addImage(String link,byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LINK_IMAGE, link);
        values.put(KEY_IMAGE, image);
        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }



    public NewsSave getNews(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NEWS, new String[]{KEY_ID, KEY_LINK}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        String string = cursor.getString(1);
        db.close();
        return converNews(string);
    }

    public NewsSave converNews(String string) {
        NewsSave news = new NewsSave(

        );
        String[] arr = string.split("[-]");
        news.setLink(string);
        for (String s : arr) {
            if (!TextUtils.isEmpty(s)) {
                if (s.contains("Image:")) {
                    String urlImg = s.substring(s.indexOf("http://"));
                } else if (s.contains("Title:")) {
                    s = s.substring(6);
                    Log.e("doc bao title", s);
                    news.setTitle(s);
                } else if (s.contains("PubDate:")) {
                    s = s.substring(8);
                    Log.e("doc bao pubday", s);
                    news.setPubdate(s);
                } else {
                    news.setDescription(s);
                }

            }
        }
        return news;
    }

    public byte[] getImage(String link) {
        SQLiteDatabase db = this.getReadableDatabase();
        byte[] image = new byte[0];
        Cursor cursor = db.query(TABLE_IMAGE, new String[]{KEY_LINK_IMAGE, KEY_IMAGE}, KEY_LINK_IMAGE + "=?",
                new String[]{String.valueOf(link)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            image = cursor.getBlob(1);
        }
        db.close();
        return image;
    }
    public String getImage(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        String linkImage ="";
        Cursor cursor = db.query(TABLE_IMAGE, new String[]{KEY_LINK_IMAGE, KEY_IMAGE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor.moveToFirst()) {
            linkImage = cursor.getString(0);
        }
        db.close();
        return linkImage;
    }
    public void deleteNews(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NEWS, KEY_ID + " = ?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteAllRecords() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NEWS, null, null);
        db.delete(TABLE_IMAGE, null, null);
        db.close();
    }

    public void deleteNews(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NEWS, KEY_TITLE + " = ?",
                new String[]{String.valueOf(title)});
        db.close();
    }
}
