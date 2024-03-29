package top.ayuan.prettyball.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;

import java.util.Timer;
import java.util.TimerTask;

import top.ayuan.prettyball.main.Main;
import top.ayuan.prettyball.utils.Tools;

/**
 * 开始界面，点击开始界面进过加载过程到主界面
 */
public class StartView {
    /**
     * 背景图
     */
    private Bitmap bmpBackground;
    /**
     * 位置：背景
     */
    private Point pBackground;

    /**
     * 图片组：Loading动画
     */
    private Bitmap[] arrBmpLoading;

    /**
     * loading图片位置
     */
    private Point pLoading;

    /**
     * 线程：loading动画效果实现
     */
    private Timer timerLoading = null;

    /**
     * 图片：按下任意键
     */

    private Bitmap bmpPressAnything;
    /**
     * 位置：按下任意键
     */

    private Point pPressAnything;

    /**
     * 资源读取回掉模式
     */
    private LoadResourceListener listener;

    /**
     * 下标：loading动画的下标
     */
    private int niFrame;

    /**
     * 计数
     */
    private int niPressAnythingCount;

    /**
     * 状态：按下任意键开始
     */
    private boolean isPressAnything;

    /**
     * 在构造方法中赋值
     */
    public StartView() {
        bmpBackground = Tools.readBitmapFromAssets("image/system/start.png");

        pBackground = new Point(
                Main.getRECT_GANESCREEN_X() + (Main.getRECT_GANESCREEN_WIDTH() - bmpBackground.getWidth()) / 2,
                Main.getRECT_GANESCREEN_Y() + (Main.getRECT_GANESCREEN_HEIGHT() - bmpBackground.getHeight()) / 2
        );

        arrBmpLoading = Tools.readBitmapFolderFromAssets("image/system/loading");

        pLoading = new Point(
                Main.getRECT_GANESCREEN_X() + (Main.getRECT_GANESCREEN_WIDTH() - arrBmpLoading[0].getWidth()) / 2,
                Main.getRECT_GANESCREEN_Y() + (Main.getRECT_GANESCREEN_HEIGHT() - arrBmpLoading[0].getHeight()) / 2
        );

        bmpPressAnything = Tools.readBitmapFromAssets("image/system/press.png");

        pPressAnything = new Point(
                Main.getRECT_GANESCREEN_X() + (Main.getRECT_GANESCREEN_WIDTH() - bmpPressAnything.getWidth()) / 2,
                Main.getRECT_GANESCREEN_Y() + (Main.getRECT_GANESCREEN_HEIGHT() - bmpPressAnything.getHeight()) / 2
        );
    }

    /**
     * 绘制方法
     */
    public void onDraw(Canvas canvas) {
        // 绘制开始游戏的背景
        canvas.drawBitmap(bmpBackground, pBackground.x, pBackground.y, null);

        if (isPressAnything) {
            // 绘制press图片
            if (niPressAnythingCount % 3 == 0) {
                canvas.drawBitmap(bmpPressAnything, pPressAnything.x, pPressAnything.y, null);
            }
        } else {
            // 绘制loading图片
            canvas.drawBitmap(arrBmpLoading[niFrame], pLoading.x, pLoading.y, null);
        }
    }

    /**
     * 逻辑
     */
    public void logic() {
        if (isPressAnything) {
            niPressAnythingCount++;
        } else {
            niFrame = (niFrame + 1) % arrBmpLoading.length;
        }
    }

    /**
     * 开始方法
     */
    public void start() {
        if (timerLoading == null) {
            timerLoading = new Timer();
            timerLoading.schedule(new LoadingAnimationMonitor(), 40, 40);
        }
        // 读取资源
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 防止加载过快，而无法看到loading动画
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (listener != null) {
                    // 读取资源
                    isPressAnything = listener.loadResource();
                }
            }
        }).start();
    }

    /**
     * 是否可以开始游戏
     */
    public boolean isStartGame() {
        return isPressAnything;
    }

    /**
     * 添加资源读取对象
     */
    public void addLoadResourceListener(LoadResourceListener listener) {
        this.listener = listener;
    }

    /**
     * 删除资源读取回调对象
     */
    public void removeLoadResourceListener() {
        if (listener != null) {
            listener = null;
        }
    }

    /**
     * 关闭方法
     */
    public void close() {
        if (timerLoading != null) {
            timerLoading.cancel();
            timerLoading = null;
        }
    }

    /**
     * TimerTask的实现类
     */
    private class LoadingAnimationMonitor extends TimerTask {
        @Override
        public void run() {
            logic();
        }
    }
}
