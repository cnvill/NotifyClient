package com.cn.notifyserver.Class;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;


/**
 * Created by CN on 16/10/2014.
 */
public class MiServicioGps extends Service implements LocationListener {
    private final Context ctx;
    double latitud;
    double longitud;
    LocationManager locationManager;
    Location location;
    boolean gpsActivo;
    public String messageBody;

    public MiServicioGps()
    {
        super();
        this.ctx=this.getApplicationContext();
    }

    public MiServicioGps(Context c)
    {
        super();
        this.ctx = c;
        getLocation();
    }

    public void setCoordenadas()
    {
        messageBody= latitud+"|"+longitud ;
    }

    public void getLocation(){

        try {
            locationManager =(LocationManager)this.ctx.getSystemService(LOCATION_SERVICE);
            gpsActivo=locationManager.isProviderEnabled(locationManager.GPS_PROVIDER);

        } catch (Exception e) {
            // TODO: handle exception
        }

        if(gpsActivo){
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER , 1500, 10, this);
            //obteniendo la ultima coordenada conocida
            location= locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            if(location != null){
                latitud = location.getLatitude();
                longitud = location.getLongitude();
            }
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        //Actualización de coordenadas
        if(location != null){
            latitud=location.getLatitude();
            longitud=location.getLongitude();
            messageBody=latitud+"|"+longitud ;
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }



}
