package com.ugb.controlesbasicos20;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    private static final String dbname="TiendaSQLite";
    private static final int v=1;
    private static final String SQLdb = "CREATE TABLE pelicula (ID integer primary key autoincrement, titulo text, " +
            "sinopsis text, duracion text, actor text, foto text)";
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
    public String administrar_peli(String accion, String[] datos){
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "";
            if (accion.equals("nuevo")) {
                sql = "INSERT INTO pelicula (titulo, sinopsis, duracion, actor, foto) VALUES('" + datos[1] +
                        "','" + datos[2] + "','" + datos[3] + "','" + datos[4] + "','" + datos[5] + "')";
            } else if (accion.equals("modificar")) {
                sql = "UPDATE pelicula SET titulo='" + datos[1] + "',sinopsis='" + datos[2] + "',duracion='" +
                        datos[3] + "',actor='" + datos[4] + "', foto='"+ datos[5] +"' WHERE ID='" + datos[0] + "'";
            } else if (accion.equals("eliminar")) {
                sql = "DELETE FROM pelicula WHERE ID='" + datos[0] + "'";
            }
            db.execSQL(sql);
            return "ok";
        }catch (Exception e){
            return e.getMessage();
        }
    }
    public Cursor consultar_peli(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM pelicula ORDER BY titulo", null);
        return cursor;
    }
}
