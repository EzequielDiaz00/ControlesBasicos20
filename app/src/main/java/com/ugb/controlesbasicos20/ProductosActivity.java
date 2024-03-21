package com.ugb.controlesbasicos20;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ProductosActivity extends AppCompatActivity {

    Bundle parametros = new Bundle();
    FloatingActionButton btnAgregarProd;
    ListView lts;
    Cursor cProd;
    Producto productosAdapter;
    DB db;
    final ArrayList<Producto> alProd = new ArrayList<Producto>();
    final ArrayList<Producto> alProdCopy = new ArrayList<Producto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        btnAgregarProd = findViewById(R.id.fabAgregarProd);
        btnAgregarProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parametros.putString("accion","nuevo");
                abrirActividad(parametros);
            }
        });
        obtenerDatosProd();
        buscarProd();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        cProd.moveToPosition(info.position);
        menu.setHeaderTitle(cProd.getString(1)); //1 es el nombre
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        try {
            int itemId = item.getItemId();
            if (itemId == R.id.mnxAgregar) {
                parametros.putString("accion", "nuevo");
                abrirActividad(parametros);
                return true;
            } else if (itemId == R.id.mnxModificar) {
                String[] productos = {
                        cProd.getString(0), //idProd
                        cProd.getString(1), //codigo
                        cProd.getString(2), //descripcion
                        cProd.getString(3), //marca
                        cProd.getString(4), //presentacion
                        cProd.getString(5), //precio
                        cProd.getString(6), //foto
                };
                parametros.putString("accion", "modificar");
                parametros.putStringArray("productos", productos);
                abrirActividad(parametros);
                return true;
            } else if (itemId == R.id.mnxEliminar) {
                eliminarProd();
                return true;
            }
            return super.onContextItemSelected(item);
        } catch (Exception e) {
            mostrarMsg("Error al seleccionar una opción del menú: " + e.getMessage());
            return super.onContextItemSelected(item);
        }
    }

    private void eliminarProd(){
        try{
            AlertDialog.Builder confirmar = new AlertDialog.Builder(ProductosActivity.this);
            confirmar.setTitle("Estás seguro de eliminar: ");
            confirmar.setMessage(cProd.getString(1)); //1 es el nombre
            confirmar.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String respuesta = db.administrar_prod("eliminar", new String[]{cProd.getString(0)});//0 es el idAmigo
                    if(respuesta.equals("ok")){
                        mostrarMsg("Producto eliminado con éxito");
                        obtenerDatosProd();
                    } else {
                        mostrarMsg("Error al eliminar el producto: "+ respuesta);
                    }
                }
            });
            confirmar.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            confirmar.create().show();
        } catch (Exception e){
            mostrarMsg("Error al eliminar producto: "+ e.getMessage());
        }
    }

    private void abrirActividad(Bundle parametros){
        Intent abrirActividad = new Intent(getApplicationContext(), MainActivity.class);
        abrirActividad.putExtras(parametros);
        startActivity(abrirActividad);
    }

    private void obtenerDatosProd(){
        try {
            alProd.clear();
            alProdCopy.clear();

            db = new DB(ProductosActivity.this, "", null, 1);
            cProd = db.consultar_prod();

            if( cProd.moveToFirst() ){
                lts = findViewById(R.id.ltsProd);
                do{
                    Producto producto = new Producto(
                            cProd.getString(0),//idProd
                            cProd.getString(1),//codigo
                            cProd.getString(2),//descripcion
                            cProd.getString(3),//marca
                            cProd.getString(4),//presentacion
                            cProd.getString(5),//precio
                            cProd.getString(6) //foto
                    );
                    alProd.add(producto);
                } while(cProd.moveToNext());
                alProdCopy.addAll(alProd);

                lts.setAdapter(new AdaptadorImagenes(ProductosActivity.this, alProd));

                registerForContextMenu(lts);
            } else {
                mostrarMsg("No hay Datos de amigos que mostrar.");
            }
        } catch (Exception e){
            mostrarMsg("Error al mostrar datos: "+ e.getMessage());
        }
    }

    private void buscarProd(){
        TextView tempVal;
        tempVal = findViewById(R.id.txtBuscarProd);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    alProd.clear();
                    String valor = tempVal.getText().toString().trim().toLowerCase();
                    if (valor.length() <= 0) {
                        alProd.addAll(alProdCopy);
                    } else {
                        for (Producto producto : alProdCopy) {
                            String codigo = producto.getCodigo();
                            String descripcion = producto.getDescripcion();
                            String marca = producto.getMarca();
                            String presentacion = producto.getPresentacion();
                            String precio = producto.getPrecio();
                            if (codigo.toLowerCase().trim().contains(valor) ||
                                    descripcion.toLowerCase().trim().contains(valor) ||
                                    marca.trim().contains(valor) ||
                                    presentacion.trim().toLowerCase().contains(valor) ||
                                    precio.trim().contains(valor)) {
                                alProd.add(producto);
                            }
                        }
                    }
                    // Notificar al adaptador de los cambios en los datos
                    ((AdaptadorImagenes) lts.getAdapter()).notifyDataSetChanged();
                } catch (Exception e){
                    mostrarMsg("Error al buscar: "+ e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void mostrarMsg(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}
