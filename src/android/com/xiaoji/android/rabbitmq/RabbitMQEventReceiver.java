package com.xiaoji.android.rabbitmq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class RabbitMQEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
      Log.i("RabbitMQPlugin", "Received action " + intent.getAction());

      if (intent.getAction().equals("com.xiaoji.rabbitmq.SERVICE_DESTROY")) {
          Intent sevice = new Intent(context, RabbitMQClientService.class);
          context.startService(sevice);
      }
    }
}
