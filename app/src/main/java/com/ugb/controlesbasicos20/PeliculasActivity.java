package com.ugb.controlesbasicos20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PeliculasActivity extends AppCompatActivity {

    Bundle parametros = new Bundle();
    FloatingActionButton btnAgregarAuto;
    ListView lts;
    Cursor cPeli;
    Pelicula peliAdapter;
    DB db;
    final ArrayList<Pelicula> alPeli = new ArrayList<Pelicula>();
    final ArrayList<Pelicula> alPeliCopy = new ArrayList<Pelicula>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peliculas);

        btnAgregarAuto = findViewById(R.id.fabAgregarPeli);
        btnAgregarAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parametros.putString("accion","nuevo");
                abrirActividad(parametros);
            }
        });
        obtenerDatosPeli();
        buscarPeli();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        cPeli.moveToPosition(info.position);
        menu.setHeaderTitle(cPeli.getString(1)); //1 es el nombre
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
                String[] peli = {
                        cPeli.getString(0),
                        cPeli.getString(1),
                        cPeli.getString(2),
                        cPeli.getString(3),
                        cPeli.getString(4),
                        cPeli.getString(5),
                };
                parametros.putString("accion", "modificar");
                parametros.putStringArray("peli", peli);
                abrirActividad(parametros);
                return true;
            } else if (itemId == R.id.mnxEliminar) {
                eliminarPeli();
                return true;
            }
            return super.onContextItemSelected(item);
        } catch (Exception e) {
            mostrarMsg("Error al seleccionar una opción del menú: " + e.getMessage());
            return super.onContextItemSelected(item);
        }
    }

    private void eliminarPeli(){
        try{
            AlertDialog.Builder confirmar = new AlertDialog.Builder(PeliculasActivity.this);
            confirmar.setTitle("Estás seguro de eliminar: ");
            confirmar.setMessage(cPeli.getString(1)); //1 es el nombre
            confirmar.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String respuesta = db.administrar_peli("eliminar", new String[]{cPeli.getString(0)});//0 es el idAmigo
                    if(respuesta.equals("ok")){
                        mostrarMsg("Pelicula eliminado con éxito");
                        obtenerDatosPeli();
                    } else {
                        mostrarMsg("Error al eliminar la pelicula: "+ respuesta);
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
            mostrarMsg("Error al eliminar pelicula: "+ e.getMessage());
        }
    }

    private void abrirActividad(Bundle parametros){
        Intent abrirActividad = new Intent(getApplicationContext(), MainActivity.class);
        abrirActividad.putExtras(parametros);
        startActivity(abrirActividad);
    }

    private void obtenerDatosPeli(){
        try {
            alPeli.clear();
            alPeliCopy.clear();

            db = new DB(PeliculasActivity.this, "", null, 1);
            cPeli = db.consultar_peli();

            if( cPeli.moveToFirst() ){
                lts = findViewById(R.id.ltsPeli);
                do{
                    Pelicula peli = new Pelicula(
                            cPeli.getString(0),
                            cPeli.getString(1),
                            cPeli.getString(2),
                            cPeli.getString(3),
                            cPeli.getString(4),
                            cPeli.getString(5)
                    );
                    alPeli.add(peli);
                } while(cPeli.moveToNext());
                alPeli.addAll(alPeli);

                lts.setAdapter(new AdaptadorImagenes(PeliculasActivity.this, alPeli));

                registerForContextMenu(lts);
            } else {
                mostrarMsg("No hay datos que mostrar.");
            }
        } catch (Exception e){
            mostrarMsg("Error al mostrar datos: "+ e.getMessage());
        }
    }

    private void buscarPeli(){
        TextView tempVal;
        tempVal = findViewById(R.id.txtBuscarPeli);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    alPeli.clear();
                    String valor = tempVal.getText().toString().trim().toLowerCase();
                    if (valor.length() <= 0) {
                        alPeli.addAll(alPeliCopy);
                    } else {
                        for (Pelicula peli : alPeliCopy) {
                            String titulo = peli.getTitulo();
                            String sinopsis = peli.getSinopsis();
                            String duracion = peli.getDuracion();
                            String actor = peli.getActor();
                            if (    titulo.toLowerCase().trim().contains(valor) ||
                                    sinopsis.toLowerCase().trim().contains(valor) ||
                                    duracion.trim().contains(valor) ||
                                    actor.trim().toLowerCase().contains(valor)){
                                alPeli.add(peli);
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