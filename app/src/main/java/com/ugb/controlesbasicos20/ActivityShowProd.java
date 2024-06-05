package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActivityShowProd extends AppCompatActivity {

    TextView lblCod, lblNom, lblMar, lblDes, lblPre, lblCos;
    Button btnModificar, btnEliminar, btnVender;
    ImageView imgFotoProd;
    FloatingActionButton fabHome, fabInv, fabFin;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite;
    FirebaseFirestore databaseFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_prod);

        lblCod = findViewById(R.id.lblCod);
        lblNom = findViewById(R.id.lblNom);
        lblMar = findViewById(R.id.lblMar);
        lblDes = findViewById(R.id.lblDes);
        lblPre = findViewById(R.id.lblPre);
        lblCos = findViewById(R.id.lblCos);
        btnModificar = findViewById(R.id.btnModificar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnVender = findViewById(R.id.btnVender);
        imgFotoProd = findViewById(R.id.imgProd);
        fabHome = findViewById(R.id.fabHome);
        fabInv = findViewById(R.id.fabInvent);
        fabFin = findViewById(R.id.fabFinance);

        dbSqlite = new DBSqlite(this);
        dbWrite = dbSqlite.getWritableDatabase();
        databaseFirebase = FirebaseFirestore.getInstance();

        ClassProductos producto = (ClassProductos) getIntent().getSerializableExtra("producto");

        if (producto != null) {
            lblCod.setText(producto.getCodigo());
            lblNom.setText(producto.getNombre());
            lblMar.setText(producto.getMarca());
            lblDes.setText(producto.getDescripcion());
            lblPre.setText(producto.getPrecio().toString());
            lblCos.setText(producto.getCosto().toString());

            String urlCompletaFoto = producto.getFoto();
            Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
            imgFotoProd.setImageBitmap(imagenBitmap);
        }

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

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassProductos productoSeleccionado = (ClassProductos) getIntent().getSerializableExtra("producto");

                Intent intent = new Intent(getApplicationContext(), ActivityAddProd.class);
                intent.putExtra("producto", productoSeleccionado);
                startActivity(intent);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion();
            }
        });

        btnVender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassProductos productoSeleccionado = (ClassProductos) getIntent().getSerializableExtra("producto");

                Intent intent = new Intent(getApplicationContext(), ActivityAddVent.class);
                intent.putExtra("producto", productoSeleccionado);
                startActivity(intent);
            }
        });
    }

    private void mostrarDialogoConfirmacion() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este producto?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eliminarProducto();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void eliminarProducto() {
        ClassProductos producto = (ClassProductos) getIntent().getSerializableExtra("producto");

        if (producto != null) {
            String selection = DBSqlite.TableProd.COLUMN_CODIGO + " = ?";
            String[] selectionArgs = {producto.getCodigo()};

            // Eliminar producto de SQLite
            int deletedRows = dbWrite.delete(DBSqlite.TableProd.TABLE_PROD, selection, selectionArgs);

            // Eliminar producto de Firebase
            String userEmail = new ActivityMain().userEmailLogin;
            databaseFirebase.collection(userEmail)
                    .document("tableProductos")
                    .collection(producto.getCodigo())
                    .document(producto.getNombre())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Exito //
                    })
                    .addOnFailureListener(e -> {
                        // Error //
                    });

            if (deletedRows > 0) {
                Intent intent = new Intent(getApplicationContext(), ActivityProductos.class);
                startActivity(intent);
                finish();
            } else {
                // Error //
            }
        }
    }
}
