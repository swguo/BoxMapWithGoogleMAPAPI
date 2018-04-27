package com.cmrdb.gpswithgoogleapi;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class GPSUpdateService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;

    String mLatitudeLabel = "緯度";
    String mLongitudeLabel = "經度";

    Handler mHandler;
    final Runnable runnable = new Runnable() {
        public void run() {
            // TODO Auto-generated method stub
            // 需要背景作的事
            mHandler.postDelayed(runnable, 500); //0.5秒
            Log.d("WHERE", "runnable");
            // 這行指令在 IDE 會出現紅線，不過仍可正常執行，可不予理會
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null)
            {
                Log.d("location",String.format("%s: %f", mLatitudeLabel, mLastLocation.getLatitude())+", "+String.format("%s: %f", mLongitudeLabel, mLastLocation.getLongitude()));
            }
            else
            {
                Log.d("location","偵測不到定位，請確認定位功能已開啟。");
            }
        }
    };

    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public GPSUpdateService() {
        Log.d("WHERE", "GPSUpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("WHERE", "onCreate");

        mHandler = new Handler();
        mHandler.post(runnable);

        buildGoogleApiClient();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("WHERE", "onStartCommand");
        mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
    }

    // 當 GoogleApiClient 連上 Google Play Service 後要執行的動作
    @Override
    public void onConnected(Bundle connectionHint)
    {
//        // 這行指令在 IDE 會出現紅線，不過仍可正常執行，可不予理會
//        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        if (mLastLocation != null)
//        {
//            Log.d("location",String.format("%s: %f", mLatitudeLabel, mLastLocation.getLatitude())+", "+String.format("%s: %f", mLongitudeLabel, mLastLocation.getLongitude()));
//        }
//        else
//        {
//            Log.d("location","偵測不到定位，請確認定位功能已開啟。");
//        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        Log.i("where", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause)
    {
        Log.i("where", "Connection suspended");
        mGoogleApiClient.connect();
    }
}
