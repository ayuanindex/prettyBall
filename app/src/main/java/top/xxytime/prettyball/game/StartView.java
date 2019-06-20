package top.xxytime.prettyball.game;

import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.Timer;

/**
 * 开始界面，点击开始界面经过家在过程到达主界面
 */
public class StartView {
    /**
     * 图片：背景图片
     */
    private Bitmap bmpBackground;

    /**
     * 位置：背景图片位置
     */
    private Point pBackground;

    /**
     * 图片组：Loading动画
     */
    private Bitmap[] pLoading;

    /**
     * 线程：Loading动画效果实现
     */
    private Timer timerLoading;

    /**
     * 图片：按下任意键
     */
    private Bitmap bmpPressAnything;

    /**
     * 位置：按下任意键的位置
     */
    private Point pPressAnything;

    /**
     * 资源读取回调对象
     */
    private LoadResourceListener loadResourceListener;

    /**
     * 下标：Loading动画的下标
     */
    private int niFrame;
}
