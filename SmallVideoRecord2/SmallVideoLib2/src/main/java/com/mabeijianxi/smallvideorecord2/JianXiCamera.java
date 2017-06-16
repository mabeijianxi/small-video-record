package com.mabeijianxi.smallvideorecord2;

import android.text.TextUtils;

import com.mabeijianxi.smallvideorecord2.jniinterface.FFmpegBridge;

import java.io.File;

/**
 * Created by jianxi on 2017/6/5.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */

public class JianXiCamera {
    /** 应用包名 */
    private static String mPackageName;
    /** 应用版本名称 */
    private static String mAppVersionName;
    /** 应用版本号 */
    private static int mAppVersionCode;
    /** 视频缓存路径 */
    private static String mVideoCachePath;

    /** 执行FFMPEG命令保存路径 */
    public final static String FFMPEG_LOG_FILENAME_TEMP = "jx_ffmpeg.log";

    /**
     *
     * @param debug debug模式
     * @param logPath 命令日志存储地址
     */
    public static void initialize(boolean debug,String logPath) {

        if(debug&&TextUtils.isEmpty(logPath)){
            logPath=mVideoCachePath+"/"+FFMPEG_LOG_FILENAME_TEMP;
        }else if(!debug){
            logPath=null;
        }
        FFmpegBridge.initJXFFmpeg(debug,logPath);

    }



    /** 获取视频缓存文件夹 */
    public static String getVideoCachePath() {
        return mVideoCachePath;
    }

    /** 设置视频缓存路径 */
    public static void setVideoCachePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        mVideoCachePath = path;

    }
}
