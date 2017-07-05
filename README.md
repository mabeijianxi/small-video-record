# small-video-record
Android端音频视频采集，底层利用FFmpeg编码压缩处理（small-video-record2已从C到Java全面开源）！

## 效果如下：
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/new_sample.gif)
## 使用：
[small-video-record1 使用步骤](https://github.com/mabeijianxi/small-video-record/blob/master/1.x_use_help.md)<br><br>
[small-video-record2 使用步骤](https://github.com/mabeijianxi/small-video-record/blob/master/2.x_use_help.md)<br>
## 特点：
* 边采集边编码。
* 利用FFmpeg自定义录制各种时长、分辨率、码率、帧率、转码速度的视频。
* small-video-record2已解耦FFmpeg，可根据自己需求定制FFmpeg。
* 暴露FFmpeg命令操作接口，可自定义更多功能。
* small-video-record2 支持全平台，如果你手机 cpu 是64位的将达到秒编！
* 可选择本地视频进行个性化压缩，如果你手机 cpu 是64位的速度将相对很快。
* 录制简单，几行代码完成集成，几个参数搞定录制。
## 开发步骤、源码详解、工具准备：
[利用FFmpeg玩转Android视频录制与压缩（一）](http://blog.csdn.net/mabeijianxi/article/details/63335722)<br>
[利用FFmpeg玩转Android视频录制与压缩（二）](http://blog.csdn.net/mabeijianxi/article/details/72983362)<br>
[利用FFmpeg玩转Android视频录制与压缩（三）](http://blog.csdn.net/mabeijianxi/article/details/73011313)<br>
[编译Android下可执行命令的FFmpeg](http://blog.csdn.net/mabeijianxi/article/details/72904694)<br>
[编译Android下可用的FFmpeg(包含libx264与libfdk-aac)](http://blog.csdn.net/mabeijianxi/article/details/72904694)<br>
[Android下玩JNI的新老三种姿势](http://blog.csdn.net/mabeijianxi/article/details/68525164)<br>
## 关于small-video-record2：

###### 源码编译:
你需要拥有ndk环境、AndroidStudio版本大于2.2、AndroidStudio装有Cmake插件。本项目编译的 FFmpeg 是精简版的，如果你需要更强大的功能<br>
可以前往 [https://github.com/mabeijianxi/FFmpeg4Android](https://github.com/mabeijianxi/FFmpeg4Android) ,里面有编译好的更全的库,<br>且包含所有编译脚本，与源码。
###### 待开发功能
* 视频暂停录制功能（已完成）。
* 暴露全屏录制控制参数(已完成)。
* 全平台编译（已完成）。
* 录制时码率模式控制。
* 进度回调。
* 美颜功能。
* 更多未知功能...


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

	2017-07-05:
	提交 2.0.0 稳定版,修复 bug 若干
	增加全平台编译
	优化录制与压缩速度，对于 64 位 CPU 的手机，录制转码达到秒转，本地压缩速度提升近 2 倍多。
	增加全屏录制功能
	增加暂停录制功能
	
	2017-06-14:
	提交2.0.0-beta3，本地压缩新增分辨率缩放功能。
	
	2017-06-13:
	提交2.0.0-beta2，更改默认压缩速度为最快，开始多线程编码。
	
	2017-06-10:
	修改编译脚本，增加可移植性

	
## small-video-record 更新日志：

	2017-06-14:
	提交1.2.2，本地压缩新增分辨率缩放功能。
	
	2017-06-13:
	提交1.2.1，更改默认压缩速度为最快。
	
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
###### small-video-record:
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/sample.png)
[Download Demo1](http://fir.im/smallvideorecord)
###### small-video-record2:
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/sample2.png)
[Download Demo2](https://fir.im/jianxiMediaRecord2)
