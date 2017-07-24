package mabeijianxi.camera.model;

/**
 * Created by jianxi on 2017/3/16.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */

public class CBRMode extends BaseMediaBitrateConfig {

    /**
     *
     * @param bufSize
     * @param bitrate 固定码率值
     */
    public CBRMode(int bufSize, int bitrate){
        if(bufSize<=0||bitrate<=0){
            throw new IllegalArgumentException("bufSize or bitrate value error!");
        }
        this.bufSize=bufSize;
        this.bitrate=bitrate;
        this.mode= MODE.CBR;
    }
}
