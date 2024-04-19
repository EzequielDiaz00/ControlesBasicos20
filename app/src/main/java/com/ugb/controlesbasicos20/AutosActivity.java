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

public class AutosActivity extends AppCompatActivity {

    Bundle parametros = new Bundle();
    FloatingActionButton btnAgregarAuto;
    ListView lts;
    Cursor cAuto;
    Auto autosAdapter;
    DB db;
    final ArrayList<Auto> alAuto = new ArrayList<Auto>();
    final ArrayList<Auto> alAutoCopy = new ArrayList<Auto>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autos);

        btnAgregarAuto = findViewById(R.id.fabAgregarAuto);
        btnAgregarAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parametros.putString("accion","nuevo");
                abrirActividad(parametros);
            }
        });
        obtenerDatosAuto();
        buscarAuto();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mimenu, menu);

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        cAuto.moveToPosition(info.position);
        menu.setHeaderTitle(cAuto.getString(1)); //1 es el nombre
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
                String[] autos = {
                        cAuto.getString(0),
                        cAuto.getString(1),
                        cAuto.getString(2),
                        cAuto.getString(3),
                        cAuto.getString(4),
                        cAuto.getString(5),
                        cAuto.getString(6),
                };
                parametros.putString("accion", "modificar");
                parametros.putStringArray("autos", autos);
                abrirActividad(parametros);
                return true;
            } else if (itemId == R.id.mnxEliminar) {
                eliminarAuto();
                return true;
            }
            return super.onContextItemSelected(item);
        } catch (Exception e) {
            mostrarMsg("Error al seleccionar una opción del menú: " + e.getMessage());
            return super.onContextItemSelected(item);
        }
    }

    private void eliminarAuto(){
        try{
            AlertDialog.Builder confirmar = new AlertDialog.Builder(AutosActivity.this);
            confirmar.setTitle("Estás seguro de eliminar: ");
            confirmar.setMessage(cAuto.getString(1)); //1 es el nombre
            confirmar.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String respuesta = db.administrar_auto("eliminar", new String[]{cAuto.getString(0)});//0 es el idAmigo
                    if(respuesta.equals("ok")){
                        mostrarMsg("Auto eliminado con éxito");
                        obtenerDatosAuto();
                    } else {
                        mostrarMsg("Error al eliminar el auto: "+ respuesta);
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
            mostrarMsg("Error al eliminar auto: "+ e.getMessage());
        }
    }

    private void abrirActividad(Bundle parametros){
        Intent abrirActividad = new Intent(getApplicationContext(), MainActivity.class);
        abrirActividad.putExtras(parametros);
        startActivity(abrirActividad);
    }

    private void obtenerDatosAuto(){
        try {
            alAuto.clear();
            alAutoCopy.clear();

            db = new DB(AutosActivity.this, "", null, 1);
            cAuto = db.consultar_auto();

            if( cAuto.moveToFirst() ){
                lts = findViewById(R.id.ltsAuto);
                do{
                    Auto auto = new Auto(
                            cAuto.getString(0),
                            cAuto.getString(1),
                            cAuto.getString(2),
                            cAuto.getString(3),
                            cAuto.getString(4),
                            cAuto.getString(5),
                            cAuto.getString(6)
                    );
                    alAuto.add(auto);
                } while(cAuto.moveToNext());
                alAutoCopy.addAll(alAuto);

                lts.setAdapter(new AdaptadorImagenes(AutosActivity.this, alAuto));

                registerForContextMenu(lts);
            } else {
                mostrarMsg("No hay datos que mostrar.");
            }
        } catch (Exception e){
            mostrarMsg("Error al mostrar datos: "+ e.getMessage());
        }
    }

    private void buscarAuto(){
        TextView tempVal;
        tempVal = findViewById(R.id.txtBuscarAuto);
        tempVal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    alAuto.clear();
                    String valor = tempVal.getText().toString().trim().toLowerCase();
                    if (valor.length() <= 0) {
                        alAuto.addAll(alAutoCopy);
                    } else {
                        for (Auto auto : alAutoCopy) {
                            String marca = auto.getMarca();
                            String motor = auto.getMotor();
                            String chasis = auto.getChasis();
                            String vin = auto.getVin();
                            String combustion = auto.getCombustion();
                            if (    marca.toLowerCase().trim().contains(valor) ||
                                    motor.toLowerCase().trim().contains(valor) ||
                                    chasis.trim().contains(valor) ||
                                    vin.trim().toLowerCase().contains(valor) ||
                                    combustion.trim().contains(valor)) {
                                alAuto.add(auto);
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