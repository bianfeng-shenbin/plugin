//
//  ProtocolSysfuncPlus.m
//  PluginProtocol
//
//  Created by 王崇欢 on 15/3/24.
//  Copyright (c) 2015年 zhangbin. All rights reserved.
//

#import <Foundation/Foundation.h>

#include "stdlib.h"
#include "ProtocolSysfuncPlus.h"
#define MAX_LOG_LEN 256


namespace cocos2d
{
    namespace plugin
    {
        
        ProtocolSysfuncPlus::ProtocolSysfuncPlus()
        {
        }
        
        ProtocolSysfuncPlus::~ProtocolSysfuncPlus()
        {
        }
        
        void ProtocolSysfuncPlus::configDeveloperInfo(TSysfuncPlusDeveloperInfo devInfo)
        {
        }
        
        void ProtocolSysfuncPlus::installApkPlugin(const string& path)
        {
            installApk_android( path);
        }
        
        void ProtocolSysfuncPlus::deleteApkPlugin(const string& path)
        {
            deleteApk_android( path);
        }
        
				void ProtocolSysfuncPlus::saveStringDataPlugin(const char* lpszKey, const char* lpszData)
				{
					if (lpszKey != NULL) {
					//=================NSUserDefaults========================           
					//Save  
						NSUserDefaults *saveDefaults = [NSUserDefaults standardUserDefaults];  
						[saveDefaults setObject:[NSString stringWithUTF8String:lpszData] forKey:[NSString stringWithUTF8String:lpszKey]];   
					}
				}
				
				bool ProtocolSysfuncPlus::loadStringDataPlugin(const char* lpszKey, string& strReturn)
				{
				    if (lpszKey != NULL) {
				        //=================NSUserDefaults========================   
				        NSUserDefaults *saveDefaults = [NSUserDefaults standardUserDefaults]; 
				        NSString* data = [saveDefaults objectForKey:[NSString stringWithUTF8String:lpszKey]];  
				        const char* ret = [data cStringUsingEncoding:NSUTF8StringEncoding];
				        if (ret) {
				            strReturn = ret;
				            return true;
				        }
				        else {
				            return false;   
				        }
				    }
				    return false;
				}
				
				void ProtocolSysfuncPlus::removeDataPlugin(const char* lpszKey)
				{
				
				}
        
        string ProtocolSysfuncPlus::getPhoneNumberPlugin()
        {
            return getPhoneNumber_android();
        }
        
        int ProtocolSysfuncPlus::sendSmsPlugin(const string& phonenumber, const string& msg)
        {
            return sendSms_android(phonenumber, msg);
        }
        
        void ProtocolSysfuncPlus::installApk_android(string path)
        {
        }
        
        void ProtocolSysfuncPlus::deleteApk_android(string path)
        {
        }
        
        string ProtocolSysfuncPlus::getPhoneNumber_android()
        {
            return "";
        }
        
        int ProtocolSysfuncPlus::sendSms_android(const string& phonenumber, const string& msg)
        {
            return 0;
        }
        
        void ProtocolSysfuncPlus::javaLog(const char* logTag, const char* pFormat, ...)
        {
        }
        
        
        
        /////////////////////////////////////////////////////////////////////
        
        
    }
} // namespace cocos2d { namespace plugin {


