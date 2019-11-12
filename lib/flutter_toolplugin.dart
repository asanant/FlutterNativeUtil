import 'dart:async';

import 'package:flutter/services.dart';

class FlutterToolplugin {
  static const MethodChannel _channel =
      const MethodChannel('flutter_toolplugin');

  static Future<String> get getKeyChainSyDid async {
    final String value = await _channel.invokeMethod('getKeyChainSyDid');
    return value;
  }

  static Future<void>  saveKeyChainSyDiy(String value) async {
    await _channel.invokeMethod('saveChainSyDid',value);
  }

}
