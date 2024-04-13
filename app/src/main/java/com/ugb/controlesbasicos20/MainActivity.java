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

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView tempVal;
    Button btn;
    FloatingActionButton btnRegresar;
    String id="", rev="", idProd="", accion="nuevo";
    ImageView img;
    String urlCompletaFoto;
    Intent tomarFotoIntent;
    utilidades utls;
    DB db;
    detectarInternet di;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utls = new utilidades();
        db = new DB(getApplicationContext(), "", null, 1);
        di = new detectarInternet(getApplicationContext());

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
                try {
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

                    String respuesta = "";
                    if( di.hayConexionInternet() ) {
                        //obtener datos a enviar al servidor
                        JSONObject datosProd = new JSONObject();
                        if (accion.equals("modificar")) {
                            datosProd.put("_id", id);
                            datosProd.put("_rev", rev);
                        }
                        datosProd.put("idProd", idProd);
                        datosProd.put("codigo", codigo);
                        datosProd.put("descripcion", descripcion);
                        datosProd.put("marca", marca);
                        datosProd.put("presentacion", presentacion);
                        datosProd.put("precio", precio);
                        datosProd.put("urlCompletaFoto", urlCompletaFoto);
                        //enviamos los datos
                        enviarDatosServidor objGuardarDatosServidor = new enviarDatosServidor(getApplicationContext());
                        respuesta = objGuardarDatosServidor.execute(datosProd.toString()).get();
                        //comprobacion de la respuesta
                        JSONObject respuestaJSONObject = new JSONObject(respuesta);
                        if (respuestaJSONObject.getBoolean("ok")) {
                            id = respuestaJSONObject.getString("id");
                            rev = respuestaJSONObject.getString("rev");
                        } else {
                            respuesta = "Error al guardar en servidor: " + respuesta;
                        }
                    }
                    String[] datos = new String[]{id, rev, idProd, codigo, descripcion, marca, presentacion, precio, urlCompletaFoto};
                    respuesta = db.administrar_prod(accion, datos);
                    if (respuesta.equals("ok")) {
                        mostrarMsg("Producto registrado con exito.");
                        listarProd();
                    } else {
                        mostrarMsg("Error al intentar registrar el producto: " + respuesta);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mostrarMsg("Error al guadar datos en el servidor o en SQLite: "+ e.getMessage());
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
        try {
            if (requestCode == 1 && resultCode == RESULT_OK) {
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                Producto producto = new Producto();
                producto.setFoto(urlCompletaFoto);
                img.setImageBitmap(imagenBitmap);
            } else {
                mostrarMsg("Se canceló la toma de la foto");
            }
        } catch (Exception e) {
            mostrarMsg("Error al seleccionar la foto: " + e.getMessage());
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
                JSONObject jsonObject = new JSONObject(parametros.getString("amigos")).getJSONObject("value");
                id = jsonObject.getString("_id");
                rev = jsonObject.getString("_rev");
                idProd = jsonObject.getString("idProd");

                tempVal = findViewById(R.id.txtCod);
                tempVal.setText(jsonObject.getString("codigo"));

                tempVal = findViewById(R.id.txtDes);
                tempVal.setText(jsonObject.getString("descripcion"));

                tempVal = findViewById(R.id.txtMar);
                tempVal.setText(jsonObject.getString("marca"));

                tempVal = findViewById(R.id.txtPres);
                tempVal.setText(jsonObject.getString("presentacion"));

                tempVal = findViewById(R.id.txtPrec);
                tempVal.setText(jsonObject.getString("precio"));

                urlCompletaFoto = jsonObject.getString("urlCompletaFoto");
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                img.setImageBitmap(imagenBitmap);
            }else{//nuevos registros
                idProd = utls.generarIdUnico();
            }
        }catch (Exception e){
            mostrarMsg("Error al mostrar los datos: ");
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