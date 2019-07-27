package com.xiaoji.cordova.plugin.rabbitmq;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.app.Service;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;

import java.io.IOException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiaoji.android.rabbitmq.RabbitMQInterface;

/**
 * RabbitMQ Cordova Plugin
 * 是否需要缓存数据(个别手机收不到被共享消息)
 *
 * This class echoes a string called from JavaScript.
 */
public class RabbitMQPlugin extends CordovaPlugin {

  private static RabbitMQPlugin instance;
  private static Activity cordovaActivity;

  private Context mContext;
  public Service service;
  private RabbitMQReceiver mqReceiver = null;

  public RabbitMQPlugin() {
    Log.i("RabbitMQPlugin", "RabbitMQPlugin constructor");
    instance = this;
  }

  @Override
  public void onStart() {
  }

  @Override
  public void onStop() {
  }

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    Log.i("RabbitMQPlugin", "RabbitMQPlugin initialize");

    super.initialize(cordova, webView);
    mContext = cordova.getActivity().getApplicationContext();

    cordovaActivity = cordova.getActivity();

    if (mqReceiver == null) {
      mqReceiver = new RabbitMQReceiver();

      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction("com.xiaoji.cordova.plugin.rabbitmq.MESSAGE_RECEIVED");

      cordovaActivity.getApplicationContext().registerReceiver(mqReceiver, intentFilter);
    }

    //Cordova Plugin JS command trigger
    //RabbitMQInterface.init(mContext, instance, new JSONArray());

  }

  @Override
  public void onDestroy() {
    Log.i("RabbitMQPlugin", "RabbitMQ Cordova Plugin onDestroy.");
    if (mqReceiver != null) {
      cordovaActivity.getApplicationContext().unregisterReceiver(mqReceiver);
    }
  }

  /**
   * 插件初始化
   */
  @Override
  protected void pluginInitialize() {
    Log.i("RabbitMQPlugin", "RabbitMQPlugin plugin initialize");
  }

  /**
   * 插件主入口
   */
  @Override
  public boolean execute(String action, final JSONArray args, CallbackContext callbackContext) throws JSONException {
    Log.i("RabbitMQPlugin", "RabbitMQPlugin action " + action);
    if (action.equals("init")) {
      if (mContext == null) {
        Log.i("RabbitMQPlugin", "RabbitMQPlugin mContext is null");
      }
      cordova.getThreadPool().execute(new Runnable() {
          @Override
          public void run() {
            RabbitMQInterface.init(mContext, instance, args);
          }
      });
      return true;
    }

    if (action.equals("coolMethod")) {
        String message = args.getString(0);
        this.coolMethod(message, callbackContext);
        return true;
    }

    return false;
  }

  private void coolMethod(String message, CallbackContext callbackContext) {
      if (message != null && message.length() > 0) {
          callbackContext.success(message);
      } else {
          callbackContext.error("Expected one non-empty string argument.");
      }
  }

  static void transmitMessageReceived(JSONObject message) {
    Log.i("RabbitMQPlugin", "received message transmit to mwxing.");

    if (instance == null) {
        return;
    }

    String format = "window.plugins.RabbitMQPlugin.receivedMessageInAndroidCallback(%s);";
    final String js = String.format(format, message.toString());
    Log.i("RabbitMQPlugin", js);
    cordovaActivity.runOnUiThread(new Runnable() {
        @Override
        public void run() {
            instance.webView.loadUrl("javascript:" + js);
        }
    });
  }
}
