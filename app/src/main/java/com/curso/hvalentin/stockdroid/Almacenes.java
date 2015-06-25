package com.curso.hvalentin.stockdroid;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.curso.hvalentin.stockdroid.Clases.Almacen;
import com.curso.hvalentin.stockdroid.Clases.AlmacenesAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta Clase se encarga de mostrar todos los Almacenes creados así como de gestionar las acciones de almacén
 */
public class Almacenes extends ActionBarActivity {

    private String usuario;
    private ProgressDialog progreso;
    JSONParser jsonParser = new JSONParser();
    ArrayList<Almacen> almacenes;
    private AlmacenesAdapter aa;
    private SharedPreferences.Editor editor;
    private SharedPreferences sp;

    JSONArray almacenesJSON = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_almacenes);

        //Obetenemos el usuario de la sesion
       sp= getSharedPreferences("sesion", Context.MODE_PRIVATE);
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

        //Creamos el arraylist donde cargaremos los diferentes almacenes obtenidos
        almacenes=new ArrayList<Almacen>();

        //Obtenemos la listView para inyectarle los datos
        ListView listView =(ListView) findViewById(R.id.lvAlmacenes);

        //Instanciamos el adapter de Almacenes
        aa = new AlmacenesAdapter(this, almacenes);
        //fijamos el adapter a la ListView
        listView.setAdapter(aa);
        //Ejecutamos la rutina de descarga de almacenes
        new DescargarAlmacenes().execute("getAlmacenes", usuario);

        //Creamos un escuchador para cuando se pulse sobre un item de la lista
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Instanciamos el intent Estantería
                Intent in = new Intent(getApplicationContext(),
                        Estanterias.class);
                //Guardamos en la sesión los datos que vamos a necesitar
                editor= sp.edit();
                editor.putString("CodigoAlmacen", almacenes.get(position).getCodigo());
                editor.putString("NombreAlmacen", almacenes.get(position).getNombre());
                editor.putString("DescripcionAlmacen", almacenes.get(position).getDescripcion());
                editor.commit();

                //Lanzamos la activity
                startActivity(in);

            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_almacenes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.Aviso))
                        .setMessage(getString(R.string.confirmaSalir))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /**
                                 * Si el usuario acepta borramos los datos de sesion y cerramos
                                 */
                                editor= sp.edit();
                                editor.clear();
                                editor.commit();
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                break;
        }


        switch (item.getItemId()) {
            //Si hemos pulsado sobre añadir
            case R.id.add_icon:
                //Lanzamos la actividad de creación de Almacenes
                Intent ed = new Intent(getApplicationContext(),
                        CrearAlmacen.class);
                startActivity(ed);
                this.finish();
                break;
        }
        // Handle action bar item_generic clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        //Si pulsamos en el icono de volver de Android

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.Aviso))
                .setMessage(getString(R.string.confirmaSalir))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        /**
                         * Si el usuario acepta borramos los datos de sesion y cerramos
                         */
                        editor= sp.edit();
                        editor.clear();
                        editor.commit();
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    //Clase para descargar los almacenes
    //REST
    class  DescargarAlmacenes extends AsyncTask<String, Integer, Boolean>{
        @Override
        protected void onPreExecute() {
            //Lanzamos el dialogo de progreso
            super.onPreExecute();
            progreso = new ProgressDialog(Almacenes.this);
            progreso.setMessage((getString(R.string.CargandoAlmacenes)));
            progreso.setIndeterminate(false);
            progreso.setCancelable(false);
            progreso.show();

        }



        @Override
        protected Boolean doInBackground(String... params) {
            //Cargamos los parametros que nos han pasado
            //en el objeto JSON
            List<NameValuePair> parametros = new ArrayList<NameValuePair>();
            parametros.add(new BasicNameValuePair("getAlmacenes", params[0]));
            parametros.add(new BasicNameValuePair("usuario", params[1]));
            JSONObject jsonObject = jsonParser.makeHttpRequest(getString(R.string.RESTurl), "POST", parametros);
            String mensaje = "";

            try{
                int success=jsonObject.getInt("success");
                if(success == 1){
                    //Obtenemos el Array "almacenes" que nos devuelve el servicio  REST
                    almacenesJSON = jsonObject.getJSONArray("almacenes");
                    for (int i = 0; i< almacenesJSON.length(); i++){
                        //Cargamos en el ArrayList de almacenes todos los datos de cada objeto JSON interno
                        JSONObject object = almacenesJSON.getJSONObject(i);

                        //Creamos una clase almacén y rellenamos todas sus propiedades
                        Almacen almacenAux = new Almacen();
                        almacenAux.setCodigo(object.getString("Codigo"));
                        almacenAux.setCreador(object.getString("Creador"));
                        almacenAux.setDescripcion(object.getString("Descripcion"));
                        almacenAux.setNombre(object.getString("Nombre"));

                        almacenes.add(almacenAux);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.ErrorAlmacenes)+" "+mensaje, Toast.LENGTH_LONG).show();
                }

            }catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
           return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {

            progreso.dismiss();

            if(aBoolean){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //Este método informa a AlmacenesAdapter de que sus datos han cambiado
                        aa.notifyDataSetChanged();
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(), getString(R.string.ErrorConexion), Toast.LENGTH_LONG).show();
            }
        }
    }
}
