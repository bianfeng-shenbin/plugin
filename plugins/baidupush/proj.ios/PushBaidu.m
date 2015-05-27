/****************************************************************************
Copyright (c) 2012-2013 cocos2d-x.org

http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
#import "PushBaidu.h"
#import "PushWrapper.h"
#import <UIKit/UIKit.h>

#define OUTPUT_LOG(...)     if (self.debug) NSLog(__VA_ARGS__);

@implementation PushBaidu

@synthesize debug = __debug;


- (void) configDeveloperInfo: (NSMutableDictionary*) cpInfo
{
    [BPush setupChannel:[PushWrapper getUpChannel]];
    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:
         UIRemoteNotificationTypeAlert
         | UIRemoteNotificationTypeBadge
         | UIRemoteNotificationTypeSound];
    
    [BPush setDelegate:self];
    [PushWrapper setObj:self];
}

- (void) bindChannel:(NSData *)deviceToken
{
    [BPush registerDeviceToken:deviceToken];
    [BPush bindChannel];
    [BPush setTag:@"ios"];
}

- (void) handleNotification:(NSDictionary*)userInfo
{
    NSString *alert = [[userInfo objectForKey:@"aps"] objectForKey:@"alert"];
    NSString *userInfoStr = [userInfo description];
    //220
    //if ([UIApplication sharedApplication].applicationState == UIApplicationStateActive) {
        [PushWrapper onNotificationResult:self withTitle:@"Title" withDescription:alert withCustomContentString:userInfoStr];
    //}

    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
    
    [BPush handleNotification:userInfo];
}

- (void) setDebugMode: (BOOL) isDebugMode
{
    OUTPUT_LOG(@"appstore setDebugMode invoked(%d)", isDebugMode);
    self.debug = isDebugMode;
}

- (NSString*) getSDKVersion
{
    return @"3.0.0";
}

- (NSString*) getPluginVersion
{
    return @"0.2.0";
}

//////////////////////////////////////////////////////////////////////

//BPushDelegate
- (void)onMethod:(NSString*)method response:(NSDictionary*)data
{
    NSLog(@"onMethod%@", method);
    
    if ([BPushRequestMethod_Bind isEqualToString:method])
    {
        NSLog(@"绑定!!!§§");
        
        NSDictionary* res = [[NSDictionary alloc] initWithDictionary:data];
        NSLog(@"kind of requestid str %@", [[res valueForKey:BPushRequestRequestIdKey] isKindOfClass:[NSString class]] ? @"YES" : @"NO");
        NSLog(@"kind of requestid num %@", [[res valueForKey:BPushRequestRequestIdKey] isKindOfClass:[NSNumber class]] ? @"YES" : @"NO");
        NSString *requestid = nil;
        if([[res valueForKey:BPushRequestRequestIdKey] isKindOfClass:[NSNumber class]])
        {
            NSNumber *requestidNum = [res valueForKey:BPushRequestRequestIdKey];
            requestid = [NSString stringWithFormat:@"%ld", [requestidNum longValue]];
        }
        else if([[res valueForKey:BPushRequestRequestIdKey] isKindOfClass:[NSString class]])
        {
            requestid = [res valueForKey:BPushRequestRequestIdKey];
        }
        NSString *appid = [res valueForKey:BPushRequestAppIdKey];
        NSString *userid = [res valueForKey:BPushRequestUserIdKey];
        NSString *channelid = [res valueForKey:BPushRequestChannelIdKey];
        NSString *returnCode = [res valueForKey:BPushRequestErrorCodeKey];
        NSLog(@"app_id:%@",appid);
        NSLog(@"user_id:%@",userid);
        NSLog(@"channel_id:%@",channelid);
        NSLog(@"returnCode:%@",returnCode);
        if (requestid) {
            NSLog(@"request_id:%@",requestid);
        }
        if(returnCode && ([returnCode intValue] == BPushErrorCode_Success))
        {
            NSMutableDictionary* data = [NSMutableDictionary dictionary];
            [data setObject:appid forKey:@"app_id"];
            [data setObject:userid forKey:@"user_id"];
            [data setObject:channelid forKey:@"channel_id"];
            [data setObject:requestid forKey:@"request_id"];
            [PushWrapper onBindResult:self withRet:kBind withDic:data];
        }
    }
    else if ([BPushRequestMethod_Unbind isEqualToString:method])
    {
        NSLog(@"解绑定");
        [PushWrapper onBindResult:self withRet:kUnBind withDic:nil];
    }
}

@end
