
/**
 * Created by jianxi on 2017/5/18.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */
#ifndef JIANXIFFMPEG_JX_PCM_ENCODE_AAC_H
#define JIANXIFFMPEG_JX_PCM_ENCODE_AAC_H


#include "base_include.h"
#include "jx_user_arguments.h"
using namespace std;

/**
 * pcm编码为aac
 */
class JXPCMEncodeAAC {
public:
    JXPCMEncodeAAC(UserArguments* arg);
public:
    int initAudioEncoder();

    static void* startEncode(void* obj);

    void user_end();

    void release();

    int sendOneFrame(uint8_t* buf);

    int encodeEnd();
    ~JXPCMEncodeAAC() {
    }
private:
    int flush_encoder(AVFormatContext *fmt_ctx, unsigned int stream_index);

private:
    threadsafe_queue<uint8_t *> frame_queue;
    AVFormatContext *pFormatCtx;
    AVOutputFormat *fmt;
    AVStream *audio_st;
    AVCodecContext *pCodecCtx;
    AVCodec *pCodec;

    AVFrame *pFrame;
    AVPacket pkt;

    int got_frame = 0;
    int ret = 0;
    int size = 0;

    int i;
    int is_end=JX_FALSE;
    int is_release=JX_FALSE;
    UserArguments *arguments;

};

#endif //JIANXIFFMPEG_JX_PCM_ENCODE_AAC_H
