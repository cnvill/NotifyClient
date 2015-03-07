package com.cn.notifyserver.BD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by CN on 19/10/2014.
 */
public class DataBaseManager {

    private DbHelper helper;
    private SQLiteDatabase db;

    public DataBaseManager(Context ctx){
        helper= new DbHelper(ctx);
        db=helper.getReadableDatabase();
    }

    //Create table Position
    public static final String tableNamePosition="tposition";
    public static final String ptId="_id";
    public static final String ptName="nombre";
    public static final String ptTelefono="telefono";
    public static final String ptEstado="estado";

    public static final String createTablePosition="create table "+tableNamePosition+ "("
            + ptId + " integer primary key autoincrement, "
            + ptName + " text not null, "
            + ptTelefono+ " text null, "
            + ptEstado +" integer );";

    public ContentValues generarContentValues(String nombre, String telefono){
        ContentValues valores= new ContentValues();
        valores.put(ptName, nombre);
        valores.put(ptTelefono, telefono);
        valores.put(ptEstado, "1");
        return valores;
    }

    public void insertar(String nombre, String telefono){
        db.insert(tableNamePosition, null ,generarContentValues(nombre, telefono));
    }

    public void eliminar(String id)
    {
        db.delete(tableNamePosition, ptId + "=?", new String[]{id});
    }

    public void Update(String id, String nombre, String telefono){
        db.update(tableNamePosition, generarContentValues(nombre, telefono),  ptId+"=?", new String[]{nombre, telefono });
    }

    public Cursor cargarCursorContactos(){
        String[] columnas= new String[]{ptId, ptName, ptTelefono, ptEstado};
        return db.query(tableNamePosition, columnas, null, null, null, null, null);
    }

    public Cursor buscarContacto(String nombre){

        String[] columnas= new String[]{ ptId, ptName, ptTelefono, ptEstado};
        /*try {
            Thread.sleep(1000);
        } catch (Exception e) {
                e.printStackTrace();
        }*/

        return db.query(tableNamePosition, columnas, ptName+" like ?", new String[]{ "%"+nombre+"%" }, null, null, null);

    }

    public Cursor buscarContactoId(String id){

        String[] columnas= new String[]{ ptId, ptName, ptTelefono, ptEstado};
        return db.query(tableNamePosition, columnas, ptId+"=?", new String[]{id}, null, null, null);
    }
    
    public boolean ContactoExiste(String nroTelefono){
        boolean respt=false;
        String[] columnas= new String[]{ ptId, ptName, ptTelefono, ptEstado};
        Cursor c= db.query(tableNamePosition, columnas, ptTelefono+"=?", new String[]{ nroTelefono }, null, null, null);
        if(c.moveToFirst())
            respt=true;
            
        return respt;   
    }

    //End Table Position

    //Create Table Parameter

    public static final String tableNameParameter="tparameter";
    public static final String pId="_id";
    public static final String pCode="codigo";
    public static final String pValue="valor";
    public static final String pEstado="estado";


    public static final String createTableParameter="create table "+tableNameParameter+ "("
            + pId + " integer primary key autoincrement, "
            + pCode + " text not null, "
            + pValue+ " text not null, "
            + pEstado +" integer );";

    public ContentValues generarParameterContentValues(String code, String value){
        ContentValues valores= new ContentValues();
        valores.put(pCode, code);
        valores.put(pValue, value);
        valores.put(pEstado, "1");
        return valores;
    }

    public void insertarParameter(String code, String value){
        db.insert(tableNameParameter, null ,generarParameterContentValues(code, value));
    }

    public boolean GetConfigNotify(String code){
        boolean respt=false;
        String[] columnas= new String[]{ pId, pCode, pValue, pEstado};
        Cursor c= db.query(tableNameParameter, columnas, pCode+"=?", new String[]{ code }, null, null, null);
        if(c.moveToFirst())
            respt=true;
            
        return respt;   
    }

    public String GetConfigNotifyOption(String code){
        String respt="";
        String[] columnas= new String[]{ pId, pCode, pValue, pEstado};
        Cursor c= db.query(tableNameParameter, columnas, pCode+"=?", new String[]{ code }, null, null, null);
        if(c.moveToFirst())
            respt = c.getString(2);
            
        return respt;   
    }

    //End Table Parameter 
   
	
}
