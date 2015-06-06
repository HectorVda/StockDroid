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
public class ItemsAdapter extends ArrayAdapter<Item>{
    private Context context;
    private ArrayList<Item> items;

    public ItemsAdapter(Context context, ArrayList<Item> items) {
        super(context, R.layout.item, items);
        this.context = context;
        this.items = items;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.item, null);

        Item i = items.get(position);

        if (i != null) {

            TextView nombre = (TextView) convertView.findViewById(R.id.tvNombre);
            if (nombre != null) {
                nombre.setText(items.get(position).getNombre());
            }
            TextView cantidad = (TextView) convertView.findViewById(R.id.tvCantidad);
            if (cantidad != null) {
                cantidad.setText(""+ items.get(position).getCantidad());
            }

            TextView descripcion = (TextView) convertView.findViewById(R.id.tvDescripcion);
            if (descripcion != null) {
                descripcion.setText(items.get(position).getDescripcion());
            }
        }


        return convertView;
    }

}
