package com.hydrabolt.titancast;

import android.util.Base64;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Amish on 01/05/2015.
 */
public class PacketSerializer {

    public static String generatePacket(String packetName, String[] packetInfo){

        // packet [data data data data] in base64

        String packet = packetName;
        for(String param : packetInfo){

            packet += " " + Base64.encodeToString(param.getBytes(), Base64.DEFAULT);
        }

        return packet;
    }

    public static String generatePacket(String packetName, List<String> packetInfo){
        String[] a = packetInfo.toArray(new String[packetInfo.size()]);

        return generatePacket(packetName, a);
    }

    public static ArrayList<String> parsePacket(String data){

        ArrayList<String> splitData = new ArrayList<String>(Arrays.asList(data.split("\\s+")));

        String command = splitData.get(0);
        splitData.remove(0);

        ArrayList<String> toReturn = new ArrayList<String>();
        toReturn.add(command);

        for(String param : splitData){
            try {
                String paramValue = new String(Base64.decode(param, Base64.DEFAULT));
                toReturn.add(paramValue);
            }catch(UnknownError error){

            }

        }

        return toReturn;
    }

    public static String generatePacket(String type, String data) {

        return generatePacket( type, new String[] { data } );

    }
}
