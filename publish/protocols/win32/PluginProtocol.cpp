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
#include "PluginProtocol.h"

#define LOG_TAG     "PluginProtocol"

namespace cocos2d { namespace plugin {

PluginProtocol::~PluginProtocol()
{
    
}

std::string PluginProtocol::getPluginVersion()
{
    return "";
}

std::string PluginProtocol::getSDKVersion()
{
    return "";
}

void PluginProtocol::setDebugMode(bool isDebugMode)
{
   
}

void PluginProtocol::callFuncWithParam(const char* funcName, PluginParam* param, ...)
{
    
}

void PluginProtocol::callFuncWithParam(const char* funcName, std::vector<PluginParam*> params)
{
    
}

std::string PluginProtocol::callStringFuncWithParam(const char* funcName, PluginParam* param, ...)
{
	return "";
}

std::string PluginProtocol::callStringFuncWithParam(const char* funcName, std::vector<PluginParam*> params)
{
	return "";
}

int PluginProtocol::callIntFuncWithParam(const char* funcName, PluginParam* param, ...)
{
   return 0;
}

int PluginProtocol::callIntFuncWithParam(const char* funcName, std::vector<PluginParam*> params)
{
    return 0;
}

bool PluginProtocol::callBoolFuncWithParam(const char* funcName, PluginParam* param, ...)
{
    return true;
}

bool PluginProtocol::callBoolFuncWithParam(const char* funcName, std::vector<PluginParam*> params)
{
    return true;
}

float PluginProtocol::callFloatFuncWithParam(const char* funcName, PluginParam* param, ...)
{
    return 0;
}

float PluginProtocol::callFloatFuncWithParam(const char* funcName, std::vector<PluginParam*> params)
{
    return 0;
}

}} //namespace cocos2d { namespace plugin {
