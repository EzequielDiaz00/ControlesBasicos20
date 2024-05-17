package com.ugb.controlesbasicos20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityRegister extends AppCompatActivity {

    Button btnRegister;
    Button btnLogin;
    EditText txtName, txtEmail, txtPassword, txtPasswordConfirm;
    FirebaseAuth mAuth;
    ProgressBar barProgress;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtName = findViewById(R.id.txtNombre);
        txtEmail = findViewById(R.id.txtCorreo);
        txtPassword = findViewById(R.id.txtPassword);
        txtPasswordConfirm = findViewById(R.id.txtPasswordConfirm);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        barProgress = findViewById(R.id.barProgress);

        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityLogin.class);
                startActivity(intent);
                finish();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password, passwordConfirm;

                email = txtEmail.getText().toString();
                password = txtPassword.getText().toString();
                passwordConfirm = txtPasswordConfirm.getText().toString();

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
                if (!password.equals(passwordConfirm)){
                    Toast.makeText(ActivityRegister.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    return;
                }

                barProgress.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                barProgress.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    String userNameCorreo = txtName.getText().toString();

                                    Toast.makeText(ActivityRegister.this, "¡Usuario creado exitosamente!",
                                            Toast.LENGTH_SHORT).show();

                                    // Redirigir a MainActivity después de registrar el usuario
                                    Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
                                    intent.putExtra("USERNAME", userNameCorreo);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(ActivityRegister.this, "Error al crear el usuario",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}