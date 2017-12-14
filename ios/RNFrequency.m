#import "RNFrequency.h"
#import "TGSineWaveToneGenerator.h"

// Used to send events to JS
#if __has_include(<React/RCTBridge.h>)
#import <React/RCTBridge.h>
#elif __has_include("RCTBridge.h")
#import "RCTBridge.h"
#else
#import "React/RCTBridge.h"
#endif

#if __has_include(<React/RCTEventDispatcher.h>)
#import <React/RCTEventDispatcher.h>
#elif __has_include("RCTEventDispatcher.h")
#import "RCTEventDispatcher.h"
#else
#import "React/RCTEventDispatcher.h"
#endif

@interface RNFrequency ()
@property(strong) TGSineWaveToneGenerator *toneGenRef;
@end

@implementation RNFrequency

@synthesize bridge = _bridge;

static NSString * const AUDIO_CHANGED_NOTIFICATION = @"AUDIO_CHANGED_NOTIFICATION";

- (instancetype)init
{
    if (self = [super init]) {
        self.toneGenRef = [[TGSineWaveToneGenerator alloc] initWithChannels:2];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(audioRouteChangeListenerCallback:) name:AVAudioSessionRouteChangeNotification object:nil];
    }
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)audioRouteChangeListenerCallback:(NSNotification*)notification
{
    [_bridge.eventDispatcher sendDeviceEventWithName:AUDIO_CHANGED_NOTIFICATION
      body:(@{
        @"audioJackPluggedIn": @([RNFrequency isAudioJackInUse])
      })];
}

+ (BOOL) isAudioJackInUse
{
    AVAudioSessionRouteDescription* route = [[AVAudioSession sharedInstance] currentRoute];

    for (AVAudioSessionPortDescription* desc in [route outputs]) {
        if ([[desc portType] isEqualToString:AVAudioSessionPortHeadphones])
            return YES;
    }

    return NO;
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(playFrequency:(double)frequency duration:(double)duration resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    self.toneGenRef->_channels[0].frequency=frequency;
    self.toneGenRef->_channels[0].frequency=frequency;
    [self.toneGenRef playForDuration:duration];
    resolve(@YES);
}

RCT_EXPORT_METHOD(isAudioJackPluggedIn:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{
    resolve(@([RNFrequency isAudioJackInUse]));
}

@end
