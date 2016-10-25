package mabeijianxi.camera;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import mabeijianxi.camera.model.MediaObject;
import mabeijianxi.camera.model.MediaObject.MediaPart;
import mabeijianxi.camera.util.DeviceUtils;
import mabeijianxi.camera.util.FileUtils;
import mabeijianxi.camera.util.StringUtils;

/**
 * 视频录制抽象类
 */
public abstract class MediaRecorderBase implements Callback, PreviewCallback, IMediaRecorder {
    /**
     * 小视频高度
     */
    public static int SMALL_VIDEO_HEIGHT = 360;
    /**
     * 小视频宽度
     */
    public static int SMALL_VIDEO_WIDTH = 480;


    /**
     * 未知错误
     */
    public static final int MEDIA_ERROR_UNKNOWN = 1;
    /**
     * 预览画布设置错误
     */
    public static final int MEDIA_ERROR_CAMERA_SET_PREVIEW_DISPLAY = 101;
    /**
     * 预览错误
     */
    public static final int MEDIA_ERROR_CAMERA_PREVIEW = 102;
    /**
     * 自动对焦错误
     */
    public static final int MEDIA_ERROR_CAMERA_AUTO_FOCUS = 103;

    public static final int AUDIO_RECORD_ERROR_UNKNOWN = 0;
    /**
     * 采样率设置不支持
     */
    public static final int AUDIO_RECORD_ERROR_SAMPLERATE_NOT_SUPPORT = 1;
    /**
     * 最小缓存获取失败
     */
    public static final int AUDIO_RECORD_ERROR_GET_MIN_BUFFER_SIZE_NOT_SUPPORT = 2;
    /**
     * 创建AudioRecord失败
     */
    public static final int AUDIO_RECORD_ERROR_CREATE_FAILED = 3;

    /**
     * 视频码率 1M
     */
    public static final int VIDEO_BITRATE_NORMAL = 1024;
    /**
     * 视频码率 1.5M（默认）
     */
    public static final int VIDEO_BITRATE_MEDIUM = 1536;
    /**
     * 视频码率 2M
     */
    public static final int VIDEO_BITRATE_HIGH = 2048;

    /**
     * 开始转码
     */
    protected static final int MESSAGE_ENCODE_START = 0;
    /**
     * 转码进度
     */
    protected static final int MESSAGE_ENCODE_PROGRESS = 1;
    /**
     * 转码完成
     */
    protected static final int MESSAGE_ENCODE_COMPLETE = 2;
    /**
     * 转码失败
     */
    protected static final int MESSAGE_ENCODE_ERROR = 3;

    /**
     * 最大帧率
     */
    protected static int MAX_FRAME_RATE = 20;
    /**
     * 最小帧率
     */
    protected static int MIN_FRAME_RATE = 8;

    protected static int CAPTURE_THUMBNAILS_TIME = 1;

    protected static boolean doH264Compress = true;

    /**
     * 摄像头对象
     */
    protected Camera camera;
    /**
     * 摄像头参数
     */
    protected Camera.Parameters mParameters = null;
    /**
     * 摄像头支持的预览尺寸集合
     */
    protected List<Size> mSupportedPreviewSizes;
    /**
     * 画布
     */
    protected SurfaceHolder mSurfaceHolder;

    /**
     * 声音录制
     */
    protected AudioRecorder mAudioRecorder;
    /**
     * 转码Handler
     */
    protected EncodeHandler mEncodeHanlder;
    /**
     * 拍摄存储对象
     */
    protected MediaObject mMediaObject;

    /**
     * 转码监听器
     */
    protected OnEncodeListener mOnEncodeListener;
    /**
     * 录制错误监听
     */
    protected OnErrorListener mOnErrorListener;
    /**
     * 录制已经准备就绪的监听
     */
    protected OnPreparedListener mOnPreparedListener;

    /**
     * 帧率
     */
    protected int mFrameRate = MIN_FRAME_RATE;
    /**
     * 摄像头类型（前置/后置），默认后置
     */
    protected int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    /**
     * 视频码率
     */
    protected static int mVideoBitrate = 2048;

    protected int mSupportedPreviewWidth = 0;
    /**
     * 状态标记
     */
    protected boolean mPrepared, mStartPreview, mSurfaceCreated;
    /**
     * 是否正在录制
     */
    protected volatile boolean mRecording;
    /**
     * PreviewFrame调用次数，测试用
     */
    protected volatile long mPreviewFrameCallCount = 0;

    public MediaRecorderBase() {

    }

    /**
     * 设置预览输出SurfaceHolder
     *
     * @param sh
     */
    @SuppressWarnings("deprecation")
    public void setSurfaceHolder(SurfaceHolder sh) {
        if (sh != null) {
            sh.addCallback(this);
            if (!DeviceUtils.hasHoneycomb()) {
                sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }
        }
    }

    /**
     * 设置转码监听
     */
    public void setOnEncodeListener(OnEncodeListener l) {
        this.mOnEncodeListener = l;
        mEncodeHanlder = new EncodeHandler(this);
    }

    /**
     * 设置预处理监听
     */
    public void setOnPreparedListener(OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * 设置错误监听
     */
    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    /**
     * 是否前置摄像头
     */
    public boolean isFrontCamera() {
        return mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    /**
     * 是否支持前置摄像头
     */
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static boolean isSupportFrontCamera() {
        if (!DeviceUtils.hasGingerbread()) {
            return false;
        }
        int numberOfCameras = android.hardware.Camera.getNumberOfCameras();
        if (2 == numberOfCameras) {
            return true;
        }
        return false;
    }

    /**
     * 切换前置/后置摄像头
     */
    public void switchCamera(int cameraFacingFront) {
        switch (cameraFacingFront) {
            case Camera.CameraInfo.CAMERA_FACING_FRONT:
            case Camera.CameraInfo.CAMERA_FACING_BACK:
                mCameraId = cameraFacingFront;
                stopPreview();
                startPreview();
                break;
        }
    }

    /**
     * 切换前置/后置摄像头
     */
    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            switchCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } else {
            switchCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        }
    }

    /**
     * 自动对焦
     *
     * @param cb
     * @return
     */
    public boolean autoFocus(AutoFocusCallback cb) {
        if (camera != null) {
            try {
                camera.cancelAutoFocus();

                if (mParameters != null) {
                    String mode = getAutoFocusMode();
                    if (StringUtils.isNotEmpty(mode)) {
                        mParameters.setFocusMode(mode);
                        camera.setParameters(mParameters);
                    }
                }
                camera.autoFocus(cb);
                return true;
            } catch (Exception e) {
                if (mOnErrorListener != null) {
                    mOnErrorListener.onVideoError(MEDIA_ERROR_CAMERA_AUTO_FOCUS, 0);
                }
                if (e != null)
                    Log.e("Yixia", "autoFocus", e);
            }
        }
        return false;
    }

    /**
     * 连续自动对焦
     */
    private String getAutoFocusMode() {
        if (mParameters != null) {
            //持续对焦是指当场景发生变化时，相机会主动去调节焦距来达到被拍摄的物体始终是清晰的状态。
            List<String> focusModes = mParameters.getSupportedFocusModes();
            if ((Build.MODEL.startsWith("GT-I950") || Build.MODEL.endsWith("SCH-I959") || Build.MODEL.endsWith("MEIZU MX3")) && isSupported(focusModes, "continuous-picture")) {
                return "continuous-picture";
            } else if (isSupported(focusModes, "continuous-video")) {
                return "continuous-video";
            } else if (isSupported(focusModes, "auto")) {
                return "auto";
            }
        }
        return null;
    }

    /**
     * 手动对焦
     *
     * @param focusAreas 对焦区域
     * @return
     */
    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public boolean manualFocus(AutoFocusCallback cb, List<Area> focusAreas) {
        if (camera != null && focusAreas != null && mParameters != null && DeviceUtils.hasICS()) {
            try {
                camera.cancelAutoFocus();
                // getMaxNumFocusAreas检测设备是否支持
                if (mParameters.getMaxNumFocusAreas() > 0) {
                    // mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);//
                    // Macro(close-up) focus mode
                    mParameters.setFocusAreas(focusAreas);
                }

                if (mParameters.getMaxNumMeteringAreas() > 0)
                    mParameters.setMeteringAreas(focusAreas);

                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_MACRO);
                camera.setParameters(mParameters);
                camera.autoFocus(cb);
                return true;
            } catch (Exception e) {
                if (mOnErrorListener != null) {
                    mOnErrorListener.onVideoError(MEDIA_ERROR_CAMERA_AUTO_FOCUS, 0);
                }
                if (e != null)
                    Log.e("Yixia", "autoFocus", e);
            }
        }
        return false;
    }

    /**
     * 切换闪关灯，默认关闭
     */
    public boolean toggleFlashMode() {
        if (mParameters != null) {
            try {
                final String mode = mParameters.getFlashMode();
                if (TextUtils.isEmpty(mode) || Camera.Parameters.FLASH_MODE_OFF.equals(mode))
                    setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                else
                    setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                return true;
            } catch (Exception e) {
                Log.e("Yixia", "toggleFlashMode", e);
            }
        }
        return false;
    }

    /**
     * 设置闪光灯
     *
     * @param value
     */
    private boolean setFlashMode(String value) {
        if (mParameters != null && camera != null) {
            try {
                if (Camera.Parameters.FLASH_MODE_TORCH.equals(value) || Camera.Parameters.FLASH_MODE_OFF.equals(value)) {
                    mParameters.setFlashMode(value);
                    camera.setParameters(mParameters);
                }
                return true;
            } catch (Exception e) {
                Log.e("Yixia", "setFlashMode", e);
            }
        }
        return false;
    }

    /**
     * 设置码率
     */
    public void setVideoBitRate(int bitRate) {
        if (bitRate > 0)
            mVideoBitrate = bitRate;
    }

    /**
     * 开始预览
     */
    public void prepare() {
        mPrepared = true;
        if (mSurfaceCreated)
            startPreview();
    }

    /**
     * 设置视频临时存储文件夹
     *
     * @param key  视频输出的名称，同目录下唯一，一般取系统当前时间
     * @param path 文件夹路径
     * @return 录制信息对象
     */
    public MediaObject setOutputDirectory(String key, String path) {
        if (StringUtils.isNotEmpty(path)) {
            File f = new File(path);
            if (f != null) {
                if (f.exists()) {
                    //已经存在，删除
                    if (f.isDirectory())
                        FileUtils.deleteDir(f);
                    else
                        FileUtils.deleteFile(f);
                }

                if (f.mkdirs()) {
                    mMediaObject = new MediaObject(key, path, mVideoBitrate);
                }
            }
        }
        return mMediaObject;
    }

    /**
     * 设置视频信息
     */
    public void setMediaObject(MediaObject mediaObject) {
        this.mMediaObject = mediaObject;
    }

    public void stopRecord() {
        mRecording = false;

        // 判断数据是否处理完，处理完了关闭输出流
        if (mMediaObject != null) {
            MediaPart part = mMediaObject.getCurrentPart();
            if (part != null && part.recording) {
                part.recording = false;
                part.endTime = System.currentTimeMillis();
                part.duration = (int) (part.endTime - part.startTime);
                part.cutStartTime = 0;
                part.cutEndTime = part.duration;
                // 检测视频大小是否大于0，否则丢弃（注意有音频没视频的情况下音频也会丢弃）
                //				File videoFile = new File(part.mediaPath);
                //				if (videoFile != null && videoFile.length() < 1) {
                //					mMediaObject.removePart(part, true);
                //				}
            }
        }
    }

    /**
     * 停止所有块的写入
     */
    private void stopAllRecord() {
        mRecording = false;
        if (mMediaObject != null && mMediaObject.getMedaParts() != null) {
            for (MediaPart part : mMediaObject.getMedaParts()) {
                if (part != null && part.recording) {
                    part.recording = false;
                    part.endTime = System.currentTimeMillis();
                    part.duration = (int) (part.endTime - part.startTime);
                    part.cutStartTime = 0;
                    part.cutEndTime = part.duration;
                    // 检测视频大小是否大于0，否则丢弃（注意有音频没视频的情况下音频也会丢弃）
                    File videoFile = new File(part.mediaPath);
                    if (videoFile != null && videoFile.length() < 1) {
                        mMediaObject.removePart(part, true);
                    }
                }
            }
        }
    }

    /**
     * 检测是否支持指定特性
     */
    private boolean isSupported(List<String> list, String key) {
        return list != null && list.contains(key);
    }

    /**
     * 预处理一些拍摄参数
     * 注意：自动对焦参数cam_mode和cam-mode可能有些设备不支持，导致视频画面变形，需要判断一下，已知有"GT-N7100", "GT-I9308"会存在这个问题
     */
    @SuppressWarnings("deprecation")
    protected void prepareCameraParaments() {
        if (mParameters == null)
            return;

        List<Integer> rates = mParameters.getSupportedPreviewFrameRates();
        if (rates != null) {
            if (rates.contains(MAX_FRAME_RATE)) {
                mFrameRate = MAX_FRAME_RATE;
            } else {
                Collections.sort(rates);
                for (int i = rates.size() - 1; i >= 0; i--) {
                    if (rates.get(i) <= MAX_FRAME_RATE) {
                        mFrameRate = rates.get(i);
                        break;
                    }
                }
            }
        }

        mParameters.setPreviewFrameRate(mFrameRate);
        // mParameters.setPreviewFpsRange(15 * 1000, 20 * 1000);
//		TODO 设置浏览尺寸
        boolean findWidth = false;
        for (int i = mSupportedPreviewSizes.size() - 1; i >= 0; i--) {
            Size size = mSupportedPreviewSizes.get(i);
            if (size.height == SMALL_VIDEO_WIDTH) {

                mSupportedPreviewWidth = size.width;
                findWidth = true;
                break;
            }
        }
        if (!findWidth) {
            Log.e(getClass().getSimpleName(), "传入高度不支持或未找到对应宽度,请按照要求重新设置，否则会出现一些验证问题");
            mSupportedPreviewWidth = 640;
            SMALL_VIDEO_WIDTH = 480;
            SMALL_VIDEO_HEIGHT = 360;
        }
        mParameters.setPreviewSize(mSupportedPreviewWidth, SMALL_VIDEO_WIDTH);

        // 设置输出视频流尺寸，采样率
        mParameters.setPreviewFormat(ImageFormat.NV21);

        //设置自动连续对焦
        String mode = getAutoFocusMode();
        if (StringUtils.isNotEmpty(mode)) {
            mParameters.setFocusMode(mode);
        }

        //设置人像模式，用来拍摄人物相片，如证件照。数码相机会把光圈调到最大，做出浅景深的效果。而有些相机还会使用能够表现更强肤色效果的色调、对比度或柔化效果进行拍摄，以突出人像主体。
        //		if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT && isSupported(mParameters.getSupportedSceneModes(), Camera.Parameters.SCENE_MODE_PORTRAIT))
        //			mParameters.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);

        if (isSupported(mParameters.getSupportedWhiteBalance(), "auto"))
            mParameters.setWhiteBalance("auto");

        //是否支持视频防抖
        if ("true".equals(mParameters.get("video-stabilization-supported")))
            mParameters.set("video-stabilization", "true");

        //		mParameters.set("recording-hint", "false");
        //
        //		mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        if (!DeviceUtils.isDevice("GT-N7100", "GT-I9308", "GT-I9300")) {
            mParameters.set("cam_mode", 1);
            mParameters.set("cam-mode", 1);
        }
    }

    /**
     * 开始预览
     */
    public void startPreview() {
        if (mStartPreview || mSurfaceHolder == null || !mPrepared)
            return;
        else
            mStartPreview = true;

        try {

            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
                camera = Camera.open();
            else
                camera = Camera.open(mCameraId);

            camera.setDisplayOrientation(90);
            try {
                camera.setPreviewDisplay(mSurfaceHolder);
            } catch (IOException e) {
                if (mOnErrorListener != null) {
                    mOnErrorListener.onVideoError(MEDIA_ERROR_CAMERA_SET_PREVIEW_DISPLAY, 0);
                }
                Log.e("Yixia", "setPreviewDisplay fail " + e.getMessage());
            }

            //设置摄像头参数
            mParameters = camera.getParameters();
            mSupportedPreviewSizes = mParameters.getSupportedPreviewSizes();//	获取支持的尺寸
            prepareCameraParaments();
            camera.setParameters(mParameters);
            setPreviewCallback();
            camera.startPreview();

            onStartPreviewSuccess();
            if (mOnPreparedListener != null)
                mOnPreparedListener.onPrepared();
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnErrorListener != null) {
                mOnErrorListener.onVideoError(MEDIA_ERROR_CAMERA_PREVIEW, 0);
            }
            Log.e("Yixia", "startPreview fail :" + e.getMessage());
        }
    }

    /**
     * 预览调用成功，子类可以做一些操作
     */
    protected void onStartPreviewSuccess() {

    }

    /**
     * 设置回调
     */
    protected void setPreviewCallback() {
        Camera.Size size = mParameters.getPreviewSize();
        if (size != null) {
            PixelFormat pf = new PixelFormat();
            PixelFormat.getPixelFormatInfo(mParameters.getPreviewFormat(), pf);
            int buffSize = size.width * size.height * pf.bitsPerPixel / 8;
            try {
                camera.addCallbackBuffer(new byte[buffSize]);
                camera.addCallbackBuffer(new byte[buffSize]);
                camera.addCallbackBuffer(new byte[buffSize]);
                camera.setPreviewCallbackWithBuffer(this);
            } catch (OutOfMemoryError e) {
                Log.e("Yixia", "startPreview...setPreviewCallback...", e);
            }
            Log.e("Yixia", "startPreview...setPreviewCallbackWithBuffer...width:" + size.width + " height:" + size.height);
        } else {
            camera.setPreviewCallback(this);
        }
    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        if (camera != null) {
            try {
                camera.stopPreview();
                camera.setPreviewCallback(null);
                // camera.lock();
                camera.release();
            } catch (Exception e) {
                Log.e("Yixia", "stopPreview...");
            }
            camera = null;
        }
        mStartPreview = false;
    }

    /**
     * 释放资源
     */
    public void release() {
        stopAllRecord();
        // 停止视频预览
        stopPreview();
        // 停止音频录制
        if (mAudioRecorder != null) {
            mAudioRecorder.interrupt();
            mAudioRecorder = null;
        }

        mSurfaceHolder = null;
        mPrepared = false;
        mSurfaceCreated = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.mSurfaceHolder = holder;
        this.mSurfaceCreated = true;
        if (mPrepared && !mStartPreview)
            startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.mSurfaceHolder = holder;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder = null;
        mSurfaceCreated = false;
    }

    @Override
    public void onAudioError(int what, String message) {
        if (mOnErrorListener != null)
            mOnErrorListener.onAudioError(what, message);
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        mPreviewFrameCallCount++;
        camera.addCallbackBuffer(data);
    }

    /**
     * 测试PreviewFrame回调次数，时间1分钟
     */
    public void testPreviewFrameCallCount() {
        new CountDownTimer(1 * 60 * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                android.util.Log.e("[Vitamio Recorder]", "testFrameRate..." + mPreviewFrameCallCount);
                mPreviewFrameCallCount = 0;
            }

            @Override
            public void onFinish() {

            }

        }.start();
    }

    /**
     * 接收音频数据
     */
    @Override
    public void receiveAudioData(byte[] sampleBuffer, int len) {

    }

    /**
     * 预处理监听
     */
    public interface OnPreparedListener {
        /**
         * 预处理完毕，可以开始录制了
         */
        void onPrepared();
    }

    /**
     * 错误监听
     */
    public interface OnErrorListener {
        /**
         * 视频录制错误
         *
         * @param what
         * @param extra
         */
        void onVideoError(int what, int extra);

        /**
         * 音频录制错误
         *
         * @param what
         * @param message
         */
        void onAudioError(int what, String message);
    }

    /**
     * 转码接口
     */
    public interface OnEncodeListener {
        /**
         * 开始转码
         */
        void onEncodeStart();

        /**
         * 转码进度
         */
        void onEncodeProgress(int progress);

        /**
         * 转码完成
         */
        void onEncodeComplete();

        /**
         * 转码失败
         */
        void onEncodeError();
    }

    /**
     * 开始转码，主要是合并视频片段
     */
    public void startEncoding() {
        if (mMediaObject == null || mEncodeHanlder == null)
            return;

        mEncodeHanlder.removeMessages(MESSAGE_ENCODE_PROGRESS);
        mEncodeHanlder.removeMessages(MESSAGE_ENCODE_COMPLETE);
        mEncodeHanlder.removeMessages(MESSAGE_ENCODE_START);
        mEncodeHanlder.removeMessages(MESSAGE_ENCODE_ERROR);
        mEncodeHanlder.sendEmptyMessage(MESSAGE_ENCODE_START);
    }

    /**
     * 合并视频片段
     */
    protected void concatVideoParts() {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                //合并ts流
                String cmd = String.format("ffmpeg %s -i \"%s\" -vcodec copy -acodec copy -absf aac_adtstoasc -f mp4 -movflags faststart \"%s\"", FFMpegUtils.getLogCommand(), mMediaObject.getConcatYUV(), mMediaObject.getOutputTempVideoPath());
                boolean mergeFlag = UtilityAdapter.FFmpegRun("", cmd) == 0;
                if (doH264Compress) {
                    String cmd_transcoding = "ffmpeg -i " + mMediaObject.getOutputTempVideoPath() + " -c:v libx264 -crf 28 -preset:v veryfast -c:a libfdk_aac -vbr 4 " + mMediaObject.getOutputTempTranscodingVideoPath();

                    boolean transcodingFlag = UtilityAdapter.FFmpegRun("", cmd_transcoding) == 0;

                    boolean captureFlag = FFMpegUtils.captureThumbnails(mMediaObject.getOutputTempTranscodingVideoPath(), mMediaObject.getOutputVideoThumbPath(), SMALL_VIDEO_WIDTH + "x" + SMALL_VIDEO_HEIGHT, String.valueOf(CAPTURE_THUMBNAILS_TIME));

                    FileUtils.deleteCacheFile(mMediaObject.getOutputDirectory());

                    return mergeFlag && captureFlag && transcodingFlag;
                } else {
                    boolean captureFlag = FFMpegUtils.captureThumbnails(mMediaObject.getOutputTempVideoPath(), mMediaObject.getOutputVideoThumbPath(), SMALL_VIDEO_WIDTH + "x" + SMALL_VIDEO_HEIGHT, String.valueOf(CAPTURE_THUMBNAILS_TIME));

                    FileUtils.deleteCacheFile2TS(mMediaObject.getOutputDirectory());

                    return captureFlag && mergeFlag;

                }
            }


            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {

                    mEncodeHanlder.sendEmptyMessage(MESSAGE_ENCODE_COMPLETE);
                } else {
                    mEncodeHanlder.sendEmptyMessage(MESSAGE_ENCODE_ERROR);
                }
            }
        }.execute();
    }

    /**
     * 转码队列
     */
    public static class EncodeHandler extends Handler {

        private WeakReference<MediaRecorderBase> mMediaRecorderBase;

        public EncodeHandler(MediaRecorderBase l) {
            mMediaRecorderBase = new WeakReference<MediaRecorderBase>(l);
        }

        @Override
        public void handleMessage(Message msg) {
            MediaRecorderBase mrb = mMediaRecorderBase.get();
            if (mrb == null || mrb.mOnEncodeListener == null)
                return;
            OnEncodeListener listener = mrb.mOnEncodeListener;
            switch (msg.what) {
                case MESSAGE_ENCODE_START:
                    listener.onEncodeStart();
                    sendEmptyMessage(MESSAGE_ENCODE_PROGRESS);
                    break;
                case MESSAGE_ENCODE_PROGRESS:
                    //查询片段是否都已经转码完成
                    final int progress = UtilityAdapter.FilterParserAction("", UtilityAdapter.PARSERACTION_PROGRESS);
                    if (progress == 100) {
                        listener.onEncodeProgress(progress);
                        mrb.concatVideoParts();//合并视频
                    } else if (progress == -1) {
                        sendEmptyMessage(MESSAGE_ENCODE_ERROR);
                    } else {
                        listener.onEncodeProgress(progress);
                        sendEmptyMessageDelayed(MESSAGE_ENCODE_PROGRESS, 200);
                    }
                    break;
                case MESSAGE_ENCODE_COMPLETE:
                    listener.onEncodeComplete();
                    break;
                case MESSAGE_ENCODE_ERROR:
                    listener.onEncodeError();
                    break;
            }
        }
    }

    ;
}
