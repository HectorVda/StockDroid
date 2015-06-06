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


public class CrearAlmacen extends ActionBarActivity {
private String usuario = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_almacen);


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


        Button aceptar = (Button) findViewById(R.id.btnAceptaCrearAlmacen);
        aceptar.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String nombre = ((EditText) findViewById(R.id.etNombreCrearAlmacen)).getText().toString();
                String descripcion = ((EditText) findViewById(R.id.etDescripcionCrearAlmacen)).getText().toString();

                if (nombre != "") {
                        new CreaAlmacenWs().execute(nombre, descripcion, usuario);

                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.errorNombreVacio), Toast.LENGTH_LONG).show();
                }
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_crear_almacen, menu);
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
    private class CreaAlmacenWs extends AsyncTask<String, Integer, Boolean> {
        final String NAMESPACE = getString(R.string.wsNameSpace);
        final String URL = NAMESPACE + "/SoapServer.php?wsdl";
        final String METHOD_NAME = "createAlmacen";
        final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

        Integer creado = 0;


        protected Boolean doInBackground(String... params) {
            boolean resultado = true;

            SoapObject soapobject = new SoapObject(NAMESPACE, METHOD_NAME);
            soapobject.addProperty("nombre", params[0]);
            soapobject.addProperty("descripcion", params[1]);
            soapobject.addProperty("Usuario", params[2]);



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
                    Intent intent = new Intent(getApplicationContext(), Almacenes.class);
                    Toast.makeText(getApplicationContext(), getString(R.string.AlmacenCreado), Toast.LENGTH_LONG).show();
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.AlmacenNoCreada), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.ErrorConexion), Toast.LENGTH_LONG).show();
            }
        }
    }
}
