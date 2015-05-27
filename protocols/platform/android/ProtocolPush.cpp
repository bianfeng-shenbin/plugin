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
#include "ProtocolPush.h"
#include "PluginJniHelper.h"
#include <android/log.h>
#include "PluginUtils.h"
#include "PluginJavaData.h"

namespace cocos2d { namespace plugin {

extern "C" {
	JNIEXPORT void JNICALL Java_org_cocos2dx_plugin_PushWrapper_nativeOnBindResult(JNIEnv*  env, jobject thiz, jstring className, jint ret, jobject obj_Map)
	{
		std::string strClassName = PluginJniHelper::jstring2string(className);
		PluginProtocol* pPlugin = PluginUtils::getPluginPtr(strClassName);
		PluginUtils::outputLog("ProtocolPush", "nativeOnBindResult(), Get plugin ptr : %p", pPlugin);
		if (pPlugin != NULL)
		{
			PluginUtils::outputLog("ProtocolPush", "nativeOnBindResult(), Get plugin name : %s", pPlugin->getPluginName());
			ProtocolPush* pPush = dynamic_cast<ProtocolPush*>(pPlugin);
			if (pPush != NULL)
			{
                TPushResultInfo info;
                PluginUtils::createStlMap(obj_Map, info);
				pPush->onBindResult((PushResultCode) ret, info);
			}
		}
	}
    
    JNIEXPORT void JNICALL Java_org_cocos2dx_plugin_PushWrapper_nativeOnNotificationResult(JNIEnv*  env, jobject thiz, jstring className, jstring jtitle, jstring jdescription, jstring jcustomContentString)
	{
		std::string strClassName = PluginJniHelper::jstring2string(className);
		PluginProtocol* pPlugin = PluginUtils::getPluginPtr(strClassName);
		if (pPlugin != NULL)
		{
			PluginUtils::outputLog("ProtocolPush", "nativeOnNotificationResult(), Get plugin name : %s", pPlugin->getPluginName());
			ProtocolPush* pPush = dynamic_cast<ProtocolPush*>(pPlugin);
			if (pPush != NULL)
			{
                std::string title = PluginJniHelper::jstring2string(jtitle);
                std::string description = PluginJniHelper::jstring2string(jdescription);
                std::string customContentString = PluginJniHelper::jstring2string(jcustomContentString);
				pPush->onNotificationResult(title, description, customContentString);
			}
		}
	}
}

ProtocolPush::ProtocolPush()
: m_pListener(NULL)
{
}

ProtocolPush::~ProtocolPush()
{
}

void ProtocolPush::configDeveloperInfo(TPushDeveloperInfo devInfo)
{
    if (devInfo.empty())
    {
        PluginUtils::outputLog("ProtocolPush", "The developer info is empty!");
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

void ProtocolPush::setPushListener(PushListener* pListener)
{
	m_pListener = pListener;
}

void ProtocolPush::onBindResult(PushResultCode ret, TPushResultInfo info)
{
    if (m_pListener)
    {
    	m_pListener->onBindResult(ret, info);
    }
    else
    {
        PluginUtils::outputLog("ProtocolPush", "Result listener is null!");
    }

    PluginUtils::outputLog("ProtocolPush", "Push result is : %d", (int) ret);
}
    
void ProtocolPush::onNotificationResult(std::string title, std::string description, std::string customContentString)
{
    if (m_pListener)
    {
        m_pListener->onNotificationResult(title, description, customContentString);
    }
    else
    {
        PluginUtils::outputLog("ProtocolPush", "Result listener is null!");
    }
        
    PluginUtils::outputLog("ProtocolPush", "onNotificationResult : %s, %s, %s", title.c_str(), description.c_str(), customContentString.c_str());
}

}} // namespace cocos2d { namespace plugin {
