package mabeijianxi.camera;

import mabeijianxi.camera.model.MediaObject.MediaPart;

/**
 * 视频录制接口
 * 
 */
public interface IMediaRecorder {

	/**
	 * 开始录制
	 * 
	 * @return 录制失败返回null
	 */
	public MediaPart startRecord();
	
	/**
	 * 停止录制
	 */
	public void stopRecord();
	
	/**
	 * 音频错误
	 * 
	 * @param what 错误类型
	 * @param message 
	 */
	public void onAudioError(int what, String message);
	/**
	 * 接收音频数据
	 * 
	 * @param sampleBuffer 音频数据
	 * @param len
	 */
	public void receiveAudioData(byte[] sampleBuffer, int len);
}
