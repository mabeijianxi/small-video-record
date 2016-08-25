package mabeijianxi.camera.model;

public class MediaThemeObject {

    /** MV主题 */
    public String mMVThemeName;

    /** 音乐 */
    public String mMusicThemeName;

    /** 水印 */
    public String mWatermarkThemeName;

    /** 滤镜 */
    public String mFilterThemeName;

    // ~~~ 变声
    /** 音频文件 */
    public String mSoundText;
    /** 音频文件编号 */
    public String mSoundTextId;
    /** 变声主题名称 */
    public String mSoundThemeName;

    // ~~~ 变速
    /** 变声主题名称 */
    public String mSpeedThemeName;

    // ~~~ 静音
    /** 主题静音 */
    public boolean mThemeMute;
    /** 原声静音 */
    public boolean mOrgiMute;

    public MediaThemeObject() {

    }

    /** 检测是否是空主题，没有设置任何参数 */
    public boolean isEmpty() {
        //非空主题
        if (!"Empty".equals(mMVThemeName)) {
            return false;
        }
        //没有静音、没有音乐、没有水印、没有滤镜、没有变声、没有变速
        return !mOrgiMute && isEmpty(mMusicThemeName, mWatermarkThemeName, mFilterThemeName, mSoundThemeName, mSpeedThemeName);
    }

    private boolean isEmpty(String... themes) {
        for (String theme : themes) {
            //非空
            if (!"Empty".equals(theme)) {
                return false;
            }
        }
        return true;
    }
}
