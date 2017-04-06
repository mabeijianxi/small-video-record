package mabeijianxi.camera.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jian on 2016/8/25 17:03
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */
public final class MediaRecorderConfig implements Parcelable {
    /**
     * 录制时间
     */
    private final int RECORD_TIME_MAX;
    /**
     * 录制最少时间
     */
    private final int RECORD_TIME_MIN;
    /**
     * 小视频高度
     */
    private final int SMALL_VIDEO_HEIGHT;
    /**
     * 小视频宽度,TODO 注意：宽度不能随意穿，需要传送手机摄像头手支持录制的视频高度，注意是高度（因为会选择，具体原因不多解析）。
     * 获取摄像头所支持的尺寸的方式是{@link android.graphics.Camera #getSupportedPreviewSizes()}
     * 一般支持的尺寸的高度有：240、480、720、1080等，具体值请用以上方法获取
     */
    private final int SMALL_VIDEO_WIDTH;
    /**
     * 最大帧率
     */
    private final int MAX_FRAME_RATE;
    /**
     * 最小帧率
     */
    private final int MIN_FRAME_RATE;
    /**
     * 视频码率
     */
    private final int VIDEO_BITRATE;
    /**
     * 录制后是否需要H264压缩
     */
    private final boolean doH264Compress;
    /**
     * 录制后会剪切一帧缩略图并保存，就是取时间轴上这个时间的画面
     */
    private final int captureThumbnailsTime;

    private final boolean GO_HOME;
    /**
     * 码率配置
     */

    private final BaseMediaBitrateConfig mediaBitrateConfig;

    private final BaseMediaBitrateConfig compressConfig;

    private MediaRecorderConfig(Buidler buidler) {
        this.RECORD_TIME_MAX = buidler.RECORD_TIME_MAX;
        this.RECORD_TIME_MIN = buidler.RECORD_TIME_MIN;
        this.MAX_FRAME_RATE = buidler.MAX_FRAME_RATE;
        this.captureThumbnailsTime = buidler.captureThumbnailsTime;
        this.MIN_FRAME_RATE = buidler.MIN_FRAME_RATE;
        this.SMALL_VIDEO_HEIGHT = buidler.SMALL_VIDEO_HEIGHT;
        this.SMALL_VIDEO_WIDTH = buidler.SMALL_VIDEO_WIDTH;
        this.VIDEO_BITRATE = buidler.VIDEO_BITRATE;
        this.doH264Compress = buidler.doH264Compress;
        this.mediaBitrateConfig= buidler.mediaBitrateConfig;
        this.compressConfig = buidler.compressConfig;
        this.GO_HOME=buidler.GO_HOME;

    }


    protected MediaRecorderConfig(Parcel in) {
        RECORD_TIME_MAX = in.readInt();
        RECORD_TIME_MIN = in.readInt();
        SMALL_VIDEO_HEIGHT = in.readInt();
        SMALL_VIDEO_WIDTH = in.readInt();
        MAX_FRAME_RATE = in.readInt();
        MIN_FRAME_RATE = in.readInt();
        VIDEO_BITRATE = in.readInt();
        doH264Compress = in.readByte() != 0;
        captureThumbnailsTime = in.readInt();
        GO_HOME = in.readByte() != 0;
        mediaBitrateConfig = in.readParcelable(BaseMediaBitrateConfig.class.getClassLoader());
        compressConfig = in.readParcelable(BaseMediaBitrateConfig.class.getClassLoader());
    }

    public static final Creator<MediaRecorderConfig> CREATOR = new Creator<MediaRecorderConfig>() {
        @Override
        public MediaRecorderConfig createFromParcel(Parcel in) {
            return new MediaRecorderConfig(in);
        }

        @Override
        public MediaRecorderConfig[] newArray(int size) {
            return new MediaRecorderConfig[size];
        }
    };

    public boolean isGO_HOME() {
        return GO_HOME;
    }
    public int getCaptureThumbnailsTime() {
        return captureThumbnailsTime;
    }

    public boolean isDoH264Compress() {
        return doH264Compress;
    }

    public int getMaxFrameRate() {
        return MAX_FRAME_RATE;
    }

    public int getMinFrameRate() {
        return MIN_FRAME_RATE;
    }

    public int getRecordTimeMax() {
        return RECORD_TIME_MAX;
    }

    public int getRecordTimeMin() {
        return RECORD_TIME_MIN;
    }

    public int getSmallVideoHeight() {
        return SMALL_VIDEO_HEIGHT;
    }

    public int getSmallVideoWidth() {
        return SMALL_VIDEO_WIDTH;
    }

    public BaseMediaBitrateConfig getMediaBitrateConfig() {
        return mediaBitrateConfig;
    }

    public BaseMediaBitrateConfig getCompressConfig() {
        return compressConfig;
    }

    public int getVideoBitrate() {
        return VIDEO_BITRATE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(RECORD_TIME_MAX);
        dest.writeInt(RECORD_TIME_MIN);
        dest.writeInt(SMALL_VIDEO_HEIGHT);
        dest.writeInt(SMALL_VIDEO_WIDTH);
        dest.writeInt(MAX_FRAME_RATE);
        dest.writeInt(MIN_FRAME_RATE);
        dest.writeInt(VIDEO_BITRATE);
        dest.writeByte((byte) (doH264Compress ? 1 : 0));
        dest.writeInt(captureThumbnailsTime);
        dest.writeByte((byte) (GO_HOME ? 1 : 0));
        dest.writeParcelable(mediaBitrateConfig, flags);
        dest.writeParcelable(compressConfig, flags);
    }

    public static class Buidler {
        /**
         * 录制时间
         */
        private int RECORD_TIME_MAX = 6 * 1000;
        /**
         * 录制最少时间
         */
        private int RECORD_TIME_MIN = (int) (1.5f * 1000);
        /**
         * 小视频高度
         */
        private int SMALL_VIDEO_HEIGHT = 360;
        /**
         * 小视频宽度,TODO 注意：宽度不能随意穿，需要传送手机摄像头手支持录制的视频高度，注意是高度（因为会选择，具体原因不多解析）。
         * 获取摄像头所支持的尺寸的方式是{@link android.graphics.Camera #getSupportedPreviewSizes()}
         * 一般支持的尺寸的高度有：240、480、720、1080等，具体值请用以上方法获取
         */
        private int SMALL_VIDEO_WIDTH = 480;
        /**
         * 最大帧率
         */
        private int MAX_FRAME_RATE = 20;
        /**
         * 最小帧率
         */
        private int MIN_FRAME_RATE = 8;
        /**
         * 视频码率//todo 注意传入>0的值后码率模式将从VBR变成CBR
         */
        private int VIDEO_BITRATE;
        /**
         * 录制后是否需要H264压缩
         */
        private boolean doH264Compress = true;
        /**
         * 录制后会剪切一帧缩略图并保存，就是取时间轴上这个时间的画面
         */
        private int captureThumbnailsTime = 1;

        private BaseMediaBitrateConfig mediaBitrateConfig;

        private boolean GO_HOME=false;

        private BaseMediaBitrateConfig compressConfig;


        public MediaRecorderConfig build() {
            return new MediaRecorderConfig(this);
        }

        /**
         * @param captureThumbnailsTime 录制后会剪切一帧缩略图并保存，就是取时间轴上这个时间的画面
         * @return
         */
        public Buidler captureThumbnailsTime(int captureThumbnailsTime) {
            this.captureThumbnailsTime = captureThumbnailsTime;
            return this;
        }

        /**
         * @param doH264Compress 录制后是否需要H264压缩，（压缩后可得到清晰而小巧的视频）
         * @return
         */
        @Deprecated
        public Buidler doH264Compress(boolean doH264Compress) {
            this.doH264Compress = doH264Compress;
            if (doH264Compress){
                doH264Compress(new AutoVBRMode());
            }
            return this;
        }

        /**
         *
         * @param compressConfig 压缩配置设置,不需要要进一步压缩可不配置
         * {@link AutoVBRMode }{@link VBRMode}{@link CBRMode}
         * @return
         */
        public Buidler doH264Compress(BaseMediaBitrateConfig compressConfig) {
            this.compressConfig = compressConfig;
            return this;
        }
        /**
         * @param MAX_FRAME_RATE 最大帧率(与视频清晰度、大小息息相关)
         * @return
         */
        public Buidler maxFrameRate(int MAX_FRAME_RATE) {
            this.MAX_FRAME_RATE = MAX_FRAME_RATE;
            return this;
        }

        /**
         * @param MIN_FRAME_RATE 最小帧率(与视频清晰度、大小息息相关)
         * @return
         */
        public Buidler minFrameRate(int MIN_FRAME_RATE) {
            this.MIN_FRAME_RATE = MIN_FRAME_RATE;
            return this;
        }

        /**
         * @param RECORD_TIME_MAX 录制时间
         * @return
         */
        public Buidler recordTimeMax(int RECORD_TIME_MAX) {
            this.RECORD_TIME_MAX = RECORD_TIME_MAX;
            return this;
        }

        /**
         * @param RECORD_TIME_MIN 录制最小时间
         * @return
         */
        public Buidler recordTimeMin(int RECORD_TIME_MIN) {
            this.RECORD_TIME_MIN = RECORD_TIME_MIN;
            return this;
        }

        /**
         * @param SMALL_VIDEO_HEIGHT 小视频高度
         * @return
         */
        public Buidler smallVideoHeight(int SMALL_VIDEO_HEIGHT) {
            this.SMALL_VIDEO_HEIGHT = SMALL_VIDEO_HEIGHT;
            return this;
        }

        /**
         * @param SMALL_VIDEO_WIDTH 小视频宽度,TODO 注意：宽度不能随意传入，需要传送手机摄像头手支持录制的视频高度，注意是高度（因为会选择，具体原因不多解析）。
         *                          获取摄像头所支持的尺寸的方式是{@link android.graphics.Camera #getSupportedPreviewSizes()}
         *                          一般支持的尺寸的高度有：240、480、720、1080等，具体值请用以上方法获取
         * @return
         */
        public Buidler smallVideoWidth(int SMALL_VIDEO_WIDTH) {
            this.SMALL_VIDEO_WIDTH = SMALL_VIDEO_WIDTH;
            return this;
        }

        /**
         * @param VIDEO_BITRATE 视频码率 设置无效,请用{@link #setMediaBitrateConfig(BaseMediaBitrateConfig)}
         * @return
         */
        @Deprecated
        public Buidler videoBitrate(int VIDEO_BITRATE) {
            this.VIDEO_BITRATE = VIDEO_BITRATE;
            return this;
        }

        public Buidler goHome(boolean GO_HOME) {
            this.GO_HOME = GO_HOME;
            return this;
        }

        /**
         *
         * @param mediaBitrateConfig
         * 录制码率配置{@link AutoVBRMode }{@link VBRMode}{@link CBRMode}
         * @return
         */
        public Buidler setMediaBitrateConfig(BaseMediaBitrateConfig mediaBitrateConfig) {
            this.mediaBitrateConfig = mediaBitrateConfig;
            return this;

        }
    }

}
