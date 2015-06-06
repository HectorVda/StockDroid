package com.curso.hvalentin.stockdroid;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de gestionar un item en concreto
 */
public class item extends ActionBarActivity {
    SharedPreferences sp;

    private String CodigoItem;
    private String Nombre;
    private float Cantidad;
    private String Descripcion;
    private String CodigoEstanteria;
    JSONParser jsonParser = new JSONParser();
    private EditText cantidad;
   




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        //Llamamos a ActionBar y forzamos icono y comportamiento jerárquico
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayHomeAsUpEnabled(true);

        /**
         * Cargamos todos  los datos necesarios para gestionar el item
         *
         * (!) La cantidad es un FLOAT
         */
        sp  = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        CodigoItem = sp.getString("CodigoItem", "");
        Nombre= sp.getString("Nombre", "");
        Cantidad = sp.getFloat("Cantidad", 0f);
        Descripcion = sp.getString("Descripcion", "");
        CodigoEstanteria = sp.getString("CodigoEstanteria", "");


        /**
         * Mostramos los datos del item seleccionado en la lista de ITEMS
         */
        TextView nombre = (TextView) findViewById(R.id.tvNombreItem);
        nombre.setText(Nombre);
        cantidad = (EditText) findViewById(R.id.etCantidadItem);
        cantidad.setText("" + Cantidad);

        TextView descripcion = (TextView) findViewById(R.id.tvDescripcionItem);
        descripcion.setText(Descripcion);

        /**
         * Gestionamos el comporamiento del boton +
         */
        Button mas = (Button) findViewById(R.id.btnMas);
       mas.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               cantidad.setText(""+(Float.parseFloat(cantidad.getText().toString())+1));
               Cantidad= Float.parseFloat(cantidad.getText().toString());
           }
       });

        /**
         * Gestionamos el comportamiento del botón -
         */
        Button menos = (Button) findViewById(R.id.btnMenos);
        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cantidad.setText("" + (Float.parseFloat(cantidad.getText().toString()) - 1));
                Cantidad= Float.parseFloat(cantidad.getText().toString());
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        cantidad = (EditText) findViewById(R.id.etCantidadItem);
        switch (item.getItemId()) {
            //En caso de que estemos volviendo desde actionbar actualizo la cantidad
                case android.R.id.home:
                    Cantidad= Float.parseFloat(cantidad.getText().toString());
                    new UpdateItem().execute("updateItem");
                    Intent in = new Intent(getApplicationContext(),
                            items.class);
                    startActivity(in);
                   this.finish();

                    break;
            //Si hemos pulsado sobre editar
            case R.id.edit_icon:
                Intent ed = new Intent(getApplicationContext(),
                        EditaItem.class);
                startActivity(ed);
                this.finish();
                break;


            //Si hemos pulsado el botón de borrado
            case R.id.delete_icon:
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.Aviso))
                        .setMessage(getString(R.string.ConfirmaBorrarEstateria))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                /**
                                 * Si el usuario acepta lanzamos la eliminación
                                 */
                                new EliminaItem().execute(CodigoItem, CodigoEstanteria);
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

    //Guarda la cantidad cuando se pulsa el botón atras nativo
    @Override
    public void onBackPressed() {

        cantidad = (EditText) findViewById(R.id.etCantidadItem);
        new UpdateItem().execute("updateItem");
        Intent in = new Intent(getApplicationContext(),
                items.class);
        startActivity(in);

        finish();

    }

    /**
     * Clase encargada de actualizar la cantidad del item
     *
     * REST
     */
    class  UpdateItem extends AsyncTask<String, Integer, Boolean> {
        String mensaje = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }



        @Override
        protected Boolean doInBackground(String... params) {
            List<NameValuePair> parametros = new ArrayList<NameValuePair>();
            parametros.add(new BasicNameValuePair("updateCantidad", params[0]));
            parametros.add(new BasicNameValuePair("CodigoItem", CodigoItem));
            parametros.add(new BasicNameValuePair("numero", ""+Cantidad));
            parametros.add(new BasicNameValuePair("CodigoEstanteria", sp.getString("CodigoEstanteria", "")));
            JSONObject jsonObject = jsonParser.makeHttpRequest(getString(R.string.RESTurl), "POST", parametros);


            try{
                int success=jsonObject.getInt("success");
                if(success == 0) {
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



            if(!aBoolean){

                if(mensaje != ""){
                    Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), getString(R.string.ErrorConexion), Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    /**
     * Clase encargada de eliminar un ITEM
     *
     * SOAP
     */
    private class EliminaItem extends AsyncTask<String, Integer, Boolean> {
        final String NAMESPACE = getString(R.string.wsNameSpace);
        final String URL = NAMESPACE + "/SoapServer.php?wsdl";
        final String METHOD_NAME = "deleteItem";
        final String SOAP_ACTION = NAMESPACE + "/" + METHOD_NAME;

        Integer eliminado = 0;


        protected Boolean doInBackground(String... params) {
            boolean resultado = true;

            SoapObject soapobject = new SoapObject(NAMESPACE, METHOD_NAME);
            soapobject.addProperty("CodigoItem", params[0]);
            soapobject.addProperty("CodigoEstanteria", params[1]);



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
                    Intent intent = new Intent(getApplicationContext(), items.class);

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
