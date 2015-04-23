package com.teachmate.teachmate.DBClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper{
    public DbHelper(Context context) {
        super(context, DatabaseHelperMeta.DB_NAME, null, DatabaseHelperMeta.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("creating " , "DB");
        db.execSQL(Schema.CREATE_TABLE_DEVICE_INFO);
        db.execSQL(Schema.CREATE_TABLE_Profile);
        db.execSQL(Schema.CREATE_TABLE_REQUESTS);
        Log.v("created " , "DB");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
