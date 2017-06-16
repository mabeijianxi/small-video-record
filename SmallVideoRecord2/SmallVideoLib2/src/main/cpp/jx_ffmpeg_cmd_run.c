/**
 * Created by jianxi on 2017/6/4..
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */
#include "jx_ffmpeg_cmd_run.h"
#include "ffmpeg.h"
#include "jx_log.h"

/**
 * 以命令行方式运行，返回0表示成功
 */
JNIEXPORT jint JNICALL
Java_com_mabeijianxi_smallvideorecord2_jniinterface_FFmpegBridge_jxCMDRun(JNIEnv *env, jclass type,
        jobjectArray commands){
    int argc = (*env)->GetArrayLength(env,commands);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        jstring js = (jstring) (*env)->GetObjectArrayElement(env,commands, i);
        argv[i] = (char *) (*env)->GetStringUTFChars(env,js, 0);
    }
    return ffmpeg_cmd_run(argc,argv);
}

int ffmpeg_cmd_run(int argc, char **argv){
    return jxRun(argc, argv);

}

char *logUrl;

/**
 * 初始化debug工具
 */
JNIEXPORT void JNICALL
Java_com_mabeijianxi_smallvideorecord2_jniinterface_FFmpegBridge_initJXFFmpeg(JNIEnv *env, jclass type,
                                                                           jboolean debug,
                                                                           jstring logUrl_) {
    JNI_DEBUG = debug;
    if (JNI_DEBUG&&logUrl_!=NULL) {
        av_log_set_callback(log_callback);
        const char* log = (*env)->GetStringUTFChars(env,logUrl_, 0);
        logUrl = (char*)malloc(strlen(log));
        strcpy(logUrl,log);
        (*env)->ReleaseStringUTFChars(env,logUrl_, log);
    }

}


void log_callback(void *ptr, int level, const char *fmt,
                  va_list vl) {
    FILE *fp = NULL;

    if (!fp)
        fp = fopen(logUrl, "a+");
    if (fp) {
        vfprintf(fp, fmt, vl);
        fflush(fp);
        fclose(fp);
    }

}
