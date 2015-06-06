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

/**
 * Clase encargada de editar una Estantería
 */
public class EditaEstanteria extends ActionBarActivity {

    private String Nombre = "", CodigoEstanteria = "", Descripcion="", CodigoAlmacen = "";
    private EditText nombre, descripcion;
    private  SharedPreferences.Editor editor;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edita_estanteria);

        //Llamamos a ActionBar y forzamos icono y comportamiento jerárquico
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Obetenemos todos los datos necesarios de la sesión
         sp = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        CodigoEstanteria = sp.getString("CodigoEstanteria", "");
        CodigoAlmacen = sp.getString("CodigoAlmacen", "");
        Nombre = sp.getString("NombreEstanteria", "");
        Descripcion = sp.getString("DescripcionEstanteria", "");

        /**
         * Obtenemos los Edit text para poder utilizarlos más adelante
         * Insertamos los datos obtenidos de la sesión
         */
        nombre =  (EditText) findViewById(R.id.etNombreEditaEstanteria);
        descripcion =  (EditText) findViewById(R.id.etDescripcionEditaEstanteria);
        nombre.setText(Nombre);
        descripcion.setText(Descripcion);

        Button guardar = (Button) findViewById(R.id.btnAceptarEditaEstanteria);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Cargamos a las variables de clase los daos que hay en los editText
                 */
                Nombre = nombre.getText().toString();
                Descripcion = descripcion.getText().toString();

                /**
                 * Modificamos la estantería
                 */
                new updateEstanteria().execute(CodigoEstanteria, CodigoAlmacen, Nombre, Descripcion);

                /**
                 * Cargamos los nuevos datos en la sesión por si queremos utilizarlos más tarde
                 * Lanzamos la activity Estanterías para que muestre la estantería con los datos actuales
                 */
                editor = sp.edit();
                editor.putString("NombreEstanteria", Nombre);
                editor.putString("DescripcionEstanteria", Descripcion);
                Intent in = new Intent(getApplicationContext(),
                        Estanterias.class);
                startActivity(in);
                finish();

            }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edita_estanteria, menu);
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
     * Clase encargada de lanzar la rutina del WS para actualizarla
     *
     * SOAP
     */
    private class updateEstanteria extends AsyncTask<String, Integer, Boolean> {
        final String NAMESPACE = getString(R.string.wsNameSpace);
        final String URL = NAMESPACE + "/SoapServer.php?wsdl";
        final String METHOD_NAME = "updateEstanteria";
        final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

        Integer updated = 0;


        protected Boolean doInBackground(String... params) {
            boolean resultado = true;

            SoapObject soapobject = new SoapObject(NAMESPACE, METHOD_NAME);

            soapobject.addProperty("codigo", params[0]);
            soapobject.addProperty("CodigoAlmacen", params[1]);
            soapobject.addProperty("nombre", params[2]);
            soapobject.addProperty("descripcion", params[3]);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapobject);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                updated = (Integer) envelope.getResponse();
            } catch (Exception e) {
                resultado = false;
            }

            return resultado;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                if (updated.equals(1)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.updateOK), Toast.LENGTH_LONG).show();
                   finish();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.updateError), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.ErrorConexion), Toast.LENGTH_LONG).show();
            }
        }
    }
}
