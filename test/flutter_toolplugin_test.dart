import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_toolplugin/flutter_toolplugin.dart';

void main() {
  const MethodChannel channel = MethodChannel('flutter_toolplugin');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await FlutterToolplugin.platformVersion, '42');
  });
}
