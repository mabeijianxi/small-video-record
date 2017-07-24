package mabeijianxi.camera;

import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;

import mabeijianxi.camera.model.MediaObject.MediaPart;

/**
 * 自定义录制（Java层保存yuv和pcm数据），未完成
 * 
 */
@Deprecated
public class MediaRecorderCustom extends MediaRecorderBase {

	/** 保存YUV数据 */
	private static final int HANDLE_SAVE_YUV = 1;
	/** 保存PCM数据 */
	private static final int HANDLE_SAVE_PCM = 2;
	/** 停止数据写入 */
	private static final int HANDLE_PART_STOP = 3;

	/** 写yuv和pcm到磁盘的线程 */
	private HandlerThread mHandlerThread;
	/** 写yuv和pcm到磁盘的队列 */
	private YuvHandler mHandler;

	@Override
	public void prepare() {
		if (!mPrepared) {
			mHandlerThread = new HandlerThread("handler_thread", android.os.Process.THREAD_PRIORITY_BACKGROUND);
			mHandlerThread.start();

			mHandler = new YuvHandler(mHandlerThread.getLooper());
			mPrepared = true;
		}

		if (mSurfaceCreated)
			startPreview();
	}

	@Override
	public MediaPart startRecord() {
		MediaPart result = null;

		if (mMediaObject != null) {
			result = mMediaObject.buildMediaPart(mCameraId);
			resetAVSync();

			if (mAudioRecorder == null) {
				mAudioRecorder = new AudioRecorder(this);
				mAudioRecorder.start();
			}

			mRecording = true;
		}

		return result;
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (mRecording && mMediaObject != null && mHandler != null) {
			mHandler.sendMessage(mHandler.obtainMessage(HANDLE_SAVE_YUV, mMediaObject.getCurrentIndex(), appendVideoData(data.length), data));
		}
	}

	@Override
	public void release() {
		super.release();
		// 停止消息循环
		if (mHandlerThread != null) {
			mHandlerThread.quit();
			mHandlerThread = null;
		}
	}

	@Override
	public void startEncoding() {
		//yuv+pcm直接转成mp4
		//TODO 未完成
	}

	/** yuv、pcm存储队列 */
	private class YuvHandler extends Handler {

		public YuvHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			int position = msg.arg1;
			switch (msg.what) {
			case HANDLE_SAVE_YUV:
				int allocframe = msg.arg2;
				byte[] yuv = (byte[]) msg.obj;

				if (mMediaObject != null) {
					MediaPart part = mMediaObject.getPart(position);
					if (part != null && part.mCurrentOutputVideo != null) {
						//640x480转480x480
						try {
							//视频和音频同步，如果不够帧复制前一帧数据填补（可能出现一卡一卡的情况）
							for (int j = 0; j < allocframe; j++) {
								part.writeVideoData(yuv);
							}
						} catch (IOException e) {
							mabeijianxi.camera.util.Log.e("MediaRecorder", "save_yuv", e);
						}

						//检测本块是否已经结束
						if (!part.recording) {
							//延迟1秒检测本块是否结束
							if (hasMessages(HANDLE_PART_STOP)) {
								removeMessages(HANDLE_PART_STOP);
								sendMessageDelayed(obtainMessage(HANDLE_PART_STOP, position, 0), 1000);
							}
						}
					}
				}
				break;
			case HANDLE_SAVE_PCM:
				if (mMediaObject != null) {
					MediaPart part = mMediaObject.getPart(position);
					if (part != null) {
						try {
							part.writeAudioData((byte[]) msg.obj);
						} catch (IOException e) {
							mabeijianxi.camera.util.Log.e("MediaRecorder", "save_yuv", e);
						}
					}
				}
				break;
			case HANDLE_PART_STOP:
				if (mMediaObject != null) {
					MediaPart part = mMediaObject.getPart(position);
					if (part != null) {
						part.stop();
					}
				}
				break;
			}
			super.handleMessage(msg);
		}
	}

	/* ----------------------- 同步音视频 ----------------------- */

	private int g_audioframe = 0;
	private int g_videoframe = 0;

	/** 重置视音频同步 */
	public void resetAVSync() {
		g_audioframe = 0;
		g_videoframe = 0;
	}

	/** 根据音频同步视频 */
	@Override
	public void receiveAudioData(byte[] sampleBuffer, int len) {
		if (mRecording && mMediaObject != null) {
			g_audioframe += len;
			mHandler.sendMessage(mHandler.obtainMessage(HANDLE_SAVE_PCM, mMediaObject.getCurrentIndex(), 0, sampleBuffer));
		}
	}

	/** 根据音频同步视频 */
	public int appendVideoData(int len) {
		int allocframe = g_audioframe * 17 / 100000;
		if (g_videoframe > allocframe)
			return 0;
		allocframe -= g_videoframe;
		g_videoframe += allocframe;
		return allocframe;
	}
}
