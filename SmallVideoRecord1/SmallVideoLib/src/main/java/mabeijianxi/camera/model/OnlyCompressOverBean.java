package mabeijianxi.camera.model;

/**
 * Created by jianxi on 2017/4/1.
 * https://github.com/mabeijianxi
 * mabeijianxi@gmail.com
 */

public class OnlyCompressOverBean {

    private boolean succeed;

    private String videoPath;

    private String picPath;

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
}
