#ifndef __CCX_PROTOCOL_SYSFUNCPLUS_H__
#define __CCX_PROTOCOL_SYSFUNCPLUS_H__

#include "PluginProtocol.h"
#include <map>
#include <string>
#include <vector>

using namespace std;

namespace cocos2d
{
namespace plugin
{

typedef std::map<std::string, std::string> TSysfuncPlusDeveloperInfo;

class ProtocolSysfuncPlus : public PluginProtocol
{
public:
    ProtocolSysfuncPlus();
    virtual ~ProtocolSysfuncPlus();
    void configDeveloperInfo(TSysfuncPlusDeveloperInfo devInfo);

    static void javaLog(const char* logTag, const char* pFormat, ...);
    void installApkPlugin(const string& path); // 安装APK
    void deleteApkPlugin(const string& path); // 删除APK
    string getPhoneNumberPlugin();
	int sendSmsPlugin(const string& phonenumber, const string& msg);
	void saveStringDataPlugin(const char* lpszKey, const char* lpszData);
	bool loadStringDataPlugin(const char* lpszKey, string& strReturn);
	void removeDataPlugin(const char* lpszKey);
private:
    void installApk_android(string path);
    void deleteApk_android(string path);
    string getPhoneNumber_android();
    int sendSms_android(const string& phonenumber, const string& msg);
    
};

}
} // namespace cocos2d { namespace plugin {

#endif /* __CCX_PROTOCOL_SYSFUNCPLUS_H__ */
