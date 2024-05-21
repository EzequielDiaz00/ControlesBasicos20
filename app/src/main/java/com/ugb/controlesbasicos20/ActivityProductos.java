package com.ugb.controlesbasicos20;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

public class ActivityProductos extends AppCompatActivity {

    EditText txtBuscarProd;
    ListView listProd;
    Button btnAddProd;
    DBSqlite dbSqlite;
    SQLiteDatabase dbRead;
    AdapterProductos adapter;
    ActivityMain activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        activityMain = new ActivityMain();
        String userEmailLog = activityMain.userEmailLogin;

        txtBuscarProd = findViewById(R.id.txtBuscarProd);
        listProd = findViewById(R.id.listProd);
        btnAddProd = findViewById(R.id.btnAddProd);

        dbSqlite = new DBSqlite(this);
        dbRead = dbSqlite.getReadableDatabase();

        loadDataFromDatabase(userEmailLog);

        btnAddProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ActivityAddProd.class);
                startActivity(intent);
            }
        });
    }

    private void loadDataFromDatabase(String userEmail) {
        List<ClassProductos> productos = new ArrayList<>();

        String[] projection = {
                DBSqlite.TableProd.COLUMN_USER,
                DBSqlite.TableProd.COLUMN_CODIGO,
                DBSqlite.TableProd.COLUMN_NOMBRE,
                DBSqlite.TableProd.COLUMN_MARCA,
                DBSqlite.TableProd.COLUMN_DESCRIPCION,
                DBSqlite.TableProd.COLUMN_PRECIO,
                DBSqlite.TableProd.COLUMN_FOTO
        };

        String selection = DBSqlite.TableProd.COLUMN_USER + " = ?";
        String[] selectionArgs = {userEmail};

        Cursor cursor = null;
        try {
            cursor = dbRead.query(
                    DBSqlite.TableProd.TABLE_PROD,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int userIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_USER);
                    int codigoIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_CODIGO);
                    int nombreIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_NOMBRE);
                    int marcaIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_MARCA);
                    int descripcionIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_DESCRIPCION);
                    int precioIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_PRECIO);
                    int fotoIndex = cursor.getColumnIndex(DBSqlite.TableProd.COLUMN_FOTO);

                    if (codigoIndex != -1 && nombreIndex != -1 && precioIndex != -1) {
                        String user = cursor.getString(userIndex);
                        String codigo = cursor.getString(codigoIndex);
                        String nombre = cursor.getString(nombreIndex);
                        String marca = cursor.getString(marcaIndex);
                        String descripcion = cursor.getString(descripcionIndex);
                        Double precio = Double.valueOf(cursor.getString(precioIndex));
                        String foto = cursor.getString(fotoIndex);

                        productos.add(new ClassProductos(user, codigo, nombre, marca, descripcion, precio, foto));
                    } else {
                        Toast.makeText(this, "Error al extraer de SQLite", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        adapter = new AdapterProductos(this, productos);
        listProd.setAdapter(adapter);

        if (productos.isEmpty()) {
            Toast.makeText(this, "Aún no hay productos. ¡Agrega uno!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mostrando productos", Toast.LENGTH_SHORT).show();
        }
    }


}
