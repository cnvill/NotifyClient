package com.cn.notifyserver.BD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by CN on 19/10/2014.
 */
public class DataBaseManager {

    public static final String tableName="tposition";

    public static final String ptId="_id";
    public static final String ptName="nombre";
    public static final String ptTelefono="telefono";

    //public static final String ptLatitud="latitud";
    //public static final String ptLongitud="longitud";
    //public static final String ptFechaRegistro="fecharegistro";

    public static final String ptEstado="estado";

    public static final String createTable="create table "+tableName+ "("
            + ptId + " integer primary key autoincrement, "
            + ptName + " text not null, "
            + ptTelefono+ " text null, "
            + ptEstado +" integer );";

    private DbHelper helper;
    private SQLiteDatabase db;

    public DataBaseManager(Context ctx){
        helper= new DbHelper(ctx);
        db=helper.getReadableDatabase();
    }

    public ContentValues generarContentValues(String nombre, String telefono){
        ContentValues valores= new ContentValues();
        valores.put(ptName, nombre);
        valores.put(ptTelefono, telefono);
        valores.put(ptEstado, "1");
        return valores;
    }

    public void insertar(String nombre, String telefono){
        db.insert(tableName, null ,generarContentValues(nombre, telefono));
    }

    public void eliminar(String id)
    {
        db.delete(tableName, ptId + "=?", new String[]{id});
    }

    public void Update(String id, String nombre, String telefono){
        db.update(tableName, generarContentValues(nombre, telefono),  ptId+"=?", new String[]{nombre, telefono });
    }

    public Cursor cargarCursorContactos(){
        String[] columnas= new String[]{ptId, ptName, ptTelefono, ptEstado};
        return db.query(tableName, columnas, null, null, null, null, null);
    }

    public Cursor buscarContacto(String nombre){

        String[] columnas= new String[]{ ptId, ptName, ptTelefono, ptEstado};
		/*try {
			Thread.sleep(1000);
		} catch (Exception e) {
				e.printStackTrace();
		}*/

        return db.query(tableName, columnas, ptName+" like ?", new String[]{ "%"+nombre+"%" }, null, null, null);

    }

    public Cursor buscarContactoId(String id){

        String[] columnas= new String[]{ ptId, ptName, ptTelefono, ptEstado};
        return db.query(tableName, columnas, ptId+"=?", new String[]{id}, null, null, null);
    }
	
	public boolean ContactoExiste(String nroTelefono){
		boolean respt=false;
        String[] columnas= new String[]{ ptId, ptName, ptTelefono, ptEstado};
        Cursor c= db.query(tableName, columnas, ptTelefono+"=?", new String[]{ nroTelefono }, null, null, null);
  		if(c.moveToFirst())
			respt=true;
			
		return respt;	
	}
	
}
