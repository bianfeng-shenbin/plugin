package org.cocos2dx.plugin;

import java.util.Map;

import android.os.AsyncTask;

import com.huawei.gamebox.buoy.sdk.util.DebugConfig;

public class ReqTask extends AsyncTask<Void, Void, String>
{
    private static final String TAG = ReqTask.class.getSimpleName();
    
    private Delegate delegate = null;
    
    private Map<String, String> reqParams = null;
    
    private String reqUrl = null;
    
    public ReqTask(Delegate deg, Map<String, String> params, String url)
    {
        delegate = deg;
        reqParams = params;
        reqUrl = url;
    }
    
    @Override
    protected String doInBackground(Void... params)
    {
        String result = null;
        try
        {
            /**
             * 发起网络请求，这里使用一个一秒的延迟代替，请CP使用安全的网络实现交互
             */
            Thread.sleep(1000);
            result = "result";
            DebugConfig.d(TAG, "request the network for result");
            /**
             * 请求地址为reqUrl，请求的POST参数为reqParams，使用UTF-8编码格式
             */
            // result = NetTool.sendPostRequest(reqUrl, reqParams, "utf-8");
        }
        catch (Exception e)
        {
            DebugConfig.d(TAG, e.getMessage());
        }
        return result;
    }
    
    @Override
    protected void onPostExecute(String result)
    {
        delegate.execute(result);
    }
    
    public interface Delegate
    {
        public void execute(String result);
    }
    
}