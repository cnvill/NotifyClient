package com.cn.notifyserver.Class;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.gsm.SmsManager;
import android.widget.Toast;
import android.util.Log;

/**
 * Created by CN on 19/10/2014.
 */
public class GeneralCn {

    private Context ctx;
	public String phoneNumber;
	public String messageBody;
	
    public GeneralCn( Context ctx){
        this.ctx=ctx;
    }

    public void sendSMS(String phoneNumber, String message){
        try {

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);

        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText( ctx.getApplicationContext(),"Error al enviar sms "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public void sendSMS(String phoneNumber){
        try {

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, messageBody, null,null);

        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText( ctx.getApplicationContext(),"Error al enviar sms "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String readSMS(){

        String smsContenido="";
        try {

            Uri uri = Uri.parse("content://sms/inbox");
            Cursor cursor = ctx.getContentResolver().query(uri, new String[] { "_id", "thread_id", "address", "person", "date", "body" }, null,null,null);
            String address="",body="",time="";
            long timestamp=0;
            if (cursor != null)
            {
                try
                {
                    int count = cursor.getCount();
                    if (count > 0)
                    {
                        cursor.moveToFirst();
                        long messageId = cursor.getLong(0);
                        long threadId = cursor.getLong(1);
                        address = cursor.getString(2);
                        long contactId = cursor.getLong(3);
                        String contactId_string = String.valueOf(contactId);
                        body = cursor.getString(5);
						if(body.trim().equalsIgnoreCase(".")){
							
							Log.i("Meesage", "Delete message"+messageId);
							ctx.getContentResolver().delete(Uri.parse("content://sms/"+messageId), "date=?", 
							new String[] {cursor.getString(4) });	
						}

                    }
                }
				
                finally { cursor.close(); }
                smsContenido= address+ "|" + body;
				phoneNumber=address;
				messageBody=body;
            }


            return smsContenido;
        } catch (Exception e) {
            Toast.makeText( ctx.getApplicationContext(),"Error al leer SMS"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return smsContenido;
    }
}
