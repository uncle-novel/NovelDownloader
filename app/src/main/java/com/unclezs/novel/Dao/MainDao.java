package com.unclezs.novel.Dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.unclezs.novel.Adapter.DownloadAdapter;
import com.unclezs.novel.Crawl.NovelDownloader;
import com.unclezs.novel.Crawl.NovelSpider;

import java.util.ArrayList;
import java.util.List;

/**
 * 操作SQLite
 * Created by Uncle
 * 2019.07.24.
 */
public class MainDao extends SQLiteOpenHelper {
    public MainDao(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "ucnle.db", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists download(" +
                "title text NOT NULL," +
                "imgPath text NOT NULL," +
                "label text NOT NULL" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public void insert(SQLiteDatabase db,String title,String imgPath,String text){
        System.out.println(text+"--"+imgPath);
        db.execSQL("insert into download(title,imgPath,label) values(?,?,?)",new String[]{title,imgPath,text});
    }
    public void remove(SQLiteDatabase db,String title){
        db.execSQL("delete from download where title=?",new String[]{title});
    }
    public List<DownloadAdapter> queryAll(SQLiteDatabase db){
        List<DownloadAdapter> list=new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from download", null);
        while (cursor.moveToNext()){
            NovelSpider spider = new NovelSpider();
            spider.setNovelTitle(cursor.getString(cursor.getColumnIndex("title")));
            spider.setImgUrl(cursor.getString(cursor.getColumnIndex("imgPath")));
            NovelDownloader downloader=new NovelDownloader(null,null,null,spider);
            downloader.setText(cursor.getString(cursor.getColumnIndex("label")));
            list.add(downloader);
        }
        return list;
    }
}
