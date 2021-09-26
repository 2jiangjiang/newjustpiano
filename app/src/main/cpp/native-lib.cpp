#include <jni.h>
#include <string>
#include <oboe/Oboe.h>
#include <fcntl.h>
#include "log.h"

oboe::AudioStreamBuilder builder;
auto mode = oboe::PerformanceMode::PowerSaving;
int channelCount = 2;
int sampleRates = 44100;
int c=0;
class MyCallback : public oboe::AudioStreamCallback {
    public:
    bool isPlay;
    bool isOpen;
    int audio;
    float volume;
    MyCallback(){
        isPlay=false;
        isOpen=false;
        audio=0;
        volume=0.0;
    }
    oboe::DataCallbackResult onAudioReady(oboe::AudioStream *audioStream, void *audioData, int32_t numFrames) {
        if(read(audio, audioData, numFrames*channelCount*2)==0){
            isPlay=false;
            close(audio);
            return oboe::DataCallbackResult::Stop;
        }
        int16_t *outputData = static_cast<int16_t *>(audioData);
        for (int i = 0; i < numFrames; ++i){
            for(int j=0;j<channelCount;++j){
                outputData[i*2+j] =outputData[i*2+j]*volume;
            }
        }
        return oboe::DataCallbackResult::Continue;
    }
};

MyCallback myCallback[40] = {MyCallback()};
oboe::AudioStream *stream[40];

extern "C" JNIEXPORT jstring JNICALL
Java_ly_jj_newjustpiano_tools_SoundMixer_nativeBuild(JNIEnv* env, jobject) {
    builder = oboe::AudioStreamBuilder();
    builder.setDirection(oboe::Direction::Output);
    builder.setPerformanceMode(mode);
    builder.setSharingMode(oboe::SharingMode::Shared);
    builder.setFormat(oboe::AudioFormat::I16);
    builder.setChannelCount(channelCount);
    builder.setSampleRate(sampleRates);
    return env->NewStringUTF("Oboe Audio Channel Ready");
}
extern "C" JNIEXPORT jstring JNICALL
Java_ly_jj_newjustpiano_tools_SoundMixer_nativeSet(JNIEnv* env, jobject,jint sampleRate,jint channel){
    sampleRates=(int)sampleRate;
    channelCount=(int)channel;
    return env->NewStringUTF("Oboe Audio Channel Set");
}
extern "C" JNIEXPORT jstring JNICALL
Java_ly_jj_newjustpiano_tools_SoundMixer_nativeModeSet(JNIEnv* env, jobject,jint jmode){
    int perMode = (int)jmode;
    switch(perMode){
        case 0:
            mode=oboe::PerformanceMode::PowerSaving;
            break;
        case 1:
            mode=oboe::PerformanceMode::None;
            break;
        case 2:
            mode=oboe::PerformanceMode::LowLatency;
            break;
    }
    return env->NewStringUTF("Oboe Audio Mode Set");
}
extern "C" JNIEXPORT void JNICALL
Java_ly_jj_newjustpiano_tools_SoundMixer_nativePlay(JNIEnv* env, jobject,jstring path,jfloat volume){
    char* str =(char*) env->GetStringUTFChars(path, 0);
    int i=0;
    for(;i<40;i++){
        if(!myCallback[i].isPlay){break;}
    }
    int lateFile=-1;
    if(i==40){
        i=c;
        lateFile=myCallback[i].audio;
        c++;
        if(c==40)c=0;
    }
    close(lateFile);
    myCallback[i].audio=open(str,(0100|0002),0666);
    myCallback[i].volume=(float)volume;

    if(!myCallback[i].isOpen){
        builder.setCallback(&(myCallback[i]));
        builder.openStream(&(stream[i]));
        myCallback[i].isOpen=true;
    }
    if(!myCallback[i].isPlay){
        myCallback[i].isPlay=true;
        stream[i]->requestStart();
    }
}
