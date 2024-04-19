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
    ArrayList<Auto> datosAutosArrayList;
    LayoutInflater layoutInflater;

    public AdaptadorImagenes(Context context, ArrayList<Auto> datosProductosArrayList) {
        this.context = context;
        this.datosAutosArrayList = datosAutosArrayList;
    }

    @Override
    public int getCount() {
        return datosAutosArrayList.size();
    }

    @Override
    public Object getItem(int i) { return datosAutosArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(datosAutosArrayList.get(i).getID());
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.listview_imagenes, viewGroup, false);

        try {
            Auto auto = datosAutosArrayList.get(i);

            TextView tempVal = itemView.findViewById(R.id.lblMar);
            tempVal.setText(auto.getMarca());

            tempVal = itemView.findViewById(R.id.lblMot);
            tempVal.setText(auto.getMotor());

            tempVal = itemView.findViewById(R.id.lblCom);
            tempVal.setText(auto.getCombustion());

            ImageView imgView = itemView.findViewById(R.id.imgFoto);
            Bitmap imagenBitmap = BitmapFactory.decodeFile(auto.getFoto());
            imgView.setImageBitmap(imagenBitmap);
        } catch (Exception e) {
            Toast.makeText(context, "Error en Adaptador Imagenes: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return itemView;
    }
}
