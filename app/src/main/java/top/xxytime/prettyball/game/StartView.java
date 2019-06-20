package top.xxytime.prettyball.game;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Point;

import java.util.Timer;

import top.xxytime.prettyball.main.Main;
import top.xxytime.prettyball.utils.Tools;

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

    /**
     * 计数：按下任意键
     */
    private int niPressAnythingCount;

    /**
     * 状态：按任意键开始
     */
    private boolean isPressAnything;

    /**
     * 在构造方法中进行赋值
     */
    public StartView() {
        bmpBackground = Tools.readBitmapFromAssets("image/system/start.png");

        pBackground = new Point(
                Main.getRECT_GANESCREEN_X() + (Main.getRECT_GANESCREEN_WIDTH() - bmpBackground.getWidth()) / 2,
                Main.getRECT_GANESCREEN_Y() + (Main.getRECT_GANESCREEN_HEIGHT() - bmpBackground.getHeight()) / 2
        );

        pLoading = Tools.readBitmapFolderFromAssets("image/system/loading");

    }
}
