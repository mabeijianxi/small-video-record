package mabeijianxi.camera.model;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by jianxi on 2017/3/14.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */

public class MediaBitrateConfig implements Parcelable {

    private final int bitrate;
    private final int maxBitrate;
    private final int mode;
    private final int bufsize;

    private MediaBitrateConfig(Builder builder) {
        bitrate = builder.bitrate;
        maxBitrate = builder.maxBitrate;
        mode = builder.mode;
        this.bufsize=builder.bufsize;
    }


    protected MediaBitrateConfig(Parcel in) {
        bitrate = in.readInt();
        maxBitrate = in.readInt();
        mode = in.readInt();
        bufsize = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bitrate);
        dest.writeInt(maxBitrate);
        dest.writeInt(mode);
        dest.writeInt(bufsize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaBitrateConfig> CREATOR = new Creator<MediaBitrateConfig>() {
        @Override
        public MediaBitrateConfig createFromParcel(Parcel in) {
            return new MediaBitrateConfig(in);
        }

        @Override
        public MediaBitrateConfig[] newArray(int size) {
            return new MediaBitrateConfig[size];
        }
    };

    public int getBitrate() {
        return bitrate;
    }

    public int getMaxBitrate() {
        return maxBitrate;
    }

    public int getMode() {
        return mode;
    }

    public int getBufsize() {
        return bufsize;
    }


    public static class Builder {
        private int mode = MODE.AUTO_VBR;

        private int maxBitrate = 0;

        private int bitrate = 0;

        private int bufsize=166;

        /**
         * @param mode {@link MODE} 出入需慎重
         * @return
         */
        public Builder setMode(int mode) {
            this.mode = mode;
            return this;
        }

        /**
         *
         * @param maxBitrate VBR模式下有效
         * @return
         */
        public Builder setMaxBitrate(int maxBitrate) {
            this.maxBitrate = maxBitrate;
            return this;
        }

        public Builder setBitrate(int bitrate) {
            this.bitrate = bitrate;
            return this;
        }

        /**
         *
         * @param bufsize CBR模式下有效
         * @return
         */
        public Builder setBufsize(int bufsize) {
            this.bufsize = bufsize;
            return this;
        }

        public MediaBitrateConfig build() {
            return new MediaBitrateConfig(this);
        }
    }

    public static class MODE {
        /**
         * 默认模式，可不传入参数
         */
        public static int AUTO_VBR = 0;
        /**
         * 这个模式下可设置额定码率
         */
        public static int VBR = 1;
        /**
         * 固定码率
         */
        public static int CBR = 2;
    }
}
