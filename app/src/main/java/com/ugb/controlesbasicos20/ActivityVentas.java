package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ActivityVentas extends AppCompatActivity {

    EditText txtBuscarVent;
    ListView listVent;
    Button btnAddVent;
    DBSqlite dbSqlite;
    SQLiteDatabase dbRead;
    AdapterVentas adapter;
    ActivityMain activityMain;
    List<ClassVenta> ventas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);

        activityMain = new ActivityMain();
        String userEmailLog = activityMain.userEmailLogin;

        cargarSqlite();
        cargarObjetos();
        loadDataFromSqlite(userEmailLog);

        btnAddVent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Manejar el evento de clic en el ítem
            }
        });

        listVent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Manejar el evento de clic en el ítem
            }
        });
    }

    private void cargarObjetos() {
        txtBuscarVent = findViewById(R.id.txtBuscarVent);
        listVent = findViewById(R.id.listVent);
        btnAddVent = findViewById(R.id.btnAddVent);
    }

    private void cargarSqlite() {
        dbSqlite = new DBSqlite(this);
        dbRead = dbSqlite.getReadableDatabase();
    }

    private void loadDataFromSqlite(String userEmail) {
        ventas = new ArrayList<>();

        String[] projection = {
                DBSqlite.TableVent.COLUMN_USER,
                DBSqlite.TableVent.COLUMN_FECHA,
                DBSqlite.TableVent.COLUMN_FOTO_PROD,
                DBSqlite.TableVent.COLUMN_ID_PROD,
                DBSqlite.TableVent.COLUMN_NOMBRE_PROD,
                DBSqlite.TableVent.COLUMN_MARCA_PROD,
                DBSqlite.TableVent.COLUMN_CANTIDAD,
                DBSqlite.TableVent.COLUMN_PRECIO_UNITARIO,
                DBSqlite.TableVent.COLUMN_CLIENTE,
                DBSqlite.TableVent.COLUMN_TOTAL_VENTA,
        };

        String selection = DBSqlite.TableProd.COLUMN_USER + " = ?";
        String[] selectionArgs = {userEmail};

        Cursor cursor = null;
        try {
            cursor = dbRead.query(
                    DBSqlite.TableVent.TABLE_VENT,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int userIndex = cursor.getColumnIndex(DBSqlite.TableVent.COLUMN_USER);
                    int fechaIndex = cursor.getColumnIndex(DBSqlite.TableVent.COLUMN_FECHA);
                    int fotoIndex = cursor.getColumnIndex(DBSqlite.TableVent.COLUMN_FOTO_PROD);
                    int codigoIndex = cursor.getColumnIndex(DBSqlite.TableVent.COLUMN_ID_PROD);
                    int nombreIndex = cursor.getColumnIndex(DBSqlite.TableVent.COLUMN_NOMBRE_PROD);
                    int marcaIndex = cursor.getColumnIndex(DBSqlite.TableVent.COLUMN_MARCA_PROD);
                    int cantidadIndex = cursor.getColumnIndex(DBSqlite.TableVent.COLUMN_CANTIDAD);
                    int precioIndex = cursor.getColumnIndex(DBSqlite.TableVent.COLUMN_PRECIO_UNITARIO);
                    int clienteIndex = cursor.getColumnIndex(DBSqlite.TableVent.COLUMN_CLIENTE);
                    int totalIndex = cursor.getColumnIndex(DBSqlite.TableVent.COLUMN_TOTAL_VENTA);

                    if (codigoIndex != -1 && nombreIndex != -1 && precioIndex != -1) {
                        String user = cursor.getString(userIndex);
                        String fecha = cursor.getString(fechaIndex);
                        String foto = cursor.getString(fotoIndex);
                        String codigo = cursor.getString(codigoIndex);
                        String nombre = cursor.getString(nombreIndex);
                        String marca = cursor.getString(marcaIndex);
                        Double cantidad = Double.valueOf(cursor.getString(cantidadIndex));
                        Double precio = Double.valueOf(cursor.getString(precioIndex));
                        String cliente = cursor.getString(clienteIndex);
                        Double total = Double.valueOf(cursor.getString(totalIndex));

                        ventas.add(new ClassVenta(user, codigo, nombre, marca, precio, foto, fecha, cantidad, cliente, total));
                    } else {
                        Toast.makeText(this, "No se pudieron mostrar las ventas", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (ventas.isEmpty()) {
                Toast.makeText(this, "Aún no hay Ventas", Toast.LENGTH_SHORT).show();
            } else {
                adapter = new AdapterVentas(this, ventas);
                listVent.setAdapter(adapter);
            }
        } catch (Exception e) {
            Log.d("ActivityVentas", "error: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}