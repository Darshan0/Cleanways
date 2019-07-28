package com.chronos.cleanway;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.mapping.PositionIndicator;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.routing.RouteManager;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {


    private Map map;
    private SupportMapFragment mapFragment;
    public PositioningManager mPositionmanager;
    private double longitude;
    private double latitude;
    private FloatingActionButton floatingActionButton;
    private SensorManager sensorManager;
    public Sensor accelerometer;
    public Sensor gyroscope;
    private RoutePlan routePlan;
    private RouteManager rm;
    private RouteOptions routeOptions;
    private List<GeoCoordinate> potholelist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null){


            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("Location","\n"+latitude+"\n"+longitude);

        }






            floatingActionButton = (FloatingActionButton) findViewById(R.id.Sensor);
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
           // potholelist = new ArrayList<GeoCoordinate>();
            //potholelist.add(new GeoCoordinate(18.926457,72.833705));
            //potholelist.add(new GeoCoordinate(18.934457,72.833905));
            //potholelist.add(new GeoCoordinate(18.924186,72.8332014));

            //18.9320699 72.8392705
            rm= new RouteManager();
            routePlan = new RoutePlan();
            routeOptions = new RouteOptions();



            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
            mapFragment.init(new OnEngineInitListener() {


            @Override
            public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                if (error == OnEngineInitListener.Error.NONE) {


                    map = mapFragment.getMap();
                    map.setCenter(new GeoCoordinate(18.92979, 72.83377),
                            Map.Animation.NONE);

                    Log.d("Location","\n"+latitude+"\n"+longitude);

                    map.setZoomLevel(map.getMaxZoomLevel() - map.getMinZoomLevel());
                    map.getPositionIndicator().setVisible(true);
                    map.getPositionIndicator().setAccuracyIndicatorVisible(true);

                    Image image = new Image();
                    try {
                        image.setImageResource(R.drawable.marker);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    map.getPositionIndicator().setMarker(image);

                } else {
                    System.out.println("ERROR: Cannot initialize Map Fragment");
                }
            }
        });


      //  routePlan.addWaypoint(new GeoCoordinate(18.9320699,72.8392705));
        //routePlan.addWaypoint(new GeoCoordinate(18.9294256,72.8319897));
        //routeOptions.setTransportMode(RouteOptions.TransportMode.PEDESTRIAN);
        //routeOptions.setRouteType(RouteOptions.Type.FASTEST);
        //routePlan.setRouteOptions(routeOptions);


        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                sensorManager.registerListener(MainActivity.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(MainActivity.this,gyroscope,SensorManager.SENSOR_DELAY_NORMAL);

                routePlan.addWaypoint(new GeoCoordinate(18.92979,72.83377));

                routePlan.addWaypoint(new GeoCoordinate(18.92369,72.83194));
                routeOptions.setTransportMode(RouteOptions.TransportMode.CAR);
                routeOptions.setRouteType(RouteOptions.Type.SHORTEST);
                routePlan.setRouteOptions(routeOptions);

                rm.calculateRoute(routePlan, new RouteListener());
            }
        });


    }




    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        Sensor sensor = sensorEvent.sensor;
        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            Log.d("ACC_READINGS","X:"+sensorEvent.values[0]+"Y:"+sensorEvent.values[1]+"Z:"+sensorEvent.values[2]);
            if (sensorEvent.values[0]>=20.230202 && sensorEvent.values[1]>=20.230202);
            {
                Image image = new Image();
                try {
                    image.setImageResource(R.drawable.pothole);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                MapMarker m1 = new MapMarker(new GeoCoordinate(18.92702,72.83392));
                MapMarker m2 = new MapMarker(new GeoCoordinate(18.92561,72.83288));
                MapMarker m3 = new MapMarker(new GeoCoordinate(18.92393,72.83203));
                MapMarker m4 = new MapMarker(new GeoCoordinate(18.92369,72.83194));
                map.addMapObject(m1);
                map.addMapObject(m2);
                map.addMapObject(m3);
                map.addMapObject(m4);


            }

        }else if (sensor.getType() == Sensor.TYPE_GYROSCOPE){

            Log.d("GYRO_READINGS","X:"+sensorEvent.values[0]+"Y:"+sensorEvent.values[1]+"Z:"+sensorEvent.values[2]);
        }



    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {



    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        Log.d("Location","\n"+latitude+"\n"+longitude);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    private class RouteListener implements RouteManager.Listener{


        @Override
        public void onProgress(int i) {

        }

        @Override
        public void onCalculateRouteFinished(RouteManager.Error error, List<RouteResult> list) {

            if(error == RouteManager.Error.NONE){

                MapRoute mapRoute = new MapRoute(list.get(0).getRoute());
                map.addMapObject(mapRoute);
            }

        }
    }
}