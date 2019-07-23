/********* RabbitMQPlugin.m Cordova Plugin Implementation *******/

#import "RabbitMQPlugin.h"
#import <RMQClient/RMQClient.h>

@implementation RabbitMQPlugin

-(void)initial:(CDVInvokedUrlCommand*)command{
    //do nithng,because Cordova plugin use lazy load mode.
}

- (void)coolMethod:(CDVInvokedUrlCommand*)command
{
    CDVPluginResult* pluginResult = nil;
    NSString* echo = [command.arguments objectAtIndex:0];
    
    if (echo != nil && [echo length] > 0) {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:echo];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    
    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
}

#ifdef __CORDOVA_4_0_0

- (void)pluginInitialize {
    NSLog(@"### pluginInitialize ");
    [self initPlugin];
}

#else

- (CDVPlugin*)initWithWebView:(UIWebView*)theWebView{
    NSLog(@"### initWithWebView ");
    if (self=[super initWithWebView:theWebView]) {
    }
    [self initPlugin];
    return self;
}

#endif

-(void)initPlugin{
    if (!SharedRabbitMQPlugin) {
        SharedRabbitMQPlugin = self;
    }
    
    [self startRabbitMQ];
}

-(void)startRabbitMQ{
    NSString * const url5 = @"amqp://gtd_mq:gtd_mq@pluto.guobaa.com:5672";
    // Do any additional setup after loading the view.
    RMQConnectionDelegateLogger * const delegate = [[RMQConnectionDelegateLogger alloc] init]; // implement RMQConnectionDelegate yourself to react to errors
    RMQConnection * const conn = [[RMQConnection alloc] initWithUri:url5 delegate:delegate];
    
    [conn start];
    id<RMQChannel> ch = [conn createChannel];
    
    RMQBasicConsumeOptions option = RMQBasicConsumeNoAck;
    [ch basicConsume:@"queueName" options:option handler:^(RMQMessage *received){
        NSString* body = [[NSString alloc] initWithData:[received body] encoding:NSUTF8StringEncoding];
        NSLog(@"%@", body);
    }];
}

+(void)fireDocumentEvent:(NSString*)eventName jsString:(NSString*)jsString{
    if (SharedRabbitMQPlugin) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [SharedRabbitMQPlugin.commandDelegate evalJs:[NSString stringWithFormat:@"cordova.fireDocumentEvent('jpush.%@',%@)", eventName, jsString]];
        });
        return;
    }
}

#pragma mark 将参数返回给js
-(void)handleResultWithValue:(id)value command:(CDVInvokedUrlCommand*)command {
    CDVPluginResult *result = nil;
    CDVCommandStatus status = CDVCommandStatus_OK;
    
    if ([value isKindOfClass:[NSString class]]) {
        value = [value stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    } else if ([value isKindOfClass:[NSNull class]]) {
        value = nil;
    }
    
    if ([value isKindOfClass:[NSObject class]]) {
        result = [CDVPluginResult resultWithStatus:status messageAsString:value];//NSObject 类型都可以
    } else {
        NSLog(@"Cordova callback block returned unrecognized type: %@", NSStringFromClass([value class]));
        result = nil;
    }
    
    if (!result) {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    }
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

@end
