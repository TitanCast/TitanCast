package com.hydrabolt.titancast.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.hydrabolt.titancast.CastActivity;

/**
 * Created by Amish on 03/05/2015.
 */
public class AccelerometerSensor implements SensorEventListener {

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;

        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            CastActivity.getSensorManager().accelerometerUpdate(x, y, z);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
