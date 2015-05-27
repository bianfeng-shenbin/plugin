#include "ProtocolSysfuncPlus.h"
#include "stdlib.h"
#include <iostream>

using namespace std;



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
		}

		void ProtocolSysfuncPlus::deleteApkPlugin(const string& path)
		{
		}

		string ProtocolSysfuncPlus::getPhoneNumberPlugin()
		{
			return "";
		}

		int ProtocolSysfuncPlus::sendSmsPlugin(const string& phonenumber, const string& msg)
		{
			return 0;
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

		void ProtocolSysfuncPlus::saveStringDataPlugin(const char* lpszKey, const char* lpszData)
		{

		}
		bool ProtocolSysfuncPlus::loadStringDataPlugin(const char* lpszKey, string& strReturn)
		{
			return false;
		}
		void ProtocolSysfuncPlus::removeDataPlugin(const char* lpszKey)
		{

		}


	}
} // namespace cocos2d { namespace plugin {


