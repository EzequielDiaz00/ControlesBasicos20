package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ActivityShowProd extends AppCompatActivity {

    TextView lblCod, lblNom, lblMar, lblDes, lblPre, lblCos;
    Button btnModificar, btnEliminar;
    ImageView imgFotoProd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_prod);

        lblCod = findViewById(R.id.lblCod);
        lblNom = findViewById(R.id.lblNom);
        lblMar = findViewById(R.id.lblMar);
        lblDes = findViewById(R.id.lblDes);
        lblPre = findViewById(R.id.lblPre);
        lblCos = findViewById(R.id.lblCos);
        btnModificar = findViewById(R.id.btnModificar);
        btnEliminar = findViewById(R.id.btnEliminar);
        imgFotoProd = findViewById(R.id.imgProd);

        ClassProductos producto = (ClassProductos) getIntent().getSerializableExtra("producto");

        if (producto != null) {
            lblCod.setText(producto.getCodigo());
            lblNom.setText(producto.getNombre());
            lblMar.setText(producto.getMarca());
            lblDes.setText(producto.getDescripcion());
            lblPre.setText(producto.getPrecio().toString());
            lblCos.setText(producto.getCosto().toString());

            String urlCompletaFoto = producto.getFoto();
            Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
            imgFotoProd.setImageBitmap(imagenBitmap);
        }

        btnModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassProductos productoSeleccionado = (ClassProductos) getIntent().getSerializableExtra("producto");

                Intent intent = new Intent(getApplicationContext(), ActivityAddProd.class);
                intent.putExtra("producto", productoSeleccionado);
                startActivity(intent);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
