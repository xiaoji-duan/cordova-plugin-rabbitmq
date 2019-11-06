//
//  RabbitMQPlugin.h
//  HelloRabbitMQ
//
//  Created by Jinyang Zhang on 2019/7/22.
//  Copyright © 2019 Jinyang Zhang. All rights reserved.
//

#import <Cordova/CDV.h>

@interface RabbitMQPlugin : CDVPlugin {

    // Member variables go here.
    
}


- (void)startRabbitMQ:(NSString*)uid deviceid:(NSString*)deviceid queueName:(NSString*)queueName host:(NSString*)host port:(NSNumber*)port user:(NSString*)user passwd:(NSString*)passwd ;


@end

static RabbitMQPlugin *SharedRabbitMQPlugin;
