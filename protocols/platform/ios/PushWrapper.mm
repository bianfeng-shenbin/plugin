/****************************************************************************
Copyright (c) 2012+2013 cocos2d+x.org
Copyright (c) 2014      musenshen

http://www.cocos2d+x.org

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

#import "PushWrapper.h"
#include "PluginUtilsIOS.h"
#include "ProtocolPush.h"
#import "InterfacePush.h"
#import <UIKit/UIKit.h>
#include <map>
#include <string>

using namespace cocos2d::plugin;

static NSDictionary* g_launchOptions = nil;
static id g_obj = nil;

@implementation PushWrapper

+ (void) onBindResult:(id) obj withRet:(PushResult) ret withDic:(NSDictionary*) data
{
    NSLog(@"onBindResult!");
    PluginProtocol* pPlugin = PluginUtilsIOS::getPluginPtr(obj);
    ProtocolPush* pPush = dynamic_cast<ProtocolPush*>(pPlugin);
    if (pPush) {
        
        PushResultCode cRet = (PushResultCode) ret;
        
        TPushResultInfo info;
        for (id key in data)
        {
            id value = [data objectForKey:key];
            NSLog(@"onBindResult key %@ val %@ kind of %@"
                  , (NSString*)key
                  , (NSString*)value
                  , [value isKindOfClass:[NSString class]] ? @"YES" : @"NO");
            if (key && value) {
                const char* sKey = [(NSString*)key UTF8String];
                const char* sValue = [(NSString*)value UTF8String];
                if (sKey && sValue)
                    info.insert(std::make_pair(sKey, sValue));
            }
        }
        pPush->onBindResult(cRet, info);
    } else {
        PluginUtilsIOS::outputLog("Can't find the C++ object of the Push plugin");
    }
}

+ (void) onNotificationResult:(id) obj withTitle:(NSString*) title withDescription:(NSString*) description withCustomContentString:(NSString*) customContentString
{
    PluginProtocol* pPlugin = PluginUtilsIOS::getPluginPtr(obj);
    ProtocolPush* pPush = dynamic_cast<ProtocolPush*>(pPlugin);
    if (pPush) {
        std::string sTitle;
        std::string sDescription;
        std::string sCustomContentString;
        if (title) sTitle = [title UTF8String];
        if (description) sDescription = [description UTF8String];
        if (customContentString) sCustomContentString = [customContentString UTF8String];
        pPush->onNotificationResult(sTitle, sDescription, sCustomContentString);
    } else {
        PluginUtilsIOS::outputLog("Can't find the C++ object of the Push plugin");
    }
}

+ (void) setObj:(id) obj
{
    g_obj = obj;
}

+ (void) registerDeviceToken:(NSData *)deviceToken
{
    if (g_obj) {
        id ocObj = g_obj;
        if ([ocObj conformsToProtocol:@protocol(InterfacePush)]) {
            NSObject<InterfacePush>* curObj = ocObj;
            [curObj bindChannel:deviceToken];
        }
    }
}

+ (void) handleNotification:(NSDictionary*)userInfo
{
    if (g_obj) {
        id ocObj = g_obj;
        if ([ocObj conformsToProtocol:@protocol(InterfacePush)]) {
            NSObject<InterfacePush>* curObj = ocObj;
            [curObj handleNotification:userInfo];
        }
    }
}

+ (void) setUpChannel:(NSDictionary*)launchOptions
{
    g_launchOptions = [NSDictionary dictionaryWithDictionary:launchOptions];
    //判断程序是不是由推送服务完成的
    if (launchOptions) {
        NSDictionary* pushNotificationKey = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
        if (pushNotificationKey) {
            [UIApplication sharedApplication].applicationIconBadgeNumber = 0;
        }
    }
}

+ (NSDictionary*)getUpChannel
{
    return g_launchOptions;
}

@end
