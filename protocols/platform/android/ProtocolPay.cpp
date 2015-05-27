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
#include "PluginJniHelper.h"
#include <android/log.h>
#include "PluginUtils.h"
#include "PluginJavaData.h"

namespace cocos2d { namespace plugin { namespace combined {

extern "C" {
	JNIEXPORT void JNICALL Java_org_cocos2dx_plugin_PayWrapper_nativeOnLoginResult(JNIEnv* env, jobject thiz, jstring className, jint ret, jobject obj_Map)
	{
		std::string strClassName = PluginJniHelper::jstring2string(className);
		PluginProtocol* pPlugin = PluginUtils::getPluginPtr(strClassName);
		PluginUtils::outputLog("ProtocolPay", "nativeOnLoginResult(), Get plugin ptr : %p", pPlugin);
		if (pPlugin != NULL)
		{
			PluginUtils::outputLog("ProtocolPay", "nativeOnLoginResult(), Get plugin name : %s", pPlugin->getPluginName());
			ProtocolPay* pIAP = dynamic_cast<ProtocolPay*>(pPlugin);
			if (pIAP != NULL)
			{
				TIAPLoginResultInfo info;
				PluginUtils::createStlMap(obj_Map, info);
				pIAP->onLoginResult((LoginResultCode)ret, info);
			}
		}
	}

	JNIEXPORT void JNICALL Java_org_cocos2dx_plugin_PayWrapper_nativeOnPayResult(JNIEnv*  env, jobject thiz, jstring className, jint ret, jstring msg)
	{
		std::string strMsg = PluginJniHelper::jstring2string(msg);
		std::string strClassName = PluginJniHelper::jstring2string(className);
		PluginProtocol* pPlugin = PluginUtils::getPluginPtr(strClassName);
		PluginUtils::outputLog("ProtocolPay", "nativeOnPayResult(), Get plugin ptr : %p", pPlugin);
		if (pPlugin != NULL)
		{
			PluginUtils::outputLog("ProtocolPay", "nativeOnPayResult(), Get plugin name : %s", pPlugin->getPluginName());
			ProtocolPay* pIAP = dynamic_cast<ProtocolPay*>(pPlugin);
			if (pIAP != NULL)
			{
				pIAP->onPayResult((PayResultCode) ret, strMsg.c_str());
			}
			//else
			//{
			//	ProtocolPay::ProtocolIAPCallback callback = pIAP->getCallback();
			//	if(callback)
			//		callback(ret, strMsg);
			//}
		}
	}

}

bool ProtocolPay::m_bLoging = false;
bool ProtocolPay::m_bPaying = false;

ProtocolPay::ProtocolPay()
: m_pLoginListener(NULL)
, m_pPayListener(NULL)
{
}

ProtocolPay::~ProtocolPay()
{
}

void ProtocolPay::configDeveloperInfo(TIAPDeveloperInfo devInfo)
{
    if (devInfo.empty())
    {
        PluginUtils::outputLog("ProtocolPay", "The developer info is empty!");
        return;
    }
    else
    {
        PluginJavaData* pData = PluginUtils::getPluginJavaData(this);
    	PluginJniMethodInfo t;
        if (PluginJniHelper::getMethodInfo(t
    		, pData->jclassName.c_str()
    		, "configDeveloperInfo"
    		, "(Ljava/util/Hashtable;)V"))
    	{
        	// generate the hashtable from map
        	jobject obj_Map = PluginUtils::createJavaMapObject(&devInfo);

            // invoke java method
            t.env->CallVoidMethod(pData->jobj, t.methodID, obj_Map);
            t.env->DeleteLocalRef(obj_Map);
            t.env->DeleteLocalRef(t.classID);
        }
    }
}

void ProtocolPay::pluginLogin(TLoginInfo info){
	if (m_bLoging){
		PluginUtils::outputLog("ProtocolPay", "Now is loging");
		return;
	}

	if (info.empty()){
		if (NULL != m_pLoginListener){
			onLoginResult(kLoginFail, info);
		}
		PluginUtils::outputLog("ProtocolPay", "The login info is empty!");
		return;
	}
	else{
		m_bLoging = true;

		PluginJavaData* pData = PluginUtils::getPluginJavaData(this);
		PluginJniMethodInfo t;
		if (PluginJniHelper::getMethodInfo(t
			, pData->jclassName.c_str()
			, "pluginLogin"
			, "(Ljava/util/Hashtable;)V")){
			// generate the hashtable from map
			jobject obj_Map = PluginUtils::createJavaMapObject(&info);

			// invoke java method
			t.env->CallVoidMethod(pData->jobj, t.methodID, obj_Map);
			t.env->DeleteLocalRef(obj_Map);
			t.env->DeleteLocalRef(t.classID);
		}
	}
}

void ProtocolPay::payForProduct(TProductInfo info)
{
	if (m_bPaying)
    {
        PluginUtils::outputLog("ProtocolPay", "Now is paying");
        return;
    }

    if (info.empty())
    {
		if (NULL != m_pPayListener)
        {
            onPayResult(kPayFail, "Product info error");
        }
        PluginUtils::outputLog("ProtocolPay", "The product info is empty!");
        return;
    }
    else
    {
		m_bPaying = true;
        _curInfo = info;

        PluginJavaData* pData = PluginUtils::getPluginJavaData(this);
		PluginJniMethodInfo t;
		if (PluginJniHelper::getMethodInfo(t
			, pData->jclassName.c_str()
			, "payForProduct"
			, "(Ljava/util/Hashtable;)V"))
		{
			// generate the hashtable from map
			jobject obj_Map = PluginUtils::createJavaMapObject(&info);

			// invoke java method
			t.env->CallVoidMethod(pData->jobj, t.methodID, obj_Map);
			t.env->DeleteLocalRef(obj_Map);
			t.env->DeleteLocalRef(t.classID);
		}
    }
}

//void ProtocolPay::payForProduct(TProductInfo info, ProtocolIAPCallback cb)
//{
//	_callback = cb;
//	payForProduct(info);
//}

void ProtocolPay::setLoginListener(LoginResultListener* pListener){
	m_pLoginListener = pListener;
}

void ProtocolPay::setResultListener(PayResultListener* pListener)
{
	m_pPayListener = pListener;
}

void ProtocolPay::onLoginResult(LoginResultCode ret, TIAPLoginResultInfo info){
	m_bLoging = false;
	if (m_pLoginListener)
	{
		m_pLoginListener->onLoginResult(ret, info);
	}
	else
	{
		PluginUtils::outputLog("ProtocolPay", "Login result listener is null!");
	}
	PluginUtils::outputLog("ProtocolPay", "Login result is : %d", (int)ret);
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
        PluginUtils::outputLog("ProtocolPay", "Result listener is null!");
    }
    _curInfo.clear();
    PluginUtils::outputLog("ProtocolPay", "Pay result is : %d(%s)", (int) ret, msg);
}

}}} // namespace cocos2d { namespace plugin {
