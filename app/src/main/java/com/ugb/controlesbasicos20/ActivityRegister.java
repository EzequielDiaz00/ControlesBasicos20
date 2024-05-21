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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ActivityRegister extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    ClassVerifyNet classVerifyNet;
    ClassFoto classFoto;
    Button btnRegister, btnLogin;
    ImageButton btnFotoUser;
    EditText txtName, txtEmail, txtPassword, txtPasswordConfirm;
    FirebaseAuth mAuth;
    FirebaseFirestore databaseFirebase;
    FirebaseUser userEmailAcc;
    ProgressBar barProgress;
    String fotoUser;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite;
    SQLiteDatabase dbRead;

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
                fotoUser = urlCompletaFoto;
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                btnFotoUser.setImageBitmap(imagenBitmap);
            } else {
                Toast.makeText(this, "No se pudo tomar la foto", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo tomar la foto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        cargarObjetos();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
                finish();
            }
        });

        btnFotoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });
    }

    private void registrarUsuario() {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();
        String passwordConfirm = txtPasswordConfirm.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(ActivityRegister.this, "Ingrese el correo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(ActivityRegister.this, "Ingrese la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passwordConfirm)) {
            Toast.makeText(ActivityRegister.this, "Ingrese la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(ActivityRegister.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!classVerifyNet.isOnlineNet()) {
            Toast.makeText(ActivityRegister.this, "No hay conexión a internet. Conéctese a una red Wifi", Toast.LENGTH_SHORT).show();
            return;
        }

        barProgress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        barProgress.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            try {
                                userEmailAcc = mAuth.getCurrentUser();
                                String type = "Email";
                                insertDataSqlite(userEmailAcc.getEmail().toString());
                                insertDataFirebase(fotoUser, txtName.getText().toString(), userEmailAcc.getEmail().toString(), type);
                                Toast.makeText(ActivityRegister.this, "¡Usuario creado exitosamente!", Toast.LENGTH_SHORT).show();
                            } catch (Exception ex) {
                                Toast.makeText(ActivityRegister.this, "Error al registrar el usuario: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
                            intent.putExtra("USEREMAIL", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(ActivityRegister.this, "Error al crear el usuario",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void cargarObjetos() {
        classVerifyNet = new ClassVerifyNet(ActivityRegister.this);
        classFoto = new ClassFoto(ActivityRegister.this);

        txtName = findViewById(R.id.txtNombre);
        txtEmail = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPassword);
        txtPasswordConfirm = findViewById(R.id.txtPasswordConfirm);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnFotoUser = findViewById(R.id.btnFotoUser);
        barProgress = findViewById(R.id.barProgress);

        mAuth = FirebaseAuth.getInstance();
        databaseFirebase = FirebaseFirestore.getInstance();


        dbSqlite = new DBSqlite(this);
        dbWrite = dbSqlite.getWritableDatabase();
        dbRead = dbSqlite.getReadableDatabase();
    }

    private void insertDataSqlite(String userEmailAcc) {
        if (fotoUser == null){
            fotoUser = null;
        }

        String foto = fotoUser;
        String nombre = txtName.getText().toString();
        String email = userEmailAcc;
        String type = "Email";

        try {
            ContentValues values = new ContentValues();
            values.put(DBSqlite.TableUser.COLUMN_FOTO, foto);
            values.put(DBSqlite.TableUser.COLUMN_NOMBRE, nombre);
            values.put(DBSqlite.TableUser.COLUMN_CORREO, email);
            values.put(DBSqlite.TableUser.COLUMN_TYPE, type);

            long newRowId = dbWrite.insert(DBSqlite.TableUser.TABLE_USER, null, values);

            Toast.makeText(this, "Datos agregados correctamente a SQLite", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(this, "No se pudo ingresar datos a la base de datos: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void insertDataFirebase(String foto, String nombre, String email, String tipo) {
        databaseFirebase = FirebaseFirestore.getInstance();

        Map<String, Object> userData = new HashMap<>();
        userData.put("foto", foto);
        userData.put("nombre", nombre);
        userData.put("email", email);
        userData.put("tipoCuenta", tipo);

        databaseFirebase.collection(email).document("tableUser")
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ActivityRegister.this, "Los datos se agregaron correctamente a Firebase", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityRegister.this, "Error al agregar los datos a Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}