package com.ugb.controlesbasicos20;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView tempVal;
    Button btn;
    FloatingActionButton btnRegresar;
    String id="", accion="nuevo";
    ImageView img;
    String urlCompletaFoto;
    Intent tomarFotoIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnRegresar = findViewById(R.id.fabListaProd);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regresarLista = new Intent(getApplicationContext(), ProductosActivity.class);
                startActivity(regresarLista);
            }
        });
        btn = findViewById(R.id.btnGuardarProd);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempVal = findViewById(R.id.txtCod);
                String codigo = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtDes);
                String descripcion = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtMar);
                String marca = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtPres);
                String presentacion = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtPrec);
                String precio = tempVal.getText().toString();

                String[] datos = new String[]{id,codigo,descripcion,marca,presentacion,precio, urlCompletaFoto};
                DB db = new DB(getApplicationContext(),"", null, 1);
                String respuesta = db.administrar_prod(accion, datos);
                if( respuesta.equals("ok") ){
                    mostrarMsg("Producto registrado con exito.");
                    listarProd();
                }else {
                    mostrarMsg("Error al intentar registrar el producto: "+ respuesta);
                }
            }
        });
        img = findViewById(R.id.btnImgProd);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFotoProd();
            }
        });
        mostrarDatosProd();
    }
    private void tomarFotoProd(){
        tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fotoProd = null;
        try{
            fotoProd = crearImagenamigo();
            if( fotoProd!=null ){
                Uri urifotoAmigo = FileProvider.getUriForFile(MainActivity.this,
                        "com.ugb.controlesbasicos.fileprovider", fotoProd);
                tomarFotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, urifotoAmigo);
                startActivityForResult(tomarFotoIntent, 1);
            }else{
                mostrarMsg("No se pudo tomar la foto");
            }
        }catch (Exception e){
            mostrarMsg("Error al abrir la camara"+ e.getMessage());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if( requestCode==1 && resultCode==RESULT_OK ){
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                img.setImageBitmap(imagenBitmap);
            }else{
                mostrarMsg("Se cancelo la toma de la foto");
            }
        }catch (Exception e){
            mostrarMsg("Error al seleccionar la foto"+ e.getMessage());
        }
    }
    private File crearImagenamigo() throws Exception{
        String fechaHoraMs = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()),
                fileName = "imagen_"+fechaHoraMs+"_";
        File dirAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        if( dirAlmacenamiento.exists()==false ){
            dirAlmacenamiento.mkdirs();
        }
        File image = File.createTempFile(fileName, ".jpg", dirAlmacenamiento);
        urlCompletaFoto = image.getAbsolutePath();
        return image;
    }
    private void mostrarDatosProd(){
        try{
            Bundle parametros = getIntent().getExtras();
            accion = parametros.getString("accion");

            if(accion.equals("modificar")){
                String[] productos = parametros.getStringArray("productos");
                id = productos[0];

                tempVal = findViewById(R.id.txtCod);
                tempVal.setText(productos[1]);

                tempVal = findViewById(R.id.txtDes);
                tempVal.setText(productos[2]);

                tempVal = findViewById(R.id.txtMar);
                tempVal.setText(productos[3]);

                tempVal = findViewById(R.id.txtPres);
                tempVal.setText(productos[4]);

                tempVal = findViewById(R.id.txtPrec);
                tempVal.setText(productos[5]);

                urlCompletaFoto = productos[6];
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                img.setImageBitmap(imagenBitmap);
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar los datos amigos");
        }
    }
    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    private void listarProd(){
        Intent intent = new Intent(getApplicationContext(), ProductosActivity.class);
        startActivity(intent);
    }
}