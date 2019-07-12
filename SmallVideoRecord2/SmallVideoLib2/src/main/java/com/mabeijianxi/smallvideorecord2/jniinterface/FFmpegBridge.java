package com.mabeijianxi.smallvideorecord2.jniinterface;

import java.util.ArrayList;

/**
 * Created by jianxi on 2017/5/12.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */

public class FFmpegBridge {
    private static ArrayList<FFmpegStateListener> listeners=new ArrayList();
    static {
        System.loadLibrary("avutil");
        System.loadLibrary("fdk-aac");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("swscale");
        System.loadLibrary("swresample");
        System.loadLibrary("avfilter");
        System.loadLibrary("jx_ffmpeg_jni");
    }

    /**
     * 结束录制并且转码保存完成
     */
    public static final int ALL_RECORD_END =1;


    public final static int ROTATE_0_CROP_LF=0;
    /**
     * 旋转90度剪裁左上
     */
    public final static int ROTATE_90_CROP_LT =1;
    /**
     * 暂时没处理
     */
    public final static int ROTATE_180=2;
    /**
     * 旋转270(-90)裁剪左上，左右镜像
     */
    public final static int ROTATE_270_CROP_LT_MIRROR_LR=3;


    /**
     *
     * @return 返回ffmpeg的编译信息
     */
    public static native String getFFmpegConfig();

    /**
     *  命令形式运行ffmpeg
     * @param cmd
     * @return 返回0表示成功
     */
    private static native int jxCMDRun(String cmd[]);

    /**
     * 编码一帧视频，暂时只能编码yv12视频
     * @param data
     * @return
     */
    public static native int encodeFrame2H264(byte[] data);


    /**
     * 编码一帧音频,暂时只能编码pcm音频
     * @param data
     * @return
     */
    public static native int encodeFrame2AAC(byte[] data);

    /**
     *  录制结束
     * @return
     */
    public static native int recordEnd();

    /**
     * 初始化
     * @param debug
     * @param logUrl
     */
    public static native void initJXFFmpeg(boolean debug,String logUrl);


    public static native void nativeRelease();

    /**
     *
     * @param mediaBasePath 视频存放目录
     * @param mediaName 视频名称
     * @param filter 旋转镜像剪切处理
     * @param in_width 输入视频宽度
     * @param in_height 输入视频高度
     * @param out_height 输出视频高度
     * @param out_width 输出视频宽度
     * @param frameRate 视频帧率
     * @param bit_rate 视频比特率
     * @return
     */
    public static native int prepareJXFFmpegEncoder(String mediaBasePath, String mediaName, int filter,int in_width, int in_height, int out_width, int  out_height, int frameRate, long bit_rate);


    /**
     * 命令形式执行
     * @param cmd
     */
    public static int jxFFmpegCMDRun(String cmd){
        String regulation="[ \\t]+";
        final String[] split = cmd.split(regulation);

       return jxCMDRun(split);
    }

    /**
     * 底层回调
     * @param state
     * @param what
     */
    public static synchronized void notifyState(int state,float what){
        for(FFmpegStateListener listener: listeners){
            if(listener!=null){
                if(state== ALL_RECORD_END){
                    listener.allRecordEnd();
                }
            }
        }
    }

    /**
     *注册录制回调
     * @param listener
     */
    public static void registFFmpegStateListener(FFmpegStateListener listener){

        if(!listeners.contains(listener)){
            listeners.add(listener);
        }
    }
    public static void unRegistFFmpegStateListener(FFmpegStateListener listener){
        if(listeners.contains(listener)){
            listeners.remove(listener);
        }
    }
    public interface FFmpegStateListener {
        void allRecordEnd();
    }
}
