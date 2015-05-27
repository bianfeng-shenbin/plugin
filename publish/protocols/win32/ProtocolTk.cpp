#include "ProtocolTk.h"
#include "stdlib.h"



namespace cocos2d
{
	namespace plugin
	{

		string strFormat(const char* format, ...)
		{
			return "";
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
		}

		// 初始化
		void ProtocolTk::onInitBF(const string& appID, const string& channel, const string& groupID, const string& isFirstLaunch, int tvMode)
		{
		}

		// 登入
		void ProtocolTk::onPlayerAccountBF(int nFlag, const string& isFirstLogin, const string& userName, const string& userID, const string& areaID, const string& userType, const string& userLevel)
		{
		}

		// 0点超时事件
		void ProtocolTk::onLoginDurationBF(const string& time)
		{
			// SDK已处理 忽略
		}

		// 登出
		void ProtocolTk::onLoginOutBF(const string& dayTime, const string& oneTime)
		{
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

		// 自定义计次事件
		void ProtocolTk::onCustomTimesEveBF(const string& eve_ID, const string& eve_Lab, map<string, string>& eve)
		{
		}

		// 自定义计值事件
		void ProtocolTk::onCustomNumberEveBF(const string& eve_ID, const string& eve_Lab, const string& _value, map<string, string>& eve)
		{
		}

		// 购买道具、商品
		void ProtocolTk::onPayShoppingBF(const string& pay_type, const string& pay_price, const string& shop_id, const string& shop_name)
		{
		}

		// 注册
		void ProtocolTk::onRegisterBF(const string& ptId, const string& numId, const string& userType)
		{
		}

		//自定义事件(单个)
		void ProtocolTk::onEventSingleBF(const string& eveID)
		{
		}

		//自定义事件(单个：主key(eveID) + 子key(eveLAB) 目前只支持eveID 所以eveID和eveLAB传同一值)
		void ProtocolTk::onEventDoubleBF(const string& eveID, const string& eveLAB)
		{
		}

		//自定义事件(map eveID和eveLAB传同一值)
		void ProtocolTk::onEventMapBF(const string& eveID, const string& eveLAB, map<string, string>& eve)
		{
		}

		/////////////////////////////////////////////////////////////////////////

		void ProtocolTk::onInitBF_android(int isUserNew, string areaId, string areaName, string serverId, string serverName, string channelName, string device_code, string appId, int tvMode)
		{
		}

		void ProtocolTk::onPlayerAccountBF_android(int type, int isUserNew, string userName, string userId
			, string userType, string areaId, string areaName, string serverId, string serverName, string reason,
			string channelName, string roomName, string userLevel)
		{
		}

		void ProtocolTk::onCostSilver_android(string dibaoType, string dibaoCost)
		{
		}

		void ProtocolTk::onGameTask_android(
			string userLevel, string taskType, string taskName, string amount)
		{
		}

		void ProtocolTk::onGameBegin_android(string userLevel)
		{
		}


		void ProtocolTk::onLoginOutBF_android()
		{
		}

		void ProtocolTk::onEventSingleBF_android(string eveId)
		{
		}

		void ProtocolTk::onEventDoubleBF_android(string eveId, string eveLab)
		{
		}

		void ProtocolTk::onEventMapBF_android(string eveId, string eveLab, string mapMsg)
		{
		}

void ProtocolTk::javaLog(const char* logTag, const char* pFormat, ...)
{
}

		/////////////////////////////////////////////////////////////////////


	}
} // namespace cocos2d { namespace plugin {


