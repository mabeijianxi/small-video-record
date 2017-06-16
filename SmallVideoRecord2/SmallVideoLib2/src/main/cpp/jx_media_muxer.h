
/**
 * Created by jianxi on 2017/5/24.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */
#ifndef JIANXIFFMPEG_JX_MEDIA_MUXER_H
#define JIANXIFFMPEG_JX_MEDIA_MUXER_H


#include "base_include.h"
#define USE_H264BSF 1
#define USE_AACBSF 1


class JXMediaMuxer{
public:
    int startMuxer(const char * video, const char *audio , const char *out_file);

private:

};

#endif //JIANXIFFMPEG_JX_MEDIA_MUXER_H
