
/**
 * Created by jianxi on 2017/6/2.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */
#ifndef JIANXIFFMPEG_JX_LOG_H
#define JIANXIFFMPEG_JX_LOG_H

#include <android/log.h>

extern int JNI_DEBUG;

#define LOGE(debug, format, ...) if(debug){__android_log_print(ANDROID_LOG_ERROR, "jianxi_ffmpeg", format, ##__VA_ARGS__);}
#define LOGI(debug, format, ...) if(debug){__android_log_print(ANDROID_LOG_INFO, "jianxi_ffmpeg", format, ##__VA_ARGS__);}

#endif //JIANXIFFMPEG_JX_LOG_H
