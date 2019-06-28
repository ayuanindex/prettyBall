package top.ayuan.prettyball.main;

public interface StateCallBack {
    void notifyGameOver();

    /**
     * 小球加速
     */
    void addBallSpeed();

    /**
     * 小球减速
     */
    void downBallSpeed();
}
