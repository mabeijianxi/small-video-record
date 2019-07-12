
/**
 * Created by jianxi on 2017/5/26.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */
#ifndef JIANXIFFMPEG_JX_USER_ARGUMENTS_H
#define JIANXIFFMPEG_JX_USER_ARGUMENTS_H


#include "jni.h"
class JXJNIHandler;
typedef struct UserArguments {
    const char *media_base_path; //文件储存地址
    const char *media_name; // 文件命令前缀
    char *video_path; //视频储存地址
    char *audio_path; //音频储存地址
    char *media_path; //合成后的MP4储存地址
    int in_width; //输出宽度
    int in_height; //输入高度
    int out_height; //输出高度
    int out_width; //输出宽度
    int frame_rate; //视频帧率控制
    long long video_bit_rate; //视频比特率控制
    int audio_bit_rate; //音频比特率控制
    int audio_sample_rate; //音频采样率控制（44100）
    int v_custom_format; //一些滤镜操作控制
    JNIEnv *env; //env全局指针
    JavaVM *javaVM; //jvm指针
    jclass java_class; //java接口类的calss对象
    JXJNIHandler *handler; // 一个全局处理对象的指针
} ;
#endif //JIANXIFFMPEG_JX_USER_ARGUMENTS_H
