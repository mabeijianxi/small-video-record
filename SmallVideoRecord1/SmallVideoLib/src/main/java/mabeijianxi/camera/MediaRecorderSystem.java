package mabeijianxi.camera;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.IOException;

import mabeijianxi.camera.model.MediaObject.MediaPart;
import mabeijianxi.camera.util.FileUtils;

/**
 * 使用系统MediaRecorder录制，适合低端机
 */
public class MediaRecorderSystem extends MediaRecorderBase implements android.media.MediaRecorder.OnErrorListener {

	/** 系统MediaRecorder对象 */
	private android.media.MediaRecorder mMediaRecorder;

	public MediaRecorderSystem() {

	}

	/** 开始录制 */
	@Override
	public MediaPart startRecord() {
		if (mMediaObject != null && mSurfaceHolder != null && !mRecording) {
			MediaPart result = mMediaObject.buildMediaPart(mCameraId, ".mp4");

			try {
				if (mMediaRecorder == null) {
					mMediaRecorder = new MediaRecorder();
					mMediaRecorder.setOnErrorListener(this);
				} else {
					mMediaRecorder.reset();
				}

				// Step 1: Unlock and set camera to MediaRecorder
				camera.unlock();
				mMediaRecorder.setCamera(camera);
				mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

				// Step 2: Set sources
				mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//before setOutputFormat()
				mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//before setOutputFormat()

				mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

				//设置视频输出的格式和编码
				CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
				//                mMediaRecorder.setProfile(mProfile);
				mMediaRecorder.setVideoSize(640, 480);//after setVideoSource(),after setOutFormat()
				mMediaRecorder.setAudioEncodingBitRate(44100);
				if (mProfile.videoBitRate > 2 * 1024 * 1024)
					mMediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
				else
					mMediaRecorder.setVideoEncodingBitRate(mProfile.videoBitRate);
				mMediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);//after setVideoSource(),after setOutFormat()

				mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);//after setOutputFormat()
				mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//after setOutputFormat()

				//mMediaRecorder.setVideoEncodingBitRate(800);

				// Step 4: Set output file
				mMediaRecorder.setOutputFile(result.mediaPath);

				// Step 5: Set the preview output
				//				mMediaRecorder.setOrientationHint(90);//加了HTC的手机会有问题

				Log.e("Yixia", "OutputFile:" + result.mediaPath);

				mMediaRecorder.prepare();
				mMediaRecorder.start();
				mRecording = true;
				return result;
			} catch (IllegalStateException e) {
				e.printStackTrace();
				Log.e("Yixia", "startRecord", e);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("Yixia", "startRecord", e);
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("Yixia", "startRecord", e);
			}
		}
		return null;
	}

	/** 停止录制 */
	@Override
	public void stopRecord() {
		long endTime = System.currentTimeMillis();
		if (mMediaRecorder != null) {
			//设置后不会崩
			mMediaRecorder.setOnErrorListener(null);
			mMediaRecorder.setPreviewDisplay(null);
			try {
				mMediaRecorder.stop();
			} catch (IllegalStateException e) {
				Log.w("Yixia", "stopRecord", e);
			} catch (RuntimeException e) {
				Log.w("Yixia", "stopRecord", e);
			} catch (Exception e) {
				Log.w("Yixia", "stopRecord", e);
			}
		}

		if (camera != null) {
			try {
				camera.lock();
			} catch (RuntimeException e) {
				Log.e("Yixia", "stopRecord", e);
			}
		}

		// 判断数据是否处理完，处理完了关闭输出流
		if (mMediaObject != null) {
			MediaPart part = mMediaObject.getCurrentPart();
			if (part != null && part.recording) {
				part.recording = false;
				part.endTime = endTime;
				part.duration = (int) (part.endTime - part.startTime);
				part.cutStartTime = 0;
				part.cutEndTime = part.duration;
			}
		}
		mRecording = false;
	}

	/** 释放资源 */
	@Override
	public void release() {
		super.release();
		if (mMediaRecorder != null) {
			mMediaRecorder.setOnErrorListener(null);
			try {
				mMediaRecorder.release();
			} catch (IllegalStateException e) {
				Log.w("Yixia", "stopRecord", e);
			} catch (Exception e) {
				Log.w("Yixia", "stopRecord", e);
			}
		}
		mMediaRecorder = null;
	}

	@Override
	public void onError(MediaRecorder mr, int what, int extra) {
		try {
			if (mr != null)
				mr.reset();
		} catch (IllegalStateException e) {
			Log.w("Yixia", "stopRecord", e);
		} catch (Exception e) {
			Log.w("Yixia", "stopRecord", e);
		}
		if (mOnErrorListener != null)
			mOnErrorListener.onVideoError(what, extra);
	}

	/** 不需要视频数据回调 */
	@Override
	protected void setPreviewCallback() {
		//super.setPreviewCallback();
	}

	/** 合并视频文件 */
	@Override
	protected void concatVideoParts() {
		new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(Void... params) {
				String cmd = "";
				int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
				//将mp4转成ts
				for (int i = 0, j = mMediaObject.getMedaParts().size(); i < j; i++) {
					MediaPart part = mMediaObject.getMedaParts().get(i);
					if (FileUtils.checkFile(part.mediaPath)) {
						String ts = part.mediaPath.replace(".mp4", ".ts");
						FileUtils.deleteFile(ts);//删除
						cameraId = part.cameraId;
						cmd = String.format("ffmpeg %s -i \"%s\" -r 25 -vcodec copy -acodec copy -vbsf h264_mp4toannexb \"%s\"", FFMpegUtils.getLogCommand(), part.mediaPath, ts);
						
						if (UtilityAdapter.FFmpegRun("", cmd) == 0) {
							part.mediaPath = ts;//修改后缀名
							continue;
						}
					}
					//文件不存在或者转码失败，直接跳过
					part.mediaPath = "";
				}
				
				//处理翻转信息
				String vf = cameraId == Camera.CameraInfo.CAMERA_FACING_BACK ? "transpose=1" : "transpose=2,hflip";
				//合并ts流
				cmd = String.format("ffmpeg %s -i \"%s\" -vf \"%s\" %s -acodec copy -absf aac_adtstoasc -f mp4 -movflags faststart \"%s\"", FFMpegUtils.getLogCommand(), mMediaObject.getConcatYUV(), vf, FFMpegUtils.getVCodecCommand(), mMediaObject.getOutputTempVideoPath());

//				cmd = String.format("ffmpeg %s -i \"%s\" -vcodec copy -acodec copy -absf aac_adtstoasc -f mp4 -movflags faststart \"%s\"", FFMpegUtils.getLogCommand(), mMediaObject.getConcatYUV(), mMediaObject.getOutputTempVideoPath());

				android.util.Log.e("MediaRecorderSystem", cmd);
				return UtilityAdapter.FFmpegRun("", cmd) == 0;
			}

			@Override
			protected void onPostExecute(Boolean result) {
				if (result) {
					mEncodeHanlder.sendEmptyMessage(MESSAGE_ENCODE_COMPLETE);
				} else {
					mEncodeHanlder.sendEmptyMessage(MESSAGE_ENCODE_ERROR);
				}
			}
		}.execute();
	}
}
