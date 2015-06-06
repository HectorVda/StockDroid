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

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class CrearEstanteria extends ActionBarActivity {
    private String almacen ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_estanteria);

        //Obetenemos los datos que vamos a necesitar de la sesión
        SharedPreferences sp = getSharedPreferences("sesion", Context.MODE_PRIVATE);

        almacen = sp.getString("CodigoAlmacen", "");


        //Llamamos a ActionBar y forzamos icono y comportamiento jerárquico
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        /**
         * Creamos un ClickListener del botón aceptar
         */
        Button aceptar = (Button) findViewById(R.id.btnAceptaCreaEstanteria);
        aceptar.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String nombre = ((EditText) findViewById(R.id.etNombreCrearEstanteria)).getText().toString();
                String descripcion = ((EditText) findViewById(R.id.etDescripcionCrearEstanteria)).getText().toString();

                if(nombre != ""){
                    if(almacen != ""){
                        new CreaEstanteriaWs().execute( nombre, descripcion, almacen);
                    }else{
                        Toast.makeText(getApplicationContext(), getString(R.string.errorArgumentosInvalidos), Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(getApplicationContext(), getString(R.string.errorNombreVacio), Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crear_estanteria, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item_generic clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    /**
     * Clase que se encarga de crear la Estantería
     *
     * SOAP
     */
    private class CreaEstanteriaWs extends AsyncTask<String, Integer, Boolean> {
        /**
         * Indicamos La URL y método a utilizar
         */
        final String NAMESPACE = getString(R.string.wsNameSpace);
        final String URL = NAMESPACE + "/SoapServer.php?wsdl";
        final String METHOD_NAME = "createEstanteria";
        final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

        Integer creado = 0;


        protected Boolean doInBackground(String... params) {
            boolean resultado = true;
            /**
             * Cargamos los argumentos recibidos por parámetro
             */

            SoapObject soapobject = new SoapObject(NAMESPACE, METHOD_NAME);
            soapobject.addProperty("nombre", params[0]);
            soapobject.addProperty("descripcion", params[1]);
            soapobject.addProperty("CodigoAlmacen", params[2]);



            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapobject);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                creado = (Integer) envelope.getResponse();
            } catch (Exception e) {
                resultado = false;
            }

            return resultado;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                if (creado.equals(1)) {
                    Intent intent = new Intent(getApplicationContext(), Estanterias.class);
                    Toast.makeText(getApplicationContext(), getString(R.string.EstanteriaCreada), Toast.LENGTH_LONG).show();
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.EstanteriaNoCreada), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.ErrorConexion), Toast.LENGTH_LONG).show();
            }
        }
    }
}
