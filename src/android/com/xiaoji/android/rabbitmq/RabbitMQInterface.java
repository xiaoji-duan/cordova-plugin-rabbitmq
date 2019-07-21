package com.xiaoji.android.rabbitmq;

import android.content.Context;
import android.content.Intent;

public class RabbitMQInterface {
    public static void init(Context ctx) {
      System.out.println("RabbitMQInterface init");
        Intent intent = new Intent(ctx, RabbitMQClientService.class);
        ctx.startService(intent);
    }
}
