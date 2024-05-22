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
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ActivityAddProd extends AppCompatActivity {

    EditText txtCod, txtNom, txtMar, txtDesc, txtPrec;
    ImageView imgProd;
    ClassFoto classFoto;
    Button btnGuardarProd;
    ActivityMain activityMain;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite;
    FirebaseFirestore databaseFirebase;
    FirebaseStorage storageProd;
    StorageReference storageProdRef;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        activityMain = new ActivityMain();

        String userEmail = activityMain.userEmailLogin;

        cargarObjetos();

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

                insertDataToStorage(foto, userEmail);

                try {
                    ContentValues values = new ContentValues();
                    values.put(DBSqlite.TableProd.COLUMN_USER, userEmail);
                    values.put(DBSqlite.TableProd.COLUMN_CODIGO, codigo);
                    values.put(DBSqlite.TableProd.COLUMN_NOMBRE, nombre);
                    values.put(DBSqlite.TableProd.COLUMN_MARCA, marca);
                    values.put(DBSqlite.TableProd.COLUMN_DESCRIPCION, descripcion);
                    values.put(DBSqlite.TableProd.COLUMN_PRECIO, precio);
                    values.put(DBSqlite.TableProd.COLUMN_FOTO, foto);

                    long newRowId = dbWrite.insert(DBSqlite.TableProd.TABLE_PROD, null, values);

                    Toast.makeText(ActivityAddProd.this, "Producto agregado a SQLite", Toast.LENGTH_SHORT).show();

                    try {
                        insertDataToFirebase(userEmail, codigo, nombre, marca, descripcion, precio, foto);
                    } catch (Exception e) {
                        Toast.makeText(ActivityAddProd.this, "Error al guardar el producto en Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception ex) {
                    Toast.makeText(ActivityAddProd.this, "Error al agregar el producto en SQLite: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent(getApplicationContext(), ActivityProductos.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void cargarObjetos() {
        txtCod = findViewById(R.id.txtCod);
        txtNom = findViewById(R.id.txtNom);
        txtMar = findViewById(R.id.txtMar);
        txtDesc = findViewById(R.id.txtDesc);
        txtPrec = findViewById(R.id.txtPrec);
        btnGuardarProd = findViewById(R.id.btnGuardarProd);
        imgProd = findViewById(R.id.btnImgProd);

        dbSqlite = new DBSqlite(this);
        dbWrite = dbSqlite.getWritableDatabase();
        databaseFirebase = FirebaseFirestore.getInstance();

        storageProd = FirebaseStorage.getInstance();
        storageProdRef = storageProd.getReference();
    }

    private void insertDataToFirebase(String userEmail, String codigo, String nombre, String marca, String descripcion, String precio, String foto) {
        Map<String, Object> prodData = new HashMap<>();
        prodData.put("user", userEmail);
        prodData.put("codigo", codigo);
        prodData.put("nombre", nombre);
        prodData.put("marca", marca);
        prodData.put("descripcion", descripcion);
        prodData.put("precio", precio);
        prodData.put("foto", foto);

        databaseFirebase.collection(userEmail).document("tableProductos").collection(codigo).document(nombre).set(prodData, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ActivityAddProd.this, "Los datos se agregaron correctamente a Firebase", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivityAddProd.this, "Error al agregar los datos a Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void insertDataToStorage(String foto, String userEmail) {
        Uri file = Uri.fromFile(new File(foto));
        StorageReference prodRef = storageProdRef.child(userEmail);
        StorageReference prodFotosRef = prodRef.child("fotosProd/" + file.getLastPathSegment());
        UploadTask uploadTask = prodFotosRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ActivityAddProd.this, "DataStorage NO ejecutado: " + prodRef, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(ActivityAddProd.this, "DataStorage ejecutado correctamente: " + prodRef, Toast.LENGTH_SHORT).show();
            }
        });
    }
}