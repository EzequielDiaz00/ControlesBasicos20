package com.ugb.controlesbasicos20;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    private static final String dbname="TiendaSQLite";
    private static final int v=1;
    private static final String SQLdb = "CREATE TABLE autos (ID integer primary key autoincrement, marca text, " +
            "motor text, chasis text, vin text, combustion text, foto text)";
    public DB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, dbname, factory, v);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQLdb);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //actualizar la estrucutra de la BD.
    }
    public String administrar_auto(String accion, String[] datos){
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "";
            if (accion.equals("nuevo")) {
                sql = "INSERT INTO autos (marca, motor, chasis, vin, combustion, foto) VALUES('" + datos[1] +
                        "','" + datos[2] + "','" + datos[3] + "','" + datos[4] + "','" + datos[5] + "', '"+ datos[6] +"')";
            } else if (accion.equals("modificar")) {
                sql = "UPDATE autos SET marca='" + datos[1] + "',motor='" + datos[2] + "',chasis='" +
                        datos[3] + "',vin='" + datos[4] + "',combustion='" + datos[5] + "', foto='"+ datos[6] +"' WHERE ID='" + datos[0] + "'";
            } else if (accion.equals("eliminar")) {
                sql = "DELETE FROM autos WHERE ID='" + datos[0] + "'";
            }
            db.execSQL(sql);
            return "ok";
        }catch (Exception e){
            return e.getMessage();
        }
    }
    public Cursor consultar_auto(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM autos ORDER BY marca", null);
        return cursor;
    }
}
