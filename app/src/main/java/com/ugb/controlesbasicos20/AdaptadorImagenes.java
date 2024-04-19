package com.ugb.controlesbasicos20;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AdaptadorImagenes extends BaseAdapter {
    Context context;
    ArrayList<Pelicula> datosPeliArrayList;
    LayoutInflater layoutInflater;

    public AdaptadorImagenes(Context context, ArrayList<Pelicula> datosProductosArrayList) {
        this.context = context;
        this.datosPeliArrayList = datosPeliArrayList;
    }

    @Override
    public int getCount() {
        return datosPeliArrayList.size();
    }

    @Override
    public Object getItem(int i) { return datosPeliArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(datosPeliArrayList.get(i).getID());
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.listview_imagenes, viewGroup, false);

        try {
            Pelicula peli = datosPeliArrayList.get(i);

            TextView tempVal = itemView.findViewById(R.id.lblTit);
            tempVal.setText(peli.getTitulo());

            tempVal = itemView.findViewById(R.id.lblDur);
            tempVal.setText(peli.getDuracion());

            tempVal = itemView.findViewById(R.id.lblSin);
            tempVal.setText(peli.getSinopsis());

            ImageView imgView = itemView.findViewById(R.id.imgFoto);
            Bitmap imagenBitmap = BitmapFactory.decodeFile(peli.getFoto());
            imgView.setImageBitmap(imagenBitmap);
        } catch (Exception e) {
            Toast.makeText(context, "Error en Adaptador Imagenes: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return itemView;
    }
}
