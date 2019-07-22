package com.xiaoji.android.rabbitmq;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public class RabbitMQInterface {
    public static void init(Context ctx) {
      System.out.println("RabbitMQInterface init");
        Intent intent = new Intent(ctx, RabbitMQClientService.class);
        //ctx.startService(intent);
        ctx.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                RabbitMQClientService.MyBinder binder =
                        (RabbitMQClientService.MyBinder) service;
                instance.service = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                // fireEvent(BackgroundMode.Event.FAILURE, "'service disconnected'");
            }
        }, Context.BIND_AUTO_CREATE);
    }
}
