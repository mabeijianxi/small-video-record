package mabeijianxi.camera.model;

/**
 * Created by jian on 2016/8/25 17:03
 * mabeijianxi@gmail.com
 */
public class MediaRecorderConfig {
    private MediaRecorderConfig(){

    }
    public class Buidler{
        /**
         * 录制时间
         */
        private int RECORD_TIME_MAX= 6 * 1000;
        /**
         * 录制最少时间
         */
        private int RECORD_TIME_MIN= (int) (1.5f * 1000);

    }

}
