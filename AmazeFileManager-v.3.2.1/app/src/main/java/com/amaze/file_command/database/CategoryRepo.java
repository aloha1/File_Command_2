package com.amaze.file_command.database;

/**
 * Created by Yunwen on 2/15/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryRepo {
    private CategoryHelper categoryHelper;

    public CategoryRepo(Context context){
        categoryHelper =new CategoryHelper(context);
    }

    public int insert(Category category){
        //Open Db，write data
        SQLiteDatabase db= categoryHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Category.KEY_time, category.time);
        values.put(Category.KEY_content, category.content);
        values.put(Category.KEY_topic, category.topic);
        values.put(Category.KEY_lectureId, category.lectureId);
        values.put(Category.KEY_title, category.title);
        values.put(Category.KEY_coverImageUri, category.coverImageUri);
        values.put(Category.KEY_teacher, category.teacher);
        //
        long algorithm_Id=db.insert(Category.TABLE,null,values);
        db.close();
        return (int)algorithm_Id;
    }

    public void delete(int algorithm_Id){
        SQLiteDatabase db= categoryHelper.getWritableDatabase();
        db.delete(Category.TABLE, Category.KEY_ID+"=?", new String[]{String.valueOf(algorithm_Id)});
        db.close();
    }
    public void update(Category category){
        SQLiteDatabase db= categoryHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(Category.KEY_time, category.time);
        values.put(Category.KEY_content, category.content);
        values.put(Category.KEY_topic, category.topic);
        values.put(Category.KEY_lectureId, category.lectureId);
        values.put(Category.KEY_title, category.title);
        values.put(Category.KEY_coverImageUri, category.coverImageUri);
        values.put(Category.KEY_teacher, category.teacher);
        db.update(Category.TABLE, values, Category.KEY_ID + "=?", new String[]{String.valueOf(category.dbId)});
        db.close();
    }

    public ArrayList<HashMap<String, String>> getAlgorithmList(){
        SQLiteDatabase db= categoryHelper.getReadableDatabase();
        String selectQuery="SELECT "+
                Category.KEY_ID+","+
                Category.KEY_content+","+
                Category.KEY_topic+","+
                Category.KEY_lectureId+","+
                Category.KEY_title+","+
                Category.KEY_coverImageUri+","+
                Category.KEY_teacher+","+
                Category.KEY_time +" FROM "+ Category.TABLE;
        ArrayList<HashMap<String,String>> algorithmList = new ArrayList<>();
        Cursor cursor=db.rawQuery(selectQuery,null);

        if(cursor.moveToFirst()){
            do{
                HashMap<String,String> algorithm=new HashMap<>();
                algorithm.put("id",cursor.getString(cursor.getColumnIndex(Category.KEY_ID)));
                algorithm.put("topic",cursor.getString(cursor.getColumnIndex(Category.KEY_topic)));
                algorithm.put("lectureId",cursor.getString(cursor.getColumnIndex(Category.KEY_lectureId)));
                algorithm.put("title",cursor.getString(cursor.getColumnIndex(Category.KEY_title)));
                algorithm.put("coverImageUri",cursor.getString(cursor.getColumnIndex(Category.KEY_coverImageUri)));
                algorithm.put("teacher",cursor.getString(cursor.getColumnIndex(Category.KEY_teacher)));
                algorithm.put("content",cursor.getString(cursor.getColumnIndex(Category.KEY_content)));
                algorithmList.add(algorithm);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return algorithmList;
    }

    public Category getColumnById(int Id){
        SQLiteDatabase db= categoryHelper.getReadableDatabase();
        String selectQuery="SELECT "+
                Category.KEY_ID + "," +
                Category.KEY_content + "," +
                Category.KEY_topic + "," +
                Category.KEY_lectureId + "," +
                Category.KEY_title + "," +
                Category.KEY_coverImageUri + "," +
                Category.KEY_teacher + "," +
                Category.KEY_time +
                " FROM " + Category.TABLE
                + " WHERE " +
                Category.KEY_ID + "=?";
        int iCount=0;
        Category category =new Category();
        Cursor cursor=db.rawQuery(selectQuery,new String[]{String.valueOf(Id)});
        if(cursor.moveToFirst()){
            do{
                category.dbId =cursor.getInt(cursor.getColumnIndex(Category.KEY_ID));
                category.content =cursor.getString(cursor.getColumnIndex(Category.KEY_content));
                category.topic  =cursor.getString(cursor.getColumnIndex(Category.KEY_topic));
                category.lectureId  =cursor.getString(cursor.getColumnIndex(Category.KEY_lectureId));
                category.title  =cursor.getString(cursor.getColumnIndex(Category.KEY_title));
                category.coverImageUri  =cursor.getString(cursor.getColumnIndex(Category.KEY_coverImageUri));
                category.teacher  =cursor.getString(cursor.getColumnIndex(Category.KEY_teacher));
                category.time =cursor.getInt(cursor.getColumnIndex(Category.KEY_time));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return category;
    }

    public Category getValueByKey(int Id){
        SQLiteDatabase db= categoryHelper.getReadableDatabase();
        String selectQuery="SELECT "+
                Category.KEY_ID + "," +
                Category.KEY_content + "," +
                Category.KEY_topic + "," +
                Category.KEY_lectureId + "," +
                Category.KEY_title + "," +
                Category.KEY_coverImageUri + "," +
                Category.KEY_teacher + "," +
                Category.KEY_time +
                " FROM " + Category.TABLE
                + " WHERE " +
                Category.KEY_ID + "=?";

        int iCount=0;

        Category category =new Category();
        Cursor cursor=db.rawQuery(selectQuery,new String[]{String.valueOf(Id)});
        if(cursor.moveToFirst()){
            do{
                category.dbId =cursor.getInt(cursor.getColumnIndex(Category.KEY_ID));
                category.content =cursor.getString(cursor.getColumnIndex(Category.KEY_content));
                category.topic  =cursor.getString(cursor.getColumnIndex(Category.KEY_topic));
                category.lectureId  =cursor.getString(cursor.getColumnIndex(Category.KEY_lectureId));
                category.title  =cursor.getString(cursor.getColumnIndex(Category.KEY_title));
                category.coverImageUri  =cursor.getString(cursor.getColumnIndex(Category.KEY_coverImageUri));
                category.teacher  =cursor.getString(cursor.getColumnIndex(Category.KEY_teacher));
                category.time =cursor.getInt(cursor.getColumnIndex(Category.KEY_time));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return category;
    }

    public Category getColumnByTopic(String topic){
        SQLiteDatabase db= categoryHelper.getReadableDatabase();
        String selectQuery="SELECT "+
                Category.KEY_ID + "," +
                Category.KEY_content + "," +
                Category.KEY_topic + "," +
                Category.KEY_lectureId + "," +
                Category.KEY_title + "," +
                Category.KEY_coverImageUri + "," +
                Category.KEY_teacher + "," +
                Category.KEY_time +
                " FROM " + Category.TABLE
                + " WHERE " +
                Category.KEY_topic + "=?";
        int iCount=0;
        Category searchHistory =new Category();
        Cursor cursor=db.rawQuery(selectQuery,new String[]{String.valueOf(topic)});
        if(cursor.moveToFirst()){
            do{
                searchHistory.dbId =cursor.getInt(cursor.getColumnIndex(Category.KEY_ID));
                searchHistory.content =cursor.getString(cursor.getColumnIndex(Category.KEY_content));
                searchHistory.topic  =cursor.getString(cursor.getColumnIndex(Category.KEY_topic));
                searchHistory.lectureId  =cursor.getString(cursor.getColumnIndex(Category.KEY_lectureId));
                searchHistory.title  =cursor.getString(cursor.getColumnIndex(Category.KEY_title));
                searchHistory.coverImageUri  =cursor.getString(cursor.getColumnIndex(Category.KEY_coverImageUri));
                searchHistory.teacher  =cursor.getString(cursor.getColumnIndex(Category.KEY_teacher));
                searchHistory.time =cursor.getInt(cursor.getColumnIndex(Category.KEY_time));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return searchHistory;
    }
}
