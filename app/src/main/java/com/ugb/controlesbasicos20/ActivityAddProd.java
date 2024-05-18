package com.ugb.controlesbasicos20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ActivityAddProd extends AppCompatActivity {

    EditText txtCod, txtNom, txtMar, txtDesc, txtPrec;
    ImageButton imgProd;
    ClassFoto classFoto;
    Button btnGuardarProd;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite;

    private static final int REQUEST_CAMERA_PERMISSION = 100;

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            classFoto.tomarFoto();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                classFoto.tomarFoto();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
                String urlCompletaFoto = classFoto.urlCompletaFoto;
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                imgProd.setImageBitmap(imagenBitmap);
            } else {
                Toast.makeText(this, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo tomar la foto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prod);

        classFoto = new ClassFoto(ActivityAddProd.this);

        txtCod = findViewById(R.id.txtCod);
        txtNom = findViewById(R.id.txtNom);
        txtMar = findViewById(R.id.txtMar);
        txtDesc = findViewById(R.id.txtDesc);
        txtPrec = findViewById(R.id.txtPrec);
        btnGuardarProd = findViewById(R.id.btnGuardarProd);
        imgProd = findViewById(R.id.btnImgProd);

        dbSqlite = new DBSqlite(this);
        dbWrite = dbSqlite.getWritableDatabase();

        imgProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        btnGuardarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String codigo = txtCod.getText().toString();
                String nombre = txtNom.getText().toString();
                String marca = txtMar.getText().toString();
                String descripcion = txtDesc.getText().toString();
                String precio = txtPrec.getText().toString();
                String foto = classFoto.urlCompletaFoto;

                try {
                    ContentValues values = new ContentValues();
                    values.put(DBSqlite.TableProd.COLUMN_CODIGO, codigo);
                    values.put(DBSqlite.TableProd.COLUMN_NOMBRE, nombre);
                    values.put(DBSqlite.TableProd.COLUMN_MARCA, marca);
                    values.put(DBSqlite.TableProd.COLUMN_DESCRIPCION, descripcion);
                    values.put(DBSqlite.TableProd.COLUMN_PRECIO, precio);
                    values.put(DBSqlite.TableProd.COLUMN_FOTO, foto);

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
