package top.ayuan.prettyball.main;

public interface StateCallBack {
    void notifyGameOver();

    /**
     * 小球速度增加
     */
    void addBallSpeed();

    void downBallSpeed();
}
