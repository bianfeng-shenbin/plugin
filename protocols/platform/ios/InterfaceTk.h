#import <Foundation/Foundation.h>

@protocol InterfaceTk <NSObject>

- (void) configDeveloperInfo: (NSMutableDictionary*) devInfo;
- (void) setDebugMode: (BOOL) debug;
- (NSString*) getSDKVersion;
- (NSString*) getPluginVersion;
- (void)trackEvent:(NSString *)eventId
             label:(NSString *)eventLabel
        parameters:(NSDictionary *)parameters;
- (void)trackEvent:(NSString *)eventId;
- (void)trackEvent:(NSString *)eventId label:(NSString *)eventLabel;
////////////

- (void)onStartedWithAppKey:(NSString *)appKey withChannelId:(NSString *)channelId withGroupId:(NSString*)groupId;
- (void)setCustomEvent:(NSString *)eventId
             withLabel:(NSString *)eventLabel
               paradic:(NSDictionary *)dic
              paranum :(int)num;
- (void)onRegister: (NSString *)userName
     withUserType : (NSString *)userType
        withUserId: (NSString *)userId
      withAreaName: (NSString *)UserAreaName
       withUserLev: (int) userLev
    withUserVipLev: (NSString *)userVipLev;
- (void)onLogin:(NSString *)userName
  withUserType : (NSString *)userType
     withUserId: (NSString *)userId
   withAreaName: (NSString *)UserAreaName
    withUserLev: (int) userLev
 withUserVipLev: (NSString *)userVipLev;
- (void)logout;
- (void)onPurchase: (double) payPrice
    withPayOrderId: (NSString*) payOrderId
       withPayType: (NSString*) payType
  withPayCardValue: (double) payCardType;
-(void)updataUserInfo:(NSString *)userName
        withUserType : (NSString *)userType
           withUserId: (NSString *)userId
         withAreaName: (NSString *)UserAreaName
          withUserLev: (int) userLev
       withUserVipLev: (NSString *)userVipLev;
- (void)getCurrencyWithType:(NSString *)type withAmount:(int)amount;
- (void)consumeCurrencyWithType:(NSString *)type
                     withAmount:(int)amount
                   withItemNum:(int)      itemNum
                   withItemType: (NSString *)itemType
                   withItemName: (NSString *)itemName
                     withItemId: (NSString *)itemId;
- (void)getItem: (int)      itemNum
   withItemType: (NSString *)itemType
   withItemName: (NSString *)itemName
     withItemId: (NSString *)itemId;
- (void)consumeItem:(int)      itemNum
       withItemType: (NSString *)itemType
       withItemName: (NSString *)itemName
         withItemId: (NSString *)itemId;
- (void)setExceptionReportEnabled:(BOOL)enable;
- (void)setLogEnabled:(BOOL)enable;
- (void)setLatitude:(double)latitude longitude:(double)longitude;
- (void)trackPageBegin:(NSString *)pageName;
- (void)trackPageEnd:(NSString *)pageName;
-(NSString*)getDeviceAddr;

@end