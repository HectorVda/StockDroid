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

import com.curso.hvalentin.stockdroid.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Clase que gestiona la accion de compartir un almacén
 */
public class CompartirAlmacen extends ActionBarActivity {
private String usuario = "", CodigoAlmacen ="";
    private SharedPreferences sp;
    private EditText user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir_almacen);

        //Cargamos la sesión y el código de almacén ya que lo necesitamos para añadirselo al usuario deseado
        sp = getSharedPreferences("sesion", Context.MODE_PRIVATE);
       user = (EditText) findViewById(R.id.etUsuarioCompartir);
        CodigoAlmacen = sp.getString("CodigoAlmacen", "");

        //Llamamos a ActionBar y forzamos icono y comportamiento jerárquico
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Obtenemos el botón de aceptar y creamos un clickListener
        Button aceptar = (Button) findViewById(R.id.btnAceptarCompartir);
        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtenemos el usuario introducido y lanzamos la creación de la relación
                //Entre el usuario y el almacen seleccionado.
                usuario= user.getText().toString();
                new createGestionWS().execute(usuario, CodigoAlmacen);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compartir_almacen, menu);
        return true;

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //Si hemos pulsado sobre editar
            case R.id.home:

                Intent ed = new Intent(getApplicationContext(),
                        Estanterias.class);
                startActivity(ed);
                finish();
                break;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }


    /**
     * Esta clase interga se encarga de lanzar la rutina de generación de relaciones
     * entre un usuario y un almacén.
     *
     * SOAP
     */
    private class createGestionWS extends AsyncTask<String, Integer, Boolean> {
        /**
         * Obtenemos la URL del XML de conexión
         * Indicamos el método a ejecutar
         */
        final String NAMESPACE = getString(R.string.wsNameSpace);
        final String URL = NAMESPACE + "/SoapServer.php?wsdl";
        final String METHOD_NAME = "createGestion";
        final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

        Integer creado = 0;


        protected Boolean doInBackground(String... params) {
            boolean resultado = true;

            /**
             * Creamos el objeto SOAP
             * Añadimos los parámetros recibidos
             */
            SoapObject soapobject = new SoapObject(NAMESPACE, METHOD_NAME);
            soapobject.addProperty("Usuario", params[0]);
            soapobject.addProperty("CodigoAlmacen", params[1]);

            /**
             * Configuramos el envoltorio SOAP
             */
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapobject);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                //Obtenemos la respuesta del WS (1 = correcto, 0 = incorrecto)
                creado = (Integer) envelope.getResponse();
            } catch (Exception e) {
                resultado = false;
            }

            return resultado;
        }

        protected void onPostExecute(Boolean result) {
            /**
             * Si resultado es true, significa que no ha habido ninguna excepción por lo que hemos
             * conseguido consultar correctamente el servicio SOAP
             */
            if (result) {
                /**
                 * Dependiendo de la respuesta del servicio actuamos en consecuencia
                 */
                if (creado.equals(1)) {
                    Intent intent = new Intent(getApplicationContext(), Estanterias.class);
                    Toast.makeText(getApplicationContext(), getString(R.string.compartirOK), Toast.LENGTH_LONG).show();
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.errorCompartir), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.ErrorConexion), Toast.LENGTH_LONG).show();
            }
        }
    }
}
