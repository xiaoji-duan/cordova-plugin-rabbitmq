package com.xiaoji.android.rabbitmq;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.content.ComponentName;
import android.util.Log;

import org.json.JSONArray;

import com.xiaoji.cordova.plugin.rabbitmq.RabbitMQPlugin;

public class RabbitMQInterface {
    public static void init(Context ctx, final RabbitMQPlugin plugin, JSONArray args) {
      Log.i("RabbitMQPlugin", "RabbitMQInterface init");
        Intent intent = new Intent(ctx, RabbitMQClientService.class);

        // 设置初始化参数
        if (args.length() == 3) {
          intent.putExtra("uid", args.getString(0));
          intent.putExtra("deviceid", args.getString(1));
          intent.putExtra("queueName", args.getString(2));

          //ctx.startService(intent);
          ctx.bindService(intent, new ServiceConnection() {
              @Override
              public void onServiceConnected(ComponentName name, IBinder service) {
                  RabbitMQClientService.MyBinder binder =
                          (RabbitMQClientService.MyBinder) service;
                  plugin.service = binder.getService();
              }

              @Override
              public void onServiceDisconnected(ComponentName name) {
                  // fireEvent(BackgroundMode.Event.FAILURE, "'service disconnected'");
              }
          }, Context.BIND_AUTO_CREATE);
        }
    }
}
