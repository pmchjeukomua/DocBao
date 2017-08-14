package com.example.nguyentrung.docbao.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.nguyentrung.docbao.model.News;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by nguyentrung on 5/2/2017.
 */

public class SQLiteManager {
    public static final String PATH = "/data/data/com.example.nguyentrung.docbao/databases";
    public static final String FILE_NAME = "readingnews.sqlite";
    private Context context;
    private SQLiteDatabase sqLiteDatabase;
    public static final String TABLE = "history";
    public static final String _ID = "id";
    public static final String _TITLE = "title";
    public static final String _URLIMAGE = "urlimage";
    public static final String _DESCRIPTION = "description";
    public static final String _LINK = "link";
    public static final String _DATE = "date";
    public static final String _TYPE = "type";
    private ArrayList<News> arrNews;


    public SQLiteManager(Context context) {
        this.context = context;
        copyFile();
    }

    private void copyFile() {
        try {
            File file = new File(PATH + FILE_NAME);
            if (!file.exists()) {
                File fileParent = file.getParentFile();
                fileParent.mkdirs();
                file.createNewFile();
                InputStream inputStream = context.getAssets().open(FILE_NAME);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bytes = new byte[1024];
                int count = inputStream.read(bytes);
                while (count != -1){
                    fileOutputStream.write(bytes, 0, count);
                    count = inputStream.read(bytes);
                }
                fileOutputStream.close();
                inputStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void openDatabase(){
        sqLiteDatabase = context.openOrCreateDatabase(PATH + FILE_NAME, Context.MODE_APPEND, null);
    }

    public void closeDatabase(){
        sqLiteDatabase.close();
    }

    public ArrayList<News> getArrNews(){
        openDatabase();
        ArrayList<News> arrNewses = new ArrayList<>();
        String query = "select * from " + TABLE;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            News news = new News(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getInt(6));
            arrNewses.add(news);
            cursor.moveToNext();
        }
        closeDatabase();
        return arrNewses;
    }

    public long deleteAllRecords(){
        openDatabase();
        long result = sqLiteDatabase.delete(TABLE, null, null);
        closeDatabase();
        return result;
    }

    public long insertNewsTo(News news){
        ContentValues contentValues = new ContentValues();
        contentValues.put(_TITLE, news.getTitle());
        contentValues.put(_URLIMAGE, news.getUrlImage());
        contentValues.put(_DESCRIPTION, news.getDescription());
        contentValues.put(_LINK, news.getLink());
        contentValues.put(_DATE, news.getDate());
        contentValues.put(_TYPE, news.getType());
        openDatabase();
        long result = sqLiteDatabase.insert(TABLE, null, contentValues);
        closeDatabase();
        return result;
    }

    public long deleteNews(String link){
        String whereClause = _LINK + "=?";
        String[] whereArgs = new String[]{link};
        openDatabase();
        long result = sqLiteDatabase.delete(TABLE, whereClause, whereArgs);
        closeDatabase();
        return result;
    }
}
