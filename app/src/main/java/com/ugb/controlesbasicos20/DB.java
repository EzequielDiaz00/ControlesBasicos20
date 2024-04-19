package com.ugb.controlesbasicos20;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DB extends SQLiteOpenHelper {
    private static final String dbname="ezequiel";
    private static final int v=1;
    private static final String SQLdb = "CREATE TABLE diego (id text, rev text, idProd text, codigo text, " +
            "descripcion text, marca text, presentacion text, precio text, foto text)";
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
    public String administrar_prod(String accion, String[] datos){
        try {
            SQLiteDatabase db = getWritableDatabase();
            String sql = "";
            if (accion.equals("nuevo")) {
                sql = "INSERT INTO diego (id,rev,idProd,codigo,descripcion,marca,presentacion,precio, foto) VALUES('"+ datos[0] +"','" + datos[1] +
                        "','" + datos[2] + "','" + datos[3] + "','" + datos[4] + "','" + datos[5] + "', '"+ datos[6] +"', '"+ datos[7] +"', '"+ datos[8] +"')";
            } else if (accion.equals("modificar")) {
                sql = "UPDATE diego SET id='" + datos[0] + "', rev='" + datos[1] + "', codigo='" + datos[3] + "',descripcion='" + datos[4] + "',marca='" +
                        datos[5] + "',presentacion='" + datos[6] + "',precio='" + datos[7] + "', foto='"+ datos[8] +"' WHERE idProd='" + datos[2] + "'";
            } else if (accion.equals("eliminar")) {
                sql = "DELETE FROM diego WHERE idProd='" + datos[2] + "'";
            }
            db.execSQL(sql);
            return "ok";
        }catch (Exception e){
            return e.getMessage();
        }
    }
    public Cursor consultar_prod(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM productos ORDER BY codigo", null);
        return cursor;
    }
}
