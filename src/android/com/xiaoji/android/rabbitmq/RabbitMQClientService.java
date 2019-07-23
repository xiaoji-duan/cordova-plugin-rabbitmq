package com.xiaoji.android.rabbitmq;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;

import com.rabbitmq.client.*;
import org.json.JSONException;
import org.json.JSONObject;
import cn.jiguang.api.JCoreInterface;

import java.io.IOException;

public class RabbitMQClientService extends Service {

    public RabbitMQClientService() {
        Log.i("RabbitMQPlugin", "RabbitMQ");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JCoreInterface.asyncExecute(new MQ());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("RabbitMQPlugin", "Kathy onStartCommand - startId = " + startId + ", Thread ID = " + Thread.currentThread().getId());
        //return super.onStartCommand(intent, START_STICKY, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        stopForeground(true);
        Intent intent = new Intent("com.xiaoji.rabbitmq.SERVICE_DESTROY");
        sendBroadcast(intent);
        super.onDestroy();
    }

    private MyBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return binder;
    }

    public class MyBinder extends Binder {
      public Service getService() {
        return RabbitMQClientService.this;
      }
    }

    class MQ implements Runnable {
        private Connection conn = null;
        private Channel channel = null;

        public void run() {
            try {
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }

                connect();
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.i("RabbitMQPlugin", "MQ TEST" + ex.getMessage());
            }
        }

        private void connect() {
          if (this.conn != null) {
              if (this.conn.isOpen()) {
                  return;
              }
          }

          ConnectionFactory factory = new ConnectionFactory();
          try {
              factory.setHost("pluto.guobaa.com");
              factory.setPort(5672);
              factory.setUsername("gtd_mq");
              factory.setPassword("gtd_mq");
              factory.setVirtualHost("/");
              factory.setAutomaticRecoveryEnabled(true);
              factory.setShutdownTimeout(0);
              factory.setRequestedHeartbeat(5000);
              factory.setHandshakeTimeout(5000);
              factory.setNetworkRecoveryInterval(5000);

              this.conn = factory.newConnection();

              this.channel = this.conn.createChannel();

              this.channel.basicConsume("queueName", false, "myConsumerTag", new DefaultConsumer(channel) {

                  @Override
                  public void handleDelivery(String consumerTag,
                                             Envelope envelope,
                                             AMQP.BasicProperties properties,
                                             byte[] body)
                          throws IOException {
                      JSONObject payload = null;
                      long deliveryTag = envelope.getDeliveryTag();

                      try {
                          payload = new JSONObject(new String(body, "utf-8"));
                          Log.i("RabbitMQPlugin", payload.toString());
                          Intent sendIntent = new Intent();
                          sendIntent.putExtra("mwxing", new String(body, "utf-8"));
                          sendIntent.setAction("com.xiaoji.rabbitmq.MESSAGE_RECEIVED");
                          startActivity(sendIntent);

                          channel.basicAck(deliveryTag, false);
                      } catch (JSONException e) {
                          e.printStackTrace();
                      }
                  }
                  });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
