package com.example.websocketpractice.wsClient;

import android.util.Log;

import java.net.URI;

import tech.gusavila92.websocketclient.WebSocketClient;


public class JWebSocketClient extends WebSocketClient {


    /**
     * Initialize all the variables
     *
     * @param uri URI of the WebSocket server
     */
    public JWebSocketClient(URI uri) {
        super(uri);
    }

    @Override
    public void onOpen() {
        Log.i("WebSocket","Session is starting");
        //webSocketClient.send("Hello World!");
    }

    @Override
    public void onTextReceived(String message) {
        Log.i("WebSocket", "Message received");
    }

    @Override
    public void onBinaryReceived(byte[] data) {

    }

    @Override
    public void onPingReceived(byte[] data) {

    }

    @Override
    public void onPongReceived(byte[] data) {

    }

    @Override
    public void onException(Exception e) {
        System.out.println(e.getMessage());
    }

    @Override
    public void onCloseReceived() {
        Log.i("WebSocket", "Closed ");
        System.out.println("onCloseReceived");
    }
}
