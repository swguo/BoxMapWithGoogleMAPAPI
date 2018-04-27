package com.cmrdb.gpswithgoogleapi;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;


/*

取得自己的 Android 裝置的定位
http://oldgrayduck.blogspot.tw/2016/06/android-studio-android.html
 */

/*
gradle加
compile 'com.google.android.gms:play-services-location:8.1.0'

AndroidManifest加
<!-- 精確定位，就是 GPS -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<!-- 約略定位，就是 WI-FI -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
 */
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected double mLatitudeText;
    protected double mLongitudeText;

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    protected static final String TAG = "MainActivity";

    private MapView mapView;

    Button button;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoicGVpeXVud3UiLCJhIjoiY2pmaWEwa2xnMDhlejJ4dGYxaWQ2aTUyOCJ9.Iv3TbEGDsV_-eo-l86iA7A");
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        button = findViewById(R.id.button);


        buildGoogleApiClient();

        Intent intent=new Intent(MainActivity.this,GPSUpdateService.class);
        startService(intent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh(savedInstanceState);
            }
        });

    }
    protected void refresh(Bundle savedInstanceState){
        Mapbox.getInstance(this, "pk.eyJ1IjoicGVpeXVud3UiLCJhIjoiY2pmaWEwa2xnMDhlejJ4dGYxaWQ2aTUyOCJ9.Iv3TbEGDsV_-eo-l86iA7A");

        mapView.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = findViewById(R.id.mapView);
        mapView.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.addMarker(new MarkerOptions()
                        .position(new LatLng(mLatitudeText,mLongitudeText))
                        .title(getString(R.string.draw_marker_options_title))
                        .snippet(getString(R.string.draw_marker_options_snippet)));
            }
        });
    }

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
    }


    // 當 GoogleApiClient 連上 Google Play Service 後要執行的動作
    @Override
    public void onConnected(Bundle connectionHint)
    {
        // 這行指令在 IDE 會出現紅線，不過仍可正常執行，可不予理會
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null)
        {
            /*mLatitudeText.setText(String.format("%s: %f", mLatitudeLabel, mLastLocation.getLatitude()));
            mLongitudeText.setText(String.format("%s: %f", mLongitudeLabel, mLastLocation.getLongitude()));*/
            mLatitudeText =mLastLocation.getLatitude();
            mLongitudeText =mLastLocation.getLongitude();
        }
        else
        {
            Toast.makeText(this, "偵測不到定位，請確認定位功能已開啟。", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause)
    {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

}
