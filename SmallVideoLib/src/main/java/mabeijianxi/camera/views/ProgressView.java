package mabeijianxi.camera.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import java.util.Iterator;

import mabeijianxi.camera.R;
import mabeijianxi.camera.model.MediaObject;
import mabeijianxi.camera.util.DeviceUtils;

public class ProgressView extends View {

	/** 进度条 */
	private Paint mProgressPaint;
	/** 闪 */
	private Paint mActivePaint;
	/** 暂停/中断色块 */
	private Paint mPausePaint;
	/** 回删 */
	private Paint mRemovePaint;
	/** 三秒 */
	private Paint mThreePaint;
	/** 超时 */
	private Paint mOverflowPaint;
	private boolean mStop, mProgressChanged;
	private boolean mActiveState;
	private MediaObject mMediaObject;
	/** 最长时长 */
	private int mMaxDuration, mVLineWidth;
	private int mRecordTimeMin=1500;

	public ProgressView(Context paramContext) {
		super(paramContext);
		init();
	}

	public ProgressView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init();
	}

	public ProgressView(Context paramContext, AttributeSet paramAttributeSet,
						int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init();
	}

	private void init() {
		mProgressPaint = new Paint();
		mActivePaint = new Paint();
		mPausePaint = new Paint();
		mRemovePaint = new Paint();
		mThreePaint = new Paint();
		mOverflowPaint = new Paint();

		mVLineWidth = DeviceUtils.dipToPX(getContext(), 1);

		setBackgroundColor(getResources().getColor(R.color.camera_bg));
		mProgressPaint.setColor(0xFF45C01A);
		mProgressPaint.setStyle(Paint.Style.FILL);

		mActivePaint.setColor(getResources().getColor(android.R.color.white));
		mActivePaint.setStyle(Paint.Style.FILL);

		mPausePaint.setColor(getResources().getColor(
				R.color.camera_progress_split));
		mPausePaint.setStyle(Paint.Style.FILL);

		mRemovePaint.setColor(getResources().getColor(
				R.color.camera_progress_delete));
		mRemovePaint.setStyle(Paint.Style.FILL);

		mThreePaint.setColor(getResources().getColor(
				R.color.camera_progress_three));
		mThreePaint.setStyle(Paint.Style.FILL);

		mOverflowPaint.setColor(getResources().getColor(
				R.color.camera_progress_overflow));
		mOverflowPaint.setStyle(Paint.Style.FILL);
	}

	/** 闪动 */
	private final static int HANDLER_INVALIDATE_ACTIVE = 0;
	/** 录制中 */
	private final static int HANDLER_INVALIDATE_RECORDING = 1;

	private Handler mHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_INVALIDATE_ACTIVE:
				invalidate();
				mActiveState = !mActiveState;
				if (!mStop)
					sendEmptyMessageDelayed(0, 300);
				break;
			case HANDLER_INVALIDATE_RECORDING:
				invalidate();
				if (mProgressChanged)
					sendEmptyMessageDelayed(0, 50);
				break;
			}
			super.dispatchMessage(msg);
		}
	};

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final int width = getMeasuredWidth(), height = getMeasuredHeight();
		int left = 0, right = 0, duration = 0;
		if (mMediaObject != null && mMediaObject.getMedaParts() != null) {

			left = right = 0;
			Iterator<MediaObject.MediaPart> iterator = mMediaObject
					.getMedaParts().iterator();
			boolean hasNext = iterator.hasNext();

			// final int duration = vp.getDuration();
			int maxDuration = mMaxDuration;
			boolean hasOutDuration = false;
			int currentDuration = mMediaObject.getDuration();
			hasOutDuration = currentDuration > mMaxDuration;
			if (hasOutDuration)
				maxDuration = currentDuration;

			while (hasNext) {
				MediaObject.MediaPart vp = iterator.next();
				final int partDuration = vp.getDuration();
				// Logger.e("[ProgressView]partDuration" + partDuration +
				// " maxDuration:" + maxDuration);
				left = right;
				right = left
						+ (int) (partDuration * 1.0F / maxDuration * width);

				if (vp.remove) {
					// 回删
					canvas.drawRect(left, 0.0F, right, height, mRemovePaint);
				} else {
					// 画进度
					if (hasOutDuration) {
						// 超时拍摄
						// 前段
						right = left
								+ (int) ((mMaxDuration - duration) * 1.0F
										/ maxDuration * width);
						canvas.drawRect(left, 0.0F, right, height,
								mProgressPaint);

						// 超出的段
						left = right;
						right = left
								+ (int) ((partDuration - (mMaxDuration - duration))
										* 1.0F / maxDuration * width);
						canvas.drawRect(left, 0.0F, right, height,
								mOverflowPaint);
					} else {
						canvas.drawRect(left, 0.0F, right, height,
								mProgressPaint);
					}
				}

				hasNext = iterator.hasNext();
				if (hasNext) {
					// left = right - mVLineWidth;
					canvas.drawRect(right - mVLineWidth, 0.0F, right, height,
							mPausePaint);
				}

				duration += partDuration;
				// progress = vp.progress;
			}
		}

		// 画三秒
		if (duration < mRecordTimeMin) {
			left = (int) ((mRecordTimeMin*1.0f )/ mMaxDuration * width);
			canvas.drawRect(left, 0.0F, left + mVLineWidth, height, mThreePaint);
		}

		// 删
		//
		// 闪
		if (mActiveState) {
			if (right + 8 >= width)
				right = width - 8;
			canvas.drawRect(right, 0.0F, right + 8, getMeasuredHeight(),
					mActivePaint);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		mStop = false;
		mHandler.sendEmptyMessage(HANDLER_INVALIDATE_ACTIVE);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mStop = true;
		mHandler.removeMessages(HANDLER_INVALIDATE_ACTIVE);
	}

	// public void addProgress(MediaPart part) {
	// if (part != null) {
	// part.index = mVideoParts.size();
	// mVideoParts.add(part);
	// }
	// }

	public void setData(MediaObject mMediaObject) {
		this.mMediaObject = mMediaObject;
	}

	public void setMaxDuration(int duration) {
		this.mMaxDuration = duration;
	}

	public void start() {
		mProgressChanged = true;
	}

	public void stop() {
		mProgressChanged = false;
	}

	public void setMinTime(int recordTimeMin) {
		this.mRecordTimeMin=recordTimeMin;
	}
}
