package com.example.webrtcdemo.Handler;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketHandler{

    public static Socket socket;
    public static String socketId;
    public static JSONObject socketObj;

    public static final String SIGNALING_URI = "https://fptcapstoneproject.herokuapp.com/";

    public SocketHandler() {
        try {
            socket = IO.socket(SIGNALING_URI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static synchronized String getSocketId() {
        return socketId;
    }

    public static synchronized void setSocketId(String socketId) {
        SocketHandler.socketId = socketId;
    }

    public static synchronized JSONObject getSocketObj() {
        return socketObj;
    }

    public static synchronized void setSocketObj(JSONObject socketObj) {
        SocketHandler.socketObj = socketObj;
    }

    public static synchronized Socket getSocket() {
        return socket;
    }

    public static synchronized void setSocket(Socket socket) {
        SocketHandler.socket = socket;
    }


}
