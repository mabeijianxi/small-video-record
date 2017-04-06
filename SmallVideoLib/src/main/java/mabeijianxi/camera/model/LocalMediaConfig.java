package mabeijianxi.camera.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jianxi on 2017/4/1.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */
public final class LocalMediaConfig implements Parcelable {

    /**
     * 帧率
     */
    private final int FRAME_RATE;

    /**
     * 录制后会剪切一帧缩略图并保存，就是取时间轴上这个时间的画面
     */
    private final int captureThumbnailsTime;

    private final boolean GO_HOME;
    /**
     * 码率配置
     */
    private final BaseMediaBitrateConfig compressConfig;

    private final String videoAddress;

    private LocalMediaConfig(Buidler buidler) {
        this.captureThumbnailsTime = buidler.captureThumbnailsTime;
        this.FRAME_RATE = buidler.FRAME_RATE;
        this.compressConfig = buidler.compressConfig;
        this.videoAddress=buidler.videoPath;
        this.GO_HOME = buidler.GO_HOME;

    }

    protected LocalMediaConfig(Parcel in) {
        FRAME_RATE = in.readInt();
        captureThumbnailsTime = in.readInt();
        GO_HOME = in.readByte() != 0;
        compressConfig = in.readParcelable(BaseMediaBitrateConfig.class.getClassLoader());
        videoAddress = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(FRAME_RATE);
        dest.writeInt(captureThumbnailsTime);
        dest.writeByte((byte) (GO_HOME ? 1 : 0));
        dest.writeParcelable(compressConfig, flags);
        dest.writeString(videoAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocalMediaConfig> CREATOR = new Creator<LocalMediaConfig>() {
        @Override
        public LocalMediaConfig createFromParcel(Parcel in) {
            return new LocalMediaConfig(in);
        }

        @Override
        public LocalMediaConfig[] newArray(int size) {
            return new LocalMediaConfig[size];
        }
    };

    public boolean isGO_HOME() {
        return GO_HOME;
    }

    public int getCaptureThumbnailsTime() {
        return captureThumbnailsTime;
    }

    public int getFrameRate() {
        return FRAME_RATE;
    }


    public BaseMediaBitrateConfig getCompressConfig() {
        return compressConfig;
    }

    public String getVideoPath() {
        return videoAddress;
    }


    public static class Buidler {
        /**
         * 录制后会剪切一帧缩略图并保存，就是取时间轴上这个时间的画面
         */
        private int captureThumbnailsTime = 1;


        private boolean GO_HOME = false;

        private BaseMediaBitrateConfig compressConfig;
        private int FRAME_RATE;

        private String videoPath;

        public LocalMediaConfig build() {
            return new LocalMediaConfig(this);
        }

        /**
         * @param captureThumbnailsTime 会剪切一帧缩略图并保存，就是取时间轴上这个时间的画面
         * @return
         */
        public Buidler captureThumbnailsTime(int captureThumbnailsTime) {
            this.captureThumbnailsTime = captureThumbnailsTime;
            return this;
        }

        /**
         * @param compressConfig 压缩配置设置
         *                       {@link AutoVBRMode }{@link VBRMode}{@link CBRMode}
         * @return
         */
        public Buidler doH264Compress(BaseMediaBitrateConfig compressConfig) {
            this.compressConfig = compressConfig;
            return this;
        }


        public Buidler goHome(boolean GO_HOME) {
            this.GO_HOME = GO_HOME;
            return this;
        }

        public Buidler setFramerate(int MAX_FRAME_RATE) {
            this.FRAME_RATE = MAX_FRAME_RATE;
            return this;
        }

        public Buidler setVideoPath(String videoPath) {
            this.videoPath = videoPath;
            return this;
        }
    }

}
