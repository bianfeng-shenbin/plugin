#include "ProtocolTk.h"
#include "PluginJavaData.h"
#include "PluginJniHelper.h"
#include <android/log.h>
#include "PluginUtils.h"
#include "stdlib.h"
#define MAX_LOG_LEN 256


namespace cocos2d
{
namespace plugin
{

string strFormat(const char* format, ...)
{
#define CC_MAX_STRING_LENGTH (1024*100)
    
    std::string ret;
    
    va_list ap;
    va_start(ap, format);
    
    char* buf = (char*)malloc(CC_MAX_STRING_LENGTH);
    if (buf != nullptr)
    {
        vsnprintf(buf, CC_MAX_STRING_LENGTH, format, ap);
        ret = buf;
        free(buf);
    }
    va_end(ap);
    
    return ret;
}

string ProtocolTk::s_isFirstLaunch = "0";	//是否是新用户：新用户（1） 老用户(0)
string ProtocolTk::s_userName = "";			//用户名
string ProtocolTk::s_userID = "";			//用户ID
string ProtocolTk::s_areaID = "";			//账号类型
string ProtocolTk::s_channel = "";			//注册渠道

ProtocolTk::ProtocolTk()
{
}

ProtocolTk::~ProtocolTk()
{
}

void ProtocolTk::configDeveloperInfo(TTkDeveloperInfo devInfo)
{
	ProtocolTk::javaLog("tk", "ProtocolTk::configDeveloperInfo 1");
    if (devInfo.empty())
    {
        PluginUtils::outputLog("ProtocolTk", "The developer info is empty!");
        return;
    }
    else
    {
        s_isFirstLaunch = devInfo["isUserNew"];
        s_channel = devInfo["channelName"];

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
            ProtocolTk::javaLog("tk", "ProtocolTk::configDeveloperInfo 2");
        }
        ProtocolTk::javaLog("tk", "ProtocolTk::configDeveloperInfo 3");
    }
}

// 初始化
void ProtocolTk::onInitBF(const string& appID, const string& channel,const string& groupID, const string& isFirstLaunch, int tvMode)
{
    string areaID = "0"; //strFormat("%d",CGameData::shareGameData()->GetDefAreaID());
    string areaName = "未知";
    string serverId = "未知";
    string serverName = "未知";
    s_isFirstLaunch = isFirstLaunch;
    s_channel = channel;
    string udid,macaddr;
    char cstrUniqid[256] = "\0";
    //SysFunc::GetUniqueIdentifier(udid);
    //SysFunc::MacAddress(macaddr);
    //sprintf(cstrUniqid, "%s%s", udid.c_str(), macaddr.c_str());

		onInitBF_android(atoi(isFirstLaunch.c_str()), areaID, areaName, serverId, serverName, channel, cstrUniqid, appID, tvMode);
}

// 登入
void ProtocolTk::onPlayerAccountBF(int nFlag, const string& isFirstLogin, const string& userName, const string& userID, const string& areaID, const string& userType, const string& userLevel)
{
    s_userName = userName;
    s_userID = userID;
    s_areaID = areaID;
    onPlayerAccountBF_android(nFlag,atoi(isFirstLogin.c_str()),userName,userID,userType,areaID,"未知","未知","未知","未知",s_channel,"未知", userLevel);
    //onGameBegin("12");
    //onSilverCost("低保", "13");
    //onGameTask("2", "任务类型1", "任务名称1", "15");
}

// 0点超时事件
void ProtocolTk::onLoginDurationBF(const string& time)
{
    // SDK已处理 忽略
}

// 登出
void ProtocolTk::onLoginOutBF(const string& dayTime, const string& oneTime)
{
    // SDK已处理 参数忽略
    onLoginOutBF_android();
}

// 盘数，开局
void ProtocolTk::onGameBegin(const string& userLevel)
{
    onGameBegin_android( userLevel);
}

// 任务
void ProtocolTk::onGameTask(const string& userLevel, const string& taskType, const string& taskName, const string& amount)
{
    onGameTask_android( userLevel,  taskType,  taskName,  amount);
}

// 低保，银子消耗
void ProtocolTk::onSilverCost(const string& dibaoType, const string& dibaoCost)
{
    onCostSilver_android(dibaoType, dibaoCost);
}

// 自定义计次事件
void ProtocolTk::onCustomTimesEveBF(const string& eve_ID, const string& eve_Lab, map<string,string>& eve)
{
    if (eve.empty())
    {
        return;
    }
    eve.insert(make_pair("userName",s_userName));					// 登出用户
    eve.insert(make_pair("userId",s_userID));					// 数字账号
    eve.insert(make_pair("userType",s_areaID));					// 账号类型，例如：边锋、QQ、新浪等
    eve.insert(make_pair("channelName",s_channel));				// 注册渠道
    onEventMapBF(eve_ID,eve_Lab,eve);
}

// 自定义计值事件
void ProtocolTk::onCustomNumberEveBF(const string& eve_ID, const string& eve_Lab, const string& _value, map<string,string>& eve)
{
    eve.insert(make_pair("userName",s_userName));					// 登出用户
    eve.insert(make_pair("userId",s_userID));					// 数字账号
    eve.insert(make_pair("userType",s_areaID));					// 账号类型，例如：边锋、QQ、新浪等
    eve.insert(make_pair("value",_value));					// 账号类型，例如：边锋、QQ、新浪等
    eve.insert(make_pair("channelName",s_channel));				// 注册渠道
    onEventMapBF(eve_ID,eve_Lab,eve);
}



// 消耗事件
void ProtocolTk::onSilverConsume(
	const string& consumeType, 
	double consumeVal,
	int itemId,
	const string& itemType,
	const string& itemName,
	int itemNum)
{
	map<string,string> map_eve;
	const string _eveID = strFormat("%d",TG_EVENT_SILVER_CONSUME);
	map_eve.insert(make_pair("type",consumeType));					// 购买类型
	map_eve.insert(make_pair("consume",strFormat("%f",consumeVal)));				// 购买金额
	map_eve.insert(make_pair("itemType",itemType));				// 道具类型
	map_eve.insert(make_pair("itemId",strFormat("%d",itemId)));				// 道具类型
	map_eve.insert(make_pair("itemName",itemName));			// 道具名称
	map_eve.insert(make_pair("itemNum",strFormat("%d",itemNum)));					// 购买道具数量
	map_eve.insert(make_pair("userName",s_userName));			// 登陆用户
	map_eve.insert(make_pair("userId",s_userID));				// 数字账号
	map_eve.insert(make_pair("userType",s_areaID));				// 账号类型，例如：边锋、QQ、新浪等
	map_eve.insert(make_pair("areaName","未知"));				// 登陆区名称
	map_eve.insert(make_pair("userLev","0"));
	map_eve.insert(make_pair("vipLev","0"));	
	map_eve.insert(make_pair("leaderRank","0"));	
	map_eve.insert(make_pair("stage","0"));	
	map_eve.insert(make_pair("eliteStage","0"));
	onEventMapBF(_eveID,"充值货币消耗",map_eve);

}

// 充值事件
void ProtocolTk::onPayShoppingBF(const string& pay_type, const string& pay_price, const string& shop_id, const string& shop_name )
{
	//充值事件
    map<string,string> map_eve2;
    const string _eveID2 = strFormat("%d",TG_EVENT_RMB_RECHARGE);
    map_eve2.insert(make_pair("sellPrice",pay_price));					// 账面
    map_eve2.insert(make_pair("cardPrice",pay_price));				// 账面
    map_eve2.insert(make_pair("cardValue",pay_price));				// 账面
    map_eve2.insert(make_pair("cardType","充值卡"));                //卡类
    map_eve2.insert(make_pair("cardId","1"));                   //卡号
    map_eve2.insert(make_pair("orderId",shop_id));                   //订单号
    map_eve2.insert(make_pair("cardRebate","1"));                   //折扣
    map_eve2.insert(make_pair("rechargeType","未知"));                   //充值方式
    map_eve2.insert(make_pair("userName",s_userName));                   //充值用户
    map_eve2.insert(make_pair("userId",s_userID));                   //数字账号
    map_eve2.insert(make_pair("userType",s_areaID));                   //账号类型
    map_eve2.insert(make_pair("areaName","未知"));                   //登录区名称
    map_eve2.insert(make_pair("userLev","0"));
    map_eve2.insert(make_pair("vipLev","0"));
    map_eve2.insert(make_pair("leaderRank","0"));
    map_eve2.insert(make_pair("stage","0"));
    map_eve2.insert(make_pair("eliteStage","0"));
    map_eve2.insert(make_pair("isUserNew",s_isFirstLaunch));		// 是否新用户，1 新用户0 老用户
    map_eve2.insert(make_pair("isAreaNew",s_isFirstLaunch));		// 是否新用户(区)，1 新用户0 老用户
    onEventMapBF(_eveID2,"充值",map_eve2);
    
    	/* 废掉
		map<string,string> map_eve;
	const string _eveID = GetFormatString("%d",TG_EVENT_PAY_SHOPPING);
	map_eve.insert(make_pair("type",pay_type));					// 购买类型
	map_eve.insert(make_pair("price",pay_price));				// 购买金额
	map_eve.insert(make_pair("itemType","未知"));				// 道具类型
	map_eve.insert(make_pair("itemId",shop_id));				// 道具类型
	map_eve.insert(make_pair("itemName",shop_name));			// 道具名称
	map_eve.insert(make_pair("itemNum","1"));					// 购买道具数量
	map_eve.insert(make_pair("userName",m_userName));			// 登陆用户
	map_eve.insert(make_pair("userId",m_userID));				// 数字账号
	map_eve.insert(make_pair("userType",m_areaID));				// 账号类型，例如：边锋、QQ、新浪等
	map_eve.insert(make_pair("isUserNew",s_isFirstLaunch));		// 是否新用户，1 新用户0 老用户
	map_eve.insert(make_pair("isGameNew",s_isFirstLaunch));		// 是否新用户(游戏级)，1 新用户0 老用户
	map_eve.insert(make_pair("isAreaNew",s_isFirstLaunch));		// 是否新用户(区)，1 新用户0 老用户
	map_eve.insert(make_pair("areaName","未知"));				// 登陆区名称
	map_eve.insert(make_pair("serverId","未知"));				// 登陆服ID
	map_eve.insert(make_pair("serverName","未知"));				// 登陆服名称
	map_eve.insert(make_pair("roomName","未知"));				// 房间名称
	onEventMapBF(_eveID,"道具购买",map_eve);
	*/
}

// 注册
void ProtocolTk::onRegisterBF(const string& ptId, const string& numId, const string& userType)
{
    map<string,string> map_eve;
    const string _eveID = strFormat("%d",TG_EVENT_REGISTER);
    map_eve.insert(make_pair("userName",ptId));					// 登出用户
    map_eve.insert(make_pair("userId",numId));						// 数字账号
    map_eve.insert(make_pair("userType",userType));					// 账号类型，例如：边锋、QQ、新浪等
    map_eve.insert(make_pair("userCode","1"));						// 是否正式账号，1是0不是
    map_eve.insert(make_pair("channelName",s_channel));				// 注册渠道
    map_eve.insert(make_pair("type","0"));
    onEventMapBF(_eveID,"注册事件",map_eve);
}

//自定义事件(单个)
void ProtocolTk::onEventSingleBF(const string& eveID)
{
    onEventSingleBF_android(eveID);
}

//自定义事件(单个：主key(eveID) + 子key(eveLAB) 目前只支持eveID 所以eveID和eveLAB传同一值)
void ProtocolTk::onEventDoubleBF(const string& eveID, const string& eveLAB)
{
    onEventDoubleBF_android(eveID,eveLAB);
}

//自定义事件(map eveID和eveLAB传同一值)
void ProtocolTk::onEventMapBF(const string& eveID, const string& eveLAB, map<string,string>& eve)
{
#if (CC_TARGET_PLATFORM == CC_PLATFORM_ANDROID)

    //data format:"k1,v1;k2,v2;k3,v3;..."
    string strEve;
    map<string,string>::iterator it = eve.begin();
    for (int i=0; it!=eve.end(); i++,it++)
    {
        //if ( i >= 10)
        //{
        //	onEventMapBF_android(eveID,eveLAB,strEve);
        //	strEve = "";
        //	i = 0;
        //}
        if (it->first!="" && it->second!="" )
        {
            if (strEve=="")
            {
                strEve = strFormat("%s,%s",it->first.c_str(),it->second.c_str());
            }
            else
            {
                strEve = strFormat("%s;%s,%s",strEve.c_str(),it->first.c_str(),it->second.c_str());
            }
        }
    }
    if (strEve!="")
    {
        onEventMapBF_android(eveID,eveLAB,strEve);
    }

#endif
}

/////////////////////////////////////////////////////////////////////////

void ProtocolTk::onInitBF_android(int isUserNew, string areaId, string areaName, string serverId, string serverName, string channelName, string device_code, string appId, int tvMode)
{
    PluginJavaData* pData = PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       , "onInitBFData"
                                       , "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"))
    {
        jstring str0 = minfo.env->NewStringUTF(areaId.c_str());
        jstring str1 = minfo.env->NewStringUTF(areaName.c_str());
        jstring str2 = minfo.env->NewStringUTF(serverId.c_str());
        jstring str3 = minfo.env->NewStringUTF(serverName.c_str());
        jstring str4 = minfo.env->NewStringUTF(channelName.c_str());
        jstring str5 = minfo.env->NewStringUTF(device_code.c_str());
        jstring str6 = minfo.env->NewStringUTF(appId.c_str());
        minfo.env->CallVoidMethod(pData->jobj,minfo.methodID,isUserNew,tvMode, str0,str1,str2,str3,str4,str5, str6);
        minfo.env->DeleteLocalRef(str0);
        minfo.env->DeleteLocalRef(str1);
        minfo.env->DeleteLocalRef(str2);
        minfo.env->DeleteLocalRef(str3);
        minfo.env->DeleteLocalRef(str4);
        minfo.env->DeleteLocalRef(str5);
        minfo.env->DeleteLocalRef(str6);
        minfo.env->DeleteLocalRef(minfo.classID);

    }
    ProtocolTk::javaLog("tk", "ProtocolTk::onInitBF_android success!");
}

void ProtocolTk::onPlayerAccountBF_android(int type,int isUserNew,string userName,string userId
        ,string userType,string areaId,string areaName,string serverId,string serverName,string reason,
        string channelName,string roomName, string userLevel)
{
    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       , "onPlayerAccountBFData"
                                       ,  "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"))
    {
        jstring str0 = minfo.env->NewStringUTF(userName.c_str());
        jstring str1 = minfo.env->NewStringUTF(userId.c_str());
        jstring str2 = minfo.env->NewStringUTF(userType.c_str());
        jstring str3 = minfo.env->NewStringUTF(areaId.c_str());
        jstring str4 = minfo.env->NewStringUTF(areaName.c_str());
        jstring str5 = minfo.env->NewStringUTF(serverId.c_str());
        jstring str6 = minfo.env->NewStringUTF(serverName.c_str());
        jstring str7 = minfo.env->NewStringUTF(reason.c_str());
        jstring str8 = minfo.env->NewStringUTF(channelName.c_str());
        jstring str9 = minfo.env->NewStringUTF(roomName.c_str());
        jstring str10 = minfo.env->NewStringUTF(userLevel.c_str());
        minfo.env->CallVoidMethod(pData->jobj,minfo.methodID,type,isUserNew,str0,str1,str2,str3,str4,str5,str6,str7,str8,str9, str10);
        minfo.env->DeleteLocalRef(str0);
        minfo.env->DeleteLocalRef(str1);
        minfo.env->DeleteLocalRef(str2);
        minfo.env->DeleteLocalRef(str3);
        minfo.env->DeleteLocalRef(str4);
        minfo.env->DeleteLocalRef(str5);
        minfo.env->DeleteLocalRef(str6);
        minfo.env->DeleteLocalRef(str7);
        minfo.env->DeleteLocalRef(str8);
        minfo.env->DeleteLocalRef(str9);
        minfo.env->DeleteLocalRef(str10);
        minfo.env->DeleteLocalRef(minfo.classID);
    }
}

void ProtocolTk::onCostSilver_android(string dibaoType, string dibaoCost)
{
    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       , "onCostSilver"
                                       , "(Ljava/lang/String;Ljava/lang/String;)V"))
    {
        jstring str0 = minfo.env->NewStringUTF(dibaoType.c_str());
        jstring str1 = minfo.env->NewStringUTF(dibaoCost.c_str());
        minfo.env->CallVoidMethod(pData->jobj,minfo.methodID,str0, str1);
        minfo.env->DeleteLocalRef(str0);
        minfo.env->DeleteLocalRef(str1);
        minfo.env->DeleteLocalRef(minfo.classID);
    }
}

void ProtocolTk::onGameTask_android(
    string userLevel, string taskType, string taskName, string amount)
{
    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       , "onGameTask"
                                       , "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"))
    {
        jstring str0 = minfo.env->NewStringUTF(userLevel.c_str());
        jstring str1 = minfo.env->NewStringUTF(taskType.c_str());
        jstring str2 = minfo.env->NewStringUTF(taskName.c_str());
        jstring str3 = minfo.env->NewStringUTF(amount.c_str());
        minfo.env->CallVoidMethod(pData->jobj,minfo.methodID,str0, str1, str2, str3);
        minfo.env->DeleteLocalRef(str0);
        minfo.env->DeleteLocalRef(str1);
        minfo.env->DeleteLocalRef(str2);
        minfo.env->DeleteLocalRef(str3);
        minfo.env->DeleteLocalRef(minfo.classID);
    }
}

void ProtocolTk::onGameBegin_android(string userLevel)
{
    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       , "onGameBegin"
                                       , "(Ljava/lang/String;)V"))
    {
        jstring str0 = minfo.env->NewStringUTF(userLevel.c_str());
        minfo.env->CallVoidMethod(pData->jobj,minfo.methodID,str0);
        minfo.env->DeleteLocalRef(str0);
        minfo.env->DeleteLocalRef(minfo.classID);
    }
}


void ProtocolTk::onLoginOutBF_android()
{

    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       , "onLoginOutBFData"
                                       , "()V"))
    {
        minfo.env->CallVoidMethod(pData->jobj,minfo.methodID);
        minfo.env->DeleteLocalRef(minfo.classID);
    }
}

void ProtocolTk::onEventSingleBF_android(string eveId)
{
    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       , "onEventSingleBFData"
                                       , "(Ljava/lang/String;)V"))
    {
        jstring str0 = minfo.env->NewStringUTF(eveId.c_str());
        minfo.env->CallVoidMethod(pData->jobj,minfo.methodID,str0);

        minfo.env->DeleteLocalRef(str0);
        minfo.env->DeleteLocalRef(minfo.classID);
    }
}

void ProtocolTk::onEventDoubleBF_android(string eveId, string eveLab)
{
    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       , "onEventDoubleBFData"
                                       , "(Ljava/lang/String;Ljava/lang/String;)V"))
    {
        {
            jstring str0 = minfo.env->NewStringUTF(eveId.c_str());
            jstring str1 = minfo.env->NewStringUTF(eveLab.c_str());
            minfo.env->CallVoidMethod(pData->jobj,minfo.methodID,str0,str1);

            minfo.env->DeleteLocalRef(str0);
            minfo.env->DeleteLocalRef(str1);
            minfo.env->DeleteLocalRef(minfo.classID);
        }
    }
}

void ProtocolTk::onEventMapBF_android(string eveId, string eveLab, string mapMsg)
{
    PluginJavaData* pData =  PluginUtils::getPluginJavaData(this);
    PluginJniMethodInfo minfo;
    if (PluginJniHelper::getMethodInfo(minfo
                                       , pData->jclassName.c_str()
                                       , "onEventMapBFData"
                                       , "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V"))
    {
        {
            jstring str0 = minfo.env->NewStringUTF(eveId.c_str());
            jstring str1 = minfo.env->NewStringUTF(eveLab.c_str());
            jstring str2 = minfo.env->NewStringUTF(mapMsg.c_str());
            minfo.env->CallVoidMethod(pData->jobj,minfo.methodID,str0,str1,str2);
            minfo.env->DeleteLocalRef(str0);
            minfo.env->DeleteLocalRef(str1);
            minfo.env->DeleteLocalRef(str2);
            minfo.env->DeleteLocalRef(minfo.classID);
        }
    }
}

void ProtocolTk::javaLog(const char* logTag, const char* pFormat, ...)
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


