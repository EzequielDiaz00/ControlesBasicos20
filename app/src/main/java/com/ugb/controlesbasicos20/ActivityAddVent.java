package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityAddVent extends AppCompatActivity {

    ImageView imgFoto;
    TextView lblCodigo, lblNombre, lblMarca, lblPrecio;
    EditText txtFecha, txtCantidad, txtCliente;
    Button btnVender;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite;
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
        databaseFirebase = FirebaseFirestore.getInstance();
    }

    private void mostrarDatos() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("producto")) {
                ClassProductos producto = (ClassProductos) intent.getSerializableExtra("producto");

                if (producto != null) {
                    lblCodigo.setText(producto.getCodigo());
                    lblNombre.setText(producto.getNombre());
                    lblMarca.setText(producto.getMarca());
                    lblPrecio.setText(producto.getPrecio().toString());

                    String urlCompletaFoto = producto.getFoto();
                    Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                    imgFoto.setImageBitmap(imagenBitmap);
                }
            }
        }
    }

    private void insertDataToSqlite(String userEmail) {
        String user = userEmail;
        String codigo = lblCodigo.getText().toString();
        String nombre = lblNombre.getText().toString();
        String marca = lblMarca.getText().toString();
        double prec = Double.parseDouble(String.valueOf(lblPrecio.getText()));
        String precio = String.valueOf(prec);
        String imgProd = imgFoto.toString();
        String fecha = txtFecha.getText().toString();
        double cant = Double.parseDouble(String.valueOf(txtCantidad.getText()));
        String cantidad = String.valueOf(cant);
        String cliente = txtCliente.getText().toString();

        double totalVent = cant * prec;
        String total = String.valueOf(totalVent);

        ContentValues values = new ContentValues();
        values.put(DBSqlite.TableVent.COLUMN_USER, user);
        values.put(DBSqlite.TableVent.COLUMN_ID_PROD, codigo);
        values.put(DBSqlite.TableVent.COLUMN_NOMBRE_PROD, nombre);
        values.put(DBSqlite.TableVent.COLUMN_MARCA_PROD, marca);
        values.put(DBSqlite.TableVent.COLUMN_PRECIO_UNITARIO, precio);
        values.put(DBSqlite.TableVent.COLUMN_FOTO_PROD, imgProd);
        values.put(DBSqlite.TableVent.COLUMN_FECHA, fecha);
        values.put(DBSqlite.TableVent.COLUMN_CANTIDAD, cantidad);
        values.put(DBSqlite.TableVent.COLUMN_CLIENTE, cliente);
        values.put(DBSqlite.TableVent.COLUMN_TOTAL_VENTA, total);

        Intent intent = new Intent(getApplicationContext(), ActivityVentas.class);
        startActivity(intent);
        finish();
    }
}