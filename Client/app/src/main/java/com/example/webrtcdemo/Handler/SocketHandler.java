package com.example.webrtcdemo.Handler;

import java.io.Serializable;
import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketHandler{

    Socket socket;

    private static final String SIGNALING_URI = "http://192.168.1.110:3000";

    public SocketHandler() {
        try {
            socket = IO.socket(SIGNALING_URI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }


}
