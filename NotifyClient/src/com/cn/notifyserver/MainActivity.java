package com.cn.notifyserver;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.content.Intent;
import com.cn.notifyserver.Class.*;
import com.cn.notifyserver.BD.DataBaseManager;
import android.database.Cursor;
import android.content.DialogInterface;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
 	boolean isServerClient=false;
	GeneralCn cgeneral;
    long timeInMilliseconds = 0L;
    MiServicioGps ms;
    private long startTime = 0L;
    private Handler customHandler = new Handler();

    AlertDialog.Builder dlConfirmacion;
    Button btnSiguiente;
    RadioButton rbtnServidor, rbtnCliente;
    String messageTitle="Estas seguro de configurar como Cliente?";

    private DataBaseManager manager;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        manager= new DataBaseManager(this);

		if(manager.GetConfigNotify("config"))
		{
		
			if("servidor".equalsIgnoreCase(manager.GetConfigNotifyOption("config"))){
				this.finish();
                Intent serverActivity= new Intent(this, ServerActivity.class);
                startActivity(serverActivity);
            }
            else{

                if(ms==null){
					
                    ms = new MiServicioGps(MainActivity.this.getApplicationContext());
                    cgeneral= new GeneralCn(this);
                    ms.setCoordenadas();
                    startTime = SystemClock.uptimeMillis();
                    customHandler.postDelayed(updateTimerThread, 100);
					
                }else
                    ms.setCoordenadas();

            }

		}
		else{

			setContentView(R.layout.main);
            btnSiguiente= (Button) findViewById(R.id.btnsiguiente);
            rbtnServidor= (RadioButton) findViewById(R.id.rbtnServidor);
            rbtnCliente= (RadioButton) findViewById(R.id.rbtnCliente);
            dlConfirmacion = new AlertDialog.Builder(this);
			
            dlConfirmacion.setTitle(".:: Aviso");
            dlConfirmacion.setCancelable(false);

            AlertDialog.Builder aceptar = dlConfirmacion.setPositiveButton("Aceptar", 
            new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                    if(!isServerClient){

                        manager.insertarParameter("config", "cliente");
                        ms = new MiServicioGps(MainActivity.this.getApplicationContext());
                        cgeneral= new GeneralCn(MainActivity.this);
                        ms.setCoordenadas();
                        startTime = SystemClock.uptimeMillis();
                        customHandler.postDelayed(updateTimerThread, 100);
                        btnSiguiente.setVisibility(View.INVISIBLE);
                        rbtnServidor.setVisibility(View.INVISIBLE);
                        rbtnCliente.setVisibility(View.INVISIBLE);
                    }
                    else{

                        manager.insertarParameter("config", "servidor");
                        MainActivity.this.finish();
                        Intent serverActivity= new Intent(MainActivity.this, ServerActivity.class);
                        startActivity(serverActivity);
                    }                   
                }
            });
        
        dlConfirmacion.setNegativeButton("Cancelar", 
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                
            }
        });
		}
    }
	
	public void OnSaveOption(View view){
		dlConfirmacion.setMessage(messageTitle);
		dlConfirmacion.show();
	}
	
	public void onRadioGuardarOpcion(View view){
		boolean checked=((RadioButton)view).isChecked();
		switch(view.getId()){
			case R.id.rbtnServidor:
				if(checked){
                    messageTitle="Estas seguro de configurar como Servidor?";
                    isServerClient=true;
                }
					
					break;
			case R.id.rbtnCliente:
				if(checked){
                    messageTitle="Estas seguro de configurar como Cliente?";
                    isServerClient=false;
                }
					
				break;
		}
	}
	
    private Runnable updateTimerThread = new Runnable() {
        public void run() {

            new EscucharMensaje().execute();
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            customHandler.postDelayed(this, 3000);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.item) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class EscucharMensaje extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute(){
            Toast.makeText(getApplicationContext(), "Buscando ultima posición ...", 
			Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {

                cgeneral.readSMS();
                ms.setCoordenadas();
                if(cgeneral.messageBody.trim().equalsIgnoreCase("."))
                    cgeneral.sendSMS(cgeneral.phoneNumber, ms.messageBody);
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid){
            Toast.makeText(getApplicationContext(), "Resultado - Nº: "+cgeneral.phoneNumber +" - Posición: "+ms.messageBody, Toast.LENGTH_SHORT).show();
        }
    }
}
