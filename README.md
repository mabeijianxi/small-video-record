# small-video-record
利用FFmpeg视频录制与压缩处理，这里得感谢vitamio家的秒拍SO库，也感谢提出问题的朋友们！

###效果如下：
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/small_video.gif)
###特点：
#####1：利用FFmpeg录制各种分辨率的视频。
#####2：可设置以H264编码压缩，6秒的1M视频压缩后为200多KB，且视频还比较清晰
#####3：录制简单，一行代码完成集成。 
###使用方法：
######1：添加依赖
```java
compile 'com.mabeijianxi:small-video-record:1.0.8'
```
######2:在manifests里面添加
```java
 <activity android:name="mabeijianxi.camera.MediaRecorderActivity"/>
```
######3:在Application里面初始化小视频录制：
```java
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
        VCamera.setDebugMode(true);
        VCamera.initialize(context);
    }
```
######4:跳转录制界面：
```java
MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                .doH264Compress(true)
                .smallVideoWidth(480)
                .smallVideoHeight(360)
                .recordTimeMax(6 * 1000)
                .maxFrameRate(20)
                .minFrameRate(8)
                .captureThumbnailsTime(1)
                .recordTimeMin((int) (1.5 * 1000))
                .build();
        MediaRecorderActivity.goSmallVideoRecorder(this, SendSmallVideoActivity.class.getName(), config);
```
######一些问题：
	1：编译环境请满足：targetSdkVersion<=22
	2：出现 java.lang.UnsatisfiedLinkError错误可以尝试在gradle.properties中添加：android.useDeprecatedNdk=true，然后在主module的build.gradle中配置ndk {abiFilters "armeabi", "armeabi-v7a"}
######更新日志：
	2016-12-14：
	提交1.0.8，修复部分手机不支持输入帧率问题，彻底修复录制浏览变形

	2016-10-26:
	提交1.0.7,增强兼容性，防止录制尺寸不支持奔溃

	2016-10-14:
	提交1.0.6，修复在不支持的尺寸下无异常抛出。

	2016-10-13:
	提交小视频1.0.5，修复部分手机录制变形问题。
	
	2016-10-12：
	修复sample参数小bug。
	
	2016-08-26：
	提交小视频1.0.2，增加Buidler配置，可自定义更多内容。
	
	2016-08-26：
	提交小视频1.0.1，更新配置文件。
	
	2016-08-25：
	提交小视频1.0.0
###sample下载：
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/sample.png)
[Download Demo](http://fir.im/smallvideorecord)
