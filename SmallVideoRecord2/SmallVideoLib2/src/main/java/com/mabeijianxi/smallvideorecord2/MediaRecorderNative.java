package com.mabeijianxi.smallvideorecord2;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.mabeijianxi.smallvideorecord2.jniinterface.FFmpegBridge;
import com.mabeijianxi.smallvideorecord2.model.MediaObject;


/**
 * 视频录制：边录制边底层处理视频（旋转和裁剪）
 */
public class MediaRecorderNative extends MediaRecorderBase implements MediaRecorder.OnErrorListener, FFmpegBridge.FFmpegStateListener {

    public MediaRecorderNative() {
        FFmpegBridge.registFFmpegStateListener(this);
    }

    /**
     * 视频后缀
     */
    private static final String VIDEO_SUFFIX = ".ts";

    /**
     * 开始录制
     */
    @Override
    public MediaObject.MediaPart startRecord() {
        int vCustomFormat;
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            vCustomFormat=FFmpegBridge.ROTATE_90_CROP_LT;
        } else {
            vCustomFormat=FFmpegBridge.ROTATE_270_CROP_LT_MIRROR_LR;
        }

        FFmpegBridge.prepareJXFFmpegEncoder( mMediaObject.getOutputDirectory(), mMediaObject.getBaseName(),vCustomFormat, mSupportedPreviewWidth, SMALL_VIDEO_HEIGHT, SMALL_VIDEO_WIDTH, SMALL_VIDEO_HEIGHT, mFrameRate, mVideoBitrate);

        MediaObject.MediaPart result = null;

        if (mMediaObject != null) {

            result = mMediaObject.buildMediaPart(mCameraId, VIDEO_SUFFIX);
            String cmd = String.format("filename = \"%s\"; ", result.mediaPath);
            //如果需要定制非480x480的视频，可以启用以下代码，其他vf参数参考ffmpeg的文档：

            if (mAudioRecorder == null && result != null) {
                mAudioRecorder = new AudioRecorder(this);
                mAudioRecorder.start();
            }
            mRecording = true;

        }
        return result;
    }

    /**
     * 停止录制
     */
    @Override
    public void stopRecord() {

        super.stopRecord();
        if (mOnEncodeListener != null) {
            mOnEncodeListener.onEncodeStart();
        }
        FFmpegBridge.recordEnd();
    }

    /**
     * 数据回调
     */
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mRecording) {
            FFmpegBridge.encodeFrame2H264(data);
            mPreviewFrameCallCount++;
        }
        super.onPreviewFrame(data, camera);
    }

    /**
     * 预览成功，设置视频输入输出参数
     */
    @Override
    protected void onStartPreviewSuccess() {
//        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
//            UtilityAdapter.RenderInputSettings(mSupportedPreviewWidth, SMALL_VIDEO_WIDTH, 0, UtilityAdapter.FLIPTYPE_NORMAL);
//        } else {
//            UtilityAdapter.RenderInputSettings(mSupportedPreviewWidth, SMALL_VIDEO_WIDTH, 180, UtilityAdapter.FLIPTYPE_HORIZONTAL);
//        }
//        UtilityAdapter.RenderOutputSettings(SMALL_VIDEO_WIDTH, SMALL_VIDEO_HEIGHT, mFrameRate, UtilityAdapter.OUTPUTFORMAT_YUV | UtilityAdapter.OUTPUTFORMAT_MASK_MP4/*| UtilityAdapter.OUTPUTFORMAT_MASK_HARDWARE_ACC*/);
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            Log.w("jianxi", "stopRecord", e);
        } catch (Exception e) {
            Log.w("jianxi", "stopRecord", e);
        }
        if (mOnErrorListener != null)
            mOnErrorListener.onVideoError(what, extra);
    }

    /**
     * 接收音频数据，传递到底层
     */
    @Override
    public void receiveAudioData(byte[] sampleBuffer, int len) {
        if (mRecording && len > 0) {
            FFmpegBridge.encodeFrame2AAC(sampleBuffer);
        }
    }

    @Override
    public void allRecordEnd() {

        final boolean captureFlag = FFMpegUtils.captureThumbnails(mMediaObject.getOutputTempTranscodingVideoPath(), mMediaObject.getOutputVideoThumbPath(),  String.valueOf(CAPTURE_THUMBNAILS_TIME));

        if(mOnEncodeListener!=null){
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(captureFlag){
                        mOnEncodeListener.onEncodeComplete();
                    }else {
                        mOnEncodeListener.onEncodeError();
                    }
                }
            },0);

        }

    }
    public void activityStop(){
        FFmpegBridge.unRegistFFmpegStateListener(this);
    }
}
