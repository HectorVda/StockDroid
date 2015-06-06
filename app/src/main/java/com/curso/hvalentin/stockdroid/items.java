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

import com.curso.hvalentin.stockdroid.Clases.Item;
import com.curso.hvalentin.stockdroid.Clases.ItemsAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de gestionar las listas de items además de la edición y borrado de su Estantería
 */
public class items extends ActionBarActivity {
    private String CodigoEstanteria;
    private String NombreEstanteria;
    private ProgressDialog progreso;
    JSONParser jsonParser = new JSONParser();
    ArrayList<Item> items;
    private ItemsAdapter ia;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    JSONArray itemsJSON = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);


        /**
         * Cargamos los datos necesarios de la sesión
         */
      sp  = getSharedPreferences("sesion", Context.MODE_PRIVATE);
           CodigoEstanteria= sp.getString("CodigoEstanteria", "");
        NombreEstanteria= sp.getString("NombreEstanteria", "");

        /**
         * Si el nombre de la estantería no es vacío cargamos el título
         */
        if(NombreEstanteria != ""){
            setTitle(NombreEstanteria);
        }
        //Llamamos a ActionBar y forzamos icono y comportamiento jerárquico
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        items=new ArrayList<Item>();

        ListView listView =(ListView) findViewById(R.id.lvItems);

        ia = new ItemsAdapter(this, items);
        listView.setAdapter(ia);
        new DescargarItems().execute("getItems", CodigoEstanteria);

        /**
         * Gestionamos la pulsación sobre un item
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Instanciamos el intent con la activity
                Intent in = new Intent(getApplicationContext(),
                        item.class);
                // Cargamos los datos que vamos a necesitar en el item
                editor = sp.edit();
                editor.putString("CodigoItem",  items.get(position).getCodigo());
                editor.putString("Nombre", items.get(position).getNombre());
                editor.putFloat("Cantidad", items.get(position).getCantidad());
                editor.putString("Descripcion", items.get(position).getDescripcion());
                editor.commit();

                // Lanzamos la activity
                startActivity(in);


            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //Si hemos pulsado sobre editar editamos la estantería
            case R.id.edit_icon:
                Intent ed = new Intent(getApplicationContext(),
                        EditaEstanteria.class);
                startActivity(ed);
                this.finish();
                break;

            //Si pulsamos sobre añadir queremos añadir un item
            case R.id.add_icon:
                Intent add = new Intent(getApplicationContext(),
                        CrearItem.class);
                startActivity(add);
                this.finish();
                break;

            //Si pulsamos borrar queremos borrar una estantería
            case R.id.delete_icon:
                //Preguntamos al usuario si realmente desea borrar
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.Aviso))
                        .setMessage(getString(R.string.ConfirmaBorrarEstateria))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /**
                                 * Si el usuario acepta lanzamos la eliminación
                                 */
                                new EliminaEstanteria().execute(CodigoEstanteria);
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

        // Handle action bar item_generic clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);

    }

//Si pulsamos el botón atrás nativo finalizamos la activity
    @Override
    public void onBackPressed() {

        finish();

    }

    /**
     * Descargamos la lista de items
     *
     * REST
     */
    class  DescargarItems extends AsyncTask<String, Integer, Boolean> {
        String mensaje = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(items.this);
            progreso.setMessage(("Cargando Items."));
            progreso.setIndeterminate(false);
            progreso.setCancelable(false);
            progreso.show();

        }



        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> parametros = new ArrayList<NameValuePair>();
            parametros.add(new BasicNameValuePair("getItems", params[0]));
            parametros.add(new BasicNameValuePair("CodigoEstanteria", params[1]));
            JSONObject jsonObject = jsonParser.makeHttpRequest(getString(R.string.RESTurl), "POST", parametros);


            try{
                int success=jsonObject.getInt("success");
                if(success == 1){
                    itemsJSON = jsonObject.getJSONArray("items");
                    for (int i = 0; i< itemsJSON.length(); i++){
                        JSONObject object = itemsJSON.getJSONObject(i);


                        Item item = new Item();
                        item.setCodigo(object.getString("Codigo"));
                        item.setCodigoEstanteria(object.getString("CodigoEstanteria"));
                        item.setCantidad(Float.valueOf(object.getString("Cantidad")));
                        item.setDescripcion(object.getString("Descripcion"));
                        item.setNombre(object.getString("Nombre"));

                        items.add(item);
                    }
                }else{
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

            progreso.dismiss();

            if(aBoolean){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ia.notifyDataSetChanged();
                    }
                });
            }else{
                if(mensaje != ""){
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.ErrorConexion), Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    /**
     * Clase encargada de eliminar una estantería
     */
    private class EliminaEstanteria extends AsyncTask<String, Integer, Boolean> {
        final String NAMESPACE = getString(R.string.wsNameSpace);
        final String URL = NAMESPACE + "/SoapServer.php?wsdl";
        final String METHOD_NAME = "deleteEstanteria";
        final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

        Integer eliminado = 0;


        protected Boolean doInBackground(String... params) {
            boolean resultado = true;

            SoapObject soapobject = new SoapObject(NAMESPACE, METHOD_NAME);
            soapobject.addProperty("codigo", params[0]);



            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapobject);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                eliminado = (Integer) envelope.getResponse();
            } catch (Exception e) {
                resultado = false;
            }

            return resultado;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                if (eliminado.equals(1)) {
                    Intent intent = new Intent(getApplicationContext(), Estanterias.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.deleteError), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.ErrorConexion), Toast.LENGTH_LONG).show();
            }
        }
    }
}
