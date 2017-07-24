package com.mabeijianxi.smallvideorecord2;


import com.mabeijianxi.smallvideorecord2.jniinterface.FFmpegBridge;

/**
 * ffmpeg工具类
 * 
 */
public class FFMpegUtils {


	public static boolean captureThumbnails(String videoPath, String outputPath, String ss) {
		//ffmpeg -i /storage/emulated/0/DCIM/04.04.mp4 -s 84x84 -vframes 1 /storage/emulated/0/DCIM/Camera/miaopai/1388843007381.jpg
		//ffmpeg -i eis-sample.mpg -s 40x40 -r 1/5 -vframes 10 %d.jpg
//		FileUtils.deleteFile(outputPath);
//		String cmd = String.format("ffmpeg  -i  %s  %s  -vframes 1  %s ", "/storage/emulated/0/DCIM/mabeijianxi/1496549287250/1496549287250.mp4", ss, "/storage/emulated/0/DCIM/mabeijianxi/1496549287250/1496549287250.jpg");
//		FFmpegBridge.jxFFmpegCMDRun(cmd);
		  return FFmpegBridge.jxFFmpegCMDRun(getCaptureThumbnailsCMD(videoPath,outputPath,ss))==0;
	}
public static String getCaptureThumbnailsCMD(String videoPath, String outputPath, String ss){
	if (ss == null)
		ss = "";
	else
		ss = " -ss " + ss;
	return   String.format("ffmpeg  -i  %s  %s  -vframes 1  %s ", videoPath, ss, outputPath);
}
	/**
	 * 视频截图
	 * 
	 * @param videoPath 视频路径
	 * @param outputPath 截图输出路径
	 * @param wh 截图画面尺寸，例如84x84
	 * @param ss 截图起始时间
	 * @return
	 */
	public static boolean captureThumbnails(String videoPath, String outputPath, String wh, String ss) {
		//ffmpeg -i /storage/emulated/0/DCIM/04.04.mp4 -s 84x84 -vframes 1 /storage/emulated/0/DCIM/Camera/miaopai/1388843007381.jpg
		//ffmpeg -i eis-sample.mpg -s 40x40 -r 1/5 -vframes 10 %d.jpg
		FileUtils.deleteFile(outputPath);
		if (ss == null)
			ss = "";
		else
			ss = " -ss " + ss;
		String cmd = String.format("ffmpeg -d stdout -loglevel verbose -i \"%s\"%s -s %s -vframes 1 \"%s\"", videoPath, ss, wh, outputPath);
		return   FFmpegBridge.jxFFmpegCMDRun(cmd)==0 ;
	}


	/**
		视频剪切
		@Param videoPath 视频路径
		@Param outputPath 截图输出路径
		@Param ss 剪切起始时间，例如00:00:28
		@Param t 剪切结束时间，例如00:00:58
		@return
		*/
	public static boolean sectionVideo(String videoPath, String outputPath, String ss, String t) {
		//ffmpeg -ss 00:00:28 -i "int.mp4f" -acodec copy -vcodec copy -t 00:00:58 output.mp4
		FileUtils.deleteFile(outputPath);
		if (ss == null || ss == "")
			ss = "";
		else
			ss = " -ss " + ss;
		String cmd = String.format("ffmpeg -d stdout -loglevel verbose -i \"%s\" %s -t %s -acodec copy -vcodec copy \"%s\"", videoPath, ss, t, outputPath);
		return FFmpegBridge.jxFFmpegCMDRun(cmd)==0 ;
	}

}
