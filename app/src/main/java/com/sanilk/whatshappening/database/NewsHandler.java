package com.sanilk.whatshappening.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sanil on 22/4/18.
 */

public class NewsHandler extends SQLiteOpenHelper {
    private static final String DB_NAME="WHATS_HAPPENING_DB";
    private static final int DB_VERSION=2;

    private static final String TABLE_NAME="NEWS";
    private static final String NEWS_ID_COLUMN_NAME="id";
    private static final String TITLE_COLUMN_NAME="title";
    private static final String DESCRIPTION_COLUMN_NAME="description";
    private static final String IMAGE_URL_COLUMN_NAME="iamgeUrl";
    private static final String URL_COLUMN_NAME="url";

    private Context context;

    public NewsHandler(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context=context;

    }

    public void deleteNews(News n){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE "+NEWS_ID_COLUMN_NAME+"="+n.getNewsId()+";");
//        db.close();
    }

    public void createTable(SQLiteDatabase db){
        String s="CREATE TABLE "+TABLE_NAME+"(" +
                NEWS_ID_COLUMN_NAME+" INTEGER PRIMARY KEY, "+
                TITLE_COLUMN_NAME+" TEXT, "+
                DESCRIPTION_COLUMN_NAME+" TEXT, "+
                IMAGE_URL_COLUMN_NAME+" TEXT, "+
                URL_COLUMN_NAME+" TEXT);";
        db.execSQL(s);
//        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
//        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
//        db.close();
    }

    public int getLastId(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT MAX("+NEWS_ID_COLUMN_NAME+") FROM "+TABLE_NAME+";", new String[]{});
        int id=1;
        if(cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
//        db.close();
        return id;
    }

    public void addNews(News n){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(NEWS_ID_COLUMN_NAME, getLastId()+1);
        contentValues.put(TITLE_COLUMN_NAME, n.getTitle());
        contentValues.put(DESCRIPTION_COLUMN_NAME, n.getDescription());
        contentValues.put(IMAGE_URL_COLUMN_NAME, n.getImageUrl());
        contentValues.put(URL_COLUMN_NAME, n.getUrl());
        db.insert(
            TABLE_NAME, null, contentValues
        );
//        db.close();
    }

    public boolean doesNewsExist(String url){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+URL_COLUMN_NAME+"= ? ;", new String[]{url});
        c.moveToFirst();
        if(c.getCount()==0){
            c.close();
            db.close();
            return false;
        }
        c.close();
//        db.close();
        return true;
    }

    public News[] getNews(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM "+TABLE_NAME+";", new String[]{});
        News[] news=new News[c.getCount()];
        int i=0;

//        c.moveToFirst();
        while(c.moveToNext()){
            News n=new News(
                    c.getInt(0),
                    c.getString(1),
                    c.getString(2),
                    c.getString(3),
                    c.getString(4)
            );
            news[i]=n;
            i++;
        }
        c.close();
//        db.close();
        return news;
    }
}
