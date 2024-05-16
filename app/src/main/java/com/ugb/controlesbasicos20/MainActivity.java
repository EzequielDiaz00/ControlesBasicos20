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
    TextView lblNameUser, lblEmailUser, lblNameUserDB, lblEmailUserDB;
    FirebaseAuth auth;
    FirebaseUser userEmailAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    DBManage360 dbManage360;
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
        lblNameUserDB = findViewById(R.id.lblNameUserDB);
        lblEmailUserDB = findViewById(R.id.lblEmailUserDB);
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
        dbManage360 = new DBManage360(this);
        dbWrite = dbManage360.getWritableDatabase();
        dbRead = dbManage360.getReadableDatabase();
    }

    private void displayUserData(String userNameCorreo) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            String userNameGoogle = acct.getDisplayName();
            String userEmailGoogle = acct.getEmail();

            lblNameUser.setText(userNameGoogle);
            lblEmailUser.setText(userEmailGoogle);

            ContentValues values = new ContentValues();
            values.put(DBManage360.TableUser.COLUMN_NOMBRE, userNameGoogle);
            values.put(DBManage360.TableUser.COLUMN_CORREO, userEmailGoogle);
            long newRowId = dbWrite.insert(DBManage360.TableUser.TABLE_USER, null, values);

            showDataFromDatabase(userEmailGoogle);
        } else {
            if (userEmailAuth == null) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                lblNameUser.setText(userNameCorreo);
                lblEmailUser.setText(userEmailAuth.getEmail());
                String userEmailCorreo = userEmailAuth.getEmail().toString();


                ContentValues values = new ContentValues();
                values.put(DBManage360.TableUser.COLUMN_NOMBRE, userNameCorreo);
                values.put(DBManage360.TableUser.COLUMN_CORREO, userEmailCorreo);
                long newRowId = dbWrite.insert(DBManage360.TableUser.TABLE_USER, null, values);

                showDataFromDatabase(userEmailCorreo);
            }
        }
    }

    private void showDataFromDatabase(String userEmail) {
        String[] projection = {
                DBManage360.TableUser.COLUMN_NOMBRE,
                DBManage360.TableUser.COLUMN_CORREO
        };

        String selection = DBManage360.TableUser.COLUMN_CORREO + " = ?";
        String[] selectionArgs = {userEmail};

        Cursor cursor = dbRead.query(
                DBManage360.TableUser.TABLE_USER,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int nombreIndex = cursor.getColumnIndex(DBManage360.TableUser.COLUMN_NOMBRE);
            int correoIndex = cursor.getColumnIndex(DBManage360.TableUser.COLUMN_CORREO);

            String nombreFromDB = cursor.getString(nombreIndex);
            String correoFromDB = cursor.getString(correoIndex);

            lblNameUserDB.setText(nombreFromDB);
            lblEmailUserDB.setText(correoFromDB);

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
