//
//  KeyChainUtils.m
//  flutter_toolplugin
//
//  Created by admin on 2019/11/12.
//
#define KEY_UDID @"KEY_UDID"
#import "KeyChainUtils.h"

@implementation KeyChainUtils


+ (instancetype)shareKeyChainUDID {
    static KeyChainUtils *keyChainUDID = nil;
    static dispatch_once_t pred;
    dispatch_once(&pred, ^{
        keyChainUDID = [[self alloc] init];
    });
    return keyChainUDID;
}

-(NSString *)getData:(NSString *)key{
        NSMutableDictionary *udidKVPairs = (NSMutableDictionary *)[
            [KeyChainUtils shareKeyChainUDID] loadDataInService:key];
      NSString *value= [udidKVPairs objectForKey:key];
    if(value==nil){
        value=@"";
    }
    return value;
}


- (id)loadDataInService:(NSString *)service {
    id ret = nil;
    NSMutableDictionary *keychainQuery = [self getKeyChainDataWithService:service];

    [keychainQuery setObject:(id)kCFBooleanTrue forKey:(__bridge_transfer id)kSecReturnData];
    [keychainQuery setObject:(__bridge_transfer id)kSecMatchLimitOne
                      forKey:(__bridge_transfer id)kSecMatchLimit];
    CFDataRef keyData = NULL;
    if (SecItemCopyMatching((__bridge_retained CFDictionaryRef)keychainQuery,
                            (CFTypeRef *)&keyData) == noErr) {
        @try {
            ret = [NSKeyedUnarchiver unarchiveObjectWithData:(__bridge_transfer NSData *)keyData];
        } @catch (NSException *e) {
            NSLog(@"提取数据失败\nUnarchive of %@ failed: %@", service, e);
        } @finally {
        }
    }
    return ret;
}


- (NSMutableDictionary *)getKeyChainDataWithService:(NSString *)service {
    return [NSMutableDictionary
        dictionaryWithObjectsAndKeys:(__bridge_transfer id)kSecClassGenericPassword,
                                     (__bridge_transfer id)kSecClass, service,
                                     (__bridge_transfer id)kSecAttrService, service,
                                     (__bridge_transfer id)kSecAttrAccount,
                                     (__bridge_transfer id)kSecAttrAccessibleAfterFirstUnlock,
                                     (__bridge_transfer id)kSecAttrAccessible, nil];
}



- (void)saveData:(NSString *)key value:(NSString *)value{
    NSMutableDictionary *udidKVPairs = [NSMutableDictionary new];
    [udidKVPairs setObject:value forKey:key];
    [[KeyChainUtils shareKeyChainUDID] saveTargetData:udidKVPairs ToService:key];
}

- (void)saveTargetData:(id)data ToService:(NSString *)service {
    NSMutableDictionary *keychainQuery = [self getKeyChainDataWithService:service];
    SecItemDelete((__bridge_retained CFDictionaryRef)keychainQuery);
    [keychainQuery setObject:[NSKeyedArchiver archivedDataWithRootObject:data]
                      forKey:(__bridge_transfer id)kSecValueData];
    SecItemAdd((__bridge_retained CFDictionaryRef)keychainQuery, NULL);
}

@end
