package com.mabeijianxi.smallvideo;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import mabeijianxi.camera.MediaRecorderActivity;
import mabeijianxi.camera.VCamera;
import mabeijianxi.camera.model.AutoVBRMode;
import mabeijianxi.camera.model.BaseMediaBitrateConfig;
import mabeijianxi.camera.model.CBRMode;
import mabeijianxi.camera.model.MediaRecorderConfig;
import mabeijianxi.camera.model.VbrMode;
import mabeijianxi.camera.util.DeviceUtils;

import static com.mabeijianxi.smallvideo.R.id.et_crfSize;
import static com.mabeijianxi.smallvideo.R.id.rb_no_compress;

public class MainActivity extends AppCompatActivity {

    private View i_record;
    private View i_compress;
    private Button bt_start;
    private TextView tv_size;
    private EditText et_width;
    private EditText et_height;
    private EditText et_maxtime;
    private EditText et_mintime;
    private RadioGroup rg_compress;
    private Spinner spinner_record;
    private LinearLayout ll_compress;
    private EditText et_maxframerate;
    private Spinner spinner_compress;
    private RadioGroup rg_record_mode;
    private EditText et_record_crfSize;
    private LinearLayout ll_record_crf;
    private EditText et_record_bitrate;
    private RadioGroup rg_compress_mode;
    private EditText et_compress_bitrate;
    private EditText et_compress_crfSize;
    private LinearLayout ll_compress_crf;
    private TextView tv_record_maxbitrate;
    private EditText et_record_maxbitrate;
    private LinearLayout ll_record_bitrate;
    private TextView tv_compress_maxbitrate;
    private EditText et_compress_maxbitrate;
    private LinearLayout ll_compress_bitrate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSmallVideo(this);
        initView();
        initEvent();
        setSupportCameraSize();
    }

    private void setSupportCameraSize() {
        Camera back = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        List<Camera.Size> backSizeList = back.getParameters().getSupportedPreviewSizes();
        StringBuilder str = new StringBuilder();
        str.append("经过检查您的摄像头，如使用后置摄像头您可以输入的宽度有：");
        for (Camera.Size bSize : backSizeList) {
            str.append(bSize.height + "、");
        }
        back.release();
        Camera front = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        List<Camera.Size> frontSizeList = front.getParameters().getSupportedPreviewSizes();
        str.append("如使用前置摄像头您可以输入的宽度有：");
        for (Camera.Size fSize : frontSizeList) {
            str.append(fSize.height + "、");
        }
        front.release();
        tv_size.setText(str);
    }

    private void initEvent() {
        rg_record_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_auto:
                        ll_record_crf.setVisibility(View.VISIBLE);
                        ll_record_bitrate.setVisibility(View.GONE);
                        break;
                    case R.id.rb_vbr:
                        ll_record_crf.setVisibility(View.GONE);
                        ll_record_bitrate.setVisibility(View.VISIBLE);
                        tv_record_maxbitrate.setVisibility(View.VISIBLE);
                        et_record_maxbitrate.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_cbr:
                        ll_record_crf.setVisibility(View.GONE);
                        ll_record_bitrate.setVisibility(View.VISIBLE);
                        tv_record_maxbitrate.setVisibility(View.GONE);
                        et_record_maxbitrate.setVisibility(View.GONE);
                        break;
                }
            }
        });

        rg_compress.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case rb_no_compress:
                        ll_compress.setVisibility(View.GONE);
                        i_compress.setVisibility(View.GONE);
                        spinner_compress.setVisibility(View.GONE);
                        break;
                    case R.id.rb_compress:
                        ll_compress.setVisibility(View.VISIBLE);
                        i_compress.setVisibility(View.VISIBLE);
                        spinner_compress.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        rg_compress_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_auto:
                        ll_compress_crf.setVisibility(View.VISIBLE);
                        ll_compress_bitrate.setVisibility(View.GONE);
                        break;
                    case R.id.rb_vbr:
                        ll_compress_crf.setVisibility(View.GONE);
                        ll_compress_bitrate.setVisibility(View.VISIBLE);
                        tv_compress_maxbitrate.setVisibility(View.VISIBLE);
                        et_compress_maxbitrate.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rb_cbr:
                        ll_compress_crf.setVisibility(View.GONE);
                        ll_compress_bitrate.setVisibility(View.VISIBLE);
                        tv_compress_maxbitrate.setVisibility(View.GONE);
                        et_compress_maxbitrate.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }

    private void initView() {
        tv_size = (TextView) findViewById(R.id.tv_size);
        et_width = (EditText) findViewById(R.id.et_width);
        et_height = (EditText) findViewById(R.id.et_height);
        et_maxframerate = (EditText) findViewById(R.id.et_maxframerate);
        et_maxtime = (EditText) findViewById(R.id.et_maxtime);
        et_mintime = (EditText) findViewById(R.id.et_mintime);

        i_record = findViewById(R.id.i_record);
        rg_record_mode = (RadioGroup) i_record.findViewById(R.id.rg_mode);
        ll_record_crf = (LinearLayout) i_record.findViewById(R.id.ll_crf);
        et_record_crfSize = (EditText) i_record.findViewById(et_crfSize);
        ll_record_bitrate = (LinearLayout) i_record.findViewById(R.id.ll_bitrate);
        et_record_maxbitrate = (EditText) i_record.findViewById(R.id.et_maxbitrate);
        tv_record_maxbitrate = (TextView) i_record.findViewById(R.id.tv_maxbitrate);
        et_record_bitrate = (EditText) i_record.findViewById(R.id.et_bitrate);

        spinner_record = (Spinner) findViewById(R.id.spinner_record);

        rg_compress = (RadioGroup) findViewById(R.id.rg_compress);

        ll_compress = (LinearLayout) findViewById(R.id.ll_compress);

        i_compress = findViewById(R.id.i_compress);
        rg_compress_mode = (RadioGroup) i_compress.findViewById(R.id.rg_mode);
        ll_compress_crf = (LinearLayout) i_compress.findViewById(R.id.ll_crf);
        et_compress_crfSize = (EditText) i_compress.findViewById(et_crfSize);
        ll_compress_bitrate = (LinearLayout) i_compress.findViewById(R.id.ll_bitrate);
        et_compress_maxbitrate = (EditText) i_compress.findViewById(R.id.et_maxbitrate);
        tv_compress_maxbitrate = (TextView) i_compress.findViewById(R.id.tv_maxbitrate);
        et_compress_bitrate = (EditText) i_compress.findViewById(R.id.et_bitrate);

        spinner_compress = (Spinner) findViewById(R.id.spinner_compress);

        bt_start = (Button) findViewById(R.id.bt_start);


    }

    public void go(View c) {
        String width = et_width.getText().toString();
        String height = et_height.getText().toString();
        String maxFramerate = et_maxframerate.getText().toString();
        String maxTime = et_maxtime.getText().toString();
        String minTime = et_mintime.getText().toString();
        int recordModeCheckedId = rg_record_mode.getCheckedRadioButtonId();
        int useCompressId = rg_compress.getCheckedRadioButtonId();

        BaseMediaBitrateConfig recordMode;
        BaseMediaBitrateConfig compressMode = null;

        if (recordModeCheckedId == R.id.rb_cbr) {
            String bitrate = et_record_bitrate.getText().toString();
            if (checkStrEmpty(bitrate, "请输入额定码率")) {
                return;
            }
            recordMode = new CBRMode(166, Integer.valueOf(bitrate));
        } else if (recordModeCheckedId == R.id.rb_auto) {
            String crfSize = et_compress_crfSize.getText().toString();
            if (TextUtils.isEmpty(crfSize)) {
                recordMode = new AutoVBRMode();
            } else {
                recordMode = new AutoVBRMode(Integer.valueOf(crfSize));
            }
        } else if (recordModeCheckedId == R.id.rb_vbr) {
            String maxBitrate = et_record_maxbitrate.getText().toString();
            String bitrate = et_record_bitrate.getText().toString();

            if (checkStrEmpty(maxBitrate, "请输入最大码率") || checkStrEmpty(bitrate, "请输入额定码率")) {
                return;
            }
            recordMode = new VbrMode(Integer.valueOf(maxBitrate), Integer.valueOf(bitrate));
        } else {
            recordMode = new AutoVBRMode();
        }

        if(!spinner_record.getSelectedItem().toString().equals("none")){
            recordMode.setVelocity(spinner_record.getSelectedItem().toString());
        }

        if (checkStrEmpty(width, "请输入宽度")
                || checkStrEmpty(height, "请输入高度")
                || checkStrEmpty(maxFramerate, "请输入最高帧率")
                || checkStrEmpty(maxTime, "请输入最大录制时间")
                || checkStrEmpty(minTime, "请输入最小录制时间")
                ) {
            return;
        }


        if (useCompressId == R.id.rb_compress) {
            int compressModeCheckedId = rg_compress_mode.getCheckedRadioButtonId();

            if (compressModeCheckedId == R.id.rb_cbr) {
                String bitrate = et_compress_bitrate.getText().toString();
                if (checkStrEmpty(bitrate, "请输入二次压缩额定码率")) {
                    return;
                }
                compressMode = new CBRMode(166, Integer.valueOf(bitrate));
            } else if (compressModeCheckedId == R.id.rb_auto) {
                String crfSize = et_compress_crfSize.getText().toString();
                if (TextUtils.isEmpty(crfSize)) {
                    compressMode = new AutoVBRMode();
                } else {
                    compressMode = new AutoVBRMode(Integer.valueOf(crfSize));
                }
            } else if (compressModeCheckedId == R.id.rb_vbr) {
                String maxBitrate = et_compress_maxbitrate.getText().toString();
                String bitrate = et_compress_bitrate.getText().toString();

                if (checkStrEmpty(maxBitrate, "请输入二次压缩最大码率") || checkStrEmpty(bitrate, "请输入二次压缩额定码率")) {
                    return;
                }
                compressMode = new VbrMode(Integer.valueOf(maxBitrate), Integer.valueOf(bitrate));
            } else {
                compressMode = new AutoVBRMode();
            }

            if(!spinner_compress.getSelectedItem().toString().equals("none")){
                compressMode.setVelocity(spinner_compress.getSelectedItem().toString());
            }
        }


        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                .doH264Compress(compressMode)
                .setMediaBitrateConfig(recordMode)
                .smallVideoWidth(Integer.valueOf(width))
                .smallVideoHeight(Integer.valueOf(height))
                .recordTimeMax(Integer.valueOf(maxTime))
                .maxFrameRate(Integer.valueOf(maxFramerate))
                .captureThumbnailsTime(1)
                .recordTimeMin(Integer.valueOf(minTime))
                .build();
        MediaRecorderActivity.goSmallVideoRecorder(this, SendSmallVideoActivity.class.getName(), config);
//    不知道传入什么？用下面的参数就可以了
//
        /*
        MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                .doH264Compress(new AutoVBRMode()
//                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST)
                )
                .setMediaBitrateConfig(new AutoVBRMode()
//                        .setVelocity(BaseMediaBitrateConfig.Velocity.ULTRAFAST)
                )
                .smallVideoWidth(480)
                .smallVideoHeight(360)
                .recordTimeMax(6 * 1000)
                .maxFrameRate(20)
                .minFrameRate(8)
                .captureThumbnailsTime(1)
                .recordTimeMin((int) (1.5 * 1000))
                .build();
        MediaRecorderActivity.goSmallVideoRecorder(this, SendSmallVideoActivity.class.getName(), config);
        */

    }

    private boolean checkStrEmpty(String str, String display) {
        if (TextUtils.isEmpty(str)) {
            Toast.makeText(this, display, Toast.LENGTH_SHORT);
            return true;
        }
        return false;
    }

    public static void initSmallVideo(Context context) {
        // 设置拍摄视频缓存路径
        File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                VCamera.setVideoCachePath(dcim + "/mabeijianxi/");
            } else {
                VCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/")
                        + "/mabeijianxi/");
            }
        } else {
            VCamera.setVideoCachePath(dcim + "/mabeijianxi/");
        }
        // 开启log输出,ffmpeg输出到logcat
        VCamera.setDebugMode(true);
        // 初始化拍摄SDK，必须
        VCamera.initialize(context);
    }
}
