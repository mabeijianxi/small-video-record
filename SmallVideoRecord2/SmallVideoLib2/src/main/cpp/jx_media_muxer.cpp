
/**
 * Created by jianxi on 2017/5/24.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */

#include "jx_media_muxer.h"
extern "C"
{
#include "jx_ffmpeg_cmd_run.h"
}
#include "jx_log.h"

int JXMediaMuxer::startMuxer( const char *in_filename_v, const char *in_filename_a,const char *out_filename) {


    size_t in_filename_v_size = strlen(in_filename_v);
    char *new_in_filename_v = (char *)malloc(in_filename_v_size+1);
    strcpy((new_in_filename_v), in_filename_v);

    size_t in_filename_a_size = strlen(in_filename_a);
    char *new_in_filename_a = (char *)malloc(in_filename_a_size+1);
    strcpy((new_in_filename_a), in_filename_a);

    size_t out_filename_size = strlen(out_filename);
    char *new_out_filename = (char *)malloc(out_filename_size+1);
    strcpy((new_out_filename), out_filename);


    LOGI(JNI_DEBUG,"视音编码成功,开始合成")
    char *cmd[10];
    cmd[0]="ffmpeg";
    cmd[1]="-i";
    cmd[2]=new_in_filename_v;
    cmd[3]="-i";
    cmd[4]=new_in_filename_a;
    cmd[5]="-c:v";
    cmd[6]="copy";
    cmd[7]="-c:a";
    cmd[8]="copy";
    cmd[9]=new_out_filename;
    return ffmpeg_cmd_run(10,cmd);



 /*   AVOutputFormat *ofmt = NULL;
    //Input AVFormatContext and Output AVFormatContext
    AVFormatContext *ifmt_ctx_v = NULL, *ifmt_ctx_a = NULL,*ofmt_ctx = NULL;
    AVPacket pkt;
    int ret, i;
    int videoindex_v=-1,videoindex_out=-1;
    int audioindex_a=-1,audioindex_out=-1;
    int frame_index=0;
    int64_t cur_pts_v=0,cur_pts_a=0;

    av_register_all();
    //Input
    if ((ret = avformat_open_input(&ifmt_ctx_v, in_filename_v, 0, 0)) < 0) {
        LOGE(JNI_DEBUG,"jianxi_ffmpeg Could not open input file.");

    }
    if ((ret = avformat_find_stream_info(ifmt_ctx_v, 0)) < 0) {
        LOGE(JNI_DEBUG,"jianxi_ffmpeg Failed to retrieve input stream information");

    }

    if ((ret = avformat_open_input(&ifmt_ctx_a, in_filename_a, 0, 0)) < 0) {
        LOGE(JNI_DEBUG,"jianxi_ffmpeg Could not open input file.");

    }
    if ((ret = avformat_find_stream_info(ifmt_ctx_a, 0)) < 0) {
        LOGE(JNI_DEBUG,"jianxi_ffmpeg Failed to retrieve input stream information");

    }
    LOGE(JNI_DEBUG,"===========Input Information==========\n");
    av_dump_format(ifmt_ctx_v, 0, in_filename_v, 0);
    av_dump_format(ifmt_ctx_a, 0, in_filename_a, 0);
    LOGE(JNI_DEBUG,"======================================\n");
    //Output
    avformat_alloc_output_context2(&ofmt_ctx, NULL, NULL, out_filename);
    if (!ofmt_ctx) {
        LOGE(JNI_DEBUG,"jianxi_ffmpeg Could not create output context\n");
        ret = AVERROR_UNKNOWN;

    }
    ofmt = ofmt_ctx->oformat;

    for (i = 0; i < ifmt_ctx_v->nb_streams; i++) {
        //Create output AVStream according to input AVStream
        if(ifmt_ctx_v->streams[i]->codec->codec_type==AVMEDIA_TYPE_VIDEO){
            AVStream *in_stream = ifmt_ctx_v->streams[i];
            AVStream *out_stream = avformat_new_stream(ofmt_ctx, in_stream->codec->codec);
            videoindex_v=i;
            if (!out_stream) {
                LOGE(JNI_DEBUG,"jianxi_ffmpeg Failed allocating output stream\n");
                ret = AVERROR_UNKNOWN;

            }
            videoindex_out=out_stream->index;
            //Copy the settings of AVCodecContext
            if (avcodec_copy_context(out_stream->codec, in_stream->codec) < 0) {
                LOGE(JNI_DEBUG,"jianxi_ffmpeg Failed to copy context from input to output stream codec context\n");

            }
            out_stream->codec->codec_tag = 0;
            if (ofmt_ctx->oformat->flags & AVFMT_GLOBALHEADER)
                out_stream->codec->flags |= CODEC_FLAG_GLOBAL_HEADER;
            break;
        }
    }

    for (i = 0; i < ifmt_ctx_a->nb_streams; i++) {
        //Create output AVStream according to input AVStream
        if(ifmt_ctx_a->streams[i]->codec->codec_type==AVMEDIA_TYPE_AUDIO){
            AVStream *in_stream = ifmt_ctx_a->streams[i];
            AVStream *out_stream = avformat_new_stream(ofmt_ctx, in_stream->codec->codec);
            audioindex_a=i;
            if (!out_stream) {
                LOGE(JNI_DEBUG,"jianxi_ffmpeg Failed allocating output stream\n");
                ret = AVERROR_UNKNOWN;

            }
            audioindex_out=out_stream->index;
            //Copy the settings of AVCodecContext
            if (avcodec_copy_context(out_stream->codec, in_stream->codec) < 0) {
                LOGE(JNI_DEBUG,"jianxi_ffmpeg Failed to copy context from input to output stream codec context\n");

            }
            out_stream->codec->codec_tag = 0;
            if (ofmt_ctx->oformat->flags & AVFMT_GLOBALHEADER)
                out_stream->codec->flags |= CODEC_FLAG_GLOBAL_HEADER;

            break;
        }
    }

    LOGE(JNI_DEBUG,"==========Output Information==========\n");
    av_dump_format(ofmt_ctx, 0, out_filename, 1);
    LOGE(JNI_DEBUG,"======================================\n");
    //Open output file
    if (!(ofmt->flags & AVFMT_NOFILE)) {
        if (avio_open(&ofmt_ctx->pb, out_filename, AVIO_FLAG_WRITE) < 0) {
            LOGE(JNI_DEBUG,"jianxi_ffmpeg Could not open output file '%s'", out_filename);

        }
    }
    //Write file header
    if (avformat_write_header(ofmt_ctx, NULL) < 0) {
        LOGE(JNI_DEBUG,"jianxi_ffmpeg Error occurred when opening output file\n");

    }


    //FIX
#if USE_H264BSF
    AVBitStreamFilterContext* h264bsfc =  av_bitstream_filter_init("h264_mp4toannexb");
#endif
#if USE_AACBSF
    AVBitStreamFilterContext* aacbsfc =  av_bitstream_filter_init("aac_adtstoasc");
#endif

    while (1) {
        AVFormatContext *ifmt_ctx;
        int stream_index=0;
        AVStream *in_stream, *out_stream;

        //Get an AVPacket
        if(av_compare_ts(cur_pts_v,ifmt_ctx_v->streams[videoindex_v]->time_base,cur_pts_a,ifmt_ctx_a->streams[audioindex_a]->time_base) <= 0){
            ifmt_ctx=ifmt_ctx_v;
            stream_index=videoindex_out;

            if(av_read_frame(ifmt_ctx, &pkt) >= 0){
                do{
                    in_stream  = ifmt_ctx->streams[pkt.stream_index];
                    out_stream = ofmt_ctx->streams[stream_index];

                    if(pkt.stream_index==videoindex_v){
                        //FIX：No PTS (Example: Raw H.264)
                        //Simple Write PTS
                        if(pkt.pts==AV_NOPTS_VALUE){
                            //Write PTS
                            AVRational time_base1=in_stream->time_base;
                            //Duration between 2 frames (us)
                            int64_t calc_duration=(double)AV_TIME_BASE/av_q2d(in_stream->r_frame_rate);
                            //Parameters
                            pkt.pts=(double)(frame_index*calc_duration)/(double)(av_q2d(time_base1)*AV_TIME_BASE);
                            pkt.dts=pkt.pts;
                            pkt.duration=(double)calc_duration/(double)(av_q2d(time_base1)*AV_TIME_BASE);
                            frame_index++;
                        }

                        cur_pts_v=pkt.pts;
                        break;
                    }
                }while(av_read_frame(ifmt_ctx, &pkt) >= 0);
            }else{
                break;
            }
        }else{
            ifmt_ctx=ifmt_ctx_a;
            stream_index=audioindex_out;
            if(av_read_frame(ifmt_ctx, &pkt) >= 0){
                do{
                    in_stream  = ifmt_ctx->streams[pkt.stream_index];
                    out_stream = ofmt_ctx->streams[stream_index];

                    if(pkt.stream_index==audioindex_a){

                        //FIX：No PTS
                        //Simple Write PTS
                        if(pkt.pts==AV_NOPTS_VALUE){
                            //Write PTS
                            AVRational time_base1=in_stream->time_base;
                            //Duration between 2 frames (us)
                            int64_t calc_duration=(double)AV_TIME_BASE/av_q2d(in_stream->r_frame_rate);
                            //Parameters
                            pkt.pts=(double)(frame_index*calc_duration)/(double)(av_q2d(time_base1)*AV_TIME_BASE);
                            pkt.dts=pkt.pts;
                            pkt.duration=(double)calc_duration/(double)(av_q2d(time_base1)*AV_TIME_BASE);
                            frame_index++;
                        }
                        cur_pts_a=pkt.pts;

                        break;
                    }
                }while(av_read_frame(ifmt_ctx, &pkt) >= 0);
            }else{
                break;
            }

        }

        //FIX:Bitstream Filter
#if USE_H264BSF
        av_bitstream_filter_filter(h264bsfc, in_stream->codec, NULL, &pkt.data, &pkt.size, pkt.data, pkt.size, 0);
#endif
#if USE_AACBSF
        av_bitstream_filter_filter(aacbsfc, out_stream->codec, NULL, &pkt.data, &pkt.size, pkt.data, pkt.size, 0);
#endif


        //Convert PTS/DTS
        pkt.pts = av_rescale_q_rnd(pkt.pts, in_stream->time_base, out_stream->time_base, (AVRounding)(AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX));
        pkt.dts = av_rescale_q_rnd(pkt.dts, in_stream->time_base, out_stream->time_base, (AVRounding)(AV_ROUND_NEAR_INF|AV_ROUND_PASS_MINMAX));
        pkt.duration = av_rescale_q(pkt.duration, in_stream->time_base, out_stream->time_base);
        pkt.pos = -1;
        pkt.stream_index=stream_index;

        LOGE(JNI_DEBUG,"Write 1 Packet. size:%5d\tpts:%lld\n",pkt.size,pkt.pts);
        //Write
        if (av_interleaved_write_frame(ofmt_ctx, &pkt) < 0) {
            LOGE(JNI_DEBUG,"jianxi_ffmpeg Error muxing packet\n");
            break;
        }
        av_free_packet(&pkt);

    }
    //Write file trailer
    av_write_trailer(ofmt_ctx);

#if USE_H264BSF
    av_bitstream_filter_close(h264bsfc);
#endif
#if USE_AACBSF
    av_bitstream_filter_close(aacbsfc);
#endif

//    recordEnd:
    avformat_close_input(&ifmt_ctx_v);
    avformat_close_input(&ifmt_ctx_a);
    *//* close output *//*
    if (ofmt_ctx && !(ofmt->flags & AVFMT_NOFILE))
        avio_close(ofmt_ctx->pb);
    avformat_free_context(ofmt_ctx);
    if (ret < 0 && ret != AVERROR_EOF) {
        LOGE(JNI_DEBUG,"jianxi_ffmpeg Error occurred.\n");
        return -1;
    }
    return 0;*/
}