package com.hydrabolt.titancast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import com.hydrabolt.titancast.info_display.TitanCastNotification;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * Created by Amish on 01/05/2015.
 */
public class WSServer extends WebSocketServer {

    private static Context context;
    private static ArrayList<WebSocket> socketList;
    private static String[] empty = {};
    private static String deviceDetails;
    private static ArrayList<String> deviceDetailsRaw;
    private Activity castActivity;

    private static WebSocket acceptedWebSocket;

    public WSServer(InetSocketAddress address, Context c) {
        super(address);

        socketList = new ArrayList<WebSocket>();

        context = c;

        deviceDetailsRaw = new ArrayList<String>();
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

    public static void rejectRequest(int index) {
        try {
            socketList.get(index).send(PacketSerializer.generatePacket("reject_connect_request", empty));
            socketList.get(index).close(0);
        }catch(IndexOutOfBoundsException e){
            TitanCastNotification.showToast("You were disconnected from the application!", Toast.LENGTH_LONG);
        }
    }

    public static void acceptRequest(int index) {

        Log.d("titancast","accept");

        try {
            socketList.get(index).send(PacketSerializer.generatePacket("accept_connect_request", empty));
            Details.setConnected(true);
            acceptedWebSocket = socketList.get(index);
        }catch(IndexOutOfBoundsException e){
            Log.d("titancast-wsserver", "error - "+e.getLocalizedMessage());
            TitanCastNotification.showToast("You were disconnected from the application!", Toast.LENGTH_LONG);
        }
    }

    public static void terminateActive() {
        if(acceptedWebSocket != null){
            acceptedWebSocket.close(0);
            acceptedWebSocket = null;
            Details.setConnected(false);
            Details.setHasViewData(false);
            CastActivity.getSensorManager().disableAccelerometerSensor();
            CastActivity.getSensorManager().setDelay(10);
            TitanCastNotification.showToast("You were disconnected from the application!", Toast.LENGTH_LONG);
        }
    }

    public static void sendCustomDataToActive(String[] data) {

        if (acceptedWebSocket != null && Details.connected() && Details.hasViewData()) {
            sendPacketToActive(PacketSerializer.generatePacket("custom_data", data));
        }
    }

    public static void sendPacketToActive(String data) {
        if (acceptedWebSocket != null && Details.connected()) {
            acceptedWebSocket.send(data);
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

        if (socketList.size() > 0) {
            webSocket.close(0);
            return;
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

        ArrayList<String> msg = PacketSerializer.parsePacket(s);
        String type = msg.get(0);
        boolean conn = acceptedWebSocket == webSocket;
        boolean connectedAndInView = Details.hasViewData() && conn;

        switch (type) {

            case "request_connect":

                if (msg.size() == 4) {
                    if (conn) {
                        webSocket.send(PacketSerializer.generatePacket("already_connected", empty));
                    } else {
                        Intent intent = new Intent(context, RequestConnectScreen.class);
                        intent.putExtra("app_name", msg.get(1));
                        intent.putExtra("app_desc", msg.get(2));
                        intent.putExtra("client_id", socketList.indexOf(webSocket));
                        intent.putExtra("app_icon", msg.get(3));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
                break;

            case "cast_view_data":

                if (!Details.hasViewData() && msg.size() == 2) {
                    String url = msg.get(1);
                    Intent intent = new Intent(context, CastActivity.class);
                    intent.putExtra("url", msg.get(1));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    Details.setHasViewData(true);
                    Details.setConnected(true);

                    context.startActivity(intent);
                }
                break;

            case "custom_data":
                if (msg.size() == 2) {
                    final String toSend = msg.get(1);
                    CastActivity.h.post(new Runnable() {

                        @Override
                        public void run() {
                            CastActivity.sendCustom(toSend);
                        }
                    });
                }
                break;
            case "enable_accelerometer":

                if (connectedAndInView) {
                    CastActivity.getSensorManager().enableAccelerometerSensor("websocket enabled it");
                }
                break;

            case "disable_accelerometer":

                if (connectedAndInView) {
                    CastActivity.getSensorManager().disableAccelerometerSensor();
                }
                break;

            case "set_accelerometer_speed":

                if (connectedAndInView && msg.size() == 2) {
                    int speed = SensorManager.SENSOR_DELAY_NORMAL;

                    if (msg.get(1).equals("game")) speed = SensorManager.SENSOR_DELAY_GAME;
                    if (msg.get(1).equals("fastest")) speed = SensorManager.SENSOR_DELAY_FASTEST;
                    if (msg.get(1).equals("ui")) speed = SensorManager.SENSOR_DELAY_UI;

                    CastActivity.getSensorManager().setDelay(speed);
                }
                break;
            case "set_orientation":
                if(msg.size() == 2) {

                    String orientation = msg.get(1);

                    if(castActivity == null){
                        break;
                    }

                    if (orientation.equals("portrait")) {
                        castActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else if (orientation.equals("landscape")) {
                        castActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    } else if (orientation.equals("reverse_portrait")) {
                        castActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    } else if (orientation.equals("reverse_landscape")) {
                        castActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    } else if (orientation.equals("sensor")) {
                        castActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    }
                }
            default:

                webSocket.send(PacketSerializer.generatePacket("unknown_command", empty));
                break;

        }

    }

    public void setCastActivity(Activity a){
        this.castActivity = a;
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        Log.d("titancast-wsserver", (webSocket == null ? "wsserver-interror " : "wsserror-clerror ") + e.getLocalizedMessage());
        if(webSocket != null){
            socketList.remove(webSocket);
            if (webSocket == acceptedWebSocket) {
                terminateActive();
                CastActivity.close();
            }
        }
    }

    public void end(){
        for(WebSocket ws : socketList){
            ws.close(0);
            socketList.remove(ws);
        }
    }
}