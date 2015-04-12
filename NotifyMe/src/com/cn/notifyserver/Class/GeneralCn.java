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
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor cursor = ctx.getContentResolver().query(uriSms, new String[] 
			{ "_id", "thread_id", "address", "person", "date", "body" }, 
			null, null, "_id desc limit 1");
			
            if (cursor != null)
            {
                try
                {
                    if (cursor.getCount()> 0)
                    {
                        cursor.moveToFirst();
                        phoneNumber = cursor.getString(2);
                        messageBody = cursor.getString(5);						
                    }

					smsContenido = phoneNumber+ "|" + messageBody;
					
                }
                finally { 
				cursor.close(); 
						
				}
				
            }

        } catch (Exception e) {
            Toast.makeText( ctx.getApplicationContext(),"Error al leer SMS"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return smsContenido;
    }
	

    public void readClientSMS(){
        try {
            Uri uriSms = Uri.parse("content://sms/inbox");
            Cursor cursor = ctx.getContentResolver().query(uriSms, new String[] 
					{ "_id", "thread_id", "address", "person", "date", "body" }, 
					null, null, "_id desc limit 1");
            long messageId=0;
			long threadId=0;
            if (cursor != null)
            {
                try
                {
                    if (cursor.getCount()> 0)
                    {
                        cursor.moveToFirst();
                        messageId = cursor.getLong(0);
                        phoneNumber = cursor.getString(2);
                        messageBody = cursor.getString(5);
					}
                }
                finally { 
					cursor.close(); 
					if(messageBody.equalsIgnoreCase(".")){
						ctx.getContentResolver().delete(Uri.parse("content://sms/"+messageId), null, null);
						ctx.getContentResolver().delete(Uri.parse("content://sms/conversations/"+threadId), null, null);
						ctx.getContentResolver().delete(Uri.parse("content://sms/inbox/"+messageId), null, null);
					}
				}
            }

        } catch (Exception e) {
            Toast.makeText( ctx.getApplicationContext(),"Error al leer SMS"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
