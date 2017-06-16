/**
 * Created by jianxi on 2017/6/5.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */
#include "jx_ffmpeg_config.h"
#include "base_include.h"

jstring getEncoderConfigInfo(JNIEnv *env) {
    char info[10000] = {0};
    sprintf(info, "%s\n", avcodec_configuration());
    return env->NewStringUTF(info);
}