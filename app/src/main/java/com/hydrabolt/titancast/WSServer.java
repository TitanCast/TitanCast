package com.hydrabolt.titancast;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * Created by Amish on 01/05/2015.
 */
public class WSServer extends WebSocketServer {

    private static Context context;
    private static ArrayList < WebSocket > socketList;
    private static String[] empty = {};
    private static String deviceDetails;
    private static ArrayList < String > deviceDetailsRaw;

    private static WebSocket acceptedWebSocket;

    public WSServer(InetSocketAddress address, Context c) {
        super(address);

        socketList = new ArrayList < WebSocket > ();

        context = c;

        deviceDetailsRaw = new ArrayList < String > ();
        deviceDetailsRaw.add("device_model=" + Build.MODEL);
        deviceDetailsRaw.add("device_manufacturer=" + Build.MANUFACTURER);
        deviceDetailsRaw.add("device_android_version=" + Build.VERSION.SDK_INT);
        deviceDetailsRaw.add("titan_cast_version=" + Details.getAppVersion());
        deviceDetailsRaw.add("has_vibrator=" + ((Vibrator) c.getSystemService(c.VIBRATOR_SERVICE)).hasVibrator());

        SensorManager sm = (SensorManager) c.getSystemService(Context.SENSOR_SERVICE);

        deviceDetailsRaw.add("has_accelerometer=" + (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null));
        deviceDetailsRaw.add("has_gyroscope=" + (sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null));
        deviceDetailsRaw.add("has_ambient_temperature=" + (sm.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null));
        deviceDetailsRaw.add("has_gravity=" + (sm.getDefaultSensor(Sensor.TYPE_GRAVITY) != null));
        deviceDetailsRaw.add("has_heart_rate=" + (sm.getDefaultSensor(Sensor.TYPE_HEART_RATE) != null));
        deviceDetailsRaw.add("has_light=" + (sm.getDefaultSensor(Sensor.TYPE_LIGHT) != null));
        deviceDetailsRaw.add("has_magnetic_field=" + (sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null));
        deviceDetailsRaw.add("has_pressure=" + (sm.getDefaultSensor(Sensor.TYPE_PRESSURE) != null));
        deviceDetailsRaw.add("has_proximity=" + (sm.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null));
        deviceDetailsRaw.add("has_relative_humidity=" + (sm.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null));
        deviceDetailsRaw.add("has_rotation=" + (sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) != null));
        deviceDetailsRaw.add("has_significant_motion=" + (sm.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION) != null));
        deviceDetailsRaw.add("has_step_counter=" + (sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null));
        deviceDetailsRaw.add("has_step_detector=" + (sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null));

        deviceDetails = PacketSerializer.generatePacket("device_details", deviceDetailsRaw);
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

        if (socketList.size() > 10) {
            webSocket.close(0);
        }

        webSocket.send(deviceDetails);
        socketList.add(webSocket);
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        socketList.remove(webSocket);
        if (webSocket == acceptedWebSocket) {
            terminateActive();
            CastActivity.close();
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {

        ArrayList < String > msg = PacketSerializer.parsePacket(s);
        String type = msg.get(0);
        boolean conn = acceptedWebSocket == webSocket;

        Log.d("MOOO", Integer.toString(msg.size()));
        Log.d("MOOO", msg.get(0));

        switch(type){

            case "request_connect":
                if(msg.size() != 3) return;

                if(conn){
                    webSocket.send(PacketSerializer.generatePacket("already_connected", empty));
                }else{
                    Intent intent = new Intent(context, RequestConnectScreen.class);
                    intent.putExtra("app_name", msg.get(1));
                    intent.putExtra("app_desc", msg.get(2));
                    intent.putExtra("client_id", socketList.indexOf(webSocket));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                break;

            case "cast_view_data":

                if(Details.haveViewData || msg.size() != 2) return;

                String url = msg.get(1);
                Intent intent = new Intent(context, CastActivity.class);
                intent.putExtra("url", msg.get(1));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                break;

            case "custom_data":
                if (msg.size() != 2) return;

                final ArrayList < String > lmsg = msg;

                CastActivity.h.post(new Runnable() {

                    @Override
                    public void run() {
                        CastActivity.sendCustom(lmsg.get(1));
                    }
                });
                break;
            case "enable_accelerometer":

                if(Details.haveViewData){
                    CastActivity.getSensorManager().enableAccelerometerSensor();
                }

                break;
            case "disable_accelerometer":

                if(Details.haveViewData){
                    CastActivity.getSensorManager().disableAccelerometerSensor();
                }
                break;
            case "set_accelerometer_speed":
                if(Details.haveViewData && msg.size() == 2){
                    int speed = SensorManager.SENSOR_DELAY_NORMAL;

                    if(msg.get(1).equals("game")) speed = SensorManager.SENSOR_DELAY_GAME;
                    if(msg.get(1).equals("fastest")) speed = SensorManager.SENSOR_DELAY_FASTEST;
                    if(msg.get(1).equals("ui")) speed = SensorManager.SENSOR_DELAY_UI;

                    CastActivity.getSensorManager().setDelay(speed);
                }
                break;
            case "set_accelerometer_cap":
                if(Details.haveViewData && msg.size() == 2){
                    CastActivity.getSensorManager().setAccelerometerCap(Integer.parseInt(msg.get(1)));
                }
                break;
            default:
                webSocket.send( PacketSerializer.generatePacket("unknown_command", empty) );
                break;

        }

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    public static void rejectRequest(int index) {
        socketList.get(index).send(PacketSerializer.generatePacket("reject_connect_request", empty));
        socketList.get(index).close(0);
    }

    public static void acceptRequest(int index) {
        socketList.get(index).send(PacketSerializer.generatePacket("accept_connect_request", empty));
        Details.connected = true;
        acceptedWebSocket = socketList.get(index);
    }

    public static void terminateActive() {
        socketList.remove(acceptedWebSocket);
        acceptedWebSocket.close(0);
        acceptedWebSocket = null;
        Details.connected = false;
        Details.haveViewData = false;
    }

    public static void sendToActive(String data) {

        if(acceptedWebSocket == null || !Details.connected){
            return;
        }

        String[] dat = {
                data
        };
        try {
            acceptedWebSocket.send(PacketSerializer.generatePacket("custom_data", dat));
        }catch(UnknownError err){

        }
    }

    public static void sendPacketToActive(String data) {
        acceptedWebSocket.send(data);
    }
}