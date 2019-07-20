package com.xiaoji.cordova.plugin.rabbitmq;

import android.app.Activity;
import android.content.Context;

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

  public RabbitMQPlugin() {
    instance = this;
  }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        mContext = cordova.getActivity().getApplicationContext();

        RabbitMQInterface.init(mContext);

        cordovaActivity = cordova.getActivity();
    }

    /**
     * 插件初始化
     */
    @Override
    protected void pluginInitialize() {
    }

    /**
     * 插件主入口
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
      if (action.equals("init")) {
        RabbitMQInterface.init(mContext);
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
        if (instance == null) {
            return;
        }
        String format = "window.plugins.RabbitMQPlugin.receivedMessageInAndroidCallback(%s);";
        final String js = String.format(format, message.toString());
        System.out.println(js);
        cordovaActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instance.webView.loadUrl("javascript:" + js);
            }
        });
    }
}
