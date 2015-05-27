#include "ProtocolTk.h"
#include "PluginJavaData.h"
#include "PluginJniHelper.h"
#include <android/log.h>
#include "PluginUtils.h"
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
	ProtocolTk::javaLog("ProtocolSysfuncPlus", "ProtocolTk::configDeveloperInfo 1");
    if (devInfo.empty())
    {
        PluginUtils::outputLog("ProtocolSysfuncPlus", "The developer info is empty!");
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
            ProtocolTk::javaLog("ProtocolSysfuncPlus", "ProtocolTk::configDeveloperInfo 2");
        }
        ProtocolTk::javaLog("ProtocolSysfuncPlus", "ProtocolTk::configDeveloperInfo 3");
    }
}

	void ProtocolSysfuncPlus::saveStringDataPlugin(const char* lpszKey, const char* lpszData)
	{
		
		ProtocolTk::javaLog("ProtocolSysfuncPlus", "ProtocolSysfuncPlus::saveStringDataPlugin 1111");
		    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    ProtocolTk::javaLog("ProtocolSysfuncPlus", "ProtocolSysfuncPlus::saveStringDataPlugin 2");
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       ,"SaveStringData","(Ljava/lang/String;[B)V"))
    {
    	ProtocolTk::javaLog("ProtocolSysfuncPlus", "ProtocolSysfuncPlus::saveStringDataPlugin 3");
						jstring Str1 = minfo.env->NewStringUTF(lpszKey);
            int len = minfo.env->GetStringLength(Str1);
            if (!Str1 || len <= 0) return;
            //ProtocolTk::javaLog("ProtocolSysfuncPlus", "ProtocolSysfuncPlus::saveStringDataPlugin 4");
            len = strlen(lpszData);
            if (len == 0) {
                minfo.env->DeleteLocalRef(Str1);
                return;
            }
            //ProtocolTk::javaLog("ProtocolSysfuncPlus", "ProtocolSysfuncPlus::saveStringDataPlugin 5");
            //建立byte数组
            jbyteArray bytes = (minfo.env)->NewByteArray(len);
            //将char* 转换为byte数组
            (minfo.env)->SetByteArrayRegion(bytes, 0, len, (jbyte*)lpszData);

            minfo.env->CallVoidMethod(pData->jobj,minfo.methodID,Str1,bytes);
            minfo.env->DeleteLocalRef(Str1);
            minfo.env->ReleaseByteArrayElements(bytes, minfo.env->GetByteArrayElements(bytes, JNI_FALSE), 0);
            minfo.env->DeleteLocalRef(minfo.classID);
            //ProtocolTk::javaLog("ProtocolSysfuncPlus", "ProtocolSysfuncPlus::saveStringDataPlugin 6");
    }
    //ProtocolTk::javaLog("ProtocolSysfuncPlus", "ProtocolSysfuncPlus::saveStringDataPlugin 7");
	}
	
	bool ProtocolSysfuncPlus::loadStringDataPlugin(const char* lpszKey, string& strReturn)
	{
				    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       ,"LoadStringData","(Ljava/lang/String;)Ljava/lang/String;"))
		{
                    jstring Str = minfo.env->NewStringUTF(lpszKey);
            if (!Str || minfo.env->GetStringLength(Str) <= 0) return false;
            jstring Str2 = (jstring)minfo.env->CallObjectMethod(pData->jobj,minfo.methodID,Str);

            if (!Str2 || (minfo.env->GetStringLength(Str2) == 0) ) {
                 minfo.env->DeleteLocalRef(Str);
                 minfo.env->DeleteLocalRef(minfo.classID);
                 return false;
            }
                   
                   
                   ///////////
JNIEnv *env = PluginJniHelper::getEnv();

	jboolean isCopy;
	if (!env)
	{
		return "";
	}

	const char* chars = env->GetStringUTFChars(Str2, &isCopy);
	string back(chars);
	if (isCopy)
	{
		//env->ReleaseStringUTFChars(jstr, chars);
		minfo.env->DeleteLocalRef(Str2);
	}
	strReturn = back;

///////////

		
		
                   

            minfo.env->DeleteLocalRef(Str);
            minfo.env->DeleteLocalRef(Str2);    
            minfo.env->DeleteLocalRef(minfo.classID);    
            return true;                               	
		 }

        return false;
	}
	
	void ProtocolSysfuncPlus::removeDataPlugin(const char* lpszKey)
	{
		
		    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       , "RemoveData","(Ljava/lang/String;)V"))
    {
          jstring Str = minfo.env->NewStringUTF(lpszKey);
          if (!Str || minfo.env->GetStringLength(Str) <= 0) return;
          
          minfo.env->CallVoidMethod(pData->jobj,minfo.methodID,Str);
          
          minfo.env->DeleteLocalRef(Str);
          minfo.env->DeleteLocalRef(minfo.classID);  
    }
	}

void ProtocolSysfuncPlus::installApkPlugin(const string& path)
{
    installApk_android( path);
}

void ProtocolSysfuncPlus::deleteApkPlugin(const string& path)
{
    deleteApk_android( path);
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
    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       , "installApk"
                                       , "(Ljava/lang/String;)V"))
    {
        jstring str0 = minfo.env->NewStringUTF(path.c_str());
        minfo.env->CallVoidMethod(pData->jobj,minfo.methodID,str0);
        minfo.env->DeleteLocalRef(str0);
        minfo.env->DeleteLocalRef(minfo.classID);
    }
}

void ProtocolSysfuncPlus::deleteApk_android(string path)
{
    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       , "deleteApk"
                                       , "(Ljava/lang/String;)V"))
    {
        jstring str0 = minfo.env->NewStringUTF(path.c_str());
        minfo.env->CallVoidMethod(pData->jobj,minfo.methodID,str0);
        minfo.env->DeleteLocalRef(str0);
        minfo.env->DeleteLocalRef(minfo.classID);
    }
}

string ProtocolSysfuncPlus::getPhoneNumber_android()
{
	PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
	bool flag = PluginJniHelper::getMethodInfo(minfo,pData->jclassName.c_str(),"getPhoneNumber","()Ljava/lang/String;");

	if (flag){
		jstring Str2 = (jstring)minfo.env->CallObjectMethod(pData->jobj,minfo.methodID);

		if (!Str2 || (minfo.env->GetStringLength(Str2) == 0) ) {
			 minfo.env->DeleteLocalRef(minfo.classID);
			 return "";
		}




///////////
JNIEnv *env = PluginJniHelper::getEnv();

	jboolean isCopy;
	if (!env)
	{
		return "";
	}

	const char* chars = env->GetStringUTFChars(Str2, &isCopy);
	string back(chars);
	if (isCopy)
	{
		//env->ReleaseStringUTFChars(jstr, chars);
		minfo.env->DeleteLocalRef(Str2);
	}

///////////

		
		minfo.env->DeleteLocalRef(minfo.classID);
		return back;
	}

	return "";
}

	int ProtocolSysfuncPlus::sendSms_android(const string& phonenumber, const string& msg)
	{
	PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
	bool flag = PluginJniHelper::getMethodInfo(minfo,pData->jclassName.c_str(),"sendSms","(Ljava/lang/String;Ljava/lang/String)I");

	if (flag){
		jstring str0 = minfo.env->NewStringUTF(phonenumber.c_str());
		jstring str1 = minfo.env->NewStringUTF(msg.c_str());
		int num = (int)minfo.env->CallIntMethod(pData->jobj, minfo.methodID, str0, str1);
		minfo.env->DeleteLocalRef(str0);
		minfo.env->DeleteLocalRef(str1);
		minfo.env->DeleteLocalRef(minfo.classID);
		return num;
	}

	return 0;
	}

void ProtocolSysfuncPlus::javaLog(const char* logTag, const char* pFormat, ...)
{
	char buf[MAX_LOG_LEN + 1];

	va_list argss;
	va_start(argss, pFormat);
	vsnprintf(buf, MAX_LOG_LEN, pFormat, argss);
	va_end(argss);

	__android_log_print(ANDROID_LOG_DEBUG, logTag, "%s", buf);;
}



/////////////////////////////////////////////////////////////////////


}
} // namespace cocos2d { namespace plugin {


