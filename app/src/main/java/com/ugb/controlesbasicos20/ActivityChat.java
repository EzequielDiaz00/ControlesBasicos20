package com.ugb.controlesbasicos20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityChat extends AppCompatActivity {

    private RecyclerView recyclerViewMensajes;
    private EditText editTextMensaje;
    private Button btnEnviar;
    private DatabaseReference databaseRef;
    private String userID;
    private AdapterChat adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Inicializar la instancia de AdapterChat
        adapter = new AdapterChat(new ArrayList<>());

        // Obtener el ID del usuario de la actividad anterior
        userID = getIntent().getStringExtra("userID");

        recyclerViewMensajes = findViewById(R.id.recyclerViewMensajes);
        editTextMensaje = findViewById(R.id.editTextMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewMensajes.setLayoutManager(layoutManager);

        // Asignar el adaptador al RecyclerView
        recyclerViewMensajes.setAdapter(adapter);

        // Obtener una referencia a la base de datos de Firebase
        databaseRef = FirebaseDatabase.getInstance().getReference("chats");

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessages();
            }
        });

        // Cargar los mensajes existentes
        loadMessages();
    }

    private void sendMessages() {
        String mensaje = editTextMensaje.getText().toString();

        if (!mensaje.isEmpty()) {
            // Generar una clave única para el mensaje
            String messageId = databaseRef.push().getKey();

            // Crear un objeto HashMap para representar el mensaje
            HashMap<String, Object> mensajeMap = new HashMap<>();
            mensajeMap.put("contenido", mensaje);
            mensajeMap.put("emisor", userID); // ID del usuario que envía el mensaje

            // Guardar el mensaje en la base de datos con la clave generada
            databaseRef.child(messageId).setValue(mensajeMap);

            // Limpiar el campo de texto después de enviar el mensaje
            editTextMensaje.setText("");
        } else {
            Toast.makeText(this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMessages() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Limpiar los mensajes actuales antes de cargar nuevos mensajes
                adapter.clearMessages();

                // Iterar sobre los mensajes en la base de datos
                for (DataSnapshot mensajeSnapshot : dataSnapshot.getChildren()) {
                    String contenido = mensajeSnapshot.child("contenido").getValue(String.class);
                    String emisor = mensajeSnapshot.child("emisor").getValue(String.class);

                    // Agregar el mensaje al adaptador para que se muestre en el RecyclerView
                    adapter.addMessage(contenido, emisor);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de lectura de la base de datos
                Log.e("ActivityChat", "Error al leer mensajes de la base de datos", databaseError.toException());
            }
        });
    }
}
