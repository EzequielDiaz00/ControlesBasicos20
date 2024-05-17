package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityAddProd extends AppCompatActivity {

    EditText txtCod, txtNom, txtMar, txtDesc, txtPrec;
    Button btnGuardarProd;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prod);

        txtCod = findViewById(R.id.txtCod);
        txtNom = findViewById(R.id.txtNom);
        txtMar = findViewById(R.id.txtMar);
        txtDesc = findViewById(R.id.txtDesc);
        txtPrec = findViewById(R.id.txtPrec);
        btnGuardarProd = findViewById(R.id.btnGuardarProd);

        dbSqlite = new DBSqlite(this);
        dbWrite = dbSqlite.getWritableDatabase();

        btnGuardarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String codigo = txtCod.getText().toString();
                String nombre = txtNom.getText().toString();
                String marca = txtMar.getText().toString();
                String descripcion = txtDesc.getText().toString();
                String precio = txtPrec.getText().toString();

                try {
                    ContentValues values = new ContentValues();
                    values.put(DBSqlite.TableProd.COLUMN_CODIGO, codigo);
                    values.put(DBSqlite.TableProd.COLUMN_NOMBRE, nombre);
                    values.put(DBSqlite.TableProd.COLUMN_MARCA, marca);
                    values.put(DBSqlite.TableProd.COLUMN_DESCRIPCION, descripcion);
                    values.put(DBSqlite.TableProd.COLUMN_PRECIO, precio);

                    long newRowId = dbWrite.insert(DBSqlite.TableProd.TABLE_PROD, null, values);

                    Toast.makeText(ActivityAddProd.this, "Datos Agregados correctamente", Toast.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    Toast.makeText(ActivityAddProd.this, "Error al agregar el producto: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getApplicationContext(), ActivityProductos.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
