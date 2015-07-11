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

    private static Activity a;

    private static SensorManager sensorManager;
    private static int accelerometerCap = 1000;
    private static long accelerometerCapLast = System.currentTimeMillis();

    public TCSensorManager(Activity a){

        this.a = a;

        sensorManager = (android.hardware.SensorManager) a.getSystemService(Context.SENSOR_SERVICE);

        if(deviceHasSensor(Sensor.TYPE_ACCELEROMETER)){
            accelerometerSensor = new AccelerometerSensor();
            sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

    }

    public static boolean deviceHasSensor(int sensorType){
        return (sensorManager.getDefaultSensor(sensorType) != null);
    }

    //ALL
        public static void unregisterAll(){
            if(accelerometerSensorEnabled == 1) {
                disableAccelerometerSensor();
                accelerometerSensorEnabled = 2;
            }
        }

        public static void registerAll(){
            if(accelerometerSensorEnabled == 2) {
                enableAccelerometerSensor("registerAll");
            }
        }

    //ACCELEROMETER
        public static void enableAccelerometerSensor(String reason){
            if(accelerometerSensor != null && !(accelerometerSensorEnabled==1)){
                Log.d("AccSensor-ENABLED", reason);
                accelerometerSensorEnabled = 1;
                sensorManager.registerListener(accelerometerSensor, sensorAccelerometer, accelerometerDelay);
            }
        }
        public static void disableAccelerometerSensor(){
            if(accelerometerSensor != null && accelerometerSensorEnabled==1){
                accelerometerSensorEnabled = 0;
                sensorManager.unregisterListener(accelerometerSensor);
            }
        }
        public static boolean accelerometerEnabled(){
            return accelerometerSensorEnabled==1;
        }

        public static void setDelay(int s){
            disableAccelerometerSensor();
            accelerometerDelay = s;
            enableAccelerometerSensor("setDelay");
        }

        public static void accelerometerUpdate(float x, float y, float z){

            if(System.currentTimeMillis() - accelerometerCapLast > accelerometerCap) {
                accelerometerCapLast = System.currentTimeMillis();

                String data[] = {
                        "x=" + Float.toString(x), "y=" + Float.toString(y), "z=" + Float.toString(z)
                };

                MainActivity.getServer().sendPacketToActive(PacketSerializer.generatePacket("accelerometer-update", data));
            }
        }

    public static void setAccelerometerCap(int cap) {
        accelerometerCap = cap;
        Log.d("t", String.valueOf(accelerometerCap));
    }

    //
}
