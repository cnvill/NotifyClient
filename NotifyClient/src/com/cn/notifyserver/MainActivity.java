package com.cn.notifyserver;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.cn.notifyserver.Class.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    GeneralCn cgeneral;
    long timeInMilliseconds = 0L;
    MiServicioGps ms;
    private long startTime = 0L;
    private Handler customHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ms= new MiServicioGps(MainActivity.this.getApplicationContext());
        cgeneral= new GeneralCn(this);
        ms.setCoordenadas();
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 100);
    }


    private Runnable updateTimerThread = new Runnable() {
        public void run() {

            new EscucharMensaje().execute();
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            customHandler.postDelayed(this, 20000);
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
            Toast.makeText(getApplicationContext(), "Busqueda finalizada mensaje: "+cgeneral.phoneNumber +" Posición: "+ms.messageBody, Toast.LENGTH_SHORT).show();
        }
    }
}
