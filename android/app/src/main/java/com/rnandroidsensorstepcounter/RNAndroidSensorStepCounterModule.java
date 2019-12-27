package com.rnandroidsensorstepcounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import javax.annotation.Nonnull;

import androidx.annotation.Nullable;

public class RNAndroidSensorStepCounterModule extends ReactContextBaseJavaModule {
    public final String MODULE_NAME = "RNAndroidSensorStepCounter";
    float steps = 0;
    public static ReactApplicationContext reactContext;
    BroadcastReceiver broadcastReceiver;
    WritableMap params = Arguments.createMap();

    public RNAndroidSensorStepCounterModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        reactContext = reactApplicationContext;
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
    }


    private void sendEvent(ReactContext reactContext, String eventName, @Nullable WritableMap params) {
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @Nonnull
    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @ReactMethod
    public void ready(Promise promise) {
        try {
            boolean isReady = StepCounterService.isServiceRunning();
            promise.resolve(isReady);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @ReactMethod
    public void startService() {
        if (!StepCounterService.isServiceRunning()) {
            reactContext.startService(new Intent(reactContext, StepCounterService.class));
        }
    }

    @ReactMethod
    public void stopService() {
        if (StepCounterService.isServiceRunning()) {
            reactContext.stopService(new Intent(reactContext, StepCounterService.class));
        }
    }

    @ReactMethod
    public void getSteps() {
        params.putDouble("steps", (double) steps);
        sendEvent(reactContext, "steps", params);
    }
}
