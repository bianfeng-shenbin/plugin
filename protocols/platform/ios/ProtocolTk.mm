
#include "ProtocolTk.h"
#include "PluginUtilsIOS.h"
#import "InterfaceTk.h"


namespace cocos2d { namespace plugin {
    
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
    
    ProtocolTk::ProtocolTk()
    {
    }
    
    ProtocolTk::~ProtocolTk()
    {
    }
    
    static NSObject<InterfaceTk>* sObjTk = NULL;
    
    void ProtocolTk::configDeveloperInfo(TTkDeveloperInfo devInfo)
    {
        std::map<std::string, std::string>  devInfoNew = devInfo;
        for(std::map<std::string, std::string> ::iterator i = devInfoNew.begin(); i != devInfoNew.end(); ++i)
        {
            PluginUtilsIOS::outputLog("ProtocolTk k %s v %s", i->first.c_str(), i->second.c_str());
        }
        if (devInfoNew.empty())
        {
            PluginUtilsIOS::outputLog("The developer info is empty for %s!", this->getPluginName());
            return;
        }
        else
        {
            if(devInfoNew["appId"] == "" || devInfoNew["channelName"] == "" || devInfoNew["areaId"] == "")
            {
                is_TKBF_Open = false;
                return;
            }
            else
            {
                is_TKBF_Open = true;
            }
            s_isFirstLaunch = devInfoNew["isFirstLaunch"];
            s_channel = devInfoNew["channel"];
            
            PluginOCData* pData = PluginUtilsIOS::getPluginOCData(this);
            assert(pData != NULL);
            id ocObj = pData->obj;
            if ([ocObj conformsToProtocol:@protocol(InterfaceTk)]) {
                sObjTk = ocObj;
                NSMutableDictionary* pDict = PluginUtilsIOS::createDictFromMap(&devInfoNew);
                [sObjTk configDeveloperInfo:pDict];
            }
        }
    }
    
    string ProtocolTk::s_isFirstLaunch = "0";	//是否是新用户：新用户（1） 老用户(0)
    string ProtocolTk::s_userName = "";			//用户名
    string ProtocolTk::s_userID = "";			//用户ID
    string ProtocolTk::s_areaID = "";			//账号类型
    string ProtocolTk::s_channel = "";			//账号类型
    
    
    //BFTK
    void ProtocolTk::onInitBF(const string& appID,const string& channel,const string& groupID, const string& isFirstLaunch, int tvMode)
    {
        if(appID.size() == 0 || channel.size() == 0 || groupID.size() == 0)
        {
            is_TKBF_Open = false;
            return;
        }
        else
        {
            is_TKBF_Open = true;
        }
        s_isFirstLaunch = isFirstLaunch;
        s_channel = channel;
        //[TalkingData setSignalReportEnabled:YES];
        NSString* pAppID = [NSString stringWithUTF8String:appID.c_str()];
        NSString* pChannel = [NSString stringWithUTF8String:channel.c_str()];
        NSString* pGroupID = [NSString stringWithUTF8String:groupID.c_str()];
        //[TalkingData sessionStarted:pAppID withChannelId:pChannel withGroupId:pGroupID];
    }
    
    
    
    
    void ProtocolTk::onPlayerAccountBF(int nFlag, const string& isFirstLogin, const string& userName, const string& userID, const string& areaID, const string& userType, const string& userLevel)
    {
        if(sObjTk)
        {
            if (!is_TKBF_Open)
            {
                return;
            }
            //onStart();
            m_userName = userName;
            m_userID = userID;
            m_areaID = areaID;
            NSString* strType = [NSString stringWithFormat:@"%d", nFlag];
            NSString* strNew = [NSString stringWithUTF8String:isFirstLogin.c_str()];
            NSString* strUserName = [NSString stringWithUTF8String:userName.c_str()];
            NSString* strUserID = [NSString stringWithUTF8String:userID.c_str()];
            NSString* strAreaID = [NSString stringWithUTF8String:areaID.c_str()];
            NSString* strUserType = [NSString stringWithUTF8String:userType.c_str()];
            NSString* strUserLevel = [NSString stringWithUTF8String:userLevel.c_str()];
            /*
            NSMutableDictionary* dicParam = [NSMutableDictionary dictionary];
            [dicParam setObject:strType forKey:@"type"];
            [dicParam setObject:strNew forKey:@"isUserNew"];
            [dicParam setObject:strNew forKey:@"isGameNew"];
            [dicParam setObject:strNew forKey:@"isAreaNew"];
            [dicParam setObject:strUserName forKey:@"userName"];
            [dicParam setObject:strUserID forKey:@"userId"];
            [dicParam setObject:strAreaID forKey:@"areaId"];
            [dicParam setObject:strUserType forKey:@"userType"];
            //[dicParam setObject:strUserLevel forKey:@"userLevel"];
            NSString* strID = [NSString stringWithFormat:@"%d", TG_EVENT_LOGIN];
            */
            [sObjTk onLogin:strUserName withUserType:strUserType withUserId:strUserID withAreaName:strAreaID
                withUserLev:[strUserLevel intValue] withUserVipLev:@"0"];
            
            //[sObjTk trackEvent:strID label:@"" parameters:dicParam];
        }
    }
    
    // 盘数，开局
    void ProtocolTk::onGameBegin(const string& userLevel)
    {
        
    }
    
    // 任务
    void ProtocolTk::onGameTask(const string& userLevel, const string& taskType, const string& taskName, const string& amount)
    {
        
    }
    
    // 低保，银子消耗
    void ProtocolTk::onSilverCost(const string& dibaoType, const string& dibaoCost)
    {
        
    }
    
    void ProtocolTk::onLoginDurationBF(const string& time)
    {
        if(sObjTk)
        {
            if (!is_TKBF_Open)
            {
                return;
            }
            NSString* strUserName = [NSString stringWithUTF8String:m_userName.c_str()];
            NSString* strUserID = [NSString stringWithUTF8String:m_userID.c_str()];
            NSString* strAreaID = [NSString stringWithUTF8String:m_areaID.c_str()];
            NSString* strTime = [NSString stringWithUTF8String:time.c_str()];
            NSMutableDictionary* dicParam = [NSMutableDictionary dictionary];
            [dicParam setObject:strUserName forKey:@"userName"];
            [dicParam setObject:strUserID forKey:@"userId"];
            [dicParam setObject:strAreaID forKey:@"areaId"];
            [dicParam setObject:strTime forKey:@"duration"];
            
            NSString* strID = [NSString stringWithFormat:@"%d", TG_EVENT_OVERTIME];

            [sObjTk setCustomEvent:strID withLabel:@"" paradic:dicParam paranum:1];
            //[sObjTk trackEvent:strID label:@"" parameters:dicParam];
        }
    }
    
    void ProtocolTk::onLoginOutBF(const string& dayTime, const string& oneTime)
    {
        if(sObjTk)
        {
            if (!is_TKBF_Open)
            {
                return;
            }
            NSString* strUserName = [NSString stringWithUTF8String:m_userName.c_str()];
            NSString* strUserID = [NSString stringWithUTF8String:m_userID.c_str()];
            NSString* strAreaID = [NSString stringWithUTF8String:m_areaID.c_str()];
            NSString* strDayTime = [NSString stringWithUTF8String:dayTime.c_str()];
            NSString* strOneTime = [NSString stringWithUTF8String:oneTime.c_str()];
            NSMutableDictionary* dicParam = [NSMutableDictionary dictionary];
            [dicParam setObject:strUserName forKey:@"userName"];
            [dicParam setObject:strUserID forKey:@"userId"];
            [dicParam setObject:strAreaID forKey:@"areaId"];
            [dicParam setObject:strDayTime forKey:@"duration"];
            [dicParam setObject:strOneTime forKey:@"fullDuration"];
            
            NSString* strID = [NSString stringWithFormat:@"%d", TG_EVENT_LOGOUT];
            [sObjTk logout];
        }
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
	map_eve.insert(make_pair("userName",m_userName));			// 登陆用户
	map_eve.insert(make_pair("userId",m_userID));				// 数字账号
	map_eve.insert(make_pair("userType",m_areaID));				// 账号类型，例如：边锋、QQ、新浪等
	map_eve.insert(make_pair("areaName","未知"));				// 登陆区名称
	map_eve.insert(make_pair("userLev","0"));
	map_eve.insert(make_pair("vipLev","0"));	
	map_eve.insert(make_pair("leaderRank","0"));	
	map_eve.insert(make_pair("stage","0"));	
	map_eve.insert(make_pair("eliteStage","0"));
    
    [sObjTk consumeCurrencyWithType:[NSString stringWithUTF8String:consumeType.c_str()]
                         withAmount: itemNum
                        withItemNum: itemNum
                       withItemType: [NSString stringWithUTF8String:itemType.c_str()]
                       withItemName: [NSString stringWithUTF8String:itemName.c_str()]
                         withItemId: [NSString stringWithUTF8String:strFormat("%d",itemId).c_str()]];
	//onEventMapBF(_eveID,"充值货币消耗",map_eve);
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
    map_eve2.insert(make_pair("userName",m_userName));                   //充值用户
    map_eve2.insert(make_pair("userId",m_userID));                   //数字账号
    map_eve2.insert(make_pair("userType",m_areaID));                   //账号类型
    map_eve2.insert(make_pair("areaName","未知"));                   //登录区名称
    map_eve2.insert(make_pair("userLev","0"));
    map_eve2.insert(make_pair("vipLev","0"));
    map_eve2.insert(make_pair("leaderRank","0"));
    map_eve2.insert(make_pair("stage","0"));
    map_eve2.insert(make_pair("eliteStage","0"));
    map_eve2.insert(make_pair("isUserNew",s_isFirstLaunch));		// 是否新用户，1 新用户0 老用户
    map_eve2.insert(make_pair("isAreaNew",s_isFirstLaunch));		// 是否新用户(区)，1 新用户0 老用户
    
    
    [sObjTk onPurchase: [[NSString stringWithUTF8String:pay_price.c_str()] doubleValue]
        withPayOrderId: [NSString stringWithUTF8String:shop_id.c_str()]
           withPayType: [NSString stringWithUTF8String:pay_type.c_str()]
      withPayCardValue: [[NSString stringWithUTF8String:pay_price.c_str()] doubleValue]
     ];
    
    //onEventMapBF(_eveID2,"充值",map_eve2);
}

    
    // 注册
    void ProtocolTk::onRegisterBF(const string& userName, const string& areaID, const string& userType)
    {
        map<string,string> map_eve;
        const string _eveID = strFormat("%d",TG_EVENT_REGISTER);
        map_eve.insert(make_pair("userName",userName));					// 登出用户
        map_eve.insert(make_pair("userId","未知"));						// 数字账号
        map_eve.insert(make_pair("userType",userType));					// 账号类型，例如：边锋、QQ、新浪等
        map_eve.insert(make_pair("userCode","1"));						// 是否正式账号，1是0不是
        map_eve.insert(make_pair("channelName",s_channel));				// 注册渠道

        
        [sObjTk onRegister: [NSString stringWithUTF8String:userName.c_str()]
              withUserType: [NSString stringWithUTF8String:userType.c_str()]
                withUserId: @"none"
              withAreaName: [NSString stringWithUTF8String:areaID.c_str()]
               withUserLev: 0
            withUserVipLev: @"0"
         ];
        
        //onEventMapBF(_eveID,"注册事件",map_eve);
    }
    
    // 自定义计次事件
    void ProtocolTk::onCustomTimesEveBF(const string& eve_ID, const string& eve_Lab, map<string,string>& eve)
    {
        if (eve.empty())
        {
            return;
        }
        eve.insert(make_pair("userName",m_userName));					// 登出用户
        eve.insert(make_pair("userId",m_userID));					// 数字账号
        eve.insert(make_pair("userType",m_areaID));					// 账号类型，例如：边锋、QQ、新浪等
        eve.insert(make_pair("channelName",s_channel));				// 注册渠道
        onEventMapBF(eve_ID,eve_Lab,eve);
    }
    
    // 自定义计值事件
    void ProtocolTk::onCustomNumberEveBF(const string& eve_ID, const string& eve_Lab, const string& _value, map<string,string>& eve)
    {
        eve.insert(make_pair("userName",m_userName));					// 登出用户
        eve.insert(make_pair("userId",m_userID));					// 数字账号
        eve.insert(make_pair("userType",m_areaID));					// 账号类型，例如：边锋、QQ、新浪等
        eve.insert(make_pair("value",_value));					// 账号类型，例如：边锋、QQ、新浪等
        eve.insert(make_pair("channelName",s_channel));				// 注册渠道
        onEventMapBF(eve_ID,eve_Lab,eve);
    }
    
    //自定义事件(单个)
    void ProtocolTk::onEventSingleBF(const string& eveID)
    {
        if(sObjTk)
        {
            NSString* strID = [NSString stringWithUTF8String:eveID.c_str()];
            [sObjTk setCustomEvent:strID withLabel:@"none" paradic:nil paranum:1];
        }
    }
    
    //自定义事件(单个：主key(eveID) + 子key(eveLAB) 目前只支持eveID 所以eveID和eveLAB传同一值)
    void ProtocolTk::onEventDoubleBF(const string& eveID, const string& eveLAB)
    {
        if(sObjTk)
        {
            NSString* strID = [NSString stringWithUTF8String:eveID.c_str()];
            NSString* strLab = [NSString stringWithUTF8String:eveLAB.c_str()];
            //[sObjTk trackEvent:strID label:strLab];
            [sObjTk setCustomEvent:strID withLabel:strLab paradic:nil paranum:1];
        }
    }
    
    //自定义事件(map eveID和eveLAB传同一值)
    void ProtocolTk::onEventMapBF(const string& eveID, const string& eveLAB, map<string,string>& eve)
    {
        if(sObjTk)
        {
            NSString* strID = [NSString stringWithUTF8String:eveID.c_str()];
            NSString* strLab = [NSString stringWithUTF8String:eveLAB.c_str()];
            NSMutableDictionary* dicParam = [NSMutableDictionary dictionary];
            map<string,string>::iterator itMap = eve.begin();
            int i = 0;
            while (itMap!=eve.end()) {
                i++;
                NSString* strkey = [NSString stringWithUTF8String:itMap->first.c_str()];
                NSString* strValue = [NSString stringWithUTF8String:itMap->second.c_str()];
                [dicParam setObject:strValue forKey:strkey];
                //        if (i >= 10) {
                //            break;
                //        }
                itMap++;
            }
            if (dicParam.count > 0) {
                [sObjTk setCustomEvent:strID withLabel:strLab paradic:dicParam paranum:-1];
            }
        }
    }
    
    void ProtocolTk::javaLog(const char* logTag, const char* pFormat, ...)
    {
    }
    
}} //namespace cocos2d { namespace plugin {
