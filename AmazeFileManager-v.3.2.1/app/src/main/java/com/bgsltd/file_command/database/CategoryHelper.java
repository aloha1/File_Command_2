package com.bgsltd.file_command.database;

/**
 * Created by Yunwen on 2/15/2016.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ifanr on 2015/3/29.
 */
public class CategoryHelper extends SQLiteOpenHelper {
    //Db version
    private static final int DATABASE_VERSION = 1;
    //name of the db
    private static final String DATABASE_NAME = "me.favorite.db";

    public CategoryHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create the table
        String CREATE_TABLE_STUDENT="CREATE TABLE "+ Category.TABLE+"("
                + Category.KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + Category.KEY_topic+" TEXT, "
                + Category.KEY_time +" INTEGER, "
                + Category.KEY_lectureId+" TEXT, "
                + Category.KEY_title+" TEXT, "
                + Category.KEY_coverImageUri+" TEXT, "
                + Category.KEY_teacher+" TEXT, "
                + Category.KEY_content+" TEXT)";
        db.execSQL(CREATE_TABLE_STUDENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //if the table exist, delete it
        db.execSQL("DROP TABLE IF EXISTS "+ Category.TABLE);
        //recreate the table
        onCreate(db);
    }
}
