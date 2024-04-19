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

        btnRegresar = findViewById(R.id.fabListaAuto);
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regresarLista = new Intent(getApplicationContext(), AutosActivity.class);
                startActivity(regresarLista);
            }
        });
        btn = findViewById(R.id.btnGuardarAuto);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tempVal = findViewById(R.id.txtMar);
                String marca = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtMot);
                String motor = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtCha);
                String chasis = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtVin);
                String vin = tempVal.getText().toString();

                tempVal = findViewById(R.id.txtCom);
                String combustion = tempVal.getText().toString();

                String[] datos = new String[]{id,marca,motor,chasis,vin,combustion,urlCompletaFoto};
                DB db = new DB(getApplicationContext(),"", null, 1);
                String respuesta = db.administrar_auto(accion, datos);
                if( respuesta.equals("ok") ){
                    mostrarMsg("Auto registrado con exito.");
                    listarAuto();
                }else {
                    mostrarMsg("Error al intentar registrar el Auto: "+ respuesta);
                }
            }
        });
        img = findViewById(R.id.btnImgAuto);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tomarFotoAuto();
            }
        });
        mostrarDatosAuto();
    }
    private void tomarFotoAuto(){
        tomarFotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File fotoAuto = null;
        try{
            fotoAuto = crearImagenamigo();
            if( fotoAuto!=null ){
                Uri urifotoAmigo = FileProvider.getUriForFile(MainActivity.this,
                        "com.ugb.controlesbasicos.fileprovider", fotoAuto);
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
    private void mostrarDatosAuto(){
        try{
            Bundle parametros = getIntent().getExtras();
            accion = parametros.getString("accion");

            if(accion.equals("modificar")){
                String[] autos = parametros.getStringArray("autos");
                id = autos[0];

                tempVal = findViewById(R.id.txtMar);
                tempVal.setText(autos[1]);

                tempVal = findViewById(R.id.txtMot);
                tempVal.setText(autos[2]);

                tempVal = findViewById(R.id.txtCha);
                tempVal.setText(autos[3]);

                tempVal = findViewById(R.id.txtVin);
                tempVal.setText(autos[4]);

                tempVal = findViewById(R.id.txtCom);
                tempVal.setText(autos[5]);

                urlCompletaFoto = autos[6];
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                img.setImageBitmap(imagenBitmap);
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar los datos");
        }
    }
    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
    private void listarAuto(){
        Intent intent = new Intent(getApplicationContext(), AutosActivity.class);
        startActivity(intent);
    }
}