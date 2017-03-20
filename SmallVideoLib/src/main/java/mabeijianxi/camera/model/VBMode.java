package mabeijianxi.camera.model;

/**
 * Created by jianxi on 2017/3/16.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */

public class VBRMode extends BaseMediaBitrateConfig {
    /**
     *
     * @param maxBitrate 最大码率
     * @param bitrate 额定码率
     */
    public VBRMode(int maxBitrate, int bitrate){
        if(maxBitrate<=0||bitrate<=0){
            throw new IllegalArgumentException("maxBitrate or bitrate value error!");
        }
        this.maxBitrate=maxBitrate;
        this.bitrate=bitrate;
        this.mode= MODE.VBR;
    }
}
