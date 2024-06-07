package com.ugb.controlesbasicos20;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MensajeViewHolder> {

    private List<Mensaje> mensajes; // Cambio de String a Mensaje

    public AdapterChat(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position); // Cambio de String a Mensaje
        holder.bind(mensaje);
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

        private TextView textViewMensaje;

        public MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMensaje = itemView.findViewById(R.id.textViewMensajeEmisor);
        }

        public void bind(Mensaje mensaje) {
            textViewMensaje.setText(mensaje.getContenido());
        }
    }
}
