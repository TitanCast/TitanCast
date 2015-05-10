package com.hydrabolt.titancast;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Amish on 01/05/2015.
 */
public class FormattingTools {

    public static String getIP(int ip) {

        String ipStr = String.format("%d.%d.%d.%d",
                        (ip & 0xff),
                        (ip >> 8 & 0xff),
                        (ip >> 16 & 0xff),
                        (ip >> 24 & 0xff));

        String finalRet = "";
        for(String bit : ipStr.split("\\.")){

            String part = Integer.toHexString(Integer.parseInt(bit));
            if(part.length() == 1) part = "0" + part;

            finalRet += " "+part;

        }

        return finalRet.substring(1);

    }

}
