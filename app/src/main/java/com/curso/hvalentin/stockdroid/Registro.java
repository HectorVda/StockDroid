package com.curso.hvalentin.stockdroid;

import android.app.Activity;
import android.content.Intent;
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
 * Clase encargada de registrar un nuevo usuario
 */
public class Registro extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        Button boton = (Button) findViewById(R.id.btnSignin);
        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Obtenemos los diferentes datos
                 */
                String usuario = ((EditText) findViewById(R.id.etUsuarioRegister)).getText().toString();
                String pass = ((EditText) findViewById(R.id.etPasswordRegister)).getText().toString();
                String pass2 = ((EditText) findViewById(R.id.etPassRepeat)).getText().toString();
                String nombre = ((EditText) findViewById(R.id.etNombreRegister)).getText().toString();
                String apellidos = ((EditText) findViewById(R.id.etApellidosRegister)).getText().toString();

                if(pass.equals(pass2)){
                    /**
                     * Si ha introducido bien la contraseña en las dos ocasiones,
                     * registramos al usuario
                     */
                    new RegistraWS().execute(usuario, pass, nombre, apellidos);
                }else{
                    //Si las contraseñas no son iguales se le indica al usuario
                    Toast.makeText(getApplicationContext(), getString(R.string.passError), Toast.LENGTH_LONG).show();
                }


            }
        });
        /**
         * Si pulsamos sobre volver finalizamos la activity
         */
        Button volver = (Button) findViewById(R.id.btnVolverRegistro);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registro, menu);
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
     * Si pulsamos en el botón nativo Atras finalizamos la activity
     */
    public void onBackPressed() {

        finish();

    }

    /**
     * Clase encargada de registrar al usuario
     *
     * SOAP
     */
    private class RegistraWS extends AsyncTask<String, Integer, Boolean> {
        final String NAMESPACE = getString(R.string.wsNameSpace);
        final String URL = NAMESPACE + "/SoapServer.php?wsdl";
        final String METHOD_NAME = "registrar";
        final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

        Integer registro = 0;


        protected Boolean doInBackground(String... params) {
            boolean resultado = true;

            SoapObject soapobject = new SoapObject(NAMESPACE, METHOD_NAME);
            soapobject.addProperty("NombreUsuario", params[0]);
            soapobject.addProperty("pass", params[1]);
            soapobject.addProperty("nombre", params[2]);
            soapobject.addProperty("apellidos", params[3]);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapobject);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                registro = (Integer) envelope.getResponse();
            } catch (Exception e) {
                resultado = false;
            }

            return resultado;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                if (registro.equals(1)) {
                    Intent intent = new Intent(getApplicationContext(), Almacenes.class);
                    Toast.makeText(getApplicationContext(), getString(R.string.registerOK), Toast.LENGTH_LONG).show();
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.registerError), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.ErrorConexion), Toast.LENGTH_LONG).show();
            }
        }
    }
}

