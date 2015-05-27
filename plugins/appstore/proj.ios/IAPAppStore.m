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
#import "IAPAppStore.h"
#import "PayWrapper.h"

#define OUTPUT_LOG(...)     if (self.debug) NSLog(__VA_ARGS__);
#define ARRAY_SIZE(a) sizeof(a)/sizeof(a[0])

@implementation IAPAppStore

@synthesize debug = __debug;

const char* jailbreak_tool_pathes[] = {
    "/Applications/Cydia.app",
    "/Library/MobileSubstrate/MobileSubstrate.dylib",
    "/bin/bash",
    "/usr/sbin/sshd",
    "/etc/apt"
};

- (bool)isJailBreak
{
    for (int i=0; i<ARRAY_SIZE(jailbreak_tool_pathes); i++) {
        if ([[NSFileManager defaultManager] fileExistsAtPath:[NSString stringWithUTF8String:jailbreak_tool_pathes[i]]]) {
            NSLog(@"The device is jail broken!");
            return YES;
        }
    }
    NSLog(@"The device is NOT jail broken!");
    return NO;
}

- (void) configDeveloperInfo: (NSMutableDictionary*) cpInfo
{
    // Init Store
    [[SKPaymentQueue defaultQueue] addTransactionObserver:self];
}

- (void) payForProduct: (NSMutableDictionary*) profuctInfo
{
    if ( profuctInfo != nil )
    {
        NSString* strProductID = (NSString*) [profuctInfo objectForKey:@"productId"];
        if (strProductID != nil) {
            if ([self isJailBreak]) {
                SKPayment *payment = [SKPayment paymentWithProductIdentifier:strProductID];
                [[SKPaymentQueue defaultQueue] addPayment:payment];
            }
            else {
                NSMutableSet *products = [NSMutableSet setWithCapacity:1];
                [products addObject:strProductID];
                SKProductsRequest *request = [[[SKProductsRequest alloc] initWithProductIdentifiers:products] autorelease];
                request.delegate = self;
                [request start];
            }
        }
        else {
            OUTPUT_LOG(@"payForProduct null productId");
        }
    }
}

- (void) setDebugMode: (BOOL) isDebugMode
{
    OUTPUT_LOG(@"appstore setDebugMode invoked(%d)", isDebugMode);
    self.debug = isDebugMode;
}

- (NSString*) getSDKVersion
{
    return @"7.0";
}

- (NSString*) getPluginVersion
{
    return @"0.2.0";
}

//////////////////////////////////////////////////////////////////////
- (void)productsRequest:(SKProductsRequest *)request didReceiveResponse:(SKProductsResponse *)response
{
    NSArray *products = response.products;
    //NSArray *validproducts = response.invalidProductIdentifiers;
    if (products.count > 0 ) {
        SKProduct* product = [products objectAtIndex:0];
        SKPayment *payment = [SKPayment paymentWithProductIdentifier:product.productIdentifier];
        [[SKPaymentQueue defaultQueue] addPayment:payment];
    }
    else {
        [PayWrapper onPayResult:self withRet:kIAPFail withMsg:@"道具不存在"];
    }
}

//////////////////////////////////////////////////////////////////////
- (void)paymentQueue:(SKPaymentQueue *)queue updatedTransactions:(NSArray *)transactions
{
    for (SKPaymentTransaction *transaction in transactions)
    {
        switch (transaction.transactionState)
        {
            case SKPaymentTransactionStatePurchasing:
                [self addTransaction:transaction];
                break;
            case SKPaymentTransactionStatePurchased:
                [self completeTransaction:transaction];
                break;
            case SKPaymentTransactionStateFailed:
                [self failedTransaction:transaction];
                break;
            case SKPaymentTransactionStateRestored:
                [self restoreTransaction:transaction];
            default:
                break;
        }
    }
}

- (void) addTransaction: (SKPaymentTransaction *)transaction
{
    
}

- (void) failedTransaction: (SKPaymentTransaction *)transaction
{
    if (transaction.error.code != SKErrorPaymentCancelled)
    {
        // Optionally, display an error here.
        [PayWrapper onPayResult:self withRet:kIAPFail withMsg:@"支付失败"];
    }
    else {
        [PayWrapper onPayResult:self withRet:kIAPCancel withMsg:@"支付取消"];
    }
    [[SKPaymentQueue defaultQueue] finishTransaction: transaction];
}

- (void) restoreTransaction: (SKPaymentTransaction *)transaction
{
    [[SKPaymentQueue defaultQueue] finishTransaction: transaction];
    
}

- (void) completeTransaction:(SKPaymentTransaction*)transaction
{
    //将完成后的交易信息移出队列
    [[SKPaymentQueue defaultQueue]finishTransaction: transaction];
    
    NSString *str = [[NSString alloc] initWithData:transaction.transactionReceipt encoding:NSUTF8StringEncoding];
    NSLog(@"THE TRANSACTION RECEIPT DATA IS %@", str);
    [PayWrapper onPayResult:self withRet:kIAPSuccess withMsg:str];
}

@end
