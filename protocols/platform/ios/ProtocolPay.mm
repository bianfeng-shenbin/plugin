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
#include "ProtocolPay.h"
#include "PluginUtilsIOS.h"
#import "InterfacePay.h"

namespace cocos2d { namespace plugin {
namespace combined{
    
bool ProtocolPay::m_bPaying = false;

ProtocolPay::ProtocolPay()
: m_pPayListener(NULL)
, m_pLoginListener(NULL)
{
}

ProtocolPay::~ProtocolPay()
{
    PluginUtilsIOS::erasePluginOCData(this);
}

void ProtocolPay::configDeveloperInfo(TIAPDeveloperInfo devInfo)
{
    if (devInfo.empty())
    {
        PluginUtilsIOS::outputLog("The developer info is empty for %s!", this->getPluginName());
        return;
    }
    else
    {
        PluginOCData* pData = PluginUtilsIOS::getPluginOCData(this);
        assert(pData != NULL);
        
        id ocObj = pData->obj;
        if ([ocObj conformsToProtocol:@protocol(InterfacePay)]) {
            NSObject<InterfacePay>* curObj = ocObj;
            NSMutableDictionary* pDict = PluginUtilsIOS::createDictFromMap(&devInfo);
            [curObj configDeveloperInfo:pDict];
        }
    }
}

void ProtocolPay::pluginLogin(TLoginInfo info)
{
}
    
void ProtocolPay::payForProduct(TProductInfo info)
{
    if (m_bPaying)
    {
        PluginUtilsIOS::outputLog("Now is paying");
        return;
    }

    if (info.empty())
    {
        if (NULL != m_pPayListener)
        {
            onPayResult(kPayFail, "Product info error");
        }
        PluginUtilsIOS::outputLog("The product info is empty for %s!", this->getPluginName());
        return;
    }
    else
    {
        m_bPaying = true;
        _curInfo = info;
        
        PluginOCData* pData = PluginUtilsIOS::getPluginOCData(this);
        assert(pData != NULL);
        
        id ocObj = pData->obj;
        if ([ocObj conformsToProtocol:@protocol(InterfacePay)]) {
            NSObject<InterfacePay>* curObj = ocObj;
            NSMutableDictionary* dict = PluginUtilsIOS::createDictFromMap(&info);
            [curObj payForProduct:dict];
        }
    }
}

void ProtocolPay::setResultListener(PayResultListener* pListener)
{
    m_pPayListener = pListener;
}

void ProtocolPay::setLoginListener(LoginResultListener* pListener)
{
    m_pLoginListener = pListener;
}
    
void ProtocolPay::onPayResult(PayResultCode ret, const char* msg)
{
    m_bPaying = false;
    if (m_pPayListener)
    {
        m_pPayListener->onPayResult(ret, msg, _curInfo);
    }
    else
    {
        PluginUtilsIOS::outputLog("Pay result listener of %s is null!", this->getPluginName());
    }

    _curInfo.clear();
    PluginUtilsIOS::outputLog("Pay result of %s is : %d(%s)", this->getPluginName(), (int) ret, msg);
}
    
void ProtocolPay::onLoginResult(LoginResultCode ret, TIAPLoginResultInfo info)
{
    if (m_pLoginListener)
    {
        m_pLoginListener->onLoginResult(ret, info);
    }
    else
    {
        PluginUtilsIOS::outputLog("Login result listener of %s is null!", this->getPluginName());
    }
    
    PluginUtilsIOS::outputLog("Login result of %s is : %d", this->getPluginName(), (int) ret);
}

}}} //namespace cocos2d { namespace plugin {
