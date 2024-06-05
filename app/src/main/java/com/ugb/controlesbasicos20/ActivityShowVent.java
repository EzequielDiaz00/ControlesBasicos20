package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ActivityShowVent extends AppCompatActivity {

    FloatingActionButton fabHome, fabInv, fabFin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_vent);

        fabHome = findViewById(R.id.fabHome);
        fabInv = findViewById(R.id.fabInvent);
        fabFin = findViewById(R.id.fabFinance);

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
    }
}