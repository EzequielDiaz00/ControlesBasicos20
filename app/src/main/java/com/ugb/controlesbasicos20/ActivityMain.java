package com.ugb.controlesbasicos20;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class ActivityMain extends AppCompatActivity {

    TabHost tbhMain;
    Button btnCerrarSesion;
    TextView lblNameUser, lblEmailUser;
    FirebaseAuth auth;
    FirebaseUser userEmailAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite;
    SQLiteDatabase dbRead;
    FirebaseFirestore databaseFirebase;

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
    }

    private void initializeViews() {
        tbhMain = findViewById(R.id.tbhMain);
        tbhMain.setup();
        TabHost.TabSpec spec = tbhMain.newTabSpec("Inicio");
        tbhMain.addTab(tbhMain.newTabSpec("INV").setContent(R.id.tabInventario).setIndicator("Inventario", null));
        tbhMain.addTab(tbhMain.newTabSpec("INI").setContent(R.id.tabInicio).setIndicator("Inicio", null));
        tbhMain.addTab(tbhMain.newTabSpec("FIN").setContent(R.id.tabFinanzas).setIndicator("Finanzas", null));
        tbhMain.setCurrentTab(1);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        lblNameUser = findViewById(R.id.lblNameUser);
        lblEmailUser = findViewById(R.id.lblEmailUser);
        auth = FirebaseAuth.getInstance();
        userEmailAuth = auth.getCurrentUser();
        databaseFirebase = FirebaseFirestore.getInstance();
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
            String userNameGoogle = acct.getDisplayName();
            String userEmailGoogle = acct.getEmail();

            //MOSTRAR DATOS DE USUARIOS EN LABELS///////////////////////////////////////////////////
            /*lblNameUser.setText(userNameGoogle);
            lblEmailUser.setText(userEmailGoogle);*/

            ContentValues values = new ContentValues();
            values.put(DBSqlite.TableUser.COLUMN_NOMBRE, userNameGoogle);
            values.put(DBSqlite.TableUser.COLUMN_CORREO, userEmailGoogle);
            long newRowId = dbWrite.insert(DBSqlite.TableUser.TABLE_USER, null, values);

            insertDataToFirestore(userNameGoogle, userEmailGoogle);
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

                String userEmailCorreo = userEmailAuth.getEmail().toString();

                ContentValues values = new ContentValues();
                values.put(DBSqlite.TableUser.COLUMN_NOMBRE, userNameCorreo);
                values.put(DBSqlite.TableUser.COLUMN_CORREO, userEmailCorreo);
                long newRowId = dbWrite.insert(DBSqlite.TableUser.TABLE_USER, null, values);

                insertDataToFirestore(userNameCorreo, userEmailCorreo);
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
        } else {
            // No hay datos en la base de datos para el usuario con el correo electrónico proporcionado
        }
    }

    private void insertDataToFirestore(String userName, String userEmail) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", userName);
        userData.put("correo", userEmail);

        databaseFirebase.collection("users").document(userEmail)
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
