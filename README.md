# small-video-record
[![license](http://img.shields.io/badge/license-Apache2.0-brightgreen.svg?style=flat)](https://github.com/mabeijianxi/small-video-record/blob/master/LICENSE)
[![Release Version](https://img.shields.io/badge/release-2.0.3-red.svg)](https://github.com/mabeijianxi/small-video-record/releases)

[查看中文文档](https://github.com/mabeijianxi/small-video-record/blob/master/document/README_CH.md )

Used for capturing audio and video in Android system. The bottom layer uses FFmpeg to compression processing (in small-video-record2, C source code and Java source code are all open).

## The effect is as follows:
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/new_sample.gif)

## Instructions:
* [small-video-record1 ](https://github.com/mabeijianxi/small-video-record/blob/master/document/1.x_en_using_help.md)<br>
* [small-video-record2 ](https://github.com/mabeijianxi/small-video-record/blob/master/document/2.x_en_using_help.md)<br>


## Features:
* Collect when encoding. 
* Use FFmpeg to customize recording the video which has different time, resolution, bit rate, frame rate and transcoding speed.
* Small-video-record2 has been decoupled FFmpeg, you can customize FFmpeg based on your needs.
* Exposure FFmpeg command operation interface, you can customize more features.
* Small-video-record2 support full platform, if your phone’s cpu is 64-bit then the code will be compiled instantaneously!
* You can choose the local video for personalized compression, if your phone’s cpu is 64-bit then the speed will be relatively fast.
* Recording is simple, a few lines of code can complete the integration and a few parameters can complete the recording.


## About issues:
If you meet some troubles when integrating, you can first scan the APK operation to see if there is a problem either. If there is no problem, then you should check whether the steps of integration are missing or you can find the answer inside the issues. If it still not works, then you can add another issue accompanied with a detailed log, and when debugging, try to change the mark “debug” to “true” from the beginning, thus there will be more detailed log.
## Development steps, source code analysis, tool preparation：
[利用FFmpeg玩转Android视频录制与压缩（一）](http://blog.csdn.net/mabeijianxi/article/details/63335722)<br>
[利用FFmpeg玩转Android视频录制与压缩（二）](http://blog.csdn.net/mabeijianxi/article/details/72983362)<br>
[利用FFmpeg玩转Android视频录制与压缩（三）](http://blog.csdn.net/mabeijianxi/article/details/73011313)<br>
[编译Android下可执行命令的FFmpeg](http://blog.csdn.net/mabeijianxi/article/details/72904694)<br>
[编译Android下可用的全平台FFmpeg(包含libx264与libfdk-aac)](http://blog.csdn.net/mabeijianxi/article/details/74544879)<br>
[Android下玩JNI的新老三种姿势](http://blog.csdn.net/mabeijianxi/article/details/68525164)<br>

## About small-video-record 2:

###### Small-video-record2 Source Code Compilation:
You need to have ndk environment, AndroidStudio version greater than 2.2, AndroidStudio equipped with Cmake plugin.


###### Customized FFmpeg
FFmpeg used in this project is a simplified version, if you feel it’s not enough or want to customize, then you can enter [https://github.com/mabeijianxi/FFmpeg4Android](https://github.com/mabeijianxi/FFmpeg4Android), download a enhanced version of FFmpeg SO Library compiled by me or run the script to re-customization according to the inside document.

###### 2.0.0 Official Version
ts record on the 64-bit mobile phone basically no delay and the local compression speed is 2 times than before. Because of this great improvement, a new full-screen recording and pause recording function can be added.


## Configuration Instructions:

#### Partial Method Description

| 	name    | parameter type | instruction |
|:-------------: |:---------------:| :-------------:|
|initialize|boolean/String| The first one means whether the log input or not. The log has two parts, one is printed directly to the console, you can clearly see the video recording process, and the other one is an output log according to the FFmpeg order, through this log you can quickly locate the implementation of the error. The second parameter is the save location of the log, and if there is no incoming, the record will be saved to your video cache root directory. |
|   fullScreen   | boolean | Set whether or not full screen recording is required.   |
| smallVideoWidth     |    int     |    Video width, corresponding to the long side of the phone, input invalid in full-screen recording (the width and height is reversed in 1.x version) .      |
| smallVideoHeight | int        |  Video height, corresponding to the short side of the phone. You need to enter the support size of the camera, chaos invalid (the width and height is reversed in 1.x version) |
|recordTimeMax|int| Unit per millisecond, maximum recording time |
|recordTimeMin|int| Unit per millisecond, minimum recording time |
|maxFrameRate|int| The maximum frame rate, to some extent affects the video quality and size. Don’t set too high, for the transcoding speed of some low allocation phones may not keep up |
|videoBitrate|int| Bit rate, to some extent affects the video quality and size. In theory, the number is bigger then the quality will be better |
|captureThumbnailsTime|int|Start time of capturing thumbnails|
|doH264Compress|BaseMediaBitrateConfig|Set compression mode, support AutoVBRMode、CBRMode、VBRMode|
|setScale|float|Set video scale, if there is no requirement of the video size, you needn’t input. If larger than one, you should perform scaling operation|
#### AutoVBRMode
In this mode, you can easily control the video quality according to the grades.

| name | type | instructions |
|:----:|:-----:|:-------:|
|AutoVBRMode|int|This is a constructing method, to receive an “int” compression level, 0 \~ 51, the number is larger, then the picture is vaguer, and the video is much smaller. 18\~28 is recommended|
|setVelocity|String| Set transcoding speed, you can choose ultrafast、superfast、veryfast、faster、fast、medium、slow、slower、veryslow、placebo |
#### VBRMode
| name | type | instructions |
|:----:|:-----:|:-------:|
|VBRMode|int/int|This is a constructing method, to receive a maximum code rate and a nominal code rate. When encoding, based on the nominal code rate and try not to exceed the maximum code rate|
|setVelocity|String| Set transcoding speed, you can choose ultrafast、superfast、veryfast、faster、fast、medium、slow、slower、veryslow、placebo |
#### CBRMode
In this mode, you can more accurately control the video quality and size

| name | type | instructions |
|:----:|:-----:|:-------:|
|CBRMode|int/int|This is a constructing method, to receive a size of buffer value and a fixed code rate. When encoding, the fixed code rate as the standard|
|setVelocity|String|Set transcoding speed, you can choose ultrafast、superfast、veryfast、faster、fast、medium、slow、slower、veryslow、placebo |

## Small-video-record2 Update Log:
	
	2017-07-20：
		Submit 2.0.3.
		Fix a series of low-version phone compatibility issues, including SO Library loading order, temporary memory recovery, end memory recovery, etc.

	2017-07-17:
		Submit 2.0.2
		Repair part of the low-end machine audio acceleration issues.
		Added support for multi-video synthesis commands.
		
	2017-07-06:
		Submit 2.0.1 stable version; solve the compatibility problems of some phones which don’t support the mathematical function library.

		
	2017-07-05:
		Submit 2.0.0, fix many bugs.
		Add full platform compilation.
		Optimize recording and compression speed. For 64-bit CPU mobile phone, you can immediately record transcoding, and the local compression speed increased nearly 2 times.
		Add full screen recording function.
		Add pause recording function.
	
	2017-06-14:
		Submit 2.0.0-beta3; add new resolution zoom function in local compression.
	
	2017-06-13:
		Submit 2.0.0-beta2; change the default compression speed to the fastest, start multi-threaded encoding.
	
	2017-06-10:
		Modify compilation scripts to increase portability.

	
## Small-video-record Update Log:

	2017-06-14:
		Submit 1.2.2; add new resolution zoom function in local compression.
	
	2017-06-13:
		Submit 1.2.1; change the default compression speed to the fastest.
	
	2017-04-06:
		Submit 1.2.0; add the choice to select the local video compression; modify a series of bugs.
	
	2017-03-16:
		Submit 1.1.0; add more accurately control of code rate, transcoding speed, compression level and other configurable parameters; fix some bugs.	

	2017-03-14：
		Submit 1.0.9; add configurable code rate mode (VBR, CBR) and their sizes.
	
	2016-12-14：
		Submit 1.0.8; solve the problem of some phones which do not support the input frame rate; completely repair the deformation problem when browsing pictures.

	2016-10-26:
		Submit 1.0.7; enhance compatibility, to prevent collapse in case that recording size doesn’t support.

	2016-10-14:
		Submitted 1.0.6; repair the problem that abnormal information appeared under unsupported sizes. 

	2016-10-13:
		Submit a small video 1.0.5; repair the deformation problems of some mobile phones when recording.
	
	2016-10-12：
		Repair “sample” parameter and small bug.
	
	2016-08-26：
		Submit small video 1.0.2; add “Buidler” configuration, and customize more content.
	
	2016-08-26：
		Submit small video 1.0.1; update the configuration file.
	
	2016-08-25：
		Submit small video 1.0.0
## Sample download：
###### small-video-record2:
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/sample2.png)
[Download Demo2](https://fir.im/jianxiMediaRecord2)
###### small-video-record:
![sample](https://github.com/mabeijianxi/small-video-record/blob/master/image/sample.png)
[Download Demo1](http://fir.im/smallvideorecord)

## License

small-video-record is licensed under the Apache License 2.0. See the [LICENSE](https://github.com/mabeijianxi/small-video-record/blob/master/LICENSE) file.
