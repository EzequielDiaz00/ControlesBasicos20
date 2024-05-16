package com.ugb.controlesbasicos20;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBManage360 extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "manage360";

    public static class TableUser implements BaseColumns {
        public static final String TABLE_USER = "Users";
        public static final String COLUMN_NOMBRE = "Nombre";
        public static final String COLUMN_CORREO = "Correo";
    }

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TableUser.TABLE_USER + " (" +
                    TableUser._ID + " INTEGER PRIMARY KEY," +
                    TableUser.COLUMN_NOMBRE + " TEXT," +
                    TableUser.COLUMN_CORREO + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TableUser.TABLE_USER;

    public DBManage360(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
