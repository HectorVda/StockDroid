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
 * Created by Sene on 28/05/2015.
 */
public class EstanteriasAdapter extends ArrayAdapter<Estanteria> {
    private Context context;
    private ArrayList<Estanteria> estanterias;

    public EstanteriasAdapter(Context context, ArrayList<Estanteria> estanterias) {
        super(context, R.layout.item_generic, estanterias);
        this.context = context;
        this.estanterias = estanterias;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item_generic, null);

        Estanteria i = estanterias.get(position);

        if (i != null) {

            TextView nombre = (TextView) convertView.findViewById(R.id.tvNombre);
            if (nombre != null) {
                nombre.setText(estanterias.get(position).getNombre());
            }

            TextView descripcion = (TextView) convertView.findViewById(R.id.tvDescripcion);
            if (descripcion != null) {
                descripcion.setText(estanterias.get(position).getDescripcion());
            }
        }

        return convertView;
    }

}
