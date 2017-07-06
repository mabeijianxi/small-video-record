# small-video-record
Android端音频视频采集，底层利用FFmpeg编码压缩处理（small-video-record2已从C到Java全面开源）！

## 效果如下：
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/new_sample.gif)
## 使用：
* [small-video-record1 使用步骤](https://github.com/mabeijianxi/small-video-record/blob/master/1.x_use_help.md)<br>
* [small-video-record2 使用步骤](https://github.com/mabeijianxi/small-video-record/blob/master/2.x_use_help.md)<br>
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
[编译Android下可用的全平台FFmpeg(包含libx264与libfdk-aac)](http://blog.csdn.net/mabeijianxi/article/details/74544879)<br>
[Android下玩JNI的新老三种姿势](http://blog.csdn.net/mabeijianxi/article/details/68525164)<br>

## 关于small-video-record2：

###### small-video-record2 源码编译:
你需要拥有ndk环境、AndroidStudio版本大于2.2、AndroidStudio装有Cmake插件。

###### 定制化 FFmpeg
本工程用的 FFmpeg 是精简版，如果你觉得不够用或者想定制化那么可以前往https://github.com/mabeijianxi/FFmpeg4Android下载我编译好的一个增强版 FFmpeg SO 库，或者根据里面文档运行脚本重新定制即可。

###### 2.0.0正式版
其在 64 位手机上录制基本0延迟，本地压缩在之前速度上提升2倍+，正出入这个性能上质的提升，也新增加了全屏录制与暂停录制功能！
###### 待开发功能
* 视频暂停录制功能（已完成）。
* 暴露全屏录制控制参数(已完成)。
* 全平台编译（已完成）。
* 录制时码率模式控制。
* 进度回调。
* 美颜功能。
* 更多未知功能...


## 配置说明：

#### 部分方法说明

| 	名称    | 参数类型 | 说明 |
|:-------------: |:---------------:| :-------------:|
|   fullScreen   | boolean | 设置是否需要全屏录制   |
| smallVideoWidth     |    int     |    视频宽度，对应手机长边方向，全屏录制时输入无效(1.x版本宽高颠倒)      |
| smallVideoHeight | int        |  视频高度，对应手机短边方向，需输入摄像头所支持尺寸，乱输无效（(1.x版本宽高颠倒） |
|recordTimeMax|int|单位为毫秒，最大录制时间|
|recordTimeMin|int|单位为毫秒，最小录制时间|
|maxFrameRate|int|最大帧率，一定程度上影响视频质量与大小，不要太高，有的低配手机转码速度可能会跟不上|
|videoBitrate|int|比特率，一定程度上影响视频质量与大小，理论上值越大质量将会越好|
|captureThumbnailsTime|int|缩略图剪裁其实时间|
|doH264Compress|BaseMediaBitrateConfig|设置压缩模式，支持 AutoVBRMode、CBRMode、VBRMode|
|setScale|float|视频缩放，对视频大小没要求无需输入，大于1时才执行缩放操作|
#### AutoVBRMode
此模式下可根据等级轻松控制视频质量

|名称|类型|说明|
|:----:|:-----:|:-------:|
|AutoVBRMode|int|这是个构造方法，接收一个int的压缩等级，0~51，值越大约模糊，视频越小，建议18~28|
|setVelocity|String| 设置转码速度，可选值有 ultrafast、superfast、veryfast、faster、fast、medium、slow、slower、veryslow、placebo |
#### VBRMode
|名称|类型|说明|
|:----:|:-----:|:-------:|
|VBRMode|int/int|这是个构造方法，接收一个最大码率，与一个额定码率，编码时以额定码率为基础，会尽量不超过最大码率|
|setVelocity|String| 设置转码速度，可选值有 ultrafast、superfast、veryfast、faster、fast、medium、slow、slower、veryslow、placebo |
#### CBRMode
此模式可毕竟准确的控制视频质量与大小

|名称|类型|说明|
|:----:|:-----:|:-------:|
|CBRMode|int/int|这是个构造方法，接收一个缓冲区值大小，与一个固定码率值，编码时将以固定码率为标准编码|
|setVelocity|String| 设置转码速度，可选值有 ultrafast、superfast、veryfast、faster、fast、medium、slow、slower、veryslow、placebo |

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
###### small-video-record2:
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/sample2.png)
[Download Demo2](https://fir.im/jianxiMediaRecord2)
###### small-video-record:
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/sample.png)
[Download Demo1](http://fir.im/smallvideorecord)
