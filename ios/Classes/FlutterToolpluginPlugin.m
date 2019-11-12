#import "FlutterToolpluginPlugin.h"
#import "KeyChainUtils.h"

@implementation FlutterToolpluginPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"flutter_toolplugin"
            binaryMessenger:[registrar messenger]];
  FlutterToolpluginPlugin* instance = [[FlutterToolpluginPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getKeyChainSyDid" isEqualToString:call.method]) {
      result([[KeyChainUtils shareKeyChainUDID] getData:@"sport583_sydid"]);
  } else if([@"saveChainSyDid" isEqualToString:call.method]) {
      @try {
          [[KeyChainUtils shareKeyChainUDID] saveData:@"sport583_sydid" value:call.arguments];
      } @catch (NSException *exception) {
          result(FlutterMethodNotImplemented);
      }
  }else {
    result(FlutterMethodNotImplemented);
  }
}

@end
