package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ugb.controlesbasicos20.R;

public class MainActivity extends AppCompatActivity {

    TextView tempVal;
    Button btn;
    String id="", accion="nuevo";
    FloatingActionButton btnRegresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegresar = findViewById(R.id.fabVerAmigos);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirActividad();
            }
        });

        btn = findViewById(R.id.btnGuardarAmigo);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tempVal = findViewById(R.id.txtnombre);
                String nombre = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtdireccion);
                String direccion = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtTelefono);
                String tel = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtEmail);
                String email = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtDui);
                String dui = tempVal.getText().toString();

                String[] datos = new String[]{id, nombre, direccion, tel, email, dui};

                DB db = new DB(getApplicationContext(), "", null, 1);
                String respuesta = db.administrar_amigos(accion, datos);

                if(respuesta.equals("ok")){
                    Toast.makeText(getApplicationContext(), "Amigo resgistrado con exito", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Error al registrar el amigo" + respuesta, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void abrirActividad(){
        Intent abrirActividad = new Intent(getApplicationContext(), lista_amigos.class);
        startActivity(abrirActividad);
    }
}
