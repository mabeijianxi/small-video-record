package mabeijianxi.camera;

import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;

import mabeijianxi.camera.model.MediaObject.MediaPart;
import mabeijianxi.camera.util.DeviceUtils;
import mabeijianxi.camera.util.FileUtils;
import mabeijianxi.camera.util.Log;
import mabeijianxi.camera.util.StringUtils;
import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;
import java.io.IOException;

/**
 * ffmpeg工具类
 * 
 */
public class FFMpegUtils {

	/** 音量 100% -vol 100 */
	public static final float AUDIO_VOLUME_HIGH = 1F;
	/** 音量 66% */
	public static final float AUDIO_VOLUME_MEDIUM = 0.66F;
	/** 音量 33% */
	public static final float AUDIO_VOLUME_LOW = 0.33F;
	/** 音量 关闭 */
	public static final int AUDIO_VOLUME_CLOSE = 0;

	/** FFMPEG输出log到logcat */
	private static final String FFMPEG_COMMAND_LOG_LOGCATE = " -d stdout -loglevel verbose";
	/** FFMPEG视频编码参数 */
	private static final String FFMPEG_COMMAND_VCODEC = " -pix_fmt yuv420p -vcodec libx264 -profile:v baseline -preset ultrafast";

	/** 获取log输出的文件路径 */
	public static String getLogCommand() {
		if (VCamera.isLog())
			return FFMPEG_COMMAND_LOG_LOGCATE;
		else
			return " -d \"" + VCamera.getVideoCachePath() + VCamera.FFMPEG_LOG_FILENAME_TEMP + "\" -loglevel verbose";
	}

	/** 获取转码参数 -vcodec libx264 -profile:v baseline -preset ultrafast */
	public static String getVCodecCommand() {
		return FFMPEG_COMMAND_VCODEC;
	}

	/**
	 * 导入视频
	 * 
	 * @param part 分块信息
	 * @param mWindowWidth 窗口宽度
	 * @param videoWidth 视频宽度 
	 * @param videoHeight 视频高度
	 * @param cropX 剪切X坐标
	 * @param cropY 剪切Y坐标
	 * @param startTime 剪切开始时间
	 * @param endTime 剪切介绍时间
	 * @param hasAudio 是否包含音频
	 * @return
	 */
	public static boolean importVideo(MediaPart part, int mWindowWidth, int videoWidth, int videoHeight, int cropX, int cropY, boolean hasAudio) {
		if (part != null && !StringUtils.isEmpty(part.tempPath)) {
			File f = new File(part.tempPath);
			if (f != null && f.exists() && !f.isDirectory()) {
				StringBuffer buffer = new StringBuffer("ffmpeg");

				//LOG输出
				buffer.append(FFMpegUtils.getLogCommand());

				// 添加视频
				buffer.append(" -i \"");
				buffer.append(part.tempPath);
				buffer.append("\"");

				//校验视频是否旋转
				int rotation = -1;
				int width = videoWidth, height = videoHeight, cX = cropX, cY = cropY;

				float videoAspectRatio = videoWidth * 1.0F / videoHeight;

				//读取旋转信息
				if (DeviceUtils.hasJellyBeanMr1()) {
					MediaMetadataRetriever metadata = new MediaMetadataRetriever();
					metadata.setDataSource(part.tempPath);
					try {
						rotation = Integer.parseInt(metadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION));
					} catch (NumberFormatException e) {
						rotation = -1;
					}
				} else {
					rotation = UtilityAdapter.VideoGetMetadataRotate(part.tempPath);
				}

				if (rotation == 90 || rotation == 270) {
					int w = videoWidth;
					width = videoHeight;
					height = w;
				}

				buffer.append(" -vf \"scale=");
				if (width >= height) {
					//横向
					buffer.append("-1:480");
					float scaleWidth = 480 * videoAspectRatio;
					int viewWidth = (int) (mWindowWidth * videoAspectRatio);
					cX = (int) (scaleWidth * (cropX * 1.0F / viewWidth));
				} else {
					//竖向
					buffer.append("480:-1");
					float scaleHeight = 480 / videoAspectRatio;//857
					int viewHeight = (int) (mWindowWidth / videoAspectRatio);//964
					cY = (int) (scaleHeight * (cropY * 1.0F / viewHeight));//
				}

				buffer.append("[tmp];[tmp]");

				boolean hasRotation = true;
				switch (rotation) {
				case 90:
					buffer.append("transpose=1[transpose];[transpose]");
					break;
				case 270:
					buffer.append("transpose=2[transpose];[transpose]");
					break;
				case 180:
					buffer.append("vflip[vflip];[vflip]hflip[transpose];[transpose]");
					break;
				default:
					hasRotation = false;
					break;
				}

				buffer.append(" crop=480:480:");
				buffer.append(cX);
				buffer.append(":");
				buffer.append(cY);

				buffer.append("\"");

				//去除旋转信息
				if (hasRotation) {
					buffer.append(" -metadata:s:v rotate=\"\"");
				}

				// 裁剪时间 -ss是秒 -t也是秒
				buffer.append(" -ss ");
				buffer.append(String.format("%.1f", part.startTime / 1000F));

				buffer.append(" -t ");
				buffer.append(String.format("%.1f", (part.endTime - part.startTime) / 1000F));

				buffer.append(" -an -vcodec rawvideo -f rawvideo -s 480x480 -pix_fmt yuv420p -r 15 \"");
				buffer.append(part.mediaPath);
				buffer.append("\"");

				if (!hasAudio) {
					//生成采样数据 // 采样率44100 * 采样位(unsigned 16bit = 2)* 声道(1) * 时间(1秒)
					final byte[] hz = new byte[44100 * 2 * 1 * 1];
					part.prepareAudio();
					try {
						int duration = (int) (part.endTime - part.startTime);
						int forCount = duration / 1000;
						if (forCount > 0) {
							for (int i = 0; i < forCount; i++) {
								part.mCurrentOutputAudio.write(hz);
							}
						}
						if (duration % 1000 != 0) {
							int lastSize = (int) (44100 * 2 * 1 * (duration - forCount * 1000) / 1000F);
							if (lastSize % 2 != 0)
								lastSize++;
							part.mCurrentOutputAudio.write(new byte[lastSize]);
						}
					} catch (IOException e) {
						Log.e("Yixia", "convertImage2Video", e);
					} catch (Exception e) {
						Log.e("Yixia", "convertImage2Video", e);
					}
					part.stop();
				} else {
					// 裁剪时间 -ss是秒 -t也是秒
					buffer.append(" -ss ");
					buffer.append(String.format("%.1f", part.startTime / 1000F));

					buffer.append(" -t ");
					buffer.append(String.format("%.1f", (part.endTime - part.startTime) / 1000F));

					buffer.append(" -vn -acodec pcm_s16le -f s16le -ar 44100 -ac 1 \"");
					buffer.append(part.audioPath);
					buffer.append("\"");
				}

				boolean result = UtilityAdapter.FFmpegRun("", buffer.toString()) == 0;

				if (!result) {
					//转码失败，写日志
					VCamera.copyFFmpegLog(buffer.toString());
				}
				return result;
			}
		}
		return false;
	}

	/**
	 * 图片转视频（用于图片导入）
	 */
	public static boolean convertImage2Video(MediaPart part) {
		if (part != null && !StringUtils.isEmpty(part.tempPath)) {
			File f = new File(part.tempPath);
			if (f != null && f.exists() && !f.isDirectory()) {
				// float duration = part.duration / 1000F;
				// === 生成1秒的视频 ===

				// 获取图片的宽高和方向
				int width = 0, height = 0, rotation = -1, cropX = 0, cropY = 0;
				try {
					ExifInterface exif = new ExifInterface(part.tempPath);
					width = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
					height = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
					rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

				} catch (IOException e) {
					Log.e("Yixia", "convertImage2Video", e);
				}

				StringBuffer scaleBuffer = new StringBuffer();
				if (width > 0 && height > 0) {
					float videoAspectRatio = width * 1.0F / height;

					scaleBuffer.append(" -vf \"scale=");
					if (width > height) {
						// 横向
						scaleBuffer.append("-1:480");
						float scaleWidth = 480 * videoAspectRatio;
						cropX = (int) ((scaleWidth - 480) / 2);
					} else {
						scaleBuffer.append("480:-1");
						float scaleHeight = 480 / videoAspectRatio;
						cropY = (int) ((scaleHeight - 480) / 2);
					}
					scaleBuffer.append("[tmp];[tmp]");
					// boolean hasRotation = true;
					switch (rotation) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						scaleBuffer.append("transpose=1[transpose];[transpose]");
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						scaleBuffer.append("transpose=2[transpose];[transpose]");
						break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						scaleBuffer.append("vflip[vflip];[vflip]hflip[transpose];[transpose]");
						break;
					}

					scaleBuffer.append(" crop=480:480:");
					scaleBuffer.append(cropX);
					scaleBuffer.append(":");
					scaleBuffer.append(cropY);
					scaleBuffer.append("\"");
				}

				scaleBuffer.append(" -s 480x480");

				// ffmpeg -y -loop 1 -f image2 -i Goddess@2x.png -vcodec
				// rawvideo -r 15 -t 1 -f rawvideo -s 480x480 -pix_fmt nv21
				// Goddess.yuv

				// -s 480x480
				String cmd = String.format("ffmpeg %s -y -loop 1 -f image2 -i \"%s\" -vcodec rawvideo -r 15 -t %.1f -f rawvideo %s -pix_fmt yuv420p \"%s\"", FFMpegUtils.getLogCommand(), part.tempPath, part.duration / 1000F, scaleBuffer.toString(), part.mediaPath);
				if (UtilityAdapter.FFmpegRun("", cmd) == 0) {
					// === 生成1秒的音频 === //44100*2*1*1
					// 采样率44100 * 采样位(unsigned 16bit = 2)* 声道(1) * 时间(1秒)
					final byte[] hz = new byte[44100 * 2 * 1 * (int) (part.duration / 1000F)];
					part.prepareAudio();
					try {
						part.mCurrentOutputAudio.write(hz);
						part.stop();
						return true;
					} catch (IOException e) {
						Log.e("Yixia", "convertImage2Video", e);
					} catch (Exception e) {
						Log.e("Yixia", "convertImage2Video", e);
					}
				} else {
					//转码失败，写日志
					VCamera.copyFFmpegLog(cmd);
				}
				return true;
			}
		}

		return false;
	}
	public static boolean captureThumbnails(String videoPath, String outputPath, String ss) {
		//ffmpeg -i /storage/emulated/0/DCIM/04.04.mp4 -s 84x84 -vframes 1 /storage/emulated/0/DCIM/Camera/miaopai/1388843007381.jpg
		//ffmpeg -i eis-sample.mpg -s 40x40 -r 1/5 -vframes 10 %d.jpg
		FileUtils.deleteFile(outputPath);
		if (ss == null)
			ss = "";
		else
			ss = " -ss " + ss;
		String cmd = String.format("ffmpeg -d stdout -loglevel verbose -i \"%s\"%s  -vframes 1 \"%s\"", videoPath, ss, outputPath);
		return UtilityAdapter.FFmpegRun("", cmd) == 0;
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
		return UtilityAdapter.FFmpegRun("", cmd) == 0;
	}

	//	/**
	//	 * 综合转码
	//	 *
	//	 * @param mMediaObject 视频数据存储对象，涵主题、小部件和视频片段
	//	 * @param targetPath 目标路径
	//	 * @param windowWidth 视频宽度
	//	 * @param complexWatermark 是否合并主题（预览页主题和主题音乐）
	//	 * @param needCrop 是否需要剪切（如果yuv已经是480x480就不需要裁剪了）
	//	 * @return
	//	 */
	//	public static boolean videoTranscoding(MediaObject mMediaObject, String targetPath, int windowWidth, boolean complexWatermark) {
	//		if (mMediaObject == null || StringUtils.isEmpty(targetPath) || windowWidth <= 0)
	//			return false;
	//
	//		StringBuffer buffer = new StringBuffer("ffmpeg");
	//
	//		buffer.append(getLogCommand());
	//
	//		//输入音频
	//		buffer.append(" -f s16le -ar 44100 -ac 1 -i \"");
	//		buffer.append(mMediaObject.getConcatPCM());
	//		buffer.append("\"");
	//
	//		//合并主题音乐
	//		MediaThemeObject theme = mMediaObject.mThemeObject;
	//		if (complexWatermark) {
	//			if (theme != null && StringUtils.isNotEmpty(theme.audio)) {
	//				float volume = theme.volumn;
	//				//主题音乐
	//				buffer.append(" -i \"");
	//				buffer.append(theme.audio);
	//				buffer.append("\"");
	//				buffer.append(" -filter_complex \"");
	//				if (volume >= 0) {
	//					buffer.append("[1:a]volume=");
	//					buffer.append(String.format("%.2f", volume));
	//					buffer.append("[a1];[0:a][a1]");
	//				}
	//				buffer.append("amix=inputs=2:duration=first:dropout_transition=2\"");
	//			}
	//		}
	//
	//		// 添加视频
	//		buffer.append(" -f rawvideo -pix_fmt yuv420p -s 480x480 -r 15 -i \"");
	//		buffer.append(mMediaObject.getConcatYUV());
	//		buffer.append("\"");
	//
	//		//是否动态水印
	//		final int watermarkCount = theme == null ? 0 : theme.watermarkes.size();
	//		ArrayList<String> widgetsInput = new ArrayList<String>();
	//		ArrayList<String> widgetsOverlay = new ArrayList<String>();
	//
	//		float videoAspectRatio = windowWidth * 1.0F / 480;
	//
	//		//添加水印
	//		if (watermarkCount > 0 && complexWatermark && theme.frameCount > 0) {
	//			for (int i = 0; i < watermarkCount; i++) {
	//				widgetsInput.add(String.format(" -i \"%s\"", theme.watermarkes.get(i)));
	//				//				Log.e("FFMpegUtils", "videoTranscoding...frameDuration:" + theme.frameDuration);
	//				if (watermarkCount == 1)
	//					widgetsOverlay.add("overlay=0:0");
	//				else
	//					widgetsOverlay.add(String.format("overlay=x='if(eq(floor(mod(t*%d,%d)),%d), 0, -500)':y=0", 1000 / (theme.frameDuration / watermarkCount), watermarkCount, i));
	//			}
	//		}
	//
	//		//添加小部件
	//		for (MediaPart part : mMediaObject.getMedaParts()) {
	//			if (part != null && part.widgets != null && part.widgets.size() > 0) {
	//				for (int i = 0, j = part.widgets.size(); i < j; i++) {
	//					ThemeWidget widget = part.widgets.get(i);
	//					widgetsInput.add(String.format(" -i \"%s\"", widget.watermark));
	//					float minTime = (part.startTime - part.cutStartTime) / 1000F;
	//					float maxTime = (part.endTime - (part.endTime - part.startTime - part.cutEndTime)) / 1000F;
	//					widgetsOverlay.add(String.format("overlay=x='if(between(t, %.3f, %.3f), %d, -500)':y=%d", minTime, maxTime, (int) (widget.overlayX / videoAspectRatio), (int) (widget.overlayY / videoAspectRatio)));
	//				}
	//			}
	//		}
	//
	//		//讲小部件和主题加入命令行
	//		if (widgetsInput.size() > 0) {
	//			buffer.append(StringUtils.join(widgetsInput, " "));
	//			buffer.append(" -filter_complex \"");
	//			buffer.append(StringUtils.join(widgetsOverlay, ","));
	//			buffer.append("\"");
	//		}
	//
	//		buffer.append(getVCodecCommand());
	//		buffer.append(" -b:v " + mMediaObject.getVideoBitrate() + "k -g 30");//-ar 44100 -ac 2 -b:a 64k
	//		buffer.append(" -acodec libfdk_aac -ar 44100 -ac 1 -b:a 64k");
	//		buffer.append(" -f mp4 -movflags faststart \"");
	//		buffer.append(targetPath);
	//		buffer.append("\"");
	//
	//		boolean result = UtilityAdapter.FFmpegRun("", buffer.toString()) == 0;
	//		if (!result) {
	//			VCamera.copyFFmpegLog(buffer.toString());
	//		}
	//		return result;
	//	}
}
