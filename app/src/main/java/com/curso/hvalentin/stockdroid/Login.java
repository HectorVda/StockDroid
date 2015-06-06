package com.curso.hvalentin.stockdroid;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Clase encargada de gestionar el login
 * MAIN ACTIVITY
 */
public class Login extends Activity {
    private SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Creamos sesión para guardar usuario
        SharedPreferences sp = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        editor = sp.edit();
        Button login = (Button) findViewById(R.id.btnLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Revisamos si el usuario existe
                 */
                String usuario = ((EditText) findViewById(R.id.etUsuario)).getText().toString();
                String pass = ((EditText) findViewById(R.id.etPassword)).getText().toString();
                new loginWS().execute(usuario, pass);


            }
        });

        /**
         * Gestionamos el botón de registro
         */
        Button registro = (Button) findViewById(R.id.btnSignUp);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Registro.class);
                startActivity(intent);

            }

        });


    }

    /**
     * Clase encargada de enlazar la activity con el WS
     * <p/>
     * SOAP
     */
    private class loginWS extends AsyncTask<String, Integer, Boolean> {
        final String NAMESPACE = getString(R.string.wsNameSpace);
        final String URL = NAMESPACE + "/SoapServer.php?wsdl";
        final String METHOD_NAME = "login";
        final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

        Integer login = 0;

        private String usuario = "";

        protected Boolean doInBackground(String... params) {
            boolean resultado = true;

            SoapObject soapobject = new SoapObject(NAMESPACE, METHOD_NAME);
            soapobject.addProperty("user", params[0]);
            soapobject.addProperty("pass", params[1]);

            usuario = params[0];

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapobject);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                login = (Integer) envelope.getResponse();
            } catch (Exception e) {

                resultado = false;
            }

            return resultado;
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                if (login.equals(1)) {
                    /**
                     * Si el usuario existe lo añadimos a la sesión
                     * Lanzamos la activity Almacenes de dicho usuario
                     */
                    editor.putString("usuario", usuario);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), Almacenes.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.ErrorLogin), Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.ErrorConexion), Toast.LENGTH_LONG).show();
            }
        }
    }

}


