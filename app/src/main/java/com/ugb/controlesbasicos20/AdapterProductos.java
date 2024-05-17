package com.ugb.controlesbasicos20;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterProductos extends ArrayAdapter<ClassProductos> {

    public AdapterProductos(Context context, List<ClassProductos> productos) {
        super(context, 0, productos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_productos, parent, false);
        }

        ClassProductos producto = getItem(position);

        TextView tvCodigo = convertView.findViewById(R.id.lblCod);
        TextView tvNombre = convertView.findViewById(R.id.lblNom);
        TextView tvMarca = convertView.findViewById(R.id.lblMar);
        TextView tvPrecio = convertView.findViewById(R.id.lblPrec);

        if (producto != null) {
            Log.d("AdapterProductos", "Codigo: " + producto.getCodigo());
            Log.d("AdapterProductos", "Nombre: " + producto.getNombre());
            Log.d("AdapterProductos", "Marca: " + producto.getMarca());
            Log.d("AdapterProductos", "Precio: " + producto.getPrecio());

            tvCodigo.setText(producto.getCodigo());
            tvNombre.setText(producto.getNombre());
            tvMarca.setText(producto.getMarca());
            tvPrecio.setText("$ " + producto.getPrecio());
        }

        return convertView;
    }
}
