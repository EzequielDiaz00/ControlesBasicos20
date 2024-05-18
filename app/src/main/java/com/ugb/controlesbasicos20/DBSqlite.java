package com.ugb.controlesbasicos20;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class DBSqlite extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "manage360";

    public static class TableUser implements BaseColumns {
        public static final String TABLE_USER = "Users";
        public static final String COLUMN_NOMBRE = "Nombre";
        public static final String COLUMN_CORREO = "Correo";
        public static final String COLUMN_FOTO = "Foto";
    }

    public static class TableProd implements BaseColumns {
        public static final String TABLE_PROD = "Productos";
        public static final String COLUMN_CODIGO = "Codigo";
        public static final String COLUMN_NOMBRE = "Nombre";
        public static final String COLUMN_MARCA = "Marca";
        public static final String COLUMN_DESCRIPCION = "Descripcion";
        public static final String COLUMN_PRECIO = "Precio";
        public static final String COLUMN_FOTO = "Foto";
    }

    private static final String SQL_CREATE_TABLE_USER =
            "CREATE TABLE " + TableUser.TABLE_USER + " (" +
                    TableUser._ID + " INTEGER PRIMARY KEY," +
                    TableUser.COLUMN_NOMBRE + " TEXT," +
                    TableUser.COLUMN_CORREO + " TEXT," +
                    TableUser.COLUMN_FOTO + " TEXT)";

    private static final String SQL_CREATE_TABLE_PROD =
            "CREATE TABLE " + TableProd.TABLE_PROD + " (" +
                    TableProd._ID + " INTEGER PRIMARY KEY," +
                    TableProd.COLUMN_CODIGO + " TEXT," +
                    TableProd.COLUMN_NOMBRE + " TEXT," +
                    TableProd.COLUMN_MARCA + " TEXT," +
                    TableProd.COLUMN_DESCRIPCION + " TEXT," +
                    TableProd.COLUMN_PRECIO + " TEXT," +
                    TableProd.COLUMN_FOTO + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TableUser.TABLE_USER;

    public DBSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_USER);
        db.execSQL(SQL_CREATE_TABLE_PROD);
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
