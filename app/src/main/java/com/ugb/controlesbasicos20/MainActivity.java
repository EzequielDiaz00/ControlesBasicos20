package com.ugb.controlesbasicos20;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button btnCerrarSesion;
    TextView lblNameUser, lblEmailUser;
    FirebaseAuth auth;
    FirebaseUser userEmailAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    DBSqlite dbSqlite;
    SQLiteDatabase dbWrite;
    SQLiteDatabase dbRead;

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
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        lblNameUser = findViewById(R.id.lblNameUser);
        lblEmailUser = findViewById(R.id.lblEmailUser);
        auth = FirebaseAuth.getInstance();
        userEmailAuth = auth.getCurrentUser();
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

            showDataFromDatabase(userEmailGoogle);
        } else {
            if (userEmailAuth == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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

    private void signOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
