package org.cocos2dx.plugin;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

public class TokenInfo {

    private String accessToken; // Access Token值

    private Long expiresIn; // Access Token的有效期 以秒计

    private String account; // 登录账户名

    /**
     * {
     *   "data": {
     *     "expires_in": "36000",
     *     "access_token": "xxxxxxxxxxxxxxxx"
     *   },
     *   "error_code": 0
     * }
     */
    public static TokenInfo parseJson(String jsonString) {
        TokenInfo tokenInfo = null;
        if (!TextUtils.isEmpty(jsonString)) {
            Log.d("TokenInfo", jsonString);
            try {
                JSONObject dataJsonObj = new JSONObject(jsonString);

                String accessToken = dataJsonObj.optString("access_token");
                Long expiresIn = dataJsonObj.optLong("expires_in");

                tokenInfo = new TokenInfo();
                tokenInfo.setAccessToken(accessToken);
                tokenInfo.setExpiresIn(expiresIn);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tokenInfo;
    }

    public boolean isValid() {
        return !TextUtils.isEmpty(accessToken);
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String toJsonString() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("error_code", 0);

            JSONObject dataObj = new JSONObject();
            dataObj.put("expires_in", expiresIn);
            dataObj.put("access_token", accessToken);

            obj.put("data", dataObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }

}
