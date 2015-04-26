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
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
 	boolean isServerClient=false;
	GeneralCn cgeneral;
    MiServicioGps ms;
    private Timer timer;

    AlertDialog.Builder dlConfirmacion;
    AlertDialog alertActiveGPS = null;

    Button btnSiguiente;
    RadioButton rbtnServidor, rbtnCliente;
	TextView lblNotifyMe;
    String messageTitle="Estas seguro de configurar como Cliente?";

    private DataBaseManager manager;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        manager= new DataBaseManager(this);
		timer= new Timer();
		
		if(manager.GetConfigNotify("config"))
		{
		
			if("servidor".equalsIgnoreCase(manager.GetConfigNotifyOption("config"))){
                Intent serverActivity= new Intent(this, ServerActivity.class);
                startActivity(serverActivity);
				this.finish();
            }
            else{
				
				setContentView(R.layout.main);
				lblNotifyMe=(TextView)findViewById(R.id.helloNotify);
				btnSiguiente= (Button) findViewById(R.id.btnsiguiente);
				rbtnServidor= (RadioButton) findViewById(R.id.rbtnServidor);
				rbtnCliente= (RadioButton) findViewById(R.id.rbtnCliente);
				btnSiguiente.setVisibility(View.INVISIBLE);
				rbtnServidor.setVisibility(View.INVISIBLE);
				rbtnCliente.setVisibility(View.INVISIBLE);
				lblNotifyMe.setText("Notify Me, Ejecutando en segundo plano ...");
				
                
                    ms = new MiServicioGps(MainActivity.this.getApplicationContext());
                    if(!ms.gpsActivo){
						AlertNoGps();
						return;                        
					}
                    if(cgeneral==null)
                        cgeneral= new GeneralCn(this);
               		
                    EscucharMensaje update= new EscucharMensaje();
					timer.scheduleAtFixedRate(update, 0, 1000);

            }
		}
		else{

			setContentView(R.layout.main);
			lblNotifyMe=(TextView)findViewById(R.id.helloNotify);
            btnSiguiente= (Button) findViewById(R.id.btnsiguiente);
            rbtnServidor= (RadioButton) findViewById(R.id.rbtnServidor);
            rbtnCliente= (RadioButton) findViewById(R.id.rbtnCliente);
			lblNotifyMe.setText("Notify Me");
			
			dlConfirmacion = new AlertDialog.Builder(this);
			
            dlConfirmacion.setTitle(".:: Aviso");
            dlConfirmacion.setCancelable(false);

            AlertDialog.Builder aceptar = dlConfirmacion.setPositiveButton("Aceptar", 
			new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int id) {
                    if(!isServerClient){

                        ms = new MiServicioGps(MainActivity.this.getApplicationContext());
                         if(!ms.gpsActivo)
							 AlertNoGps();
                        else{    
							manager.insertarParameter("config", "cliente");
                        	cgeneral= new GeneralCn(MainActivity.this);
                        	ms.setCoordenadas();

							EscucharMensaje update= new EscucharMensaje();
							timer.scheduleAtFixedRate(update, 0, 1000);

                        	btnSiguiente.setVisibility(View.INVISIBLE);
                        	rbtnServidor.setVisibility(View.INVISIBLE);
                        	rbtnCliente.setVisibility(View.INVISIBLE);
						}
                    }
                    else{

                        manager.insertarParameter("config", "servidor");
                        Intent serverActivity= new Intent(MainActivity.this, ServerActivity.class);
                        startActivity(serverActivity);
						MainActivity.this.finish();
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

      private void AlertNoGps() {
		  final AlertDialog.Builder builderGps = new AlertDialog.Builder(this);
		  builderGps.setMessage("El sistema GPS esta desactivado, se tiene que activar")
			  .setCancelable(false)
			  .setPositiveButton("Ajustes", new DialogInterface.OnClickListener() {
				  public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
					  startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					  }
			  }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
				  public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id){
					  alertActiveGPS.show();
					  //AlertNoGps();			  
				  }
			  });
		  alertActiveGPS = builderGps.create();
		  alertActiveGPS.show();           
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
	

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    class EscucharMensaje extends TimerTask{
    	@Override
		public void run() {
          try{
			  cgeneral.readClientSMS();
			  ms.setCoordenadas();
			  if(cgeneral.messageBody.equalsIgnoreCase(".")){
				  cgeneral.sendSMS(cgeneral.phoneNumber, cgeneral.phoneNumber+"|"+ms.positionBody);
				  cgeneral.messageBody="";
			  }
				  
		  }catch(Exception ex){
			  Toast.makeText(getApplicationContext(), ""+ex.getMessage(), Toast.LENGTH_SHORT).show();
		  }
        }
    }
}
