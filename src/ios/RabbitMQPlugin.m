/********* RabbitMQPlugin.m Cordova Plugin Implementation *******/

#import "RabbitMQPlugin.h"
#import <RMQClient/RMQClient.h>

@implementation NSDictionary (RabbitMQ)
-(NSString*)toJsonString{
    NSError  *error;
    NSData   *data       = [NSJSONSerialization dataWithJSONObject:self options:0 error:&error];
    NSString *jsonString = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    return jsonString;
}
@end

@implementation NSString (RabbitMQ)
-(NSDictionary*)toDictionary{
    NSError      *error;
    NSData       *jsonData = [self dataUsingEncoding:NSUTF8StringEncoding];
    NSDictionary *dict     = [NSJSONSerialization JSONObjectWithData:jsonData options:0 error:&error];
    return dict;
}
@end

@implementation RabbitMQPlugin

- (void)initial:(CDVInvokedUrlCommand*)command{
    //初始化访问参数
    //NSDictionary* params = [command.arguments objectAtIndex:0];
    NSString *uid =  [command argumentAtIndex:0];
    NSString *deviceid =  [command argumentAtIndex:1];
    NSString *queueName =  [command argumentAtIndex:2];
    NSString *host =  [command argumentAtIndex:3];
    NSNumber *port =  [command argumentAtIndex:4];
    NSString *user = [command argumentAtIndex:5];
    NSString *passwd =  [command argumentAtIndex:6];
    
    NSLog(@"uid=%@;deviceid=%@;queueName=%@;host=%@;user=%@;passwd=%@", uid,deviceid,queueName,host,user,passwd);

//    [[[[[[[[self startRabbitMQ] uid: uid] deviceid: deviceid] queueName: queueName] host: host] port: port] user: user] passwd: passwd];
    [self startRabbitMQ:uid deviceid:deviceid queueName:queueName host:host port:port user:user passwd:passwd];
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

   // [self startRabbitMQ];
}

-(void)startRabbitMQ:(NSString*)uid deviceid:(NSString*)deviceid queueName:(NSString*)queueName host:(NSString*)host port:(NSNumber*)port user:(NSString*)user passwd:(NSString*)passwd
{
    NSString * const url5 = [[NSString alloc] initWithFormat:@"amqp://%@:%@@%@:%@", user, passwd, host, port];
    // Do any additional setup after loading the view.
//    RMQConnectionDelegateLogger * const delegate = [[RMQConnectionDelegateLogger alloc] init]; // implement RMQConnectionDelegate yourself to react to errors
    RMQConnection * const conn = [[RMQConnection alloc] initWithUri:url5 delegate:[RMQConnectionDelegateLogger new]];

    [conn start];
    id<RMQChannel> ch = [conn createChannel];
    
    RMQQueue *q = [ch queue:queueName options:RMQQueueDeclareDurable];
    
    //RMQExchange *x = [ch fanout:@"exchange.mwxing.fanout"];
    
    //RMQExchange *x1 = [ch direct:@"exchange.mwxing.direct"];
    
    
    //"mwxing.announce";
    
    
    [ch queueBind:queueName exchange:@"exchange.mwxing.fanout" routingKey:@"mwxing.announce"];
    //[q bind: x routingKey:@"mwxing.announce"];
    
    //"mwxing.[unionid]";
    
    [ch queueBind:queueName exchange:@"exchange.mwxing.direct" routingKey:[[NSString alloc] initWithFormat:@"mwxing.%@", uid]];
    //[q bind: x1 routingKey:[[NSString alloc] initWithFormat:@"mwxing.%@", uid]];
    
    //public String routingkeyDevice = "mwxing.[unionid].[deviceId]";
    NSArray *array = [queueName componentsSeparatedByString:@"."];
    
    [ch queueBind:queueName exchange: @"exchange.mwxing.direct" routingKey:[[NSString alloc] initWithFormat:@"mwxing.%@.%@", uid,array[1]]];
    //[q bind:x1 routingKey:[[NSString alloc] initWithFormat:@"mwxing.%@.%@", uid,array[1]]];

    RMQBasicConsumeOptions option = RMQBasicConsumeNoAck;
    [ch basicConsume: queueName options:option handler:^(RMQMessage *received){
        
        NSString* body = [[NSString alloc] initWithData:[received body] encoding:NSUTF8StringEncoding];
                          
        NSDictionary *finishDic;
        finishDic = [NSDictionary dictionaryWithObjectsAndKeys:
                     body, @"body",nil];
        NSLog(@"%@", body);
        
        [RabbitMQPlugin fireDocumentEvent:@"receivedMessage"
                                 jsString:[finishDic toJsonString]];
    }];
}

+(void)fireDocumentEvent:(NSString*)eventName jsString:(NSString*)jsString{
    if (SharedRabbitMQPlugin) {
        //RabbitMQPlugin.receivedMessageInAndroidCallback
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [SharedRabbitMQPlugin.commandDelegate evalJs:[NSString stringWithFormat:@"cordova.fireDocumentEvent('rabbitmq.%@',%@)", eventName, jsString]];
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
