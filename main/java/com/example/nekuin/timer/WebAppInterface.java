package com.example.nekuin.timer;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class WebAppInterface {

    private Context mContext;
    private String data;
    private boolean hasData = false;

    WebAppInterface(Context ctx){
        this.mContext = ctx;
    }

    public String getData(){
        return this.data;
    }

    @JavascriptInterface
    public void sendData(String data){
        this.data = data;
        Log.d("webappInterface", data);
        hasData = true;
    }

    public boolean dataCheck(){
        return this.hasData;
    }
}
