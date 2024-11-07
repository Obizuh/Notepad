package com.example.notepad.utils;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.notepad.sqlite.SQLiteHelper;
import com.example.notepad.bean.NoteBean;

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
    @SuppressLint("Range")
    //查询
    public List<NoteBean> queryNote() {
        List<NoteBean> list = new ArrayList<NoteBean>();
        String sql = "SELECT * FROM " + SQLiteHelper.U_NOTEPAD + " ORDER BY id DESC";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                NoteBean bean = new NoteBean();
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                bean.setId(id);
                bean.setTitle(title);
                bean.setContent(content);
                bean.setTime(time);
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
    //保存
    public boolean saveNote(String title, String content,String time) {
        boolean saveSuccess = false;
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("content", content);
        cv.put("time", getTime()); // 获取当前时间并保存

        long rowId = db.insert(SQLiteHelper.U_NOTEPAD, null, cv);
        if (rowId > 0) {
            saveSuccess = true;
        }
        return saveSuccess;
    }
    //修改
    public boolean updateNote(int id, String title, String content, String time) {
        boolean updateSuccess = false;
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("content", content);
        cv.put("time", time);
        int rows = db.update(SQLiteHelper.U_NOTEPAD, cv, "id=?", new String[]{id + ""});
        if (rows > 0) {
            updateSuccess = true;
        }
        return updateSuccess;
    }
    //删除
    public boolean deleteNote(int id) {
        boolean delSuccess = false;
        int rows = db.delete(SQLiteHelper.U_NOTEPAD, "id=?", new String[]{String.valueOf(id)});
        if (rows > 0) {
            delSuccess = true;
        }
        return delSuccess;
    }
}