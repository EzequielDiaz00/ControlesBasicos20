package com.ugb.controlesbasicos20;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ActivityMain extends AppCompatActivity {
    TabHost tbhMain;
    Button btnCerrarSesion, btnFotoUser, btnAbrirProductos;
    TextView lblNameUser, lblEmailUser;
    ClassFoto classFoto;
    FirebaseAuth auth;
    FirebaseUser userEmailAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    String userType;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite;
    SQLiteDatabase dbRead;
    FirebaseFirestore databaseFirebase;
    FirebaseStorage storageFirebase;
    StorageReference storageRef;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    public static String userEmailLogin;

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupGoogleSignIn();
        setupDatabase();

        String userNameCorreo = getIntent().getStringExtra("USERNAME");

        displayUserData(userNameCorreo);

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        btnFotoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        btnAbrirProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityProductos.class);
                startActivity(intent);
            }
        });
    }

    private void initializeViews() {
        tbhMain = findViewById(R.id.tbhMain);
        tbhMain.setup();
        TabHost.TabSpec spec = tbhMain.newTabSpec("Inicio");
        tbhMain.addTab(tbhMain.newTabSpec("INV").setContent(R.id.tabInventario).setIndicator("Inventario", null));
        tbhMain.addTab(tbhMain.newTabSpec("INI").setContent(R.id.tabInicio).setIndicator("Inicio", null));
        tbhMain.addTab(tbhMain.newTabSpec("FIN").setContent(R.id.tabFinanzas).setIndicator("Finanzas", null));
        tbhMain.setCurrentTab(1);

        classFoto = new ClassFoto(ActivityMain.this);
        btnAbrirProductos = findViewById(R.id.btnVistaProductos);
        btnFotoUser = findViewById(R.id.btnFotoUser);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        lblNameUser = findViewById(R.id.lblNameUser);
        lblEmailUser = findViewById(R.id.lblEmailUser);
        auth = FirebaseAuth.getInstance();
        userEmailAuth = auth.getCurrentUser();
        databaseFirebase = FirebaseFirestore.getInstance();
        storageFirebase = FirebaseStorage.getInstance();
        storageRef = storageFirebase.getReference();
    }

    private void setupGoogleSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Add this line to request an ID token.
                .requestEmail()
                .build();
        gsc = GoogleSignIn.getClient(this, gso);
    }

    private void setupDatabase() {
        dbSqlite = new DBSqlite(this);
        dbWrite = dbSqlite.getWritableDatabase();
        dbRead = dbSqlite.getReadableDatabase();
    }
    private void displayUserData(String userNameCorreo) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            userType = "Google";
            String userNameGoogle = acct.getDisplayName();
            String userEmailGoogle = acct.getEmail();
            userEmailLogin = userEmailGoogle;

            //MOSTRAR DATOS DE USUARIOS EN LABELS///////////////////////////////////////////////////
            /*lblNameUser.setText(userNameGoogle);
            lblEmailUser.setText(userEmailGoogle);*/

            ContentValues values = new ContentValues();
            values.put(DBSqlite.TableUser.COLUMN_NOMBRE, userNameGoogle);
            values.put(DBSqlite.TableUser.COLUMN_CORREO, userEmailGoogle);
            long newRowId = dbWrite.insert(DBSqlite.TableUser.TABLE_USER, null, values);

            insertDataToFirestoreUser(userNameGoogle, userEmailGoogle, userType);
            showDataFromDatabase(userEmailGoogle);
        } else {
            if (userEmailAuth == null) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
                finish();
            } else {

                //MOSTRAR DATOS DE USUARIOS EN LABELS///////////////////////////////////////////////
                /*lblNameUser.setText(userNameCorreo);
                lblEmailUser.setText(userEmailAuth.getEmail());*/

                userType = "Email";
                String userEmailCorreo = userEmailAuth.getEmail().toString();
                userEmailLogin = userEmailCorreo;

                ContentValues values = new ContentValues();
                values.put(DBSqlite.TableUser.COLUMN_NOMBRE, userNameCorreo);
                values.put(DBSqlite.TableUser.COLUMN_CORREO, userEmailCorreo);
                long newRowId = dbWrite.insert(DBSqlite.TableUser.TABLE_USER, null, values);

                insertDataToFirestoreUser(userNameCorreo, userEmailCorreo, userType);
                showDataFromDatabase(userEmailCorreo);
            }
        }
    }

    private void showDataFromDatabase(String userEmail) {
        String[] projection = {
                DBSqlite.TableUser.COLUMN_NOMBRE,
                DBSqlite.TableUser.COLUMN_CORREO
        };

        String selection = DBSqlite.TableUser.COLUMN_CORREO + " = ?";
        String[] selectionArgs = {userEmail};

        Cursor cursor = dbRead.query(
                DBSqlite.TableUser.TABLE_USER,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int nombreIndex = cursor.getColumnIndex(DBSqlite.TableUser.COLUMN_NOMBRE);
            int correoIndex = cursor.getColumnIndex(DBSqlite.TableUser.COLUMN_CORREO);

            String nombreFromDB = cursor.getString(nombreIndex);
            String correoFromDB = cursor.getString(correoIndex);

            lblNameUser.setText(nombreFromDB);
            lblEmailUser.setText(correoFromDB);

            cursor.close();
        }
    }

    public void insertDataToFirestoreUser(String userName, String userEmail, String userType) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("cuenta", userType);
        userData.put("nombre", userName);
        userData.put("correo", userEmail);

        databaseFirebase.collection(userEmail).document("tableUser")
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ActivityMain.this, "Los datos se agregaron correctamente a Firebase", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActivityMain.this, "Error al agregar los datos a Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
