package com.curso.hvalentin.stockdroid;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Almacenes extends ActionBarActivity {

    private String usuario;
    private ProgressDialog progreso;
    JSONParser jsonParser = new JSONParser();
    ArrayList<Almacen> almacenes;

    // Nombres de nodo del objeto JSON
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private static final String TAG_USUARIO = "usuario";
    private static final String TAG_ALMACENES = "almacenes";
    JSONArray almacenesJSON = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almacenes);

        //Obetenemos el usuario de la sesion
        SharedPreferences sp = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        usuario = sp.getString("usuario", "");
        //Si el usuario es vacío salimos de la activity
        if (usuario.equals("")){
            finish();
        }

        //Llamamos a ActionBar y forzamos icono y comportamiento jerárquico
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        almacenes=new ArrayList<Almacen>();

        new DescargarAlmacenes().execute();


        ListView listView =(ListView) findViewById(R.id.list_item);



    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_almacenes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class  DescargarAlmacenes extends AsyncTask<String, Integer, Boolean>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(Almacenes.this);
            progreso.setMessage(("Cargando sus Almacenes."));
            progreso.setIndeterminate(false);
            progreso.setCancelable(false);
            progreso.show();

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            progreso.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                   // ListAdapter adapter = new SimpleAdapter(Almacenes.this,);


                }
            });
        }

        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> parametros = new ArrayList<NameValuePair>();
            JSONObject jsonObject = jsonParser.makeHttpRequest(getString(R.string.RESTurl), "POST", parametros);

            Log.d("Almacenes: ", jsonObject.toString());
            try{
                int success=jsonObject.getInt(TAG_SUCCESS);
                if(success == 1){
                    almacenesJSON = jsonObject.getJSONArray(TAG_ALMACENES);
                    for (int i = 0; i< almacenesJSON.length(); i++){
                        JSONObject object = almacenesJSON.getJSONObject(i);

                        String mensaje = object.getString(TAG_MESSAGE);

                        Almacen almacenAux = new Almacen();
                        almacenAux.setCodigo(object.getString("Codigo"));
                        almacenAux.setCreador(object.getString("Creador"));
                        almacenAux.setDescripcion(object.getString("Descripcion"));
                        almacenAux.setNombre(object.getString("Nombre"));

                        almacenes.add(almacenAux);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.ErrorAlmacenes), Toast.LENGTH_LONG).show();
                }

            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
