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


public class EditaAlmacen extends ActionBarActivity {
private String usuario = "", Nombre = "", Descripcion="", CodigoAlmacen="";
    private EditText nombre, descripcion;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edita_almacen);


        //Obetenemos el usuario de la sesion
         sp = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        usuario = sp.getString("usuario", "");
        CodigoAlmacen = sp.getString("CodigoAlmacen", "");
        Nombre = sp.getString("NombreAlmacen", "");
        Descripcion = sp.getString("DescripcionAlmacen", "");
        nombre=(EditText) findViewById(R.id.etNombreEditaAlmacen);
        descripcion=(EditText) findViewById(R.id.etDescripcionEditaAlmacen);
        nombre.setText(Nombre);
        descripcion.setText(Descripcion);

        Button guardar = (Button) findViewById(R.id.btnGuardarEditaAlmacen);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Nombre = nombre.getText().toString();
                Descripcion = descripcion.getText().toString();

                new updateAlmacen().execute(CodigoAlmacen, Nombre, Descripcion);

                editor = sp.edit();
                editor.putString("NombreAlmacen", Nombre);
                editor.putString("DescripcionAlmacen", Descripcion);
                Intent in = new Intent(getApplicationContext(),
                        Almacenes.class);
                startActivity(in);
                finish();
            }
        });

        //Llamamos a ActionBar y forzamos icono y comportamiento jerárquico
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edita_almacen, menu);
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


    private class updateAlmacen extends AsyncTask<String, Integer, Boolean> {
        final String NAMESPACE = getString(R.string.wsNameSpace);
        final String URL = NAMESPACE + "/SoapServer.php?wsdl";
        final String METHOD_NAME = "updateAlmacen";
        final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

        Integer updated = 0;


        protected Boolean doInBackground(String... params) {
            boolean resultado = true;

            SoapObject soapobject = new SoapObject(NAMESPACE, METHOD_NAME);

            soapobject.addProperty("CodigoAlmacen", params[0]);
            soapobject.addProperty("nombre", params[1]);
            soapobject.addProperty("descripcion", params[2]);



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
