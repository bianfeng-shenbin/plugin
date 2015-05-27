#ifndef __CCX_PROTOCOL_TK_H__
#define __CCX_PROTOCOL_TK_H__

#include "PluginProtocol.h"
#include <map>
#include <string>
#include <vector>


using namespace std;

//TDID_EXCEPTION
#define TDKEY_UPDATE_FAIL_ERROR "更新失败原因"
#define TDKEY_SYSCLOSE_STATE			"程序异常退出"

#define TDVALUE_SYSCLOSE_SCENE			"TDVALUE_SYSCLOSE_SCENE"		// 当前场景
#define TDVALUE_SYSCLOSE_STATE			"场景"

#define TDID_EXCEPTION                  "异常统计"

#define TDKEY_SAVE_LOCAL				"TDKEY_SAVE_LOCAL"
//BFTK游戏标记
#define TDKEY_FIRST_GAME                    "TDKEY_FIRST_GAME"
#define TDKEY_FIRST_LOGIN                   "TDKEY_FIRST_LOGIN"

#define TDKEY_DATE                          "TDKEY_DATE"
#define TDKEY_CLOCK                         "TDKEY_CLOCK"
#define TDKEY_DAY_DURATION                  "TDKEY_DAY_DURATION"
#define TDKEY_DUIHUAN_HUANWEI_DAY			"TDKEY_DUIHUAN_HUANWEI_DAY"
#define TDKEY_BANBEN_DENGLU_DATE			"TDKEY_BANBEN_DENGLU_DAY"


typedef enum
{
    TG_EVENT_INVALID = 0,
    TG_EVENT_SERVER_SEL = 1,//大区选择
    TG_EVENT_REFRESH_SERVER_LIST = 2,//刷新服务列表
    TG_EVENT_PLAY_SINGLE = 3,//单机试玩
    TG_EVENT_LOGIN = 4,//正常登陆
    TG_EVENT_TOURIST_LOGIN = 5,//游客登陆
    TG_EVENT_REGISTER = 6,//注册
    TG_EVENT_FEEDBACK = 7,//反馈
    TG_EVENT_MORE_GAME = 8,//更多游戏
    TG_EVENT_SHENFEN_MODE = 9,//身份模式
    TG_EVENT_JINGJI_MODE = 10,//竞技模式
    TG_EVENT_BISAI_MODE = 11,//比赛模式
    TG_EVENT_SHOP = 12,//场选择－商城
    TG_EVENT_CHOUJIANG = 13,//商城－抽奖
    // TG_EVENT_CHONGZHI = 14,//商城－充值 废掉 改为 TG_EVENT_RMB_RECHARGE
    TG_EVENT_HALL_MENU = 15,//大厅－菜单
    TG_EVENT_HALL_MENU_BUILD = 16,//大厅－菜单－创建
    TG_EVENT_HALL_MENU_SET = 17,//大厅－菜单－设置
    TG_EVENT_HALL_MENU_PACKAGE = 18,//大厅－菜单－包裹
    TG_EVENT_HALL_MENU_LINGJIANG = 19,//大厅－菜单－领奖
    TG_EVENT_HALL_MENU_SHOP = 20,//大厅－菜单－商城
    TG_EVENT_HALL_REFRESH = 21,//大厅－刷新
    TG_EVENT_HALL_SHOW_ALL_TABLE = 22,//大厅－显示全部
    TG_EVENT_HALL_SEARCH = 23,//大厅－查找房间
    TG_EVENT_HALL_QUICK_START = 24,//大厅－快速开始
    TG_EVENT_HALL_BACK = 25,//大厅－返回
    TG_EVENT_HALL_PLAYER_INFO = 26,//大厅－玩家信息
    TG_EVENT_HALL_ZHANGONG = 27,//大厅－战功
    TG_EVENT_SET_OK = 28,//设置－确定
    TG_EVENT_HALL_SHOW_WAIT_TABLE = 29,//大厅－显示等待
    TG_EVENT_MATCH_BAOMING = 30,//比赛－报名参赛
    TG_EVENT_MATCH_BACK = 31,//比赛－返回
    TG_EVENT_MATCH_INTRUDUCE = 32,//比赛－比赛介绍
    TG_EVENT_MATCH_TOUSHU = 33,//比赛－投诉
    TG_EVENT_SET_RESET = 34,//设置－重置
    TG_EVENT_MATCH_MATCH = 35,//比赛－开始匹配
    TG_EVENT_TABLE_OPEN_MODIFY_VIEW = 36,//桌子－打开修改界面
    TG_EVENT_TABLE_MODIFY = 37,//桌子－修改
    TG_EVENT_TABLE_SWITCH = 38,//桌子－换桌
    TG_EVENT_TABLE_BACK = 39,//桌子－返回
    TG_EVENT_TABLE_DIANJIANG = 40,//桌子－点将
    TG_EVENT_TABLE_DEL_SHENFEN = 41,//桌子－派出身份
    TG_EVENT_TABLE_START_GAME = 42,//桌子－开始游戏
    TG_EVENT_GAME_MENU = 43,//游戏－菜单
    TG_EVENT_GAME_MENU_SET = 44,//游戏－菜单－设置
    TG_EVENT_GAME_MENU_PAIJU = 45,//游戏－菜单－牌局
    TG_EVENT_GAME_MENU_CHAT = 46,//游戏－菜单－聊天
    TG_EVENT_GAME_MENU_TUOGUAN = 47,//游戏－菜单－托管
    TG_EVENT_GAME_MENU_PAUSE = 48,//游戏－菜单－暂停
    TG_EVENT_GAME_MENU_EXIT = 49,//游戏－菜单－退出
    TG_EVENT_GAME_SLIDE_TUOGUAN = 50,//游戏－滑动托管
    TG_EVENT_CHONGZHI_FEEDBACK = 51,//充值－问题反馈
    TG_EVENT_MATCH_CANCEL_MATCH = 52,//比赛－取消匹配
    TG_EVENT_LAUNCH = 53, // 程序启动
    TG_EVENT_OVERTIME = 54,//超时事件
    TG_EVENT_LOGOUT = 55,//退出事件
    TG_EVENT_REG_DETAIL = 56,//注册事件，需要传递用户名，用户id，账号类型
    TG_EVENT_PAY_SHOPPING = 60,// 商品购买事件
    TG_EVENT_MAX = 200,

	TG_EVENT_RMB_RECHARGE = 14,// 充值事件，说明：如果玩家是用点卡充值的，如果一个玩家用28块钱买了价值30元的点卡，30元点卡可以兑换游戏内300个元宝，则cardPrice为30，sellPrice为28，cardValue为300
	TG_EVENT_ITEM_CONSUME = 61, // 消耗事件，消耗道具 说明：跟钱无关的物品消耗均在此事件内，包括游戏资源，如果消耗一个喇叭，则 itemName喇叭，consume填1，如果消耗10个木头，获得了1个包子，则 itemName木头，consume填10，get_itemname填包子，get_num填1
	TG_EVENT_SILVER_CONSUME = 62, // 消耗事件，消耗游戏币 说明：游戏充值获得的货币（例如元宝）的产出和消耗，包含系统产出充值货币，如果通关赠送了10个元宝，consume填10，如果消耗10个元宝买9个包子，则consume填-10， itemName填包子， itemNum填9


    // 自定义事件接口 80000 - 99999
    TG_EVENT_CUSTOM_INVALID = 80000,									//自定义事件开始
    TG_EVENT_CUSTOM_SHOPPING = 80001,									//请求购买事件
    TG_EVENT_CUSTOM_SHOP_FULL = 80002,									//购买充值结果事件

    TG_EVENT_CUSTOM_NEWPLAYER_VALID = 80010,							//新玩家有效用户(打满1局游戏)
    TG_EVENT_CUSTOM_GAME_TIMES = 80020,									//玩家单次游戏时间(分)
    TG_EVENT_CUSTOM_GAME_TIME = 80021,									//玩家单次游戏盘数
    TG_EVENT_CUSTOM_LOGIN_FAIL = 80030,									//玩家登录失败
    TG_EVENT_CUSTOM_LOGIN_SUCCESS = 80031,								//玩家登录成功
    TG_EVENT_CUSTOM_OVERTIMES = 80040,									//玩家游戏超时事件（出牌）
    TG_EVENT_CUSTOM_FUNCTION_BTN = 80050,								//模块按钮点击事件(80050-80060)
    TG_EVENT_CUSTOM_CUT_GAME = 80060,									//游戏断线事件

    TG_EVENT_CUSTOM_SECNE_CHANGE_EVE = 80150,							//游戏场景切换事件(80150 - 80180)
    TG_EVENT_CUSTOM_AFFICHE_BTN = 80110,								//公告按钮点击事件
    TG_EVENT_CUSTOM_CHANGE_USER_BTN = 80111,							//切换账号按钮点击事件
    TG_EVENT_CUSTOM_USER_LOGIN_BTN = 80112,								//登录账号按钮点击事件
    TG_EVENT_CUSTOM_USER_PASSWORD_SAVE_EVE = 80113,						//账号密码保存事件
    TG_EVENT_CUSTOM_USER_PASSWORD_NO_SAVE_EVE = 80114,					//账号密码不保存事件
    TG_EVENT_CUSTOM_JION_ROOM = 80200,									//进入房间组事件
    TG_EVENT_CUSTOM_JION_ROOM_BACK = 80201,								//进入房间组结果事件

    TG_EVENT_CUSTOM_DEFEND_REAL_NAME_BTN = 80220,						//防沉迷实名认证按钮
    TG_EVENT_CUSTOM_DEFEND_CANCEL_NAME_BTN = 80221,						//防沉迷取消实名认证按钮
    TG_EVENT_CUSTOM_DEFEND_REAL_NAME_SEND = 80223,						//防沉迷实名认证事件
    TG_EVENT_CUSTOM_DEFEND_CANCEL_NAME_SEND = 80224,					//防沉迷取消实名认证事件
    TG_EVENT_CUSTOM_DEFEND_REAL_NAME_BACK = 80225,						//防沉迷实名认证返回事件
    TG_EVENT_CUSTOM_DEFEND_INFO_SEND_BACK = 80210,						//防沉迷数据上传返回事件(成功)
    TG_EVENT_CUSTOM_DEFEND_INFO_SEND_BACK_FAIL = 80211,					//防沉迷数据上传返回事件(失败)
    TG_EVENT_CUSTOM_DEFEND_INFO_SEND_BACK_OUT = 80212,					//防沉迷数据上传返回事件(超时)
    TG_EVENT_CUSTOM_DEFEND_INFO_STATE = 80230,							//防沉迷时段(80230-80240)

    TG_EVENT_CUSTOM_HELP_SCENE_BTN_EVE = 80300,							//帮助场景按钮事件（80300 - 80320）

    TG_EVENT_CUSTOM_USERINFO_SCENE_BTN_EVE = 80400,						//个人中心场景按钮事件(80400 - 80420)

    TG_EVENT_CUSTOM_NPC_BTN_EVE = 80500,								//NPC按钮事件
    TG_EVENT_CUSTOM_NPC_PAG_EVE = 80502,								//包裹按钮事件
    TG_EVENT_CUSTOM_NPC_PAG_BACK = 80503,								//NPC包裹返回事件
    TG_EVENT_CUSTOM_NPC_PAG_BACK_NUM = 80504,							//NPC包裹返回事件(记值)

    TG_EVENT_CUSTOM_GAME_CHANGE_DESK = 80600,							//游戏场景按钮事件（80600-80650）
    TG_EVENT_CUSTOM_GAME_UNUSUAL_OUT = 80660,							//游戏异常退出

    TG_EVENT_CUSTOM_MUSIC_SET = 80700,									//设置按钮事件音乐
    TG_EVENT_CUSTOM_MUSIC_SET_SOUND = 80701,							//设置按钮事件音效
    TG_EVENT_CUSTOM_ANIM_SET_OPEN = 80702,								//设置按钮事件动画效果
    TG_EVENT_CUSTOM_ANIM_SET_CLOSE = 80703,								//设置按钮事件动画效果

    TG_EVENT_CUSTOM_CHAT_TEXT = 80710,									//常用语文字(80710-80729)
    TG_EVENT_CUSTOM_CHAT_EMOTION = 80730,								//常用语表情(80730-80750)

    TG_EVENT_CUSTOM_SCENE_LOADING_TIMES = 80800,						//场景切换时间(80800-80830) 80800:大厅 80801:商城

    TG_EVENT_CUSTOM_GAME_START_TIMESPACE = 80900,						//成桌时间(进游戏场到开始游戏时间)
    TG_EVENT_CUSTOM_GAME_OVER = 80901,									//怒退时间(进游戏场到开始游戏时间)
    TG_EVENT_CUSTOM_GAME_TIMES_ONE_ROUND = 80902,						//一局游戏的时间
    TG_EVENT_CUSTOM_GAME_CHANGE_DEST_STATE = 80910,						//换桌倾向(80910-80920)：例：80911桌上1人 80912桌上2人.....
    TG_EVENT_CUSTOM_BTN_BIND_MOBILEPHONE = 80930,                       //绑定手机按钮点击事件
    TG_EVENT_CUSTOM_BTN_CANCEL_BIND_MOBILEPHONE = 80931,                //取消绑定手机按钮点击事件
    TG_EVENT_CUSTOM_BIND_MOBILEPHONE_SUCCESS = 80932,                   //绑定手机成功事件
    TG_EVENT_CUSTOM_BIND_MOBILEPHONE_FAILE = 80933,                     //绑定手机失败事件
    TG_EVENT_CUSTOM_QUICK_REGIST_ACCOUNT_COUNT = 80934,                 //快速注册账号个数
    TG_EVENT_CUSTOM_BIND_MOBILEPHONE_TIME = 80935,                      //绑定手机操作时间
    TG_EVENT_CUSTOM_BIND_MOBILEPHONE_ERROR = 80936,                     //绑定手机异常事件
    TG_EVENT_CUSTOM_BTN_CHANGE_PASSWORD = 80937,                        //修改密码按钮点击事件
    TG_EVENT_CUSTOM_CHANGE_PASSWORD_SUCCESS = 80938,                    //修改密码成功事件
    TG_EVENT_CUSTOM_CHANGE_PASSWORD_FAILE = 80939,                      //修改密码失败事件
    TG_EVENT_CUSTOM_BTN_CANCEL_CHANGE_PASSWORD = 80940,                 //取消修改密码事件
    TG_EVENT_CUSTOM_CHANGE_PASSWORD_TIME = 80941,                       //修改密码操作时间
    TG_EVENT_CUSTOM_HAD_BIND_MOBILEPHONE = 80942,                       //手机号以绑定事件

TG_EVENT_CUSTOM_LOGIN_TIME = 80943, // 登录时间
TG_EVENT_CUSTOM_UPDATE_FAIL = 80944, // 更新失败

    TG_EVENT_CUSTOM_MAX = 99999,										//自定义事件

} eTGEventType;

namespace cocos2d
{
namespace plugin
{

typedef std::map<std::string, std::string> TTkDeveloperInfo;

class ProtocolTk : public PluginProtocol
{
public:
    ProtocolTk();
    virtual ~ProtocolTk();
    void configDeveloperInfo(TTkDeveloperInfo devInfo);
public:
		static void javaLog(const char* logTag, const char* pFormat, ...);
    // 登入
    void onPlayerAccountBF(int nFlag, const string& isFirstLogin, const string& userName, const string& userID, const string& areaID, const string& userType, const string& userLevel);
    // 0点超时事件
    void onLoginDurationBF(const string& time);
    // 登出
    void onLoginOutBF(const string& dayTime, const string& oneTime);
    // 盘数，开局
    void onGameBegin(const string& userLevel);
    // 任务
    void onGameTask(const string& userLevel, const string& taskType, const string& taskName, const string& amount);
    // 低保，银子消耗
    void onSilverCost(const string& dibaoType, const string& dibaoCost);
    // 自定义计次事件
    void onCustomTimesEveBF(const string& eve_ID, const string& eve_Lab, map<string,string>& eve);
    // 自定义计值事件
    void onCustomNumberEveBF(const string& eve_ID, const string& eve_Lab, const string& _value, map<string,string>& eve);
    // 注册
    void onRegisterBF(const string& ptid, const string& numId, const string& userType);
    //自定义事件(单个)
    void onEventSingleBF(const string& eveID);
    //自定义事件(单个：主key(eveID) + 子key(eveLAB) 目前只支持eveID 所以eveID和eveLAB传同一值)
    void onEventDoubleBF(const string& eveID, const string& eveLAB);
    //自定义事件(map eveID和eveLAB传同一值)
    void onEventMapBF(const string& eveID, const string& eveLAB, map<string,string>& eve);
    
		// 充值
		void onPayShoppingBF(const string& pay_type, const string& pay_price, const string& shop_id, const string& shop_name);
		// 消耗（如果是消耗银子，consumeVal必须是负值）
		/*type	string		进出类型，进有：赠送，剧情掉落，通关奖励等 ，出有：购买
		consume	double		进出量，账号的充值货币增加计为正值，账号的充值货币减少计为负值
		itemId	int	 	消耗道具ID
		itemType	string	 	消耗道具类型
		itemName	string	 	消耗道具名称
		itemNum	int	 	消耗道具数量*/
		void onSilverConsume(
			const string& consumeType, 
			double consumeVal,
			int itemId,
			const string& itemType,
			const string& itemName,
			int itemNum);
private:
    static string s_isFirstLaunch;	//是否是新用户：新用户（1） 老用户(0)
    static string s_userName;			//用户名
    static string s_userID;			//用户ID
    static string s_areaID;			//账号类型
    static string s_channel;			//注册渠道
    string m_userName;
    string m_userID;
    string m_areaID;
    bool is_TKBF_Open;
private:
	  // 初始化改为调用configDeveloperInfo，所以private，参数放入map
    void onInitBF(const string& appID, const string& channel,const string& groupID, const string& isFirstLaunch, int tvMode);
    void onInitBF_android(int isUserNew, string areaId, string areaName, string serverId, string serverName, string channelName, string device_code, string appId, int tvMode);
    void onPlayerAccountBF_android(int type,int isUserNew,string userName,string userId
                                   ,string userType,string areaId,string areaName,string serverId,string serverName,string reason,
                                   string channelName,string roomName, string userLevel);
    void onCostSilver_android(string dibaoType, string dibaoCost);
    void onGameTask_android(
        string userLevel, string taskType, string taskName, string amount);
    void onGameBegin_android(string userLevel);
    void onLoginOutBF_android();
    void onEventSingleBF_android(string eveId);
    void onEventDoubleBF_android(string eveId, string eveLab);
    void onEventMapBF_android(string eveId, string eveLab, string mapMsg);
};

}
} // namespace cocos2d { namespace plugin {

#endif /* __CCX_PROTOCOL_TK_H__ */
