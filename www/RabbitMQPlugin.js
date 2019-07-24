var exec = require('cordova/exec');
var cordova = require('cordova');
var RabbitMQPlugin = function() {};

RabbitMQPlugin.prototype.isPlatformIOS = function() {
  return (
    device.platform === "iPhone" ||
    device.platform === "iPad" ||
    device.platform === "iPod touch" ||
    device.platform === "iOS"
  );
};

RabbitMQPlugin.prototype.errorCallback = function(msg) {
  console.log("RabbitMQPlugin Callback Error: " + msg);
};

RabbitMQPlugin.prototype.callNative = function(
  name,
  args,
  successCallback,
  errorCallback
) {
  if (errorCallback) {
    exec(successCallback, errorCallback, "RabbitMQPlugin", name, args);
  } else {
    exec(
      successCallback,
      this.errorCallback,
      "RabbitMQPlugin",
      name,
      args
    );
  }
};

RabbitMQPlugin.prototype.init = function(uid, deviceid, queueName) {
  if (this.isPlatformIOS()) {
    console.log("RabbitMQPlugin.prototype.initial");
    this.callNative("initial", [uid, deviceid, queueName], null);
  } else {
    console.log("RabbitMQPlugin.prototype.init");
    this.callNative("init", [uid, deviceid, queueName], null);
  }
};

// Common methods
RabbitMQPlugin.prototype.coolMethod = function() {
  if (this.isPlatformIOS()) {
    this.callNative("coolMethod", [], null);
  } else {
    this.callNative("coolMethod", [], null);
  }
};

RabbitMQPlugin.prototype.receivedMessageInAndroidCallback = function(data) {
  if (device.platform === "Android") {
    var val = JSON.stringify(data);
    console.log("RabbitMQ val: " + val);
    var event = JSON.parse(val);
    cordova.fireDocumentEvent("rabbitmq.receivedMessage", event);
  }
};

if (!window.plugins) {
  window.plugins = {};
}

if (!window.plugins.RabbitMQPlugin) {
  window.plugins.RabbitMQPlugin = new RabbitMQPlugin();
}

module.exports = new RabbitMQPlugin();
