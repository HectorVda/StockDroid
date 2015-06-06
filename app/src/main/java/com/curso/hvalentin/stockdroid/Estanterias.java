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

import com.curso.hvalentin.stockdroid.Clases.Estanteria;
import com.curso.hvalentin.stockdroid.Clases.EstanteriasAdapter;

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
 * Clase encargada de mostrar y gestionar todas las estanterías de un almacén
 */
public class Estanterias extends ActionBarActivity {

    private String CodigoAlmacen;
    private String NombreAlmacen;
    private String Usuario;
    private ProgressDialog progreso;
    JSONParser jsonParser = new JSONParser();
    ArrayList<Estanteria> estanterias;
    private EstanteriasAdapter ea;
    SharedPreferences sp;
    SharedPreferences.Editor editor;
    JSONArray EstanteriasJSON = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estanteria);

        /**
         * Cargamos los datos necesarios desde la sesión
         */
        sp = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        CodigoAlmacen = sp.getString("CodigoAlmacen", "");
        NombreAlmacen = sp.getString("NombreAlmacen", "");
        Usuario = sp.getString("usuario", "");
        /**
         * Si el nombre de alamcén no es vacío
         * se carga el nombre del almacén actual
         * como título de la activity
         */
        if (NombreAlmacen != "") {
            setTitle(NombreAlmacen);
        }


        //Llamamos a ActionBar y forzamos icono y comportamiento jerárquico
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        /**
         * Necesitamos un ArrayList para inyectar los datos obtenidos del WS
         * La listView donde lo mostraremos
         * y un adapter para poder cargar correctamente los datos
         */
        estanterias = new ArrayList<Estanteria>();
        ListView listView = (ListView) findViewById(R.id.lvEstanterias);
        ea = new EstanteriasAdapter(this, estanterias);
        listView.setAdapter(ea);

        /**
         * Descargamos las estanterías
         */
        new DescargarEstanterias().execute("getEstanterias", CodigoAlmacen);

        /**
         * Creamos un listener para cuando se pulse sobre un item de la lista
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * Instanciamos la activity que vamos a lanzar
                 */
                Intent in = new Intent(getApplicationContext(),
                        items.class);
                /**
                 * Cargamos en la sesión los datos que vamos a necesitar
                 */
                editor = sp.edit();
                editor.putString("CodigoEstanteria", estanterias.get(position).getCodigo());
                editor.putString("NombreEstanteria", estanterias.get(position).getNombre());
                editor.putString("DescripcionEstanteria", estanterias.get(position).getDescripcion());
                editor.commit();

                /**
                 * Lanzamos la activity
                 */
                startActivity(in);

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_estanteria, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /**
         * Gestionamos los botones del ActionBar
         */
        switch (item.getItemId()) {
            //Si hemos pulsado sobre editar
            case R.id.edit_icon:
                Intent ed = new Intent(getApplicationContext(),
                        EditaAlmacen.class);
                startActivity(ed);
                finish();
                break;
            //Si pulsamos sobre Añadir
            case R.id.add_icon:
                Intent add = new Intent(getApplicationContext(),
                        CrearEstanteria.class);
                startActivity(add);
                finish();
                break;
            /**
             * Si pulsamos sobre borrar:
             *
             * Si estamos en la activity de Estanterías significa que hemos pulsado sobre un
             * Almacén, por lo cual entendemos que queremos eliminar dicho almacén
             */
            case R.id.delete_icon:
                /**
                 * Alertamos al usuario de si desea borrar realmente el almacén
                 */
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.Aviso))
                        .setMessage(getString(R.string.ConfirmaBorrar))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /**
                                 * Si el usuario acepta lanzamos la eliminación
                                 */
                                new EliminaAlmacen().execute(CodigoAlmacen, Usuario);
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
            //Si pulsamos sobre Share abrimos la activity de compartir un Almacén
            case R.id.share_icon:
                Intent share = new Intent(getApplicationContext(),
                        CompartirAlmacen.class);
                startActivity(share);
                finish();
                break;
        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {


        finish();

    }

    /**
     * Clase que se encarga de descargar las estanterías desde WS
     *
     * REST
     */
    class DescargarEstanterias extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progreso = new ProgressDialog(Estanterias.this);
            progreso.setMessage(("Cargando estanterías."));
            progreso.setIndeterminate(false);
            progreso.setCancelable(false);
            progreso.show();

        }


        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> parametros = new ArrayList<NameValuePair>();
            parametros.add(new BasicNameValuePair("getEstanterias", params[0]));
            parametros.add(new BasicNameValuePair("CodigoAlmacen", params[1]));
            JSONObject jsonObject = jsonParser.makeHttpRequest(getString(R.string.RESTurl), "POST", parametros);
            String mensaje = "";

            try {
                int success = jsonObject.getInt("success");
                if (success == 1) {
                    EstanteriasJSON = jsonObject.getJSONArray("estanterias");
                    for (int i = 0; i < EstanteriasJSON.length(); i++) {
                        JSONObject object = EstanteriasJSON.getJSONObject(i);


                        Estanteria estanteria = new Estanteria();
                        estanteria.setCodigo(object.getString("Codigo"));
                        estanteria.setCodigoAlmacen(object.getString("CodigoAlmacen"));
                        estanteria.setDescripcion(object.getString("Descripcion"));
                        estanteria.setNombre(object.getString("Nombre"));

                        estanterias.add(estanteria);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            progreso.dismiss();

            if (aBoolean) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ea.notifyDataSetChanged();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.ErrorConexion), Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Clase que se encarga de eliminar un Almacén
     *
     * SOAP
     */
    private class EliminaAlmacen extends AsyncTask<String, Integer, Boolean> {
        final String NAMESPACE = getString(R.string.wsNameSpace);
        final String URL = NAMESPACE + "/SoapServer.php?wsdl";
        final String METHOD_NAME = "deleteGestion";
        final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

        Integer eliminado = 0;


        protected Boolean doInBackground(String... params) {
            boolean resultado = true;

            /**
             * Necesitamos el codigo de almacen a borrar
             * y el usuario que está eliminando dicho almacén
             */
            SoapObject soapobject = new SoapObject(NAMESPACE, METHOD_NAME);
            soapobject.addProperty("CodigoAlmacen", params[0]);
            soapobject.addProperty("Usuario", params[1]);


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
                    /**
                     * Como hemos borrado el almacén la estantería actual no existe por lo que tenemos que abrir
                     * la activity de almacenes de nuevo
                     */
                    Intent intent = new Intent(getApplicationContext(), Almacenes.class);
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
