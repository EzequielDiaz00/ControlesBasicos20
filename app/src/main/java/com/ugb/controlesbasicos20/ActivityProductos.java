package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActivityProductos extends AppCompatActivity {

    EditText txtBuscarProd;
    ListView listProd;
    Button btnAddProd;
    DBSqlite dbSqlite;
    SQLiteDatabase dbRead;
    AdapterProductos adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        txtBuscarProd = findViewById(R.id.txtBuscarProd);
        listProd = findViewById(R.id.listProd);
        btnAddProd = findViewById(R.id.btnAddProd);

        dbSqlite = new DBSqlite(this);
        dbRead = dbSqlite.getReadableDatabase();

        loadDataFromDatabase();

        btnAddProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityAddProd.class);
                startActivity(intent);
            }
        });
    }

    private void loadDataFromDatabase() {
        List<ClassProductos> productos = new ArrayList<>();

        String[] projection = {
                DBSqlite.TableProd.COLUMN_CODIGO,
                DBSqlite.TableProd.COLUMN_NOMBRE,
                DBSqlite.TableProd.COLUMN_MARCA,
                DBSqlite.TableProd.COLUMN_PRECIO
        };

        Cursor cursor = null;
        try {
            cursor = dbRead.query(
                    DBSqlite.TableProd.TABLE_PROD,
                    projection,
                    null,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int codigoIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_CODIGO);
                    int nombreIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_NOMBRE);
                    int marcaIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_MARCA);
                    int precioIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_PRECIO);

                    if (codigoIndex != -1 && nombreIndex != -1 && precioIndex != -1) {
                        String codigo = cursor.getString(codigoIndex);
                        String nombre = cursor.getString(nombreIndex);
                        String marca = cursor.getString(marcaIndex);
                        Double precio = Double.valueOf(cursor.getString(precioIndex));

                        productos.add(new ClassProductos(codigo, nombre, marca, null, precio));
                    } else {
                        Toast.makeText(this, "Error al extraer datos de la base de datos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        adapter = new AdapterProductos(this, productos);
        listProd.setAdapter(adapter);

        if (productos.isEmpty()) {
            Toast.makeText(this, "No se encontraron productos", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Datos extraídos correctamente", Toast.LENGTH_SHORT).show();
        }
    }


}
