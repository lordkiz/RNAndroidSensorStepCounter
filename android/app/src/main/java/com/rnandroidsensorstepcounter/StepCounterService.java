package com.rnandroidsensorstepcounter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class StepCounterService extends Service implements SensorEventListener {
    int steps = 0;
    static StepCounterService instance = null;
    SensorManager sensorManager;
    Sensor sensor;
    Intent intent;


    public static boolean isServiceRunning() {
        // if service is on, it's instance should not be null and it can ping
        try {
            return instance != null && instance.ping();
        } catch (NullPointerException e) {
            return false;
        }
    }

    private boolean ping() {
        //If service is on it should be able to ping
        return true;
    }

    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @Override
    public void  onCreate() {
        super.onCreate();
        instance = this;
        intent = new Intent();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId ) {
        super.onStartCommand(intent, flags, startId);

        sensorManager = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            sensorManager.registerListener(this, sensor, 10);
        }
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
        sensorManager.unregisterListener(this, sensor);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        WritableMap stepObject = Arguments.createMap();
        stepObject.putDouble("steps", event.values[0]);
        Log.i("steps", Double.toString(event.values[0]));
        intent.putExtra("steps", event.values[0]);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        sendEvent(RNAndroidSensorStepCounterModule.reactContext, "steps", stepObject);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
