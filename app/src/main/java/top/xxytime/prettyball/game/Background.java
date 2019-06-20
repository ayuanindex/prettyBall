package top.xxytime.prettyball.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import top.xxytime.prettyball.main.Main;
import top.xxytime.prettyball.utils.Tools;

/**
 * 背景的实体类
 * 封装背景图片的属性
 * 绘制：绘制一张图片
 * 图片切换：每个三秒中切换一张图片
 */
public class Background {

    /**
     * 背景切换时间为5分钟
     */
    private final int NI_BACKDROUND_CHANGE_TIME = 1000;

    /**
     * 图片渐变背景图片
     */
    private Bitmap bmpBackgroundAlpha;

    /**
     * 判断是否渐变
     */
    private boolean isShowing;

    /**
     * 笔刷，渐变
     */
    private Paint paintBackGround;

    /**
     * 背景图片的整数
     */
    private final int NI_BACKGROUND_MAX = 3;

    /**
     * 当前背景图片
     */
    private Bitmap bmpbackground;

    /**
     * 当前背景图片编号
     */
    private int niBackgroundId;

    private boolean isThread = false;


    /**
     * 构造方法
     */
    public Background() {
        // 给笔刷赋值
        paintBackGround = new Paint();
        // 设置透明度（0：完全透明--255：不透明）
        paintBackGround.setAlpha(0);
        bmpbackground = Tools.readBitmapFromAssets("image/background/" + niBackgroundId + ".png");
    }

    /**
     * 重置方法
     */
    public void reset() {
        // 当前背景重置
        if (niBackgroundId == 0) {
            niBackgroundId = 0;
            bmpbackground = Tools.readBitmapFromAssets("image/background/" + niBackgroundId + ".png");
        }
        // 重置渐变背景
        if (bmpBackgroundAlpha != null) {
            // 渐变背景图设置为null
            bmpBackgroundAlpha = null;
            // 画笔透明度设置为0
            paintBackGround.setAlpha(0);
            isShowing = false;
        }
    }

    /**
     * 绘制方法
     *
     * @param canvas
     */
    public void onDraw(Canvas canvas) {
        canvas.drawBitmap(bmpbackground, Main.getRECT_GANESCREEN_X(), Main.getRECT_GANESCREEN_Y(), null);
        if (bmpBackgroundAlpha != null) {
            canvas.drawBitmap(bmpBackgroundAlpha, Main.getRECT_GANESCREEN_X(), Main.getRECT_GANESCREEN_Y(), paintBackGround);
        }
    }

    /**
     * 核心逻辑方法
     */
    public void logic() {
        if (isShowing) {
            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 透明度值变化
            int niOldAlpha = paintBackGround.getAlpha();
            System.out.println(niOldAlpha);
            niOldAlpha++;


            //渐变图已经不透明了，将背景完全遮挡
            if (niOldAlpha >= 255) {
                // 将渐变图赋值给当前显示的背景图片
                bmpbackground = bmpBackgroundAlpha;
                // 等待渐变图片肩负
                try {
                    Thread.sleep(80);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 销毁背景图片
                bmpBackgroundAlpha = null;
                // 数值还原
                isShowing = false;
                paintBackGround.setAlpha(0);
            } else {
                paintBackGround.setAlpha(niOldAlpha);

            }
        } else {//不在渐变过程
            try {
                // 切换间隔休眠时间
                Thread.sleep(NI_BACKDROUND_CHANGE_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 渐变背景图得到图片
            niBackgroundId++;
            if (niBackgroundId == NI_BACKGROUND_MAX) {
                niBackgroundId = 0;
            }
            bmpBackgroundAlpha = Tools.readBitmapFromAssets("image/background/" + niBackgroundId + ".png");
            // 改变状态
            isShowing = true;
        }


        // 图片切换的逻辑
        /*niBackgroundId++;
        if (niBackgroundId == NI_BACKGROUND_MAX) {
            niBackgroundId = 0;
        }
        bmpBackgroundAlpha = Tools.readBitmapFromAssets("image/background/" + niBackgroundId + ".png");*/
    }

    /**
     * 线程启动方法
     */
    public void start() {
        if (!isThread) {
            isThread = true;
            new Thread(new LogicMonitor()).start();
        }
    }

    /**
     * 线程关闭方法
     */
    public void close() {
        if (isThread) {
            isThread = false;
        }
    }

    /**
     * 通过内部类的方式创建一个线程
     */
    private class LogicMonitor implements Runnable {
        @Override
        public void run() {
            while (isThread) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logic();
            }
        }
    }
}