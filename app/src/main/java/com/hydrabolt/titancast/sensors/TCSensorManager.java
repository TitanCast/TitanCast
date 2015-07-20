package com.hydrabolt.titancast.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Log;

import com.hydrabolt.titancast.MainActivity;
import com.hydrabolt.titancast.PacketSerializer;

/**
 * Created by Amish on 04/05/2015.
 */
public class TCSensorManager {

    private static int
            accelerometerSensorEnabled = 0;

    private static int
            accelerometerDelay = SensorManager.SENSOR_DELAY_NORMAL;


    private static AccelerometerSensor accelerometerSensor;
    private static Sensor sensorAccelerometer;

    private static SensorManager sensorManager;

    public TCSensorManager(Activity a) {

        sensorManager = (android.hardware.SensorManager) a.getSystemService(Context.SENSOR_SERVICE);

        if (deviceHasSensor(Sensor.TYPE_ACCELEROMETER)) {
            accelerometerSensor = new AccelerometerSensor();
            sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

    }

    public static boolean deviceHasSensor(int sensorType) {
        return (sensorManager.getDefaultSensor(sensorType) != null);
    }

    //ALL
    public static void unregisterAll() {
        if (accelerometerSensorEnabled == 1) {
            disableAccelerometerSensor();
            accelerometerSensorEnabled = 2;
        }
    }

    public static void registerAll() {
        if (accelerometerSensorEnabled == 2) {
            enableAccelerometerSensor("register all called");
        }
    }

    //ACCELEROMETER
    public static void enableAccelerometerSensor(String why) {
        if (accelerometerSensor != null && !(accelerometerSensorEnabled == 1)) {
            Log.d("titancast-accelerometer", "accelerometer enabled BECAUSE "+why);
            accelerometerSensorEnabled = 1;
            sensorManager.registerListener(accelerometerSensor, sensorAccelerometer, accelerometerDelay);
        }
    }

    public static void disableAccelerometerSensor() {
        if (accelerometerSensor != null && accelerometerSensorEnabled != 0) {
            Log.d("titancast-accelerometer", "accelerometer disabled");
            accelerometerSensorEnabled = 0;
            sensorManager.unregisterListener(accelerometerSensor);
        }
    }

    public static boolean accelerometerEnabled() {
        return accelerometerSensorEnabled == 1;
    }

    public static void setDelay(int s) {
        disableAccelerometerSensor();
        accelerometerDelay = s;
        enableAccelerometerSensor("delay was set");
    }

    public static void accelerometerUpdate(float x, float y, float z) {

        String data[] = {
                Float.toString(x),
                Float.toString(y),
                Float.toString(z)
        };

        MainActivity.getServer().sendPacketToActive(PacketSerializer.generatePacket("accelerometer-update", data));
    }
    //
}
