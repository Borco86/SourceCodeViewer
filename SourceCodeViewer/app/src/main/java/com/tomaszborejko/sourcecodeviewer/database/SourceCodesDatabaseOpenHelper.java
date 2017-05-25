package com.tomaszborejko.sourcecodeviewer.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Borco on 2017-05-25.
 */

public class SourceCodesDatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "source_codes.db";
    private static final int DATABASE_VERSION = 1;

    public SourceCodesDatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + SourceCodesTableContract.TABLE_NAME + " ("
            + SourceCodesTableContract.COLUMN_WEBSITE_URL + " TEXT,"
            + SourceCodesTableContract.COLUMN_SOURCE_CODE + " TEXT)";

    private static String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + SourceCodesTableContract.TABLE_NAME;

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL(SQL_DROP_TABLE);
            onCreate(db);
        }
    }

    public Cursor searchQuery(String userInputUrl) {
        Cursor cursor = getReadableDatabase().query(SourceCodesTableContract.TABLE_NAME,
                new String[]{
                        SourceCodesTableContract.COLUMN_SOURCE_CODE
                }, SourceCodesTableContract.COLUMN_WEBSITE_URL + " LIKE ?", new String[]{
                        userInputUrl + "%"
                }, null, null, null);
        return cursor;
    }

}


