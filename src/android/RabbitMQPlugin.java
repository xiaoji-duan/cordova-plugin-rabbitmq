package com.xiaoji.cordova.plugin.rabbitmq;

import android.app.Activity;
import android.content.Context;
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
 * This class echoes a string called from JavaScript.
 */
public class RabbitMQPlugin extends CordovaPlugin {

  private static RabbitMQPlugin instance;
  private static Activity cordovaActivity;

  private Context mContext;
  public Service service;

  public RabbitMQPlugin() {
    Log.i("RabbitMQPlugin", "RabbitMQPlugin constructor");
    instance = this;
  }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
      Log.i("RabbitMQPlugin", "RabbitMQPlugin initialize");
        super.initialize(cordova, webView);
        mContext = cordova.getActivity().getApplicationContext();

        RabbitMQInterface.init(mContext, instance);

        cordovaActivity = cordova.getActivity();
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
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      Log.i("RabbitMQPlugin", "RabbitMQPlugin action " + action);
      if (action.equals("init")) {
        if (mContext == null) {
          Log.i("RabbitMQPlugin", "RabbitMQPlugin mContext is null");
        }
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
              RabbitMQInterface.init(mContext, instance);
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
