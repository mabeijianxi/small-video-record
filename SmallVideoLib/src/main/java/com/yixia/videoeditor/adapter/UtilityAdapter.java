package com.yixia.videoeditor.adapter;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

public class UtilityAdapter {
	static {
		System.loadLibrary("utility");
	}

	/** 初始化底层库 */
	public static native void FFmpegInit(Object context, String settings);

	/** 获取当前转码时间 */
	public static native int FFmpegVideoGetTransTime(int flag);

	/** 开始播放器数据的录制 */
	public static native boolean VitamioStartRecord(String yuv, String pcm);

	/** 停止播放器数据的录制 */
	public static native int VitamioStopRecord(int flag);

	/** 获取视频回调指针 */
	public static native int GetVitamioVideoCallbackPointer(int flag);

	/** 获取音频回调指针 */
	public static native int GetVitamioAudioCallbackPointer(int flag);

	/** 获取视频旋转信息 */
	public static native int VideoGetMetadataRotate(String filename);

	/**
	 * 执行ffmpeg命令 tag 任务的唯一标识，如果标识为""，以阻塞方式运行，否则以异步方式运行 FFmpegRun("",
	 * "ffmpeg -i \"生成的mp4\" -y -f image2 -ss 1 -t 0.001 -s 480x480 \"输出.jpg\" "
	 * )
	 * 
	 * @param strtag
	 *            任务的唯一标识，如果标识为""，以阻塞方式运行，否则以异步方式运行
	 * @param strcmd
	 *            命令行
	 * @return 返回执行结果
	 */
	public static native int FFmpegRun(String tag, String cmd);

	/** 结束异步执行的ffmpeg */
	public static native void FFmpegKill(String tag);

	/** 检测ffmpeg实例是否正在运行 */
	public static native boolean FFmpegIsRunning(String tag);

	/** 获取视频信息，相当于调用ffprobe */
	public static native String FFmpegVideoGetInfo(String filename);

	/**
	 * 传入参数width，height为surfaceview创建时给出的，后面的outwidth，outheight为输出的视频高宽，初始化时传入0，0，返回纹理id
	 * @param width surfaceview创建时给出的
	 * @param height surfaceview创建时给出的
	 * @return
	 */
	public static native int RenderViewInit(int width, int height);

	/** 设置摄像头预览数据尺寸，摄像头旋转、翻转设置 */
	public static final int FLIPTYPE_NORMAL = 0x0;
	/** 设置摄像头预览数据尺寸，摄像头旋转、翻转设置 */
	public static final int FLIPTYPE_HORIZONTAL = 0x1;
	/** 设置摄像头预览数据尺寸，摄像头旋转、翻转设置 */
	public static final int FLIPTYPE_VERTICAL = 0x2;

	/**
	 * 设置输入参数
	 * 
	 * @param inw 视频输入宽
	 * @param inh 视频输入高
	 * @param org 后置摄像头0，前置摄像头180
	 * @param flip 后置摄像头FLIPTYPE_NORMAL，前摄像头FLIPTYPE_HORIZONTAL
	 */
	public static native void RenderInputSettings(int inw, int inh, int org, int flip);

	//设置输出视频流尺寸，采样率
	public static final int OUTPUTFORMAT_YUV = 0x1;
	public static final int OUTPUTFORMAT_RGBA = 0x2;
	public static final int OUTPUTFORMAT_MASK_ZIP = 0x4;
	public static final int OUTPUTFORMAT_MASK_NEED_LASTSNAP = 0x8;
	public static final int OUTPUTFORMAT_MASK_HARDWARE_ACC = 0x10;
	public static final int OUTPUTFORMAT_MASK_MP4 = 0x20;

	/**
	 * 设置视频输出参数
	 * 
	 * @param outw 视频输出宽
	 * @param outh 视频输出高
	 * @param outfps  视频输出帧率
	 * @param format  视频输出格式，参考OUTPUTFORMAT_*
	 */
	public static native void RenderOutputSettings(int outw, int outh, int outfps, int format);

	//设置特效
	public static final int FILTERTYPE_FILTER = 0;
	public static final int FILTERTYPE_FRAME = 1;

	public static native void RenderSetFilter(int type, String filter);

	//进行显示
	public static native void RenderStep();

	//提供摄像头数据
	public static native void RenderDataYuv(byte[] yuv);

	/** 提供录音数据，必须是44100Hz，1channel，16bit unsigned */
	public static native void RenderDataPcm(byte[] pcm);

	/** 获取最后一帧数据，如果失败会返回一副全透明的图，如果内存失败，会返回空，alpha的值为0-1，0为全透明 */
	public static native int[] RenderGetDataArgb(float alpha);

	/** 设置输出数据文件，设置完就开始录制 */
	public static native boolean RenderOpenOutputFile(String video, String audio);

	/** 关闭输出数据文件，关闭后就停止录制 */
	public static native void RenderCloseOutputFile();

	/** 关闭输出数据文件，关闭后就停止录制 */
	public static native boolean RenderIsOutputJobFinish();

	/** 暂停录制 */
	public static native void RenderPause(boolean pause);

	/** 暂停特效 */
	public static void FilterParserPause(boolean pause) {
		if (mAudioTrack != null) {
			if (pause) {
				mAudioTrack.pause();
			} else {
				mAudioTrack.play();
			}
		}
		RenderPause(pause);
	}

	/**
	 * 特效处理器
	 * 
	 * @param settings 特效设置: inv=/sdcard/v.rgb; ina=/sdcard/p.pcm; out=/sdcard/o.mp4; text=/sdcard/txt.png
	 * @param surface Surface
	 * @param holder SurfaceHolder
	 */
	public static native boolean FilterParserInit(String strings, Object surface);

	//查询目前特效处理信息
	public static final int FILTERINFO_PROCESSEDFRAME = 0; ///<从开始累计已处理的帧数
	public static final int FILTERINFO_CACHEDFRAME = 1; ///<目前可用的帧数
	public static final int FILTERINFO_STARTPLAY = 2; ///<开始播放
	public static final int FILTERINFO_PAUSEPLAY = 3; ///<暂停播放
	public static final int FILTERINFO_PROGRESS = 4; ///<当前处理进度
	public static final int FILTERINFO_TOTALMS = 5; ///<经特效处理后，文件的时长，单位毫秒
	public static final int FILTERINFO_CAUSEGC = 6; ///<清理渲染使用的缓存

	public static native int FilterParserInfo(int mode);

	/** 停止特效处理 */
	public static native void FilterParserFree();

	//特效组处理
	public static final int PARSERACTION_INIT = 0; ///<设置全局的属性，在一开始进入预览界面时调用
	public static final int PARSERACTION_UPDATE = 1; ///<设置摄像头相关属性，在摄像头打开时调用
	public static final int PARSERACTION_START = 2; ///<设置开始捕捉，并指定保存的文件
	public static final int PARSERACTION_STOP = 3; ///<设置停止捕捉
	public static final int PARSERACTION_FREE = 4; ///<释放占用，这时没完成的进度也会被取消
	public static final int PARSERACTION_PROGRESS = 5; ///<查询处理的进度

	/**
	 * 特效处理
	 * 
	 * @param settings
	 * @param actiontype
	 * @return
	 */
	public static native int FilterParserAction(String settings, int actiontype);

	public static native boolean SaveData(String filename, int[] data, int flag);

	private static volatile boolean gInitialized;

	public static boolean	isInitialized(){
		return gInitialized;
	}
	
	/** 初始化 */
	public static void initFilterParser() {
		if (!gInitialized) {
			gInitialized = true;
			FilterParserAction("", PARSERACTION_INIT);
		}
	}

	public static void freeFilterParser() {
		gInitialized = false;
		FilterParserAction("", PARSERACTION_FREE);
	}

	/**
	 * 变声
	 * 
	 * @param inPath wav音频输入
	 * @param outPath wav音频输出
	 * @param tempoChange 变速(语速增加%xx)
	 * @param pitch  // 音幅变调
	 * @param pitchSemitone //音程变调
	 */
	public static native int SoundEffect(String inPath, String outPath, float tempoChange, float pitch, int pitchSemitone);

	protected static AudioTrack mAudioTrack;

	/** 底层音频初始化 */
	@SuppressWarnings("deprecation")
  public static boolean ndkAudioInit() {
		int desiredFrames = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT);// * 8;
		//desiredFrames = 101376
		if (mAudioTrack == null) {
			mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, desiredFrames, AudioTrack.MODE_STREAM);

			// Instantiating AudioTrack can "succeed" without an exception and the track may still be invalid
			// Ref: https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/AudioTrack.java
			// Ref: http://developer.android.com/reference/android/media/AudioTrack.html#getState()

			if (mAudioTrack.getState() != AudioTrack.STATE_INITIALIZED) {
				mAudioTrack = null;
				Log.w("ndkAudio", "Init failed!");
				return false;
			}
			mAudioTrack.play();
		}
		return true;
	}

	/** 底层音频输出 */
	public static void ndkAudioWrite(short[] buffer, int cnt) {
		int limitcount = 100;
		int result;
		for (int i = 0; i < cnt;) {
			limitcount--;
			if (limitcount <= 0) {
				Log.e("ndkAudio", "avoid dead loop");
				break;
			}
			result = mAudioTrack.write(buffer, i, cnt - i);
			if (result > 0) {
				i += result;
			} else if (result == 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// Nom nom
				}
			} else {
				Log.w("ndkAudio", "write failed!");
				return;
			}
		}
	}

	public static void ndkAudioQuit() {
		if (mAudioTrack != null) {
			mAudioTrack.stop();
			mAudioTrack.release();
			mAudioTrack = null;
		}
	}

	//key
	public static final int NOTIFYKEY_PLAYSTATE = 1;
	/** 播放发生缓冲时报告 */
	public static final int NOTIFYVALUE_BUFFEREMPTY = 0;
	/** 恢复播放时报告 */
	public static final int NOTIFYVALUE_BUFFERFULL = 1;
	/** 播放完成时报告 */
	public static final int NOTIFYVALUE_PLAYFINISH = 2;
	/** 无法播放时报告 */
	public static final int NOTIFYVALUE_HAVEERROR = 3;

	/** 底层回调 */
	public static int ndkNotify(int key, int value) {
		if (mOnNativeListener != null) {
			mOnNativeListener.ndkNotify(key, value);
		} else {
			Log.e("ndkNotify", "ndkNotify key:" + key + ", value: " + value);
		}
		return 0;
	}

	/** 注册监听回调 */
	public static void registerNativeListener(OnNativeListener l) {
		mOnNativeListener = l;
	}

	private static OnNativeListener mOnNativeListener;

	/** 底层通知 */
	public static interface OnNativeListener {
		public void ndkNotify(int key, int value);
	}
}
