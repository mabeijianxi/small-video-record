# small-video-record
Android端音频视频采集，底层利用FFmpeg编码压缩处理！

## 效果如下：
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/new_sample.gif)
## 特点：
* 边采集边编码。
* 利用FFmpeg自定义录制各种时长、分辨率、码率、帧率、转码速度的视频。
* small-video-record2已解耦FFmpeg，可根据自己需求定制FFmpeg。
* 视频采用libx264编解,音频采用libfdk-aac,相对效率高。
* 可选择本地视频压缩。
* 可选择本地视频压缩。
* 录制简单，几行代码完成集成，几个参数搞定录制。
## 源码详解：
[利用FFmpeg玩转Android视频录制与压缩（一）](http://blog.csdn.net/mabeijianxi/article/details/63335722)
## small-video-record2：
###### 变化
* 输入的高度宽度概念与small-video-record相反，现在相对合理。
* 暂时不支持暂停录制。
* 视频录制模式下暂时不支持码率模式控制与编码速度控制（本地压缩支持）。
###### 待开发功能
* 视频暂停录制功能。
* 录制时码率模式控制。
* 美颜功能。
* 更多未知功能...

## 使用方法：
**small-video-record2**和**small-video-record**是两个不同的项目，且不兼容，不过使用方法差不多。
#### small-video-record2使用方法
###### 1：添加依赖
```java
compile 'com.mabeijianxi:small-video-record2:1.0.2beta@aar
```
###### 2:在manifests里面添加
```java
 <activity android:name="com.mabeijianxi.smallvideorecord2.MediaRecorderActivity"/>
```
###### 3:在Application里面初始化小视频录制：
```java
public static void initSmallVideo() {
        // 设置拍摄视频缓存路径
        File dcim = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (DeviceUtils.isZte()) {
            if (dcim.exists()) {
                JianXiCamera.setVideoCachePath(dcim + "/mabeijianxi/");
            } else {
                JianXiCamera.setVideoCachePath(dcim.getPath().replace("/sdcard/",
                        "/sdcard-ext/")
                        + "/mabeijianxi/");
            }
        } else {
            JianXiCamera.setVideoCachePath(dcim + "/mabeijianxi/");
        }
        // 初始化拍摄
        JianXiCamera.initialize(false,null);
    }
```
###### 4:跳转录制界面或选择压缩：
```java
// 录制
MediaRecorderConfig config = new MediaRecorderConfig.Buidler()
                .smallVideoWidth(360)
                .smallVideoHeight(480)
                .recordTimeMax(6*1000)
                .maxFrameRate(20)
                .videoBitrate(600*1000)
                .captureThumbnailsTime(1)
                .build();
        MediaRecorderActivity.goSmallVideoRecorder(this, SendSmallVideoActivity.class.getName(), config);
// 选择本地视频压缩
LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                        final LocalMediaConfig config = buidler
                                .setVideoPath(path)
                                .captureThumbnailsTime(1)
                                .doH264Compress(new AutoVBRMode())
                                .setFramerate(15)
                                .build();
                        OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();	
```

#### small-video-record使用方法
###### 1：添加依赖
```java
compile 'com.mabeijianxi:small-video-record:1.2.0'
```
###### 2:在manifests里面添加
```java
 <activity android:name="mabeijianxi.camera.MediaRecorderActivity"/>
```
###### 3:在Application里面初始化小视频录制：
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
###### 4:跳转录制界面或选择压缩：
```java
// 录制
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
                .captureThumbnailsTime(1)
                .recordTimeMin((int) (1.5 * 1000))
                .build();
        MediaRecorderActivity.goSmallVideoRecorder(this, SendSmallVideoActivity.class.getName(), config);
// 选择本地视频压缩
LocalMediaConfig.Buidler buidler = new LocalMediaConfig.Buidler();
                        final LocalMediaConfig config = buidler
                                .setVideoPath(path)
                                .captureThumbnailsTime(1)
                                .doH264Compress(new AutoVBRMode())
                                .setFramerate(15)
                                .build();
                        OnlyCompressOverBean onlyCompressOverBean = new LocalMediaCompress(config).startCompress();	
```
###### 5:一些参数说明：
		maxFrameRate：指定最大帧率，越大视频质量越好，体积也会越大，当在cbr模式下不再是动态帧率，而是固定帧率；
		
		captureThumbnailsTime：指定剪切哪个时间的画面来作为封面图；
		
		doH264Compress：不传入值将不做进一步压缩，暂时可以传入三种模式AutoVBRMode、VBRMode、VBRMode；
		
		setMediaBitrateConfig:视频录制时期的一些配置，暂时可以传入三种模式AutoVBRMode、VBRMode、VBRMode；
		
		AutoVBRMode：可以传入一个视频等级与转码速度，等级为0-51，越大质量越差，建议18~28之间即可。转码速度有ultrafast、superfast、			veryfast、faster、fast、medium、slow、slower、veryslow、placebo。
		
		VBRMode：此模式下可以传入一个最大码率与一个额定码率，当然同样可以设置转码速度。
		
		VBRMode:可以传入一个固定码率，也可以添加一个转码速度。
## small-video-record的一些问题（2中已修复）：
	1：编译环境请满足：targetSdkVersion<=22
	2：出现 java.lang.UnsatisfiedLinkError错误可以尝试在gradle.properties中添加：android.useDeprecatedNdk=true，然后在主module的build.gradle中配置ndk {abiFilters "armeabi", "armeabi-v7a"}
## small-video-record2 更新日志：
	
	2017-06-10:
	提交1.0.2beta
	
## small-video-record 更新日志：
	
	2017-04-06:
	提交1.2.0，增加选择本地视频压缩，修改一系列bug
	
	2017-03-16:
	提交1.1.0，增加更精细的码率控制、转码速度、压缩等级等可配置参数，修复一些bug	

	2017-03-14：
	提交1.0.9，新增可配置码率模式（VBR、CBR）与其大小
	
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
### sample下载：
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/sample.png)
[Download Demo](http://fir.im/smallvideorecord)
