package top.xxytime.prettyball.game;

import top.xxytime.prettyball.main.BallCallback;
import top.xxytime.prettyball.main.Main;
import top.xxytime.prettyball.utils.Tools;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Calendar;
import java.util.Timer;

public class Ball {
    /**
     * 球类型-草莓型(增加玩家角色生命值10点)
     **/
    public static final int NI_TYPE_0 = 0;
    /**
     * 球类型-苹果型(增加玩家50分)
     **/
    public static final int NI_TYPE_1 = 1;
    /**
     * 球类型-海洋型(5秒内停止减少生命，当已经进入此状态时，则增加50点生命值)
     **/
    public static final int NI_TYPE_2 = 2;
    /**
     * 球类型-太阳型(升级，如果已经是满级状态，则当前的生命值直接加满)
     **/
    public static final int NI_TYPE_3 = 3;
    /**
     * 球类型-黑洞型(减少玩家角色生命值30点)
     **/
    public static final int NI_TYPE_4 = 4;
    /**
     * 球类型-金币型(减少分数值200分)
     **/
    public static final int NI_TYPE_5 = 5;
    /**
     * 球类型-炸弹型(直接降一级，如果已经是1级状态，则进入gameover)
     **/
    public static final int NI_TYPE_6 = 6;

    /**
     * 类型总数
     **/
    private static final int NI_TYPE_MAX = 7;
    /**
     * 重生时间累计标准值
     **/
    private static final int NI_RELIVE_TIME = 30;
    /**
     * 动画帧总数
     **/
    private static final int NI_FRAME_MAX = 6;
    /**
     * 尺寸
     **/
    private static final int NI_SIZE = 32;

    /**
     * 图片组-动画
     **/
    private static final Bitmap[][] ARR_BMP;

    static {
        //样式
        ARR_BMP = new Bitmap[NI_TYPE_MAX][];
        for (int i = 0; i < ARR_BMP.length; i++)
            ARR_BMP[i] = Tools.readBitmapFolderFromAssets("image/balls/" + i);
    }

    /**
     * 监听回调对象
     **/
    private BallCallback callback;
    /**
     * 区域-位置
     **/
    private Rect rectPosition;

    /**
     * 速度
     **/
    private int niSpeed;
    /**
     * 帧
     **/
    private int niFrame;
    /**
     * 类型
     **/
    private int niType;
    /**
     * 重生时间累计(>0：代表正处于等待重生, -1代表此小球可用, 0代表此小球正在使用)
     **/
    private int niReliveTimeCount;
    private boolean isThread;

    public Ball() {
        this.niSpeed = 4;
        rectPosition = new Rect();
        reset();
    }

    public void reset() {
        //动画
        niFrame = 0;
        //位置
        rectPosition.left = Tools.getRandomInt(Main.getRECT_GANESCREEN_X(), Main.getRECT_GANESCREEN_X() + Main.getRECT_GANESCREEN_WIDTH() - NI_SIZE);
        rectPosition.top = Main.getRECT_GANESCREEN_Y() - NI_SIZE;
        rectPosition.right = rectPosition.left + NI_SIZE;
        rectPosition.bottom = rectPosition.top + NI_SIZE;
        //累计重生时间
        niReliveTimeCount = NI_RELIVE_TIME;
        //类型
        boolean isGoodBall = Tools.getRandomInt(0, 2) == 0;//先根据第一个随机值获得好、坏球之分
        int niRandom = 0;
        if (isGoodBall) {
            niRandom = Tools.getRandomInt(0, 10);//根据第二个随机值获得具体的小球类型
            if (niRandom < 5)
                this.niType = Ball.NI_TYPE_0;
            else if (niRandom < 7)
                this.niType = Ball.NI_TYPE_1;
            else if (niRandom < 9)
                this.niType = Ball.NI_TYPE_2;
            else
                this.niType = Ball.NI_TYPE_3;
        } else {
            niRandom = Tools.getRandomInt(0, 4);//根据第二个随机值获得具体的小球类型
            if (niRandom < 2)
                this.niType = Ball.NI_TYPE_4;
            else if (niRandom < 3)
                this.niType = Ball.NI_TYPE_5;
            else
                this.niType = Ball.NI_TYPE_6;
        }
    }

    public void onDraw(Canvas canvas) {
        if (niReliveTimeCount == 0)
            canvas.drawBitmap(ARR_BMP[niType][niFrame], rectPosition.left, rectPosition.top, null);
    }

    public void logic() {
        //正在使用中
        if (niReliveTimeCount == 0) {
            niFrame = ++niFrame % NI_FRAME_MAX;
            rectPosition.top += niSpeed;
            rectPosition.bottom += niSpeed;
            //检测小球是否超出屏幕底端
            if (rectPosition.top > Main.getRECT_GANESCREEN_Y() + Main.getRECT_GANESCREEN_HEIGHT()) {
                reset();
            } else {
                callback.collideCheck(this);
            }
        }
        //重生中
        else if (niReliveTimeCount != -1) {
            niReliveTimeCount--;
            if (niReliveTimeCount == 0)
                niReliveTimeCount = -1;
        }
    }

    /**
     * 使小球进入使用状态
     */
    public void use() {
        niReliveTimeCount = 0;
    }

    /**
     * 是否可用
     *
     * @return
     */
    public boolean isEnable() {
        return niReliveTimeCount == -1;
    }

    /**
     * 是否与指定对象发生碰撞
     *
     * @param p : 玩家角色对象
     * @return
     */
    public boolean isCollideWith(Player p) {
        return Rect.intersects(rectPosition, p.getRectPosition());
    }

    /**
     * 获取类型
     *
     * @return
     */
    public int getType() {
        return niType;
    }

    /**
     * 加入小球监听
     *
     * @param callback
     */
    public void addBallListener(BallCallback callback) {
        this.callback = callback;
    }

    /**
     * 删除小球监听
     */
    public void removeListener() {
        if (callback != null)
            callback = null;
    }

    public void start() {
        if (isThread) {
            isThread = false;
            //玩家角色的动画线程启动
            new Thread(new LogicMonitor()).start();
        }
    }

    public void stop() {

    }

    private class LogicMonitor implements Runnable {
        @Override
        public void run() {
            try {
                new Thread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logic();
        }
    }
}
