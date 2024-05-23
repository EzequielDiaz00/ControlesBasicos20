package com.ugb.controlesbasicos20;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
        ImageView imgProd = convertView.findViewById(R.id.imgProd);

        if (producto != null) {
            tvCodigo.setText(producto.getCodigo());
            tvNombre.setText(producto.getNombre());
            tvMarca.setText(producto.getMarca());
            tvPrecio.setText("$ " + producto.getPrecio());

            String urlCompletaFoto = producto.getFoto();

            if (urlCompletaFoto != null && !urlCompletaFoto.isEmpty()) {
                Bitmap imagenBitmap = BitmapFactory.decodeFile(urlCompletaFoto);
                if (imagenBitmap != null) {
                    imgProd.setImageBitmap(imagenBitmap);
                } else {
                    imgProd.setImageResource(R.drawable.ic_launcher_foreground);
                }
            } else {
                // Set a placeholder image or handle the error
                imgProd.setImageResource(R.drawable.ic_launcher_foreground);
            }
        }

        return convertView;
    }
}
