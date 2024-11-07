package com.example.notepad.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.notepad.bean.NoteBean;
import com.example.notepad.sqlite.SQLiteHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBUtils {
    private static DBUtils instance = null;
    private static SQLiteHelper helper;
    private static SQLiteDatabase db;

    public DBUtils(Context context) {
        helper = new SQLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public static DBUtils getInstance(Context context) {
        if (instance == null) {
            instance = new DBUtils(context);
        }
        return instance;
    }

    public List<NoteBean> queryNote() {
        List<NoteBean> list = new ArrayList<>();
        String sql = "SELECT * FROM " + SQLiteHelper.U_NOTEPAD + " ORDER BY time DESC";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                NoteBean bean = new NoteBean();
                bean.setId(cursor.getInt(cursor.getColumnIndex("id")));
                bean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                bean.setContent(cursor.getString(cursor.getColumnIndex("content")));
                bean.setTime(cursor.getString(cursor.getColumnIndex("time")));
                list.add(bean);
            }
            cursor.close();
        }
        return list;
    }

    public static String getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public boolean saveNote(String title, String content, String time) {
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("content", content);
        cv.put("time", time);

        long rowId = db.insert(SQLiteHelper.U_NOTEPAD, null, cv);
        return rowId > 0;
    }

    public int saveNoteAndGetId(String title, String content, String time) {
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("content", content);
        cv.put("time", time);

        long rowId = db.insert(SQLiteHelper.U_NOTEPAD, null, cv);
        if (rowId != -1) {
            Cursor cursor = db.rawQuery("SELECT last_insert_rowid()", null);
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                cursor.close();
                return id;
            }
        }
        return -1;
    }

    public boolean updateNoteTime(int id, String time) {
        ContentValues cv = new ContentValues();
        cv.put("time", time);
        int rows = db.update(SQLiteHelper.U_NOTEPAD, cv, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean updateNote(int id, String title, String content, String time) {
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("content", content);
        cv.put("time", time);
        int rows = db.update(SQLiteHelper.U_NOTEPAD, cv, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean deleteNote(int id) {
        int rows = db.delete(SQLiteHelper.U_NOTEPAD, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }
}