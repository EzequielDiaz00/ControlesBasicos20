package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ActivityVentas extends AppCompatActivity {

    EditText txtBuscarVent;
    TextView lblVentas, lblGanancia;
    ListView listVent;
    FloatingActionButton fabHome, fabInv, fabFin;
    DBSqlite dbSqlite;
    SQLiteDatabase dbRead;
    AdapterVentas adapter;
    List<ClassVenta> ventas;
    List<ClassBalance> balance;
    ActivityMain activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);

        activityMain = new ActivityMain();
        String userEmailLog = activityMain.userEmailLogin;

        cargarSqlite();
        cargarObjetos();
        loadDataFromSqlite(userEmailLog);

        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
                startActivity(intent);
                finish();
            }
        });

        fabInv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityProductos.class);
                startActivity(intent);
                finish();
            }
        });

        fabFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityVentas.class);
                startActivity(intent);
                finish();
            }
        });

        listVent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassVenta ventaSeleccionada = ventas.get(position);

                Intent intent = new Intent(getApplicationContext(), ActivityShowVent.class);
                intent.putExtra("venta", ventaSeleccionada);
                startActivity(intent);
            }
        });
    }

    private void cargarObjetos() {
        txtBuscarVent = findViewById(R.id.txtBuscarVent);
        listVent = findViewById(R.id.listVent);
        lblVentas = findViewById(R.id.totVentas);
        lblGanancia = findViewById(R.id.totGanancia);
        fabHome = findViewById(R.id.fabHome);
        fabInv = findViewById(R.id.fabInvent);
        fabFin = findViewById(R.id.fabFinance);
    }

    private void cargarSqlite() {
        dbSqlite = new DBSqlite(this);
        dbRead = dbSqlite.getReadableDatabase();
    }

    private void loadDataFromSqlite(String userEmail) {
        ventas = new ArrayList<>();
        balance = new ArrayList<>();
        loadDataBalanceSqlite(userEmail);
        loadDataVentasSqlite(userEmail);
    }

    private void loadDataVentasSqlite(String userEmail) {
        String[] projectionVent = {
                DBSqlite.TableVent._ID,
                DBSqlite.TableVent.COLUMN_USER,
                DBSqlite.TableVent.COLUMN_FECHA,
                DBSqlite.TableVent.COLUMN_FOTO_PROD,
                DBSqlite.TableVent.COLUMN_FOTO_Url,
                DBSqlite.TableVent.COLUMN_ID_PROD,
                DBSqlite.TableVent.COLUMN_NOMBRE_PROD,
                DBSqlite.TableVent.COLUMN_MARCA_PROD,
                DBSqlite.TableVent.COLUMN_CANTIDAD,
                DBSqlite.TableVent.COLUMN_PRECIO_UNITARIO,
                DBSqlite.TableVent.COLUMN_CLIENTE,
                DBSqlite.TableVent.COLUMN_TOTAL_VENTA,
                DBSqlite.TableVent.COLUMN_GANANCIA
        };

        String selectionVent = DBSqlite.TableVent.COLUMN_USER + " = ?";
        String[] selectionArgsVent = {userEmail};

        try (Cursor cursorVent = dbRead.query(
                DBSqlite.TableVent.TABLE_VENT,
                projectionVent,
                selectionVent,
                selectionArgsVent,
                null,
                null,
                null
        )) {
            if (cursorVent != null) {
                while (cursorVent.moveToNext()) {
                    String ID = cursorVent.getString(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent._ID));
                    String user = cursorVent.getString(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent.COLUMN_USER));
                    String fecha = cursorVent.getString(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent.COLUMN_FECHA));
                    String foto = cursorVent.getString(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent.COLUMN_FOTO_PROD));
                    String fotoUrl = cursorVent.getString(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent.COLUMN_FOTO_Url));
                    String codigo = cursorVent.getString(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent.COLUMN_ID_PROD));
                    String nombre = cursorVent.getString(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent.COLUMN_NOMBRE_PROD));
                    String marca = cursorVent.getString(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent.COLUMN_MARCA_PROD));
                    double cantidad = cursorVent.getDouble(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent.COLUMN_CANTIDAD));
                    double precio = cursorVent.getDouble(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent.COLUMN_PRECIO_UNITARIO));
                    String cliente = cursorVent.getString(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent.COLUMN_CLIENTE));
                    double total = cursorVent.getDouble(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent.COLUMN_TOTAL_VENTA));
                    double ganancia = cursorVent.getDouble(cursorVent.getColumnIndexOrThrow(DBSqlite.TableVent.COLUMN_GANANCIA));

                    ventas.add(new ClassVenta(ID, user, codigo, nombre, marca, precio, foto, fotoUrl, fecha, cantidad, cliente, total, ganancia));
                }
                if (!ventas.isEmpty()) {
                    adapter = new AdapterVentas(this, ventas);
                    listVent.setAdapter(adapter);
                    lblVentas.setText("Ventas: " + ventas.size());
                } else {
                    lblGanancia.setText("Total Ganancia: $0");
                    lblVentas.setText("Ventas: 0");
                    Toast.makeText(this, "Aún no hay Ventas", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("ActivityVentas", "error: " + e.getMessage(), e);
        }
    }

    private void loadDataBalanceSqlite(String userEmail) {
        String[] projectionBalance = {
                DBSqlite.TableBalance.COLUMN_USER,
                DBSqlite.TableBalance.COLUMN_VENT,
                DBSqlite.TableBalance.COLUMN_PROD
        };

        String selectionBal = DBSqlite.TableBalance.COLUMN_USER + " = ?";
        String[] selectionArgsBal = {userEmail};

        try (Cursor cursorBal = dbRead.query(
                DBSqlite.TableBalance.TABLE_BALANCE,
                projectionBalance,
                selectionBal,
                selectionArgsBal,
                null,
                null,
                null
        )) {
            if (cursorBal != null) {
                while (cursorBal.moveToNext()) {
                    String user = cursorBal.getString(cursorBal.getColumnIndexOrThrow(DBSqlite.TableBalance.COLUMN_USER));
                    String venta = cursorBal.getString(cursorBal.getColumnIndexOrThrow(DBSqlite.TableBalance.COLUMN_VENT));

                    balance.add(new ClassBalance(user, null, venta));
                }
                if (!balance.isEmpty()) {
                    ClassBalance classBalance = balance.get(0);
                    lblGanancia.setText("Total Ganancia: $" + classBalance.getVenta());
                } else {
                    Toast.makeText(this, "Aún no hay Ganancias", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("ActivityVentas", "error: " + e.getMessage(), e);
        }
    }
}
