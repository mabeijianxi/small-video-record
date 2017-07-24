package mabeijianxi.camera.views;

import android.content.Context;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.IOException;

import mabeijianxi.camera.util.StringUtils;


public class SurfaceVideoView extends SurfaceView implements Callback {

	/** 定时暂停 */
	private static final int HANDLER_MESSAGE_PARSE = 0;
	/** 定时循环 */
	private static final int HANDLER_MESSAGE_LOOP = 1;

	private MediaPlayer.OnCompletionListener mOnCompletionListener;
	private MediaPlayer.OnPreparedListener mOnPreparedListener;
	private MediaPlayer.OnErrorListener mOnErrorListener;
	private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;
	private OnInfoListener mOnInfoListener;
	private OnVideoSizeChangedListener mOnVideoSizeChangedListener;
	private OnPlayStateListener mOnPlayStateListener;
	private MediaPlayer mMediaPlayer = null;
	private SurfaceHolder mSurfaceHolder = null;

	private static final int STATE_ERROR = -1;
	private static final int STATE_IDLE = 0;
	private static final int STATE_PREPARING = 1;
	private static final int STATE_PREPARED = 2;
	private static final int STATE_PLAYING = 3;
	private static final int STATE_PAUSED = 4;
	/**
	 * PlaybackCompleted状态：文件正常播放完毕，而又没有设置循环播放的话就进入该状态，
	 * 并会触发OnCompletionListener的onCompletion
	 * ()方法。此时可以调用start()方法重新从头播放文件，也可以stop()停止MediaPlayer，或者也可以seekTo()来重新定位播放位置。
	 */
	private static final int STATE_PLAYBACK_COMPLETED = 5;
	/** Released/End状态：通过release()方法可以进入End状态 */
	private static final int STATE_RELEASED = 5;

	private int mCurrentState = STATE_IDLE;
	private int mTargetState = STATE_IDLE;

	private int mVideoWidth;
	private int mVideoHeight;
	//	private int mSurfaceWidth;
	//	private int mSurfaceHeight;

	//	private float mSystemVolumn = -1;
	private int mDuration;
	private Uri mUri;

	//	SurfaceTextureAvailable

	public SurfaceVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initVideoView();
	}

	public SurfaceVideoView(Context context) {
		super(context);
		initVideoView();
	}

	public SurfaceVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initVideoView();
	}

	@SuppressWarnings("deprecation")
	protected void initVideoView() {
		//		mTryCount = 0;
		mVideoWidth = 0;
		mVideoHeight = 0;

		getHolder().setFormat(PixelFormat.RGBA_8888); // PixelFormat.RGB_565
		getHolder().addCallback(this);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();

		mCurrentState = STATE_IDLE;
		mTargetState = STATE_IDLE;
	}

	/** 更新音量 */
	public static float getSystemVolumn(Context context) {
		if (context != null) {
			try {
				AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				int maxVolumn = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * 1.0F / maxVolumn;
			} catch (UnsupportedOperationException e) {

			}
		}
		return 0.5F;
	}

	public void setOnInfoListener(OnInfoListener l) {
		mOnInfoListener = l;
	}

	public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener l) {
		mOnVideoSizeChangedListener = l;
	}

	public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
		mOnPreparedListener = l;
	}

	public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
		mOnErrorListener = l;
	}

	public void setOnPlayStateListener(OnPlayStateListener l) {
		mOnPlayStateListener = l;
	}

	public void setOnSeekCompleteListener(MediaPlayer.OnSeekCompleteListener l) {
		mOnSeekCompleteListener = l;
	}

	public static interface OnPlayStateListener {
		public void onStateChanged(boolean isPlaying);
	}

	public void setOnCompletionListener(MediaPlayer.OnCompletionListener l) {
		mOnCompletionListener = l;
	}

	public void setVideoPath(String path) {
		// && MediaUtils.isNative(path)
		if (StringUtils.isNotEmpty(path)) {
			mTargetState = STATE_PREPARED;
			openVideo(Uri.parse(path));
		}
	}

	public int getVideoWidth() {
		return mVideoWidth;
	}

	public int getVideoHeight() {
		return mVideoHeight;
	}

	public void reOpen() {
		mTargetState = STATE_PREPARED;
		openVideo(mUri);
	}

	public int getDuration() {
		return mDuration;
	}

	/** 重试 */
	private void tryAgain(Exception e) {
		mCurrentState = STATE_ERROR;
		openVideo(mUri);
	}

	public void start() {
		mTargetState = STATE_PLAYING;
		//可用状态{Prepared, Started, Paused, PlaybackCompleted}
		if (mMediaPlayer != null && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
			try {
				if (!isPlaying())
					mMediaPlayer.start();
				mCurrentState = STATE_PLAYING;
				if (mOnPlayStateListener != null)
					mOnPlayStateListener.onStateChanged(true);
			} catch (IllegalStateException e) {
				tryAgain(e);
			} catch (Exception e) {
				tryAgain(e);
			}
		}
	}

	public void pause() {
		mTargetState = STATE_PAUSED;
		//可用状态{Started, Paused}
		if (mMediaPlayer != null && (mCurrentState == STATE_PLAYING)) {
			try {
				mMediaPlayer.pause();
				mCurrentState = STATE_PAUSED;
				if (mOnPlayStateListener != null)
					mOnPlayStateListener.onStateChanged(false);
			} catch (IllegalStateException e) {
				tryAgain(e);
			} catch (Exception e) {
				tryAgain(e);
			}
		}
	}

	/** 处理音量键 */
	public void dispatchKeyEvent(Context context, KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			setVolume(getSystemVolumn(context));
			break;
		}
	}

	public void setVolume(float volume) {
		//可用状态{Idle, Initialized, Stopped, Prepared, Started, Paused, PlaybackCompleted}
		if (mMediaPlayer != null && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
			try {
				mMediaPlayer.setVolume(volume, volume);
			} catch (Exception e) {
				
			}
		}
	}

	//	public float getSystemVolume() {
	//		return mSystemVolumn;
	//	}

	public void setLooping(boolean looping) {
		//可用状态{Idle, Initialized, Stopped, Prepared, Started, Paused, PlaybackCompleted}
		if (mMediaPlayer != null && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
			try {
				mMediaPlayer.setLooping(looping);
			} catch (Exception e) {
			}
		}
	}

	public void seekTo(int msec) {
		//可用状态{Prepared, Started, Paused, PlaybackCompleted}
		if (mMediaPlayer != null && (mCurrentState == STATE_PREPARED || mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED || mCurrentState == STATE_PLAYBACK_COMPLETED)) {
			try {
				if (msec < 0)
					msec = 0;
				mMediaPlayer.seekTo(msec);
			} catch (IllegalStateException e) {
			} catch (Exception e) {
			}
		}
	}

	/** 获取当前播放位置 */
	public int getCurrentPosition() {
		int position = 0;
		//可用状态{Idle, Initialized, Prepared, Started, Paused, Stopped, PlaybackCompleted}
		if (mMediaPlayer != null) {
			switch (mCurrentState) {
			case STATE_PLAYBACK_COMPLETED:
				position = getDuration();
				break;
			case STATE_PLAYING:
			case STATE_PAUSED:
				try {
					position = mMediaPlayer.getCurrentPosition();
				} catch (IllegalStateException e) {
				} catch (Exception e) {
				}
				break;
			}
		}
		return position;
	}

	public boolean isPlaying() {
		//可用状态{Idle, Initialized, Prepared, Started, Paused, Stopped, PlaybackCompleted}
		if (mMediaPlayer != null && mCurrentState == STATE_PLAYING) {
			try {
				return mMediaPlayer.isPlaying();
			} catch (IllegalStateException e) {
			} catch (Exception e) {
			}
		}
		return false;
	}

	/** 调用release方法以后MediaPlayer无法再恢复使用 */
	public void release() {
		mTargetState = STATE_RELEASED;
		mCurrentState = STATE_RELEASED;
		if (mMediaPlayer != null) {
			try {
				mMediaPlayer.release();
			} catch (IllegalStateException e) {
			} catch (Exception e) {
			}
			mMediaPlayer = null;
		}
	}

	public SurfaceHolder getSurfaceHolder() {
		return mSurfaceHolder;
	}

	public void openVideo(Uri uri) {
		if (uri == null || mSurfaceHolder == null || getContext() == null) {
			// not ready for playback just yet, will try again later
			if (mSurfaceHolder == null && uri != null) {
				mUri = uri;
			}
			return;
		}

		mUri = uri;
		mDuration = 0;

		//Idle 状态：当使用new()方法创建一个MediaPlayer对象或者调用了其reset()方法时，该MediaPlayer对象处于idle状态。
		//End 状态：通过release()方法可以进入End状态，只要MediaPlayer对象不再被使用，就应当尽快将其通过release()方法释放掉
		//Initialized 状态：这个状态比较简单，MediaPlayer调用setDataSource()方法就进入Initialized状态，表示此时要播放的文件已经设置好了。
		//Prepared 状态：初始化完成之后还需要通过调用prepare()或prepareAsync()方法，这两个方法一个是同步的一个是异步的，只有进入Prepared状态，才表明MediaPlayer到目前为止都没有错误，可以进行文件播放。

		Exception exception = null;
		try {
			if (mMediaPlayer == null) {
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setOnPreparedListener(mPreparedListener);
				mMediaPlayer.setOnCompletionListener(mCompletionListener);
				mMediaPlayer.setOnErrorListener(mErrorListener);
				mMediaPlayer.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
				mMediaPlayer.setOnInfoListener(mInfoListener);
				//			mMediaPlayer.setScreenOnWhilePlaying(true);
				//				mMediaPlayer.setVolume(mSystemVolumn, mSystemVolumn);
				mMediaPlayer.setDisplay(mSurfaceHolder);
			} else {
				mMediaPlayer.reset();
			}
			mMediaPlayer.setDataSource(getContext(), uri);

			//			if (mLooping)
			//				mMediaPlayer.setLooping(true);//循环播放
			mMediaPlayer.prepareAsync();
			// we don't set the target state here either, but preserve the
			// target state that was there before.
			mCurrentState = STATE_PREPARING;
		} catch (IOException ex) {
			exception = ex;
		} catch (IllegalArgumentException ex) {
			exception = ex;
		} catch (Exception ex) {
			exception = ex;
		}
		if (exception != null) {
			mCurrentState = STATE_ERROR;
			if (mErrorListener != null)
				mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
		}
	}

	private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			mCurrentState = STATE_PLAYBACK_COMPLETED;
			//			mTargetState = STATE_PLAYBACK_COMPLETED;
			if (mOnCompletionListener != null)
				mOnCompletionListener.onCompletion(mp);
		}
	};

	MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			//必须是正常状态
			if (mCurrentState == STATE_PREPARING) {
				mCurrentState = STATE_PREPARED;
				try {
					mDuration = mp.getDuration();
				} catch (IllegalStateException e) {
				}

				try {
					mVideoWidth = mp.getVideoWidth();
					mVideoHeight = mp.getVideoHeight();
				} catch (IllegalStateException e) {
				}

				switch (mTargetState) {
				case STATE_PREPARED:
					if (mOnPreparedListener != null)
						mOnPreparedListener.onPrepared(mMediaPlayer);
					break;
				case STATE_PLAYING:
					start();
					break;
				}
			}
		}
	};

	OnVideoSizeChangedListener mVideoSizeChangedListener = new OnVideoSizeChangedListener() {

		@Override
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			mVideoWidth = width;
			mVideoHeight = height;
			if (mOnVideoSizeChangedListener != null)
				mOnVideoSizeChangedListener.onVideoSizeChanged(mp, width, height);
		}

	};

	OnInfoListener mInfoListener = new OnInfoListener() {

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			if (mOnInfoListener != null)
				mOnInfoListener.onInfo(mp, what, extra);
			return false;
		}
	};

	private MediaPlayer.OnSeekCompleteListener mSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {

		@Override
		public void onSeekComplete(MediaPlayer mp) {
			if (mOnSeekCompleteListener != null)
				mOnSeekCompleteListener.onSeekComplete(mp);
		}
	};

	private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
			mCurrentState = STATE_ERROR;
			//			mTargetState = STATE_ERROR;
			//FIX，可以考虑出错以后重新开始
			if (mOnErrorListener != null)
				mOnErrorListener.onError(mp, framework_err, impl_err);

			return true;
		}
	};

	/** 是否可用 */
	public boolean isPrepared() {
		return mMediaPlayer != null && (mCurrentState == STATE_PREPARED);
	}

	public boolean isComplate(){
		return mMediaPlayer != null && (mCurrentState == STATE_PLAYBACK_COMPLETED);
	}
	
	/** 是否已经释放 */
	public boolean isRelease() {
		return mMediaPlayer == null || mCurrentState == STATE_IDLE || mCurrentState == STATE_ERROR || mCurrentState == STATE_RELEASED;
	}

	private Handler mVideoHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_MESSAGE_PARSE:
				pause();
				break;
			case HANDLER_MESSAGE_LOOP:
				if (isPlaying()) {
					seekTo(msg.arg1);
					sendMessageDelayed(mVideoHandler.obtainMessage(HANDLER_MESSAGE_LOOP, msg.arg1, msg.arg2), msg.arg2);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/** 定时暂停 */
	public void pauseDelayed(int delayMillis) {
		if (mVideoHandler.hasMessages(HANDLER_MESSAGE_PARSE))
			mVideoHandler.removeMessages(HANDLER_MESSAGE_PARSE);
		mVideoHandler.sendEmptyMessageDelayed(HANDLER_MESSAGE_PARSE, delayMillis);
	}

	/** 暂停并且清除定时任务 */
	public void pauseClearDelayed() {
		pause();
		if (mVideoHandler.hasMessages(HANDLER_MESSAGE_PARSE))
			mVideoHandler.removeMessages(HANDLER_MESSAGE_PARSE);
		if (mVideoHandler.hasMessages(HANDLER_MESSAGE_LOOP))
			mVideoHandler.removeMessages(HANDLER_MESSAGE_LOOP);
	}

	/** 区域内循环播放 */
	public void loopDelayed(int startTime, int endTime) {
		if (mVideoHandler.hasMessages(HANDLER_MESSAGE_PARSE))
			mVideoHandler.removeMessages(HANDLER_MESSAGE_PARSE);
		if (mVideoHandler.hasMessages(HANDLER_MESSAGE_LOOP))
			mVideoHandler.removeMessages(HANDLER_MESSAGE_LOOP);
		int delayMillis = endTime - startTime;
		seekTo(startTime);
		if (!isPlaying())
			start();
		if (mVideoHandler.hasMessages(HANDLER_MESSAGE_LOOP))
			mVideoHandler.removeMessages(HANDLER_MESSAGE_LOOP);
		mVideoHandler.sendMessageDelayed(mVideoHandler.obtainMessage(HANDLER_MESSAGE_LOOP, getCurrentPosition(), delayMillis), delayMillis);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		boolean needReOpen = (mSurfaceHolder == null);
		mSurfaceHolder = holder;
		if (needReOpen) {
			reOpen();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mSurfaceHolder = null;
		release();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	  super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
