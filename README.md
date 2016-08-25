# small-video-record
利用FFmpeg视频录制与压缩处理，这里得感谢vatamio家的SO库。

###效果如下：
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/small_video.gif)
###特点：
#####1：利用FFmpeg录制各种尺寸的视频。
#####2：压缩压缩为H264编码，6秒的1M视频压缩后为200多KB，且视频还比较清晰
#####3：录制简单，一行代码完成集成。 
###使用方法：
######1：添加依赖
		java`compile 'com.mabeijianxi:small-video-record:1.0.0'`
######2:在manifests里面添加
		java` <activity
            android:name="mabeijianxi.camera.MediaRecorderActivity"
            />`
######3:在Application里面初始化小视频录制：
		java` public static void initSmallVideo(Context context) {
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
        VCamera.setDebugMode(true);
        VCamera.initialize(context);
    }`
######4:跳转录制界面：
	java`MediaRecorderActivity.goSmallVideoRecorder(this, SendSmallVideoActivity.class.getName(), 6 * 1000, (int) (1.5 * 1000));`
