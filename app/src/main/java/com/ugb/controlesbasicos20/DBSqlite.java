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
        public static final String COLUMN_TYPE = "Tipo";
        public static final String COLUMN_FOTO = "Foto";
    }

    public static class TableProd implements BaseColumns {
        public static final String TABLE_PROD = "Productos";
        public static final String COLUMN_USER = "User";
        public static final String COLUMN_CODIGO = "Codigo";
        public static final String COLUMN_NOMBRE = "Nombre";
        public static final String COLUMN_MARCA = "Marca";
        public static final String COLUMN_DESCRIPCION = "Descripcion";
        public static final String COLUMN_PRECIO = "Precio";
        public static final String COLUMN_COSTO = "Costo";
        public static final String COLUMN_FOTO = "Foto";
    }

    public static class TableVent implements BaseColumns {
        public static final String TABLE_VENT = "Ventas";
        public static final String COLUMN_FECHA = "Fecha";
        public static final String COLUMN_ID_PRODUCTO = "ID_Producto";
        public static final String COLUMN_CANTIDAD = "Cantidad";
        public static final String COLUMN_PRECIO_UNITARIO = "Precio_Unitario";
        public static final String COLUMN_TOTAL_VENTA = "Total_Venta";
        public static final String COLUMN_ID_CLIENTE = "ID_Cliente";
        public static final String COLUMN_METODO_PAGO = "Metodo_Pago";
    }

    private static final String SQL_CREATE_TABLE_USER =
            "CREATE TABLE " + TableUser.TABLE_USER + " (" +
                    TableUser._ID + " INTEGER PRIMARY KEY," +
                    TableUser.COLUMN_NOMBRE + " TEXT," +
                    TableUser.COLUMN_CORREO + " TEXT," +
                    TableUser.COLUMN_TYPE + " TEXT," +
                    TableUser.COLUMN_FOTO + " TEXT)";

    private static final String SQL_CREATE_TABLE_PROD =
            "CREATE TABLE " + TableProd.TABLE_PROD + " (" +
                    TableProd._ID + " INTEGER PRIMARY KEY," +
                    TableProd.COLUMN_USER + " TEXT," +
                    TableProd.COLUMN_CODIGO + " TEXT," +
                    TableProd.COLUMN_NOMBRE + " TEXT," +
                    TableProd.COLUMN_MARCA + " TEXT," +
                    TableProd.COLUMN_DESCRIPCION + " TEXT," +
                    TableProd.COLUMN_PRECIO + " TEXT," +
                    TableProd.COLUMN_COSTO + " TEXT," +
                    TableProd.COLUMN_FOTO + " TEXT)";

    private static final String SQL_CREATE_TABLE_VENT =
            "CREATE TABLE " + TableVent.TABLE_VENT + " (" +
                    TableVent._ID + " INTEGER PRIMARY KEY," +
                    TableVent.COLUMN_FECHA + " TEXT," +
                    TableVent.COLUMN_ID_PRODUCTO + " INTEGER," +
                    TableVent.COLUMN_CANTIDAD + " INTEGER," +
                    TableVent.COLUMN_PRECIO_UNITARIO + " REAL," +
                    TableVent.COLUMN_TOTAL_VENTA + " REAL," +
                    TableVent.COLUMN_ID_CLIENTE + " INTEGER," +
                    TableVent.COLUMN_METODO_PAGO + " TEXT," +
                    "FOREIGN KEY(" + TableVent.COLUMN_ID_PRODUCTO + ") REFERENCES " + TableProd.TABLE_PROD + "(" + TableProd._ID + "))";

    private static final String SQL_DELETE_ENTRIES_USER =
            "DROP TABLE IF EXISTS " + TableUser.TABLE_USER;
    private static final String SQL_DELETE_ENTRIES_PROD =
            "DROP TABLE IF EXISTS " + TableProd.TABLE_PROD;
    private static final String SQL_DELETE_ENTRIES_VENT =
            "DROP TABLE IF EXISTS " + TableVent.TABLE_VENT;

    public DBSqlite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_USER);
        db.execSQL(SQL_CREATE_TABLE_PROD);
        db.execSQL(SQL_CREATE_TABLE_VENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_USER);
        db.execSQL(SQL_DELETE_ENTRIES_PROD);
        db.execSQL(SQL_DELETE_ENTRIES_VENT);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
