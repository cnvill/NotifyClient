package com.cn.notifyserver;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.cn.notifyserver.BD.DataBaseManager;

public class NewContact extends Activity
{
    EditText txtNombre, txtTelefono;
    private DataBaseManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newcontact);

        txtNombre= (EditText) findViewById(R.id.txtNombreContacto);
        txtTelefono= (EditText) findViewById(R.id.txtNumeroContacto);
        manager= new DataBaseManager(this);
    }

    public void RegistrarContacto(View v){
        if(txtNombre.getText().toString().trim().length()==0) {
            Toast.makeText(getApplicationContext(), "Ingrese el nombre", Toast.LENGTH_SHORT).show();
            return;
        }

        if(txtTelefono.getText().toString().trim().length()==0) {
            Toast.makeText(getApplicationContext(), "Ingrese el número", Toast.LENGTH_SHORT).show();
            return;
        }
		
		if(!manager.ContactoExiste(txtTelefono.getText().toString())){
			manager.insertar(txtNombre.getText().toString(), txtTelefono.getText().toString());
			Intent intent= getIntent();
			setResult(1, intent);
			finish();	
		}
		else
			Toast.makeText(getApplicationContext(), "Ya existe un contacto con el mismo Nº de telefono", Toast.LENGTH_SHORT).show();
		
        
    }
}
