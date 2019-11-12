//
//  KeyChainUtils.h
//  flutter_toolplugin
//
//  Created by admin on 2019/11/12.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface KeyChainUtils : NSObject

@property(nonatomic,copy) NSString *udid;

+(instancetype)shareKeyChainUDID;

-(NSString *)readUDID;

-(void)saveData:(NSString *)key value:(NSString *)value;

-(NSString *)getData:(NSString *)key ;

@end

NS_ASSUME_NONNULL_END
