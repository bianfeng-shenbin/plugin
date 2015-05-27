/****************************************************************************
Copyright (c) 2012-2013 cocos2d-x.org
Copyright (c) 2014      musenshen

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
#ifndef __CCX_PROTOCOL_PUSH_H__
#define __CCX_PROTOCOL_PUSH_H__

#include "PluginProtocol.h"
#include <map>
#include <string>

namespace cocos2d { namespace plugin {

typedef std::map<std::string, std::string> TPushDeveloperInfo;
typedef std::map<std::string, std::string> TPushResultInfo;

typedef enum
{
    kPushBind = 0,              // Binding
    kPushUnBind,
    kBindTimeOut,
} PushResultCode;

class ProtocolPush;
class PushListener
{
public:
    /**
    @brief The push binding result
    */
    virtual void onBindResult(PushResultCode code, TPushResultInfo resInfo) = 0;
    
    /**
     @brief The push notification result
     */
    virtual void onNotificationResult(std::string title, std::string description, std::string customContentString) = 0;
    
};

class ProtocolPush : public PluginProtocol
{
public:
	ProtocolPush();
	virtual ~ProtocolPush();

    /**
    @brief config the application info
    @param devInfo This parameter is the info of aplication,
           different plugin have different format
    @warning Must invoke this interface before other interfaces.
             And invoked only once.
    */
    void configDeveloperInfo(TPushDeveloperInfo devInfo);

    /**
     * 绑定channel
     * @param
     *     none
     * @return
     *     none
     */
    void bindChannel(void* data);

    /**
     @brief set the Push listener
    */
    void setPushListener(PushListener* pListener);

    // For the callbak methods
    void onBindResult(PushResultCode code, TPushResultInfo info);
    
    // For the callbak methods
    void onNotificationResult(std::string title, std::string description, std::string customContentString);

protected:
    PushListener* m_pListener;
};

}} // namespace cocos2d { namespace plugin {

#endif /* __CCX_PROTOCOL_PUSH_H__ */
