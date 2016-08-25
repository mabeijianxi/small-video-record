package mabeijianxi.camera.model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

import mabeijianxi.camera.util.FileUtils;
import mabeijianxi.camera.util.StringUtils;

@SuppressWarnings("serial")
public class MediaObject implements Serializable {

	/** 拍摄 */
	public final static int MEDIA_PART_TYPE_RECORD = 0;
	/** 导入视频 */
	public final static int MEDIA_PART_TYPE_IMPORT_VIDEO = 1;
	/** 导入图片 */
	public final static int MEDIA_PART_TYPE_IMPORT_IMAGE = 2;
	/** 使用系统拍摄mp4 */
	public final static int MEDIA_PART_TYPE_RECORD_MP4 = 3;
	/** 默认最大时长 */
	public final static int DEFAULT_MAX_DURATION = 10 * 1000;
	/** 默认码率 */
	public final static int DEFAULT_VIDEO_BITRATE = 800;

	/** 视频最大时长，默认10秒 */
	private int mMaxDuration;
	/** 视频目录 */
	private String mOutputDirectory;
	/** 对象文件 */
	private String mOutputObjectPath;
	/** 视频码率 */
	private int mVideoBitrate;
	/** 最终视频输出路径 */
	private String mOutputVideoPath;
	/** 最终视频截图输出路径 */
	private String mOutputVideoThumbPath;
	/** 文件夹、文件名 */
	private String mKey;
	/** 当前分块 */
	private volatile transient MediaPart mCurrentPart;
	/** 获取所有分块 */
	private LinkedList<MediaPart> mMediaList = new LinkedList<MediaPart>();
	/** 主题 */
	public MediaThemeObject mThemeObject;

	public MediaObject(String key, String path) {
		this(key, path, DEFAULT_VIDEO_BITRATE);
	}

	public MediaObject(String key, String path, int videoBitrate) {
		this.mKey = key;
		this.mOutputDirectory = path;
		this.mVideoBitrate = videoBitrate;
		this.mOutputObjectPath = mOutputDirectory + File.separator + mKey + ".obj";
		this.mOutputVideoPath = mOutputDirectory + ".mp4";
		this.mOutputVideoThumbPath = mOutputDirectory + File.separator + mKey+".jpg";
		this.mMaxDuration = DEFAULT_MAX_DURATION;
	}

	/** 获取视频码率 */
	public int getVideoBitrate() {
		return mVideoBitrate;
	}

	/** 获取视频最大长度 */
	public int getMaxDuration() {
		return mMaxDuration;
	}

	/** 设置最大时长，必须大于1秒 */
	public void setMaxDuration(int duration) {
		if (duration >= 1000) {
			mMaxDuration = duration;
		}
	}

	/** 获取视频临时文件夹 */
	public String getOutputDirectory() {
		return mOutputDirectory;
	}

	/** 获取视频临时输出播放 */
	public String getOutputTempVideoPath() {
		return mOutputDirectory + File.separator + mKey + "_temp.mp4";
	}
	public String getOutputTempTranscodingVideoPath() {
		return mOutputDirectory +
				File.separator + mKey + ".mp4";
	}

	/** 清空主题 */
	public void cleanTheme() {
		mThemeObject = null;
		if (mMediaList != null) {
			for (MediaPart part : mMediaList) {
				part.cutStartTime = 0;
				part.cutEndTime = part.duration;
			}
		}
	}

	/** 获取视频信息春促路径 */
	public String getObjectFilePath() {
		if (StringUtils.isEmpty(mOutputObjectPath)) {
			File f = new File(mOutputVideoPath);
			String obj = mOutputDirectory + File.separator + f.getName() + ".obj";
			mOutputObjectPath = obj;
		}
		return mOutputObjectPath;
	}

	/** 获取视频最终输出地址 */
	public String getOutputVideoPath() {
		return mOutputVideoPath;
	}

	/** 获取视频截图最终输出地址 */
	public String getOutputVideoThumbPath() {
		return mOutputVideoThumbPath;
	}

	/** 获取录制的总时长 */
	public int getDuration() {
		int duration = 0;
		if (mMediaList != null) {
			for (MediaPart part : mMediaList) {
				duration += part.getDuration();
			}
		}
		return duration;
	}

	/** 获取剪切后的总时长 */
	public int getCutDuration() {
		int duration = 0;
		if (mMediaList != null) {
			for (MediaPart part : mMediaList) {
				int cut = (part.cutEndTime - part.cutStartTime);
				if (part.speed != 10) {
					cut = (int) (cut * (10F / part.speed));
				}
				duration += cut;
			}
		}
		return duration;
	}

	/** 删除分块 */
	public void removePart(MediaPart part, boolean deleteFile) {
		if (mMediaList != null)
			mMediaList.remove(part);

		if (part != null) {
			part.stop();
			// 删除文件
			if (deleteFile) {
				part.delete();
			}
			mMediaList.remove(part);
			if(mCurrentPart!=null&&part.equals(mCurrentPart)){
				mCurrentPart=null;
			}
		}
	}

		/** 
		 * 生成分块信息，主要用于拍摄
		 * 
		 * @param cameraId 记录摄像头是前置还是后置
		 * @return
		 */
		public MediaPart buildMediaPart(int cameraId) {
			mCurrentPart = new MediaPart();
			mCurrentPart.position = getDuration();
			mCurrentPart.index = mMediaList.size();
			mCurrentPart.mediaPath = mOutputDirectory + File.separator + mCurrentPart.index + ".v";
			mCurrentPart.audioPath = mOutputDirectory + File.separator + mCurrentPart.index + ".a";
			mCurrentPart.thumbPath = mOutputDirectory + File.separator + mCurrentPart.index + ".jpg";
			mCurrentPart.cameraId = cameraId;
			mCurrentPart.prepare();
			mCurrentPart.recording = true;
			mCurrentPart.startTime = System.currentTimeMillis();
			mCurrentPart.type = MEDIA_PART_TYPE_IMPORT_VIDEO;
			mMediaList.add(mCurrentPart);
			return mCurrentPart;
		}

	public MediaPart buildMediaPart(int cameraId, String videoSuffix) {
		mCurrentPart = new MediaPart();
		mCurrentPart.position = getDuration();
		mCurrentPart.index = mMediaList.size();
		mCurrentPart.mediaPath = mOutputDirectory + File.separator + mCurrentPart.index + videoSuffix;
		mCurrentPart.audioPath = mOutputDirectory + File.separator + mCurrentPart.index + ".a";
		mCurrentPart.thumbPath = mOutputDirectory + File.separator + mCurrentPart.index + ".jpg";
		mCurrentPart.recording = true;
		mCurrentPart.cameraId = cameraId;
		mCurrentPart.startTime = System.currentTimeMillis();
		mCurrentPart.type = MEDIA_PART_TYPE_IMPORT_VIDEO;
		mMediaList.add(mCurrentPart);
		return mCurrentPart;
	}

	/** 
	 * 生成分块信息，主要用于视频导入
	 * 
	 * @param path
	 * @param duration
	 * @param type
	 * @return
	 */
	public MediaPart buildMediaPart(String path, int duration, int type) {
		mCurrentPart = new MediaPart();
		mCurrentPart.position = getDuration();
		mCurrentPart.index = mMediaList.size();
		mCurrentPart.mediaPath = mOutputDirectory + File.separator + mCurrentPart.index + ".v";
		mCurrentPart.audioPath = mOutputDirectory + File.separator + mCurrentPart.index + ".a";
		mCurrentPart.thumbPath = mOutputDirectory + File.separator + mCurrentPart.index + ".jpg";
		mCurrentPart.duration = duration;
		mCurrentPart.startTime = 0;
		mCurrentPart.endTime = duration;
		mCurrentPart.cutStartTime = 0;
		mCurrentPart.cutEndTime = duration;
		mCurrentPart.tempPath = path;
		mCurrentPart.type = type;
		mMediaList.add(mCurrentPart);
		return mCurrentPart;
	}

	public String getConcatYUV() {
		StringBuilder yuv = new StringBuilder();
		if (mMediaList != null && mMediaList.size() > 0) {
			if (mMediaList.size() == 1) {
				if (StringUtils.isEmpty(mMediaList.get(0).tempMediaPath))
					yuv.append(mMediaList.get(0).mediaPath);
				else
					yuv.append(mMediaList.get(0).tempMediaPath);
			} else {
				yuv.append("concat:");
				for (int i = 0, j = mMediaList.size(); i < j; i++) {
					MediaPart part = mMediaList.get(i);
					if (StringUtils.isEmpty(part.tempMediaPath))
						yuv.append(part.mediaPath);
					else
						yuv.append(part.tempMediaPath);
					if (i + 1 < j) {
						yuv.append("|");
					}
				}
			}
		}
		return yuv.toString();
	}

	public String getConcatPCM() {
		StringBuilder yuv = new StringBuilder();
		if (mMediaList != null && mMediaList.size() > 0) {
			if (mMediaList.size() == 1) {
				if (StringUtils.isEmpty(mMediaList.get(0).tempAudioPath))
					yuv.append(mMediaList.get(0).audioPath);
				else
					yuv.append(mMediaList.get(0).tempAudioPath);
			} else {
				yuv.append("concat:");
				for (int i = 0, j = mMediaList.size(); i < j; i++) {
					MediaPart part = mMediaList.get(i);
					if (StringUtils.isEmpty(part.tempAudioPath))
						yuv.append(part.audioPath);
					else
						yuv.append(part.tempAudioPath);
					if (i + 1 < j) {
						yuv.append("|");
					}
				}
			}
		}
		return yuv.toString();
	}

	/** 获取当前分块 */
	public MediaPart getCurrentPart() {
		if (mCurrentPart != null)
			return mCurrentPart;
		if (mMediaList != null && mMediaList.size() > 0)
			mCurrentPart = mMediaList.get(mMediaList.size() - 1);
		return mCurrentPart;
	}

	public int getCurrentIndex() {
		MediaPart part = getCurrentPart();
		if (part != null)
			return part.index;
		return 0;
	}

	public MediaPart getPart(int index) {
		if (mCurrentPart != null && index < mMediaList.size())
			return mMediaList.get(index);
		return null;
	}

	/** 取消拍摄 */
	public void delete() {
		if (mMediaList != null) {
			for (MediaPart part : mMediaList) {
				part.stop();
			}
		}
		FileUtils.deleteDir(mOutputDirectory);
	}

	public LinkedList<MediaPart> getMedaParts() {
		return mMediaList;
	}

	/** 预处理数据对象 */
	public static void preparedMediaObject(MediaObject mMediaObject) {
		if (mMediaObject != null && mMediaObject.mMediaList != null) {
			int duration = 0;
			for (MediaPart part : mMediaObject.mMediaList) {
				part.startTime = duration;
				part.endTime = part.startTime + part.duration;
				duration += part.duration;
			}
		}
	}

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();
		if (mMediaList != null) {
			result.append("[" + mMediaList.size() + "]");
			for (MediaPart part : mMediaList) {
				result.append(part.mediaPath + ":" + part.duration + "\n");
			}
		}
		return result.toString();
	}

	public static class MediaPart implements Serializable {

		/** 索引 */
		public int index;
		/** 视频路径 */
		public String mediaPath;
		/** 音频路径 */
		public String audioPath;
		/** 临时视频路径 */
		public String tempMediaPath;
		/** 临时音频路径 */
		public String tempAudioPath;
		/** 截图路径 */
		public String thumbPath;
		/** 存放导入的视频和图片 */
		public String tempPath;
		/** 类型 */
		public int type = MEDIA_PART_TYPE_RECORD;
		/** 剪切视频（开始时间） */
		public int cutStartTime;
		/** 剪切视频（结束时间） */
		public int cutEndTime;
		/** 分段长度 */
		public int duration;
		/** 总时长中的具体位置 */
		public int position;
		/** 0.2倍速-3倍速（取值2~30） */
		public int speed = 10;
		/** 摄像头 */
		public int cameraId;
		/** 视频尺寸 */
		public int yuvWidth;
		/** 视频高度 */
		public int yuvHeight;
		public transient boolean remove;
		public transient long startTime;
		public transient long endTime;
		public transient FileOutputStream mCurrentOutputVideo;
		public transient FileOutputStream mCurrentOutputAudio;
		public transient volatile boolean recording;

		public MediaPart() {

		}

		public void delete() {
			FileUtils.deleteFile(mediaPath);
			FileUtils.deleteFile(audioPath);
			FileUtils.deleteFile(thumbPath);
			FileUtils.deleteFile(tempMediaPath);
			FileUtils.deleteFile(tempAudioPath);
		}

		/** 写入音频数据 */
		public void writeAudioData(byte[] buffer) throws IOException {
			if (mCurrentOutputAudio != null)
				mCurrentOutputAudio.write(buffer);
		}

		/** 写入视频数据 */
		public void writeVideoData(byte[] buffer) throws IOException {
			if (mCurrentOutputVideo != null)
				mCurrentOutputVideo.write(buffer);
		}

		public void prepare() {
			try {
				mCurrentOutputVideo = new FileOutputStream(mediaPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
			prepareAudio();
		}

		public void prepareAudio() {
			try {
				mCurrentOutputAudio = new FileOutputStream(audioPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public int getDuration() {
			return duration > 0 ? duration : (int) (System.currentTimeMillis() - startTime);
		}

		public void stop() {
			if (mCurrentOutputVideo != null) {
				try {
					mCurrentOutputVideo.flush();
					mCurrentOutputVideo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mCurrentOutputVideo = null;
			}

			if (mCurrentOutputAudio != null) {
				try {
					mCurrentOutputAudio.flush();
					mCurrentOutputAudio.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				mCurrentOutputAudio = null;
			}
		}

	}

}
