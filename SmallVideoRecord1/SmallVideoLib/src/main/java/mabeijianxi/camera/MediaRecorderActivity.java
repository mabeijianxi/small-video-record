package mabeijianxi.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;

import mabeijianxi.camera.model.MediaObject;
import mabeijianxi.camera.model.MediaRecorderConfig;
import mabeijianxi.camera.util.DeviceUtils;
import mabeijianxi.camera.util.FileUtils;
import mabeijianxi.camera.util.StringUtils;
import mabeijianxi.camera.views.ProgressView;

import static mabeijianxi.camera.MediaRecorderBase.SMALL_VIDEO_WIDTH;
import static mabeijianxi.camera.MediaRecorderBase.compressConfig;

/**
 * 视频录制
 */
public class MediaRecorderActivity extends Activity implements
        MediaRecorderBase.OnErrorListener, OnClickListener, MediaRecorderBase.OnPreparedListener,
        MediaRecorderBase.OnEncodeListener {

    /**
     * 录制最长时间
     */
    private static int RECORD_TIME_MAX = 6 * 1000;
    /**
     * 录制最小时间
     */
    private static int RECORD_TIME_MIN = (int) (1.5f * 1000);
    /**
     * 刷新进度条
     */
    private static final int HANDLE_INVALIDATE_PROGRESS = 0;
    /**
     * 延迟拍摄停止
     */
    private static final int HANDLE_STOP_RECORD = 1;

    /**
     * 下一步
     */
    private ImageView mTitleNext;
    /**
     * 前后摄像头切换
     */
    private CheckBox mCameraSwitch;
    /**
     * 回删按钮、延时按钮、滤镜按钮
     */
    private CheckedTextView mRecordDelete;
    /**
     * 闪光灯
     */
    private CheckBox mRecordLed;
    /**
     * 拍摄按钮
     */
    private TextView mRecordController;

    /**
     * 底部条
     */
    private RelativeLayout mBottomLayout;
    /**
     * 摄像头数据显示画布
     */
    private SurfaceView mSurfaceView;
    /**
     * 录制进度
     */
    private ProgressView mProgressView;

    /**
     * SDK视频录制对象
     */
    private MediaRecorderBase mMediaRecorder;
    /**
     * 视频信息
     */
    private MediaObject mMediaObject;

    /**
     * 是否是点击状态
     */
    private volatile boolean mPressedStatus;
    /**
     * 是否已经释放
     */
    private volatile boolean mReleased;
    /**
     * 视屏地址
     */
    public final static String VIDEO_URI = "video_uri";
    /**
     * 本次视频保存的文件夹地址
     */
    public final static String OUTPUT_DIRECTORY = "output_directory";
    /**
     * 视屏截图地址
     */
    public final static String VIDEO_SCREENSHOT = "video_screenshot";
    /**
     * 录制完成后需要跳转的activity
     */
    public final static String OVER_ACTIVITY_NAME = "over_activity_name";
    /**
     * 最大录制时间的key
     */
    public final static String MEDIA_RECORDER_MAX_TIME_KEY = "media_recorder_max_time_key";
    /**
     * 最小录制时间的key
     */
    public final static String MEDIA_RECORDER_MIN_TIME_KEY = "media_recorder_min_time_key";
    /**
     * 录制配置key
     */
    public final static String MEDIA_RECORDER_CONFIG_KEY = "media_recorder_config_key";

    private boolean GO_HOME;
    /**
     * @param context
     * @param overGOActivityName 录制结束后需要跳转的Activity全类名
     */
    public static void goSmallVideoRecorder(Activity context, String overGOActivityName, MediaRecorderConfig mediaRecorderConfig) {
        context.startActivity(new Intent(context, MediaRecorderActivity.class).putExtra(OVER_ACTIVITY_NAME, overGOActivityName).putExtra(MEDIA_RECORDER_CONFIG_KEY, mediaRecorderConfig));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 防止锁屏
        initData();
        loadViews();
    }

    private void initData() {
        Intent intent = getIntent();
        MediaRecorderConfig mediaRecorderConfig = intent.getParcelableExtra(MEDIA_RECORDER_CONFIG_KEY);
        if (mediaRecorderConfig == null) {
            return;
        }
        RECORD_TIME_MAX = mediaRecorderConfig.getRecordTimeMax();
        RECORD_TIME_MIN = mediaRecorderConfig.getRecordTimeMin();
        MediaRecorderBase.MAX_FRAME_RATE=mediaRecorderConfig.getMaxFrameRate();
        MediaRecorderBase.MIN_FRAME_RATE=mediaRecorderConfig.getMinFrameRate();
        MediaRecorderBase.SMALL_VIDEO_HEIGHT=mediaRecorderConfig.getSmallVideoHeight();
        SMALL_VIDEO_WIDTH=mediaRecorderConfig.getSmallVideoWidth();
        MediaRecorderBase.mVideoBitrate=mediaRecorderConfig.getVideoBitrate();
        MediaRecorderBase.mediaRecorderConfig=mediaRecorderConfig.getMediaBitrateConfig();
        MediaRecorderBase.compressConfig=mediaRecorderConfig.getCompressConfig();
        MediaRecorderBase.CAPTURE_THUMBNAILS_TIME=mediaRecorderConfig.getCaptureThumbnailsTime();
        MediaRecorderBase.doH264Compress=mediaRecorderConfig.isDoH264Compress();
        GO_HOME=mediaRecorderConfig.isGO_HOME();
    }

    /**
     * 加载视图
     */
    private void loadViews() {
        setContentView(R.layout.activity_media_recorder);
        // ~~~ 绑定控件
        mSurfaceView = (SurfaceView) findViewById(R.id.record_preview);
        mCameraSwitch = (CheckBox) findViewById(R.id.record_camera_switcher);
        mTitleNext = (ImageView) findViewById(R.id.title_next);
        mProgressView = (ProgressView) findViewById(R.id.record_progress);
        mRecordDelete = (CheckedTextView) findViewById(R.id.record_delete);
        mRecordController = (TextView) findViewById(R.id.record_controller);
        mBottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mRecordLed = (CheckBox) findViewById(R.id.record_camera_led);

        // ~~~ 绑定事件
        /*if (DeviceUtils.hasICS())
            mSurfaceView.setOnTouchListener(mOnSurfaveViewTouchListener);*/

        mTitleNext.setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
        mRecordDelete.setOnClickListener(this);
        mRecordController.setOnTouchListener(mOnVideoControllerTouchListener);

        // ~~~ 设置数据

        // 是否支持前置摄像头
        if (MediaRecorderBase.isSupportFrontCamera()) {
            mCameraSwitch.setOnClickListener(this);
        } else {
            mCameraSwitch.setVisibility(View.GONE);
        }
        // 是否支持闪光灯
        if (DeviceUtils.isSupportCameraLedFlash(getPackageManager())) {
            mRecordLed.setOnClickListener(this);
        } else {
            mRecordLed.setVisibility(View.GONE);
        }


        mProgressView.setMaxDuration(RECORD_TIME_MAX);
        mProgressView.setMinTime(RECORD_TIME_MIN);
    }

    /**
     * 初始化画布
     */
    private void initSurfaceView() {
        final int w = DeviceUtils.getScreenWidth(this);
        ((RelativeLayout.LayoutParams) mBottomLayout.getLayoutParams()).topMargin = (int) (w / (SMALL_VIDEO_WIDTH / (MediaRecorderBase.SMALL_VIDEO_HEIGHT * 1.0f)));
        int width = w;
        int height = (int) (w * ((MediaRecorderBase.mSupportedPreviewWidth*1.0f)/MediaRecorderBase.SMALL_VIDEO_WIDTH));
        //
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mSurfaceView
                .getLayoutParams();
        lp.width = width;
        lp.height = height;
        mSurfaceView.setLayoutParams(lp);
    }

    /**
     * 初始化拍摄SDK
     */
    private void initMediaRecorder() {
        mMediaRecorder = new MediaRecorderNative();

        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setOnEncodeListener(this);
        mMediaRecorder.setOnPreparedListener(this);

        File f = new File(VCamera.getVideoCachePath());
        if (!FileUtils.checkFile(f)) {
            f.mkdirs();
        }
        String key = String.valueOf(System.currentTimeMillis());
        mMediaObject = mMediaRecorder.setOutputDirectory(key,
                VCamera.getVideoCachePath() + key);
        mMediaRecorder.setSurfaceHolder(mSurfaceView.getHolder());
        mMediaRecorder.prepare();
    }


    /**
     * 点击屏幕录制
     */
    private View.OnTouchListener mOnVideoControllerTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mMediaRecorder == null) {
                return false;
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 检测是否手动对焦
                    // 判断是否已经超时
                    if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                        return true;
                    }

                    // 取消回删
                    if (cancelDelete())
                        return true;

                    startRecord();

                    break;

                case MotionEvent.ACTION_UP:
                    // 暂停
                    if (mPressedStatus) {
                        stopRecord();

                        // 检测是否已经完成
                        if (mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                            mTitleNext.performClick();
                        }
                    }
                    break;
            }
            return true;
        }

    };

    @Override
    public void onResume() {
        super.onResume();
        UtilityAdapter.freeFilterParser();
        UtilityAdapter.initFilterParser();

        if (mMediaRecorder == null) {
            initMediaRecorder();
        } else {
            mRecordLed.setChecked(false);
            mMediaRecorder.prepare();
            mProgressView.setData(mMediaObject);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRecord();
        UtilityAdapter.freeFilterParser();
        if (!mReleased) {
            if (mMediaRecorder != null)
                mMediaRecorder.release();
        }
        mReleased = false;
    }


    /**
     * 开始录制
     */
    private void startRecord() {
        if (mMediaRecorder != null) {

            MediaObject.MediaPart part = mMediaRecorder.startRecord();
            if (part == null) {
                return;
            }

            // 如果使用MediaRecorderSystem，不能在中途切换前后摄像头，否则有问题
            if (mMediaRecorder instanceof MediaRecorderSystem) {
                mCameraSwitch.setVisibility(View.GONE);
            }
            mProgressView.setData(mMediaObject);
        }

        mPressedStatus = true;
//		TODO 开始录制的图标
        mRecordController.animate().scaleX(0.8f).scaleY(0.8f).setDuration(500).start();


        if (mHandler != null) {
            mHandler.removeMessages(HANDLE_INVALIDATE_PROGRESS);
            mHandler.sendEmptyMessage(HANDLE_INVALIDATE_PROGRESS);

            mHandler.removeMessages(HANDLE_STOP_RECORD);
            mHandler.sendEmptyMessageDelayed(HANDLE_STOP_RECORD,
                    RECORD_TIME_MAX - mMediaObject.getDuration());
        }
        mRecordDelete.setVisibility(View.GONE);
        mCameraSwitch.setEnabled(false);
        mRecordLed.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        if (mRecordDelete != null && mRecordDelete.isChecked()) {
            cancelDelete();
            return;
        }

        if (mMediaObject != null && mMediaObject.getDuration() > 1) {
            // 未转码
            new AlertDialog.Builder(this)
                    .setTitle(R.string.hint)
                    .setMessage(R.string.record_camera_exit_dialog_message)
                    .setNegativeButton(
                            R.string.record_camera_cancel_dialog_yes,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    mMediaObject.delete();
                                    finish();
                                }

                            })
                    .setPositiveButton(R.string.record_camera_cancel_dialog_no,
                            null).setCancelable(false).show();
            return;
        }

        if (mMediaObject != null)
            mMediaObject.delete();
        finish();
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        mPressedStatus = false;
        mRecordController.animate().scaleX(1).scaleY(1).setDuration(500).start();

        if (mMediaRecorder != null) {
            mMediaRecorder.stopRecord();
        }

        mRecordDelete.setVisibility(View.VISIBLE);
        mCameraSwitch.setEnabled(true);
        mRecordLed.setEnabled(true);

        mHandler.removeMessages(HANDLE_STOP_RECORD);
        checkStatus();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (mHandler.hasMessages(HANDLE_STOP_RECORD)) {
            mHandler.removeMessages(HANDLE_STOP_RECORD);
        }

        // 处理开启回删后其他点击操作
        if (id != R.id.record_delete) {
            if (mMediaObject != null) {
                MediaObject.MediaPart part = mMediaObject.getCurrentPart();
                if (part != null) {
                    if (part.remove) {
                        part.remove = false;
                        mRecordDelete.setChecked(false);
                        if (mProgressView != null)
                            mProgressView.invalidate();
                    }
                }
            }
        }

        if (id == R.id.title_back) {
            onBackPressed();
        } else if (id == R.id.record_camera_switcher) {// 前后摄像头切换
            if (mRecordLed.isChecked()) {
                if (mMediaRecorder != null) {
                    mMediaRecorder.toggleFlashMode();
                }
                mRecordLed.setChecked(false);
            }

            if (mMediaRecorder != null) {
                mMediaRecorder.switchCamera();
            }

            if (mMediaRecorder.isFrontCamera()) {
                mRecordLed.setEnabled(false);
            } else {
                mRecordLed.setEnabled(true);
            }
        } else if (id == R.id.record_camera_led) {// 闪光灯
            // 开启前置摄像头以后不支持开启闪光灯
            if (mMediaRecorder != null) {
                if (mMediaRecorder.isFrontCamera()) {
                    return;
                }
            }

            if (mMediaRecorder != null) {
                mMediaRecorder.toggleFlashMode();
            }
        } else if (id == R.id.title_next) {// 停止录制
            mMediaRecorder.startEncoding();
            /*finish();
            overridePendingTransition(R.anim.push_bottom_in,
					R.anim.push_bottom_out);*/
        } else if (id == R.id.record_delete) {
            // 取消回删
            if (mMediaObject != null) {
                MediaObject.MediaPart part = mMediaObject.getCurrentPart();
                if (part != null) {
                    if (part.remove) {
                        part.remove = false;
                        mMediaObject.removePart(part, true);
                        mRecordDelete.setChecked(false);
                    } else {
                        part.remove = true;
                        mRecordDelete.setChecked(true);
                    }
                }
                if (mProgressView != null)
                    mProgressView.invalidate();

                // 检测按钮状态
                checkStatus();
            }
        }
    }


    /**
     * 取消回删
     */
    private boolean cancelDelete() {
        if (mMediaObject != null) {
            MediaObject.MediaPart part = mMediaObject.getCurrentPart();
            if (part != null && part.remove) {
                part.remove = false;
                mRecordDelete.setChecked(false);

                if (mProgressView != null)
                    mProgressView.invalidate();

                return true;
            }
        }
        return false;
    }

    /**
     * 检查录制时间，显示/隐藏下一步按钮
     */
    private int checkStatus() {
        int duration = 0;
        if (!isFinishing() && mMediaObject != null) {
            duration = mMediaObject.getDuration();
            if (duration < RECORD_TIME_MIN) {
                if (duration == 0) {
                    mCameraSwitch.setVisibility(View.VISIBLE);
                    mRecordDelete.setVisibility(View.GONE);
                }
                // 视频必须大于3秒
                if (mTitleNext.getVisibility() != View.INVISIBLE)
                    mTitleNext.setVisibility(View.INVISIBLE);
            } else {
                // 下一步
                if (mTitleNext.getVisibility() != View.VISIBLE) {
                    mTitleNext.setVisibility(View.VISIBLE);
                }
            }
        }
        return duration;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_INVALIDATE_PROGRESS:
                    if (mMediaRecorder != null && !isFinishing()) {
                        if (mMediaObject != null && mMediaObject.getMedaParts() != null && mMediaObject.getDuration() >= RECORD_TIME_MAX) {
                            stopRecord();
                            mTitleNext.performClick();
                            return;
                        }
                        if (mProgressView != null)
                            mProgressView.invalidate();
                        // if (mPressedStatus)
                        // titleText.setText(String.format("%.1f",
                        // mMediaRecorder.getDuration() / 1000F));
                        if (mPressedStatus)
                            sendEmptyMessageDelayed(0, 30);
                    }
                    break;
            }
        }
    };

    @Override
    public void onEncodeStart() {
        showProgress("", getString(R.string.record_camera_progress_message));
    }

    @Override
    public void onEncodeProgress(int progress) {
    }

    /**
     * 转码完成
     */
    @Override
    public void onEncodeComplete() {
        hideProgress();
        Intent intent = null;
        try {
            intent = new Intent(this, Class.forName(getIntent().getStringExtra(OVER_ACTIVITY_NAME)));
            intent.putExtra(MediaRecorderActivity.OUTPUT_DIRECTORY, mMediaObject.getOutputDirectory());
            if(compressConfig!=null){
                intent.putExtra(MediaRecorderActivity.VIDEO_URI, mMediaObject.getOutputTempTranscodingVideoPath());
            }else {
                intent.putExtra(MediaRecorderActivity.VIDEO_URI, mMediaObject.getOutputTempVideoPath());
            }
            intent.putExtra(MediaRecorderActivity.VIDEO_SCREENSHOT, mMediaObject.getOutputVideoThumbPath());
            intent.putExtra("go_home",GO_HOME);
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("需要传入录制完成后跳转的Activity的全类名");
        }

        finish();
    }

    /**
     * 转码失败 检查sdcard是否可用，检查分块是否存在
     */
    @Override
    public void onEncodeError() {
        hideProgress();
        Toast.makeText(this, R.string.record_video_transcoding_faild,
                Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onVideoError(int what, int extra) {

    }

    @Override
    public void onAudioError(int what, String message) {

    }

    @Override
    public void onPrepared() {
        initSurfaceView();
    }

    public void onFinished() {
        finish();
    }

    protected ProgressDialog mProgressDialog;

    public ProgressDialog showProgress(String title, String message) {
        return showProgress(title, message, -1);
    }

    public ProgressDialog showProgress(String title, String message, int theme) {
        if (mProgressDialog == null) {
            if (theme > 0)
                mProgressDialog = new ProgressDialog(this, theme);
            else
                mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.setCanceledOnTouchOutside(false);// 不能取消
            mProgressDialog.setCancelable(false);
            mProgressDialog.setIndeterminate(true);// 设置进度条是否不明确
        }

        if (!StringUtils.isEmpty(title))
            mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
        return mProgressDialog;
    }

    public void hideProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        hideProgress();
        mProgressDialog = null;
    }
}
