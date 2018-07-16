package com.example.torsti.sptesti;


import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class ServerConnection {
    public Context serverContext;
    private String sendMarkerString;
    private boolean serverDataReady = false;

    ServerConnection(Context context){
        serverContext = context;
    }
    @JavascriptInterface
    public void sendMarker(String toast){
        sendMarkerString = toast;
        Toast.makeText(serverContext, toast, Toast.LENGTH_SHORT).show();
        serverDataReady = true;
    }

    public String getMarker(){
        return sendMarkerString;
    }

    public boolean getServerReady(){
        return serverDataReady;
    }
}
