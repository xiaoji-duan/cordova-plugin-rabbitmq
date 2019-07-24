package com.xiaoji.cordova.plugin.rabbitmq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class RabbitMQReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("RabbitMQPlugin", "received message from RabbitMQ Service.");
        try {
          String extra = intent.getStringExtra("mwxing");
          JSONObject message = new JSONObject();
          message.put("body", extra);
          RabbitMQPlugin.transmitMessageReceived(message);
        } catch (Exception e) {
          Log.e("RabbitMQPlugin", "Receiver error", e.getCause());
        }
    }
}
