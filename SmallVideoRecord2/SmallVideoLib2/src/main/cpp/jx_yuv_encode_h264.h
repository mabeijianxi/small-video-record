
/**
 * Created by jianxi on 2017/5/12.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */
#ifndef JIANXIFFMPEG_JX_YUV_ENCODE_H264_H
#define JIANXIFFMPEG_JX_YUV_ENCODE_H264_H



#include "base_include.h"

#include "jx_user_arguments.h"

using namespace std;
/**
 * yuv编码h264
 */
class JXYUVEncodeH264 {
public:
    JXYUVEncodeH264(UserArguments* arg);
public:
    int initVideoEncoder();

    static void* startEncode(void * obj);

    int startSendOneFrame(uint8_t *buf);

    void user_end();

    void release();

    int encodeEnd();

    void custom_filter(const JXYUVEncodeH264 *h264_encoder, const uint8_t *picture_buf,
                       int in_y_size,
                       int format);
    ~JXYUVEncodeH264() {
    }
private:
    int flush_encoder(AVFormatContext *fmt_ctx, unsigned int stream_index);

private:
    UserArguments *arguments;
    int is_end = 0;
    int is_release = 0;
    threadsafe_queue<uint8_t *> frame_queue;
    AVFormatContext *pFormatCtx;
    AVOutputFormat *fmt;
    AVStream *video_st;
    AVCodecContext *pCodecCtx;
    AVCodec *pCodec;
    AVPacket pkt;
    AVFrame *pFrame;
    int picture_size;
    int out_y_size;
    int framecnt = 0;
    int frame_count = 0;


};

#endif //JIANXIFFMPEG_JX_YUV_ENCODE_H264_H
