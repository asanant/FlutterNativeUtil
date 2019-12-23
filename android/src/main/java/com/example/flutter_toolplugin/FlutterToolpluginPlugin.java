package com.example.flutter_toolplugin;

import android.os.Environment;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterToolpluginPlugin */
public class FlutterToolpluginPlugin implements MethodCallHandler {
  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_toolplugin");
    channel.setMethodCallHandler(new FlutterToolpluginPlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getExternalStorage")) {
      result.success(Environment.getExternalStorageDirectory().getPath());
    } else {
      result.notImplemented();
    }
  }
}
