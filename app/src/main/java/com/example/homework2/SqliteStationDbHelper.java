package com.example.homework2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteStationDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "MRTStations";
    private static final int DATABASE_VERSION =1;
    public SqliteStationDbHelper(Context c){
        super(c,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public SqliteStationDbHelper(Context c,String strDbName, int intDbVersion){
        super(c,strDbName,null,intDbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCommand = "CREATE TABLE IF NOT EXISTS stations (station_id INTEGER PRIMARY KEY,"
                +"station_name TEXT NOT NULL,"
                +"station_lat TEXT NOT NULL,"
                +"station_lng TEXT NOT NULL)";
        db.execSQL(sqlCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
