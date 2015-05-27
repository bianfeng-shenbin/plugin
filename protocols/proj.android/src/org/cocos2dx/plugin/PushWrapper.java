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
package org.cocos2dx.plugin;

import java.util.Hashtable;

import android.util.Log;

public class PushWrapper {
	public static final int PUSHRESULT_BIND = 0;
	public static final int PUSHRESULT_UNBIND  = 1;
	public static final int PUSHRESULT_TIMEOUT = 2;

	public static void onBindResult(InterfacePush obj, int ret, Hashtable<String, String> resInfo) {
		final int curRet = ret;
        final Hashtable<String, String> curResInfo = resInfo;
		final InterfacePush curObj = obj;
		PluginWrapper.runOnGLThread(new Runnable() {
			@Override
			public void run() {
				String name = curObj.getClass().getName();
				name = name.replace('.', '/');
				nativeOnBindResult(name, curRet, curResInfo);
			}
		});
	}
	private static native void nativeOnBindResult(String className, int ret, Hashtable<String, String> resInfo);
    
    public static void onNotificationResult(InterfacePush obj, String title, String description, String customContentString) {
		final String curTitle = title;
		final String curDescription = description;
		final String curCustomContentString = customContentString;
		final InterfacePush curObj = obj;
		Log.d("TTAGE", "onNotificationResult_push_wrapper");
//		PluginWrapper.runOnGLThread(new Runnable() {
//			@Override
//			public void run() {
//				String name = curObj.getClass().getName();
//				name = name.replace('.', '/');
//				Log.d("TTAGE", "onNotificationResult_push_wrapper");
//				//nativeOnNotificationResult(name, curTitle, curDescription, curCustomContentString);
//			}
//		});
	}
    
	private static native void nativeOnNotificationResult(String className, String title, String description, String customContentString);
}
