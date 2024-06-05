package com.ugb.controlesbasicos20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ActivityAddVent extends AppCompatActivity {

    ImageView imgFoto;
    TextView lblCodigo, lblNombre, lblMarca, lblPrecio;
    EditText txtFecha, txtCantidad, txtCliente;
    String urlCompletaFoto, fotoURL;
    Double gananciaVent;
    Button btnVender;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite;
    SQLiteDatabase dbRead;
    FirebaseFirestore databaseFirebase;
    ActivityMain activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vent);

        cargarObjetos();
        mostrarDatos();

        activityMain = new ActivityMain();

        btnVender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = activityMain.userEmailLogin;
                insertDataToSqlite(userEmail);
            }
        });
    }

    private void cargarObjetos() {
        imgFoto = findViewById(R.id.imgProdVent);
        lblCodigo = findViewById(R.id.lblIdProd);
        lblNombre = findViewById(R.id.lblNombre);
        lblMarca = findViewById(R.id.lblMarca);
        lblPrecio = findViewById(R.id.lblPrecio);
        txtFecha = findViewById(R.id.txtFec);
        txtCantidad = findViewById(R.id.txtCant);
        txtCliente = findViewById(R.id.txtClient);
        btnVender = findViewById(R.id.btnVender);

        dbSqlite = new DBSqlite(this);
        dbWrite = dbSqlite.getWritableDatabase();
        dbRead = dbSqlite.getReadableDatabase();
        databaseFirebase = FirebaseFirestore.getInstance();
    }

    private void mostrarDatos() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("producto")) {
            ClassProductos producto = (ClassProductos) intent.getSerializableExtra("producto");

            if (producto != null) {
                lblCodigo.setText(producto.getCodigo());
                lblNombre.setText(producto.getNombre());
                lblMarca.setText(producto.getMarca());
                lblPrecio.setText(producto.getPrecio().toString());

                urlCompletaFoto = producto.getFoto();
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                imgFoto.setImageBitmap(imagenBitmap);

                fotoURL = producto.getFotoUrl();
                gananciaVent = producto.getPrecio() - producto.getCosto();
            }
        }
    }

    private void insertDataToSqlite(String userEmail) {
        try {
            String codigo = lblCodigo.getText().toString();
            String nombre = lblNombre.getText().toString();
            String marca = lblMarca.getText().toString();
            double precio = Double.parseDouble(lblPrecio.getText().toString());
            String imgProd = urlCompletaFoto;
            String fecha = txtFecha.getText().toString();
            double cantidad = Double.parseDouble(txtCantidad.getText().toString());
            String cliente = txtCliente.getText().toString();
            String fotoUrl = fotoURL;
            double ganancia = gananciaVent * cantidad;
            double total = precio * cantidad;

            ContentValues values = new ContentValues();
            values.put(DBSqlite.TableVent.COLUMN_USER, userEmail);
            values.put(DBSqlite.TableVent.COLUMN_ID_PROD, codigo);
            values.put(DBSqlite.TableVent.COLUMN_NOMBRE_PROD, nombre);
            values.put(DBSqlite.TableVent.COLUMN_MARCA_PROD, marca);
            values.put(DBSqlite.TableVent.COLUMN_PRECIO_UNITARIO, String.valueOf(precio));
            values.put(DBSqlite.TableVent.COLUMN_FOTO_PROD, imgProd);
            values.put(DBSqlite.TableVent.COLUMN_FOTO_Url, fotoUrl);
            values.put(DBSqlite.TableVent.COLUMN_FECHA, fecha);
            values.put(DBSqlite.TableVent.COLUMN_CANTIDAD, String.valueOf(cantidad));
            values.put(DBSqlite.TableVent.COLUMN_CLIENTE, cliente);
            values.put(DBSqlite.TableVent.COLUMN_TOTAL_VENTA, String.valueOf(total));
            values.put(DBSqlite.TableVent.COLUMN_GANANCIA, ganancia);

            long newRowId = dbWrite.insert(DBSqlite.TableVent.TABLE_VENT, null, values);

            if (newRowId != -1) {
                insertDataToFirebase(userEmail, codigo, nombre, marca, String.valueOf(precio), imgProd, fotoUrl, fecha, String.valueOf(cantidad), cliente, String.valueOf(total), ganancia);
                updateDataToBalance(userEmail, ganancia, 0.0);
                startActivity(new Intent(getApplicationContext(), ActivityVentas.class));
                finish();
            } else {
                Log.d("ActivityAddVent", "No se pudieron insertar los datos");
            }
        } catch (Exception ex) {
            Log.d("ActivityAddVent", "Error al insertar datos: " + ex.getMessage());
        }
    }

    private void insertDataToFirebase(String userEmail, String codigo, String nombre, String marca, String precio, String foto, String fotoUrl, String fecha, String cantidad, String cliente, String total, Double ganancia) {
        Map<String, Object> prodData = new HashMap<>();
        prodData.put("user", userEmail);
        prodData.put("codigo", codigo);
        prodData.put("nombre", nombre);
        prodData.put("marca", marca);
        prodData.put("fecha", fecha);
        prodData.put("precio", precio);
        prodData.put("foto", foto);
        prodData.put("fotoUrl", fotoUrl);
        prodData.put("cantidad", cantidad);
        prodData.put("cliente", cliente);
        prodData.put("total", total);
        prodData.put("ganancia", ganancia);

        databaseFirebase.collection(userEmail).document("tableVentas").collection(codigo).document(cliente).set(prodData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Exito//
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ActivityAddVent", "Error al insertar los datos en Firebase: " + e.getMessage());
                    }
                });
    }

    private void updateDataToBalance(String userEmail, Double ventas, Double compra) {
        String[] projection = {
                DBSqlite.TableBalance.COLUMN_USER,
                DBSqlite.TableBalance.COLUMN_COMP,
                DBSqlite.TableBalance.COLUMN_VENT
        };

        String selection = DBSqlite.TableBalance.COLUMN_USER + " = ?";
        String[] selectionArgs = {userEmail};

        Cursor cursor = null;
        try {
            cursor = dbRead.query(
                    DBSqlite.TableBalance.TABLE_BALANCE,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor == null || !cursor.moveToFirst()) {
                // Si el cursor es null o no tiene filas, inserta un nuevo usuario
                ContentValues valuesUser = new ContentValues();
                valuesUser.put(DBSqlite.TableBalance.COLUMN_USER, userEmail);
                valuesUser.put(DBSqlite.TableBalance.COLUMN_VENT, ventas);
                valuesUser.put(DBSqlite.TableBalance.COLUMN_COMP, compra);

                long newRowId = dbWrite.insert(DBSqlite.TableBalance.TABLE_BALANCE, null, valuesUser);

                if (newRowId != -1) {
                    Log.d("ActivityAddVent", "Usuario agregado correctamente con ID: " + newRowId);
                } else {
                    Log.d("ActivityAddVent", "Error al agregar nuevo usuario");
                }
                return;
            }

            // Si el cursor no es null y tiene filas, actualiza los datos existentes
            int ventIndex = cursor.getColumnIndex(DBSqlite.TableBalance.COLUMN_VENT);
            int compIndex = cursor.getColumnIndex(DBSqlite.TableBalance.COLUMN_COMP);

            if (ventIndex != -1 && compIndex != -1) {
                Double currentVent = cursor.getDouble(ventIndex);
                Double currentComp = cursor.getDouble(compIndex);

                Double totVent = currentVent + ventas;
                Double totComp = currentComp + compra;

                ContentValues values = new ContentValues();
                values.put(DBSqlite.TableBalance.COLUMN_VENT, totVent);
                values.put(DBSqlite.TableBalance.COLUMN_COMP, totComp);

                String updateSelection = DBSqlite.TableBalance.COLUMN_USER + " = ?";
                String[] updateSelectionArgs = {userEmail};

                int rowsUpdated = dbWrite.update(DBSqlite.TableBalance.TABLE_BALANCE, values, updateSelection, updateSelectionArgs);

                if (rowsUpdated > 0) {
                    Log.d("ActivityAddVent", "Datos actualizados correctamente en el balance");
                } else {
                    Log.d("ActivityAddVent", "No se actualizaron los datos en el balance");
                }
            } else {
                Log.d("ActivityAddVent", "Columnas de ventas o compra no encontradas");
            }
        } catch (Exception e) {
            Log.d("ActivityAddVent", "Error al actualizar Balance: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
