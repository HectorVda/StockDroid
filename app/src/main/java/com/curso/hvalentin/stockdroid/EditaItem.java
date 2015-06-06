package com.curso.hvalentin.stockdroid;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.curso.hvalentin.stockdroid.Clases.Item;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class EditaItem extends ActionBarActivity {

    private String Nombre;
    private float Cantidad;
    private String Descripcion;
    private String CodigoItem;
    private EditText nombre;
    private EditText cantidad;
    private EditText descripcion;
    private JSONParser jsonParser = new JSONParser();
    private SharedPreferences sp;

    private  SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edita_item);

        //Obetenemos los datos necesarios de la sesión
         sp = getSharedPreferences("sesion", Context.MODE_PRIVATE);

        CodigoItem = sp.getString("CodigoItem", "");
        Nombre = sp.getString("Nombre", "");
        Cantidad = sp.getFloat("Cantidad", 0f);
       Descripcion = sp.getString("Descripcion", "");



         nombre = (EditText) findViewById(R.id.etNombreEditaItem);
         cantidad = (EditText) findViewById(R.id.etCantidadEditaItem);
         descripcion = (EditText) findViewById(R.id.etDescripcionEditaItem);

        nombre.setText(Nombre);
        cantidad.setText(""+Cantidad);
        descripcion.setText(Descripcion);
        //Llamamos a ActionBar y forzamos icono y comportamiento jerárquico
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);


        Button guardar= (Button) findViewById(R.id.btnGuardarEditaItem);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nombre = nombre.getText().toString();
                Cantidad = Float.parseFloat(cantidad.getText().toString());
                Descripcion = descripcion.getText().toString();

                new UpdateItem().execute("updateItem");


                editor = sp.edit();

                editor.putString("Nombre", Nombre);
                editor.putFloat("Cantidad",  Cantidad);
                editor.putString("Descripcion",  Descripcion);
                editor.commit();

                Intent in = new Intent(getApplicationContext(),
                        item.class);
                startActivity(in);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edita_item, menu);
        return true;
    }

    public void onBackPressed() {

        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:

                this.finish();

                break;
        }


        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }



    class  UpdateItem extends AsyncTask<String, Integer, Boolean> {
        String mensaje = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }



        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> parametros = new ArrayList<NameValuePair>();
            parametros.add(new BasicNameValuePair("updateItem", params[0]));
            parametros.add(new BasicNameValuePair("CodigoItem", CodigoItem));
            parametros.add(new BasicNameValuePair("numero", ""+Cantidad));
            parametros.add(new BasicNameValuePair("nombre", Nombre));
            parametros.add(new BasicNameValuePair("descripcion", Descripcion));
            parametros.add(new BasicNameValuePair("CodigoEstanteria", sp.getString("CodigoEstanteria", "")));
            JSONObject jsonObject = jsonParser.makeHttpRequest(getString(R.string.RESTurl), "POST", parametros);


            try{
                int success=jsonObject.getInt("success");
                if(success == 0) {
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                }

            }catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {



            if(!aBoolean){

                if(mensaje != ""){
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.ErrorConexion), Toast.LENGTH_LONG).show();
                }

            }
        }
    }
}
