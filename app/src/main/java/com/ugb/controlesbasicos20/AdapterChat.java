package com.ugb.controlesbasicos20;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MensajeViewHolder> {

    private List<Mensaje> mensajes;
    private String userID;
    private String otherUserID;

    public AdapterChat(List<Mensaje> mensajes, String userID, String otherUserID) {
        this.mensajes = mensajes;
        this.userID = userID;
        this.otherUserID = otherUserID;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);

        if (mensaje.getEmisor() != null && mensaje.getEmisor().equals(userID)) {
            // Si el mensaje es del usuario actual, establecer la gravedad a la derecha
            holder.layout1.setGravity(Gravity.RIGHT);
            holder.layout2.setVisibility(View.GONE); // Ocultar el contenedor del mensaje del receptor
            holder.textViewMensajeEmisor.setText(mensaje.getContenido());
        } else if (mensaje.getEmisor() != null && mensaje.getEmisor().equals(otherUserID)) {
            // Si el mensaje es del otro usuario, establecer la gravedad a la izquierda
            holder.layout1.setGravity(Gravity.LEFT);
            holder.layout2.setVisibility(View.VISIBLE); // Mostrar el contenedor del mensaje del receptor
            holder.layout1.setVisibility(View.GONE);
            holder.textViewMensajeReceptor.setText(mensaje.getContenido());
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public void addMessage(String mensaje, String emisor) {
        mensajes.add(new Mensaje(mensaje, emisor));
        notifyDataSetChanged();
    }

    public void clearMessages() {
        mensajes.clear();
        notifyDataSetChanged();
    }

    public static class MensajeViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout layout1, layout2;
        private TextView textViewMensajeEmisor, textViewMensajeReceptor;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            layout1 = itemView.findViewById(R.id.layout1);
            layout2 = itemView.findViewById(R.id.layout2);
            textViewMensajeEmisor = itemView.findViewById(R.id.textViewMensajeEmisor);
            textViewMensajeReceptor = itemView.findViewById(R.id.textViewMensajeReceptor);
        }
    }
}