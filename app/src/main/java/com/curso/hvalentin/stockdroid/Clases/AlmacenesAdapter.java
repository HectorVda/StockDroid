package com.curso.hvalentin.stockdroid.Clases;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.curso.hvalentin.stockdroid.R;

import java.util.ArrayList;

/**
 * Created by Héctor Valentín Úbeda on 27/05/2015.
 */
public class AlmacenesAdapter extends ArrayAdapter<Almacen> {
    private Context context;
    private ArrayList<Almacen> almacenes;

    public AlmacenesAdapter(Context context, ArrayList<Almacen> datos) {
        super(context, R.layout.item_generic, datos);
        this.context = context;
        this.almacenes = datos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_generic, null);

        Almacen i = almacenes.get(position);

        if (i != null) {

            TextView nombre = (TextView) convertView.findViewById(R.id.tvNombre);
            if (nombre != null) {
                nombre.setText(almacenes.get(position).getNombre());
            }

            TextView descripcion = (TextView) convertView.findViewById(R.id.tvDescripcion);
            if (descripcion != null) {
                descripcion.setText(almacenes.get(position).getDescripcion());
            }
        }

        return convertView;
    }
}
