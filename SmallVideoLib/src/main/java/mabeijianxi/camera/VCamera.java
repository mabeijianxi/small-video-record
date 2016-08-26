package mabeijianxi.camera;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.yixia.videoeditor.adapter.UtilityAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import mabeijianxi.camera.util.DeviceUtils;
import mabeijianxi.camera.util.Log;

/**
 * 拍摄SDK
 */
public class VCamera {
	/** 应用包名 */
	private static String mPackageName;
	/** 应用版本名称 */
	private static String mAppVersionName;
	/** 应用版本号 */
	private static int mAppVersionCode;
	/** 视频缓存路径 */
	private static String mVideoCachePath;
	/** SDK版本号 */
	public final static String VCAMERA_SDK_VERSION = "1.2.0";
	/** FFMPEG执行失败存的log文件 */
	public final static String FFMPEG_LOG_FILENAME = "ffmpeg.log";
	/** 执行FFMPEG命令保存路径 */
	public final static String FFMPEG_LOG_FILENAME_TEMP = "temp_ffmpeg.log";

	/**
	 * 初始化SDK
	 * 
	 * @param context
	 */
	public static void initialize(Context context) {
		mPackageName = context.getPackageName();

		mAppVersionName = getVerName(context);
		mAppVersionCode = getVerCode(context);

		//初始化底层库
		UtilityAdapter.FFmpegInit(context, String.format("versionName=%s&versionCode=%d&sdkVersion=%s&android=%s&device=%s", mAppVersionName, mAppVersionCode, VCAMERA_SDK_VERSION, DeviceUtils.getReleaseVersion(), DeviceUtils.getDeviceModel()));
	}

	/**
	 * 获取当前应用的版本号
	 * @param context
	 * @return
	 */
	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
		}
		return verCode;
	}

	/** 获取当前应用的版本名称 */
	public static String getVerName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return "";
	}

	// /** 上传错误日志 */
	// public static void uploadErrorLog() {
	// LogHelper.upload();
	// }

	/** 是否开启log输出 */
	public static boolean isLog() {
		return Log.getIsLog();
	}

	public static String getPackageName() {
		return mPackageName;
	}

	/** 是否开启Debug模式，会输出log */
	public static void setDebugMode(boolean enable) {
		Log.setLog(enable);
	}

	/** 获取视频缓存文件夹 */
	public static String getVideoCachePath() {
		return mVideoCachePath;
	}

	/** 设置视频缓存路径 */
	public static void setVideoCachePath(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		mVideoCachePath = path;

		// 生成空的日志文件
		File temp = new File(VCamera.getVideoCachePath(), VCamera.FFMPEG_LOG_FILENAME_TEMP);
		if (!temp.exists()) {
			try {
				temp.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/** 拷贝转码失败的log */
	protected static boolean copyFFmpegLog(String cmd) {
		boolean result = false;

		int size = 1 * 1024;

		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			File temp = new File(VCamera.getVideoCachePath(), VCamera.FFMPEG_LOG_FILENAME_TEMP);
			if (!temp.exists()) {
				temp.createNewFile();
				return false;
			}
			in = new FileInputStream(temp);
			out = new FileOutputStream(new File(VCamera.getVideoCachePath(), VCamera.FFMPEG_LOG_FILENAME), true);
			out.write("--------------------------------------------------\r\n".getBytes());
			out.write(cmd.getBytes());
			out.write("\r\n\r\n".getBytes());
			byte[] buffer = new byte[size];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
			result = true;
		} catch (FileNotFoundException e) {
			Log.e("upload", e);
		} catch (IOException e) {
			Log.e("upload", e);
		} catch (Exception e) {
			Log.e("upload", e);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
			}
		}
		return result;
	}
}
