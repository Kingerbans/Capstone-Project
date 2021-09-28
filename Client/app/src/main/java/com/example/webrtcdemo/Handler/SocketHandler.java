package com.example.webrtcdemo.Handler;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketHandler{

    public static Socket socket;

    public static final String SIGNALING_URI = "https://fptcapstoneproject.herokuapp.com/";

    public SocketHandler() {
        try {
            socket = IO.socket(SIGNALING_URI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static synchronized Socket getSocket() {
        return socket;
    }

    public static synchronized void setSocket(Socket socket) {
        SocketHandler.socket = socket;
    }


}
