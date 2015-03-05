package com.cn.notifyserver;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.database.Cursor;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.AsyncTask;
import com.cn.notifyserver.BD.DataBaseManager;
import com.cn.notifyserver.Class.GeneralCn;
import java.util.List;

public class ServerActivity extends Activity
{
    private DataBaseManager manager;
    Cursor cursor;
    private ListView lista, lv;
    SimpleCursorAdapter adapter;
    EditText txtBuscar;
    ImageButton btnbuscar, btnNuevo;
    GeneralCn cgeneral;
    String numeroRetorno, numeroEnvio, smsRespuesta, contactoId, longitud, latitud;
    AlertDialog.Builder dlConfirmacion;
    private ProgressDialog progress;
    Handler updateBarHandler;

  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainserver);
        
        manager= new DataBaseManager(this);
        dlConfirmacion = new AlertDialog.Builder(this);
        updateBarHandler = new Handler();

        //Instancia de datos
        cursor = manager.cargarCursorContactos();
        cgeneral=new GeneralCn(this);

        txtBuscar= (EditText) findViewById(R.id.txtBuscar);
        lista= (ListView) findViewById(R.id.lvLista);

        String[] from = new String[]{ manager.ptName, manager.ptTelefono};
        int[] to = new int[]{android.R.id.text1, android.R.id.text2 };
        adapter= new  SimpleCursorAdapter(this, android.R.layout.two_line_list_item, cursor, from, to, 0);
        lista.setAdapter(adapter);

        dlConfirmacion.setTitle(".:: Aviso");
        dlConfirmacion.setMessage("¿ Estas seguro de enviar una petición ?");
        dlConfirmacion.setCancelable(false);

        AlertDialog.Builder aceptar = dlConfirmacion.setPositiveButton("Aceptar", 
        new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialogo, int id) {

                progress = new ProgressDialog(ServerActivity.this);
                progress.setMessage("Esperando respuesta :) ");
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setProgress(0);
                progress.setMax(100);
                progress.show();
                try {
                    Cursor c = manager.buscarContactoId(contactoId);
                    if (c.moveToFirst()) {
                        numeroEnvio = c.getString(2);
                        Toast.makeText(getApplicationContext(), " Enviando petición ...", Toast.LENGTH_SHORT).show();
                        cgeneral.sendSMS(numeroEnvio, ".");
                        numeroEnvio = "+51" + numeroEnvio;
                        new EnvioRecepcionSms().execute();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Error al solicitar " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        dlConfirmacion.setNegativeButton("Cancelar", 
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                Toast.makeText(getApplicationContext(), "Se cancelo la petición", Toast.LENGTH_SHORT).show();
            }
        });

        lista.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                contactoId = String.valueOf(id);
                dlConfirmacion.show();
            }
        });
    }
	
    public void BuscarContacto(View v){
        try {
            new BuscarTask().execute();
            //Cursor c=manager.buscarContacto(txtBuscar.getText().toString());
            //adapter.changeCursor(c);
        } catch (Exception e) {
            Toast.makeText( getApplicationContext(),"Buscar "+e.getMessage() , Toast.LENGTH_SHORT ).show();
        }
    }

    public void NuevoContacto(View v){
        Intent nuevo=new Intent(this, NewContact.class);
        startActivityForResult(nuevo, 2);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO: Implement this method
		super.onActivityResult(requestCode, resultCode, data);
		adapter.changeCursor(manager.buscarContacto(txtBuscar.getText().toString()));
	}
	
	
    public class BuscarTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
            Toast.makeText(getApplicationContext(), "Buscando contacto", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            cursor=manager.buscarContacto(txtBuscar.getText().toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            adapter.changeCursor(cursor);
            Toast.makeText(getApplicationContext(), "Finalizada", Toast.LENGTH_SHORT).show();
        }
    }

  public class EnvioRecepcionSms extends AsyncTask<Void, Void, Void>{

        final Thread tProgress = new Thread(){
                    @Override
                    public void run() {
                        try {
                            while (progress.getProgress() <= 100) {
                                Thread.sleep(1000);
                                updateBarHandler.post(new Runnable() {
                                    public void run() {
                                        progress.incrementProgressBy(5);
                                    }
                                });

                                if (progress.getProgress() == progress.getMax()) {
                                    progress.dismiss();
                                    break;
                                }
                            }
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), "Error al solicitar " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        @Override
        protected void onPreExecute(){
            //Iniciando
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // TODO Auto-generated method stub
            try {
                tProgress.start();
                Thread.sleep(20000); // 20 Segundos
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error al leer Mensaje", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            
            smsRespuesta = cgeneral.readSMS();
            String[] respuesta = smsRespuesta.split("\\|");
            if(respuesta.length == 3){
              	numeroRetorno =respuesta[0];
                latitud = respuesta[1];
                longitud = respuesta[2];
                if(numeroEnvio.trim().equalsIgnoreCase(numeroRetorno.trim())) {
                    
                    Uri location = Uri.parse("geo:<"+latitud+">, <"+longitud+">?q=<"+latitud + ">, <" + longitud +">(Posición)");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

                    PackageManager packageManager = getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe)
                        startActivity(mapIntent);

               }
               else
                  Toast.makeText(getApplicationContext(), "La respuesta no es del cliente solicitado", Toast.LENGTH_SHORT).show();
           }
            else
              Toast.makeText(getApplicationContext(), ":( Se excedio el tiempo de respuesta no se puedo recuperar la posición ", Toast.LENGTH_SHORT).show();

        }
    }
    
}
